package com.fiiiiive.zippop.orders.service;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.auth.repository.CompanyRepository;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.model.entity.OrdersDetail;
import com.fiiiiive.zippop.orders.model.dto.*;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import com.google.gson.Gson;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class OrdersService {
    private final IamportClient iamportClient;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final OrdersRepository ordersRepository;
    private final GoodsRepository goodsRepository;
    private final StoreRepository storeRepository;


    @Transactional
    public VerifyOrdersRes verify(CustomUserDetails customUserDetails, String impUid, Boolean flag) throws BaseException, IamportResponseException, IOException {
        // 고객 회원 굿즈 구매
        if (!customUserDetails.getRole().equals("ROLE_CUSTOMER")){
            throw new BaseException(BaseResponseMessage.POPUP_STORE_PAY_FAIL_INVALID_ROLE);
        }
        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_NOT_FOUND_MEMBER));
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
        Payment payment = response.getResponse();
        if (payment == null) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_PAY_FAIL);
        }
        Integer payedPrice = payment.getAmount().intValue();
        String customData = payment.getCustomData();
        if (customData == null) {
            throw new BaseException(BaseResponseMessage.POPUP_STORE_PAY_FAIL);
        }
        Gson gson = new Gson();
        Map<String, Double> goodsMap = gson.fromJson(customData, Map.class);
        Integer totalPurchasePrice = 0;
        Integer addPoint = 0;
        Integer usedPoint = 0;
        List<Goods> goodsList = new ArrayList<>();
        // 사전 굿즈 구매(flag = true)
        if (flag) {
            // 결제하려는 상품 리스트 생성
            for (String key : goodsMap.keySet()) {
                int purchaseGoodsPrice = goodsMap.get(key).intValue();
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_GOODS_NULL));
                if (purchaseGoodsPrice != 1) {
                    throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_LIMIT_EXCEEDED);
                }
                goodsList.add(goods);
                totalPurchasePrice += purchaseGoodsPrice * goods.getPrice();
            }
            // 포인트 적립
            addPoint += (int) Math.round(totalPurchasePrice * 0.05);
            usedPoint = (totalPurchasePrice + 500) - payedPrice;
            if (customer.getPoint() < 3000 || customer.getPoint() - usedPoint < 0) {
                throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_POINT_EXCEEDED);
            }
            // 전체 구매 금액 (포인트 사용)
            totalPurchasePrice += 500 - usedPoint;
            // 포인트 갱신
            customer.setPoint(customer.getPoint() - usedPoint + addPoint);
        } else { // 재고 굿즈 구매 (flag == false)
            for (String key : goodsMap.keySet()) {
                Integer purchaseGoodsPrice = goodsMap.get(key).intValue();
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_GOODS_NULL));
                if (purchaseGoodsPrice > goods.getAmount()) {
                    throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_EXCEEDED);
                }
                goodsList.add(goods);
                totalPurchasePrice += purchaseGoodsPrice * goods.getPrice();
            }
            usedPoint = (totalPurchasePrice + 2500) - payedPrice;
            if (customer.getPoint() < 3000 || customer.getPoint() - usedPoint < 0) {
                throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_POINT_EXCEEDED);
            }
            totalPurchasePrice += 2500 - usedPoint;
            customer.setPoint(customer.getPoint() - usedPoint);
        }
        // 결제 금액이 동일하면
        if (payedPrice.equals(totalPurchasePrice)){
            customerRepository.save(customer);
            Orders orders = null;
            if(flag){ // 예약 굿즈 구매
                orders = Orders.builder()
                        .impUid(impUid)
                        .totalPrice(totalPurchasePrice)
                        .orderState("RESERVE_READY")
                        .deliveryCost(0)
                        .usedPoint(usedPoint)
                        .customer(customer)
                        .build();
            } else { // 재고 굿즈 구매
                orders = Orders.builder()
                        .impUid(impUid)
                        .totalPrice(totalPurchasePrice)
                        .orderState("DELIVERY_READY")
                        .deliveryCost(2500)
                        .usedPoint(usedPoint)
                        .customer(customer)
                        .build();
            }
            ordersRepository.save(orders);
            // 상품 수량 조절
            for (String key : goodsMap.keySet()) {
                Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_GOODS_NULL));
                goods.setAmount(goods.getAmount() - purchaseGoodsAmount);
                goodsRepository.save(goods);
                OrdersDetail ordersDetail = OrdersDetail.builder()
                        .eachPrice(goods.getPrice() * purchaseGoodsAmount)
                        .orders(orders)
                        .goods(goods)
                        .build();
                ordersDetailRepository.save(ordersDetail);
            }
            return VerifyOrdersRes.builder().ordersIdx(orders.getIdx()).build();
        } else {
            throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_VALIDATION_ERROR);
        }
    }

//    public List<SearchCustomerOrdersRes> searchCustomer(CustomUserDetails customUserDetails) throws BaseException {
//        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
//        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_FAIL_NOT_INVALID));
//        List<Orders> ordersList = ordersRepository.findByCustomerIdx(customUserDetails.getIdx())
//        .orElseThrow(() -> new BaseException(POPUP_PAY_SEARCH_FAIL_NOT_FOUND));
//        List<SearchCustomerOrdersRes> getCustomerOrdersResList = new ArrayList<>();
//        for(Orders orders : ordersList){
//            List<OrdersDetail> ordersDetailList = orders.getOrdersDetailList();
//            List<SearchCustomerOrdersDetailRes> searchCustomerOrdersDetailResList = new ArrayList<>();
//            for(OrdersDetail ordersDetail : ordersDetailList){
//                Goods goods = ordersDetail.getGoods();
//                SearchGoodsRes searchGoodsRes = SearchGoodsRes.builder()
//                        .goodsIdx(goods.getIdx())
//                        .goodsName(goods.getName())
//                        .goodsPrice(goods.getPrice())
//                        .goodsContent(goods.getContent())
//                        .goodsAmount(goods.getAmount())
//                        .build();
//                SearchCustomerOrdersDetailRes searchCustomerOrdersDetailRes = SearchCustomerOrdersDetailRes.builder()
//                        .customerOrdersDetailIdx(ordersDetail.getIdx())
//                        .eachPrice(ordersDetail.getEachPrice())
//                        .trackingNumber(ordersDetail.getTrackingNumber())
//                        .searchGoodsRes(searchGoodsRes)
//                        .build();
//                searchCustomerOrdersDetailResList.add(searchCustomerOrdersDetailRes);
//            }
//            SearchCustomerOrdersRes getCustomerOrdersRes = SearchCustomerOrdersRes.builder()
//                    .customerOrdersIdx(orders.getCustomerOrdersIdx())
//                    .impUid(orders.getImpUid())
//                    .usedPoint(orders.getUsedPoint())
//                    .totalPrice(orders.getTotalPrice())
//                    .orderState(orders.getOrderState())
//                    .deliveryCost(orders.getDeliveryCost())
//                    .createdAt(orders.getCreatedAt())
//                    .updatedAt(orders.getUpdatedAt())
//                    .searchCustomerOrdersDetailResList(searchCustomerOrdersDetailResList)
//                    .build();
//            getCustomerOrdersResList.add(getCustomerOrdersRes);
//        }
//        return getCustomerOrdersResList;
//    }
//
//    public List<SearchCompanyOrdersRes> searchCompany(CustomUserDetails customUserDetails) throws BaseException {
//        Company company = companyRepository.findByCompanyEmail(customUserDetails.getEmail())
//        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_INVALID_MEMBER));
//        List<CompanyOrders> companyOrdersList = companyOrdersRepository.findByCompanyIdx(customUserDetails.getIdx())
//        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_NOT_FOUND));
//        List<SearchCompanyOrdersRes> searchCompanyOrdersResList = new ArrayList<>();
//        for(CompanyOrders companyOrders : companyOrdersList){
//            List<CompanyOrdersDetail> companyOrdersDetailList = companyOrders.getCompanyOrdersDetailList();
//            List<SearchCompanyOrdersDetailRes> searchCompanyOrdersDetailResList = new ArrayList<>();
//            for(CompanyOrdersDetail companyOrdersDetail : companyOrdersDetailList) {
//                Store store = companyOrdersDetail.getStore();
//                SearchStoreRes searchStoreRes = SearchStoreRes.builder()
//                        .storeIdx(store.getIdx())
//                        .companyEmail(store.getCompanyEmail())
//                        .storeName(store.getName())
//                        .storeContent(store.getContent())
//                        .storeAddress(store.getAddress())
//                        .category(store.getCategory())
//                        .likeCount(store.getLikeCount())
//                        .totalPeople(store.getTotalPeople())
//                        .storeStartDate(store.getStartDate())
//                        .storeEndDate(store.getEndDate())
//                        .build();
//                SearchCompanyOrdersDetailRes searchCompanyOrdersDetailRes = SearchCompanyOrdersDetailRes.builder()
//                        .companyOrdersDetailIdx(companyOrdersDetail.getIdx())
//                        .totalPrice(companyOrdersDetail.getTotalPrice())
//                        .searchStoreRes(searchStoreRes)
//                        .createdAt(companyOrdersDetail.getCreatedAt())
//                        .updatedAt(companyOrdersDetail.getUpdatedAt())
//                        .build();
//                searchCompanyOrdersDetailResList.add(searchCompanyOrdersDetailRes);
//            }
//            SearchCompanyOrdersRes searchCompanyOrdersRes = SearchCompanyOrdersRes.builder()
//                    .companyOrdersIdx(companyOrders.getIdx())
//                    .impUid(companyOrders.getImpUid())
//                    .createdAt(companyOrders.getCreatedAt())
//                    .updatedAt(companyOrders.getUpdatedAt())
//                    .searchCompanyOrdersDetailResList(searchCompanyOrdersDetailResList)
//                    .build();
//            searchCompanyOrdersResList.add(searchCompanyOrdersRes);
//        }
//        return searchCompanyOrdersResList;
//    }
}

