package com.fiiiiive.zippop.orders.service;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsImageRes;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.model.entity.GoodsImage;
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
import com.fiiiiive.zippop.store.model.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import com.google.gson.Gson;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrdersService {
    private final IamportClient iamportClient;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final OrdersRepository ordersRepository;
    private final GoodsRepository goodsRepository;
    private final StoreRepository storeRepository;



    // READY: 배송 준비
    // CANCEL: 배송 취소
    // COMPLETE: 고객 주문 확정
    // DELIVERY: 기업 배송 확정
    // 주문 검증
    @Transactional
    public VerifyOrdersRes verifyOrders(CustomUserDetails customUserDetails, String impUid, Boolean flag) throws BaseException, IamportResponseException, IOException {
        // 고객 회원 굿즈 구매
        if (!customUserDetails.getRole().equals("ROLE_CUSTOMER")){
            throw new BaseException(BaseResponseMessage.POPUP_STORE_PAY_FAIL_INVALID_ROLE);
        }
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx())
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
        Goods goods = null;
        if (flag) {
            // 결제하려는 상품 리스트 생성
            for (String key : goodsMap.keySet()) {
                int purchaseGoodsPrice = goodsMap.get(key).intValue();
                goods = goodsRepository.findByGoodsIdx(Long.parseLong(key))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_GOODS_NULL));
                if (purchaseGoodsPrice != 1) {
                    refund(payment.getImpUid(), payment);
                    throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_LIMIT_EXCEEDED);
                }
                goodsList.add(goods);
                totalPurchasePrice += purchaseGoodsPrice * goods.getPrice();
            }
            // 포인트 적립
            addPoint += (int) Math.round(totalPurchasePrice * 0.05);
            usedPoint = (totalPurchasePrice + 500) - payedPrice;
            if (usedPoint != 0 && (customer.getPoint() < 3000 || customer.getPoint() - usedPoint < 0)) {
                refund(payment.getImpUid(), payment);
                throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_POINT_EXCEEDED);
            }
            // 전체 구매 금액 (포인트 사용)
            totalPurchasePrice += 500 - usedPoint;
            // 포인트 갱신
            customer.setPoint(customer.getPoint() - usedPoint + addPoint);
        } else { // 재고 굿즈 구매 (flag == false)
            for (String key : goodsMap.keySet()) {
                Integer purchaseGoodsPrice = goodsMap.get(key).intValue();
                goods = goodsRepository.findByGoodsIdx(Long.parseLong(key))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_GOODS_NULL));
                if (purchaseGoodsPrice > goods.getAmount()) {
                    refund(payment.getImpUid(), payment);
                    throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_EXCEEDED);
                }
                goodsList.add(goods);
                totalPurchasePrice += purchaseGoodsPrice * goods.getPrice();
            }
            usedPoint = (totalPurchasePrice + 2500) - payedPrice;
            if (usedPoint != 0 && (customer.getPoint() < 3000 || customer.getPoint() - usedPoint < 0)) {
                refund(payment.getImpUid(), payment);
                throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_POINT_EXCEEDED);
            }

            totalPurchasePrice += 2500 - usedPoint;
            customer.setPoint(customer.getPoint() - usedPoint);
        }
        // 결제 금액이 동일하면
        if (payedPrice.equals(totalPurchasePrice) && goods !=  null){
            customerRepository.save(customer);
            Orders orders = null;
            if(flag){ // 예약 굿즈 구매
                orders = Orders.builder()
                        .impUid(impUid)
                        .totalPrice(totalPurchasePrice)
                        .status("RESERVE_READY")
                        .deliveryCost(0)
                        .usedPoint(usedPoint)
                        .customer(customer)
                        .storeIdx(goods.getStore().getIdx())
                        .build();
            } else { // 재고 굿즈 구매
                orders = Orders.builder()
                        .impUid(impUid)
                        .totalPrice(totalPurchasePrice)
                        .status("STOCK_READY")
                        .deliveryCost(2500)
                        .usedPoint(usedPoint)
                        .customer(customer)
                        .storeIdx(goods.getStore().getIdx())
                        .build();
            }
            ordersRepository.save(orders);
            // 상품 수량 조절
            for (String key : goodsMap.keySet()) {
                Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
                goods = goodsRepository.findByGoodsIdx(Long.parseLong(key))
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
            log.info("결제 성공 : 주문 번호 {}", orders.getIdx());
            return VerifyOrdersRes.builder().ordersIdx(orders.getIdx()).build();
        } else {
            refund(payment.getImpUid(), payment);
            throw new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_VALIDATION_ERROR);
        }
    }

    // 주문 취소 및 환불
    @Transactional
    public void cancelOrders(CustomUserDetails customUserDetails, Long ordersIdx) throws BaseException, IamportResponseException, IOException {
        if (!customUserDetails.getRole().equals("ROLE_CUSTOMER")){
            throw new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_INVALID_ROLE);
        }
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_FAIL_NOT_FOUND_MEMBER));
        // 결제
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_INVALID_MEMBER));
        // DELIVERY 상태가 아닐때
        if(!Objects.equals(orders.getStatus(), "STOCK_DELIVERY") || !Objects.equals(orders.getStatus(), "RESERVE_DELIVERY")) {

            if(Objects.equals(orders.getStatus(), "STOCK_CANCEL") || Objects.equals(orders.getStatus(), "STOCK_CANCEL")){
                throw new BaseException(BaseResponseMessage.POPUP_PAY_REFUND_FAIL_ALREADYCANCELD);
            }
            // 결제 정보 조회
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(orders.getImpUid());
            Payment payment = response.getResponse();
            String customData = payment.getCustomData();
            if (customData == null) {
                throw new BaseException(BaseResponseMessage.POPUP_STORE_PAY_FAIL);
            }
            // 상품 수량 복구
            Gson gson = new Gson();
            Map<String, Double> goodsMap = gson.fromJson(customData, Map.class);
            for (String key : goodsMap.keySet()) {
                Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_PAY_GOODS_NULL));
                goods.setAmount(goods.getAmount() + purchaseGoodsAmount);
                goodsRepository.save(goods);
            }
            // 포인트 복구
            customer.setPoint(customer.getPoint() + orders.getUsedPoint());
            customerRepository.save(customer);

            String orderStatus = null;
            if(Objects.equals(orders.getStatus(), "STOCK_READY") || Objects.equals(orders.getStatus(), "STOCK_COMPLETE") ){
                orderStatus = "STOCK_CANCEL";
            }
            else if(Objects.equals(orders.getStatus(), "RESERVE_READY") || Objects.equals(orders.getStatus(), "RESERVE_COMPLETE")){
                orderStatus = "RESERVE_CANCEL";
            } else {
                throw new BaseException(BaseResponseMessage.PAY_COMPLETE_FAIL_IS_DELIVERY);
            }
            orders.setStatus(orderStatus);
            ordersRepository.save(orders);
            refund(payment.getImpUid(), payment);
        } else {
            throw new BaseException(BaseResponseMessage.POPUP_PAY_REFUND_FAIL_IS_COMPLETE);
        }
    }


    // 주문 확정
    @Transactional
    public void completeOrders(CustomUserDetails customUserDetails, Long storeIdx, Long ordersIdx) throws BaseException, IamportResponseException, IOException {
        if(Objects.equals(customUserDetails.getRole(), "ROLE_COMPANY")){ // 기업회원 주문 확정(배송 했을때)
            Store store = storeRepository.findByStoreIdx(storeIdx)
                    .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
            if(!(store.getCompanyEmail().equals(customUserDetails.getEmail()))){
                throw new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_INVALID_MEMBER);
            }
            Orders orders = ordersRepository.findByOrdersIdxAndStoreIdx(ordersIdx, storeIdx)
                    .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_NOT_FOUND));
            String orderStatus = null;
            if(Objects.equals(orders.getStatus(), "STOCK_READY") || Objects.equals(orders.getStatus(), "STOCK_COMPLETE") ){
                orderStatus = "STOCK_DELIVERY";
            }
            else if(Objects.equals(orders.getStatus(), "RESERVE_READY") || Objects.equals(orders.getStatus(), "RESERVE_COMPLETE")){
                orderStatus = "RESERVE_DELIVERY";
            } else {
                throw new BaseException(BaseResponseMessage.PAY_COMPLETE_FAIL_IS_CANCEL);
            }
            orders.setStatus(orderStatus);
            ordersRepository.save(orders);
        } else if (Objects.equals(customUserDetails.getRole(), "ROLE_CUSTOMER")){ // 고객회원 주문 확정
            Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx())
                    .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_NOT_FOUND));
            if(!(orders.getCustomer().getIdx().equals(customUserDetails.getIdx()))){
                throw new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_INVALID_MEMBER);
            }
            String orderStatus = null;
            if(Objects.equals(orders.getStatus(), "STOCK_READY")){
                orderStatus = "STOCK_COMPLETE";
            }
            else if(Objects.equals(orders.getStatus(), "RESERVE_READY")){
                orderStatus = "RESERVE_COMPLETE";
            } else {
                throw new BaseException(BaseResponseMessage.PAY_COMPLETE_FAIL_IS_CANCEL);
            }
            orders.setStatus(orderStatus);
            ordersRepository.save(orders);
        } else {
            throw new BaseException(BaseResponseMessage.POST_UPDATE_FAIL_INVALID_MEMBER);
        }
    }

    // 환불
    public IamportResponse refund(String impUid, Payment info) throws BaseException {
        CancelData cancelData = new CancelData(impUid, true, info.getAmount());
        try {
            return iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (IamportResponseException | IOException e) {
            throw new BaseException(BaseResponseMessage.PAY_FAIL_IAMPORTONE_SERVER_ERROR);
        }
    }

    public SearchOrdersRes searchOrdersAsCustomer(CustomUserDetails customUserDetails, Long ordersIdx) throws BaseException {
        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_FAIL_NOT_INVALID));
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_NOT_FOUND));
        List<SearchOrdersDetailRes> searchOrdersDetailResList = new ArrayList<>();
        for(OrdersDetail ordersDetail : orders.getOrdersDetailList()){
            Goods goods = ordersDetail.getGoods();
            List<SearchGoodsImageRes> searchGoodsImageResList = new ArrayList<>();;
            for(GoodsImage goodsImage : goods.getGoodsImageList()){
                SearchGoodsImageRes searchGoodsImageRes = SearchGoodsImageRes.builder()
                        .goodsImageIdx(goodsImage.getIdx())
                        .goodsImageUrl(goodsImage.getUrl())
                        .build();
                searchGoodsImageResList.add(searchGoodsImageRes);
            }
            SearchGoodsRes searchGoodsRes = SearchGoodsRes.builder()
                        .goodsIdx(goods.getIdx())
                        .goodsName(goods.getName())
                        .goodsPrice(goods.getPrice())
                        .goodsContent(goods.getContent())
                        .goodsAmount(goods.getAmount())
                        .searchGoodsImageResList(searchGoodsImageResList)
                        .build();
            SearchOrdersDetailRes searchOrdersDetailRes = SearchOrdersDetailRes.builder()
                    .ordersDetailIdx(ordersDetail.getIdx())
                    .eachPrice(ordersDetail.getEachPrice())
                    .createdAt(ordersDetail.getCreatedAt())
                    .updatedAt(ordersDetail.getUpdatedAt())
                    .searchGoodsRes(searchGoodsRes)
                    .build();
            searchOrdersDetailResList.add(searchOrdersDetailRes);
        }
        return SearchOrdersRes.builder()
                .ordersIdx(orders.getIdx())
                .impUid(orders.getImpUid())
                .name(customer.getName())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .phoneNumber(customer.getPhoneNumber())
                .usedPoint(orders.getUsedPoint())
                .totalPrice(orders.getTotalPrice())
                .orderStatus(orders.getStatus())
                .deliveryCost(orders.getDeliveryCost())
                .createdAt(orders.getCreatedAt())
                .updatedAt(orders.getUpdatedAt())
                .searchOrdersDetailResList(searchOrdersDetailResList)
                .build();
    }



    public Page<SearchOrdersRes> searchAllOrdersAsCustomer(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_FAIL_NOT_INVALID));
        Page<Orders> result = ordersRepository.findAllByCustomerIdx(customUserDetails.getIdx(),PageRequest.of(page, size));
        if (!result.hasContent()) {
            throw new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_NOT_FOUND);
        }
        Page<SearchOrdersRes> searchOrdersResPage = result.map(orders -> {
            return SearchOrdersRes.builder()
                    .ordersIdx(orders.getIdx())
                    .impUid(orders.getImpUid())
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .address(customer.getAddress())
                    .phoneNumber(customer.getPhoneNumber())
                    .usedPoint(orders.getUsedPoint())
                    .totalPrice(orders.getTotalPrice())
                    .orderStatus(orders.getStatus())
                    .deliveryCost(orders.getDeliveryCost())
                    .createdAt(orders.getCreatedAt())
                    .updatedAt(orders.getUpdatedAt())
                    .build();
        });
        return searchOrdersResPage;
    }

    public SearchOrdersRes searchOrdersAsCompany(CustomUserDetails customUserDetails, Long storeIdx, Long ordersIdx) throws BaseException {
        Store store = storeRepository.findByStoreIdx(storeIdx)
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        if(!(store.getCompanyEmail().equals(customUserDetails.getEmail()))){
            throw new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_INVALID_MEMBER);
        }
        Orders orders = ordersRepository.findByOrdersIdxAndStoreIdx(ordersIdx, storeIdx)
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_NOT_FOUND));

        List<SearchOrdersDetailRes> searchOrdersDetailResList = new ArrayList<>();
        for(OrdersDetail ordersDetail : orders.getOrdersDetailList()){
            Goods goods = ordersDetail.getGoods();
            List<SearchGoodsImageRes> searchGoodsImageResList = new ArrayList<>();;
            for(GoodsImage goodsImage : goods.getGoodsImageList()){
                SearchGoodsImageRes searchGoodsImageRes = SearchGoodsImageRes.builder()
                        .goodsImageIdx(goodsImage.getIdx())
                        .goodsImageUrl(goodsImage.getUrl())
                        .build();
                searchGoodsImageResList.add(searchGoodsImageRes);
            }
            SearchGoodsRes searchGoodsRes = SearchGoodsRes.builder()
                    .goodsIdx(goods.getIdx())
                    .goodsName(goods.getName())
                    .goodsPrice(goods.getPrice())
                    .goodsContent(goods.getContent())
                    .goodsAmount(goods.getAmount())
                    .searchGoodsImageResList(searchGoodsImageResList)
                    .build();
            SearchOrdersDetailRes searchOrdersDetailRes = SearchOrdersDetailRes.builder()
                    .ordersDetailIdx(ordersDetail.getIdx())
                    .eachPrice(ordersDetail.getEachPrice())
                    .createdAt(ordersDetail.getCreatedAt())
                    .updatedAt(ordersDetail.getUpdatedAt())
                    .searchGoodsRes(searchGoodsRes)
                    .build();
            searchOrdersDetailResList.add(searchOrdersDetailRes);
        }
        return SearchOrdersRes.builder()
                .ordersIdx(orders.getIdx())
                .impUid(orders.getImpUid())
                .name(orders.getCustomer().getName())
                .email(orders.getCustomer().getEmail())
                .address(orders.getCustomer().getAddress())
                .phoneNumber(orders.getCustomer().getPhoneNumber())
                .usedPoint(orders.getUsedPoint())
                .totalPrice(orders.getTotalPrice())
                .deliveryCost(orders.getDeliveryCost())
                .orderStatus(orders.getStatus())
                .createdAt(orders.getCreatedAt())
                .updatedAt(orders.getUpdatedAt())
                .searchOrdersDetailResList(searchOrdersDetailResList)
                .build();
    }

    public Page<SearchOrdersRes> searchAllOrdersAsCompany(CustomUserDetails customUserDetails, Long storeIdx, int page, int size) throws BaseException {
        Store store = storeRepository.findByStoreIdx(storeIdx)
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_STORE_SEARCH_FAIL_NOT_EXIST));
        if(!(store.getCompanyEmail().equals(customUserDetails.getEmail()))){
            throw new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_INVALID_MEMBER);
        }
        Page<Orders> result = ordersRepository.findAllByStoreIdx(storeIdx, PageRequest.of(page, size))
                .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_GOODS_SEARCH_FAIL_STORE_NOT_NOT_FOUND));
        if (!result.hasContent()) {
            throw new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_NOT_FOUND);
        }
        Page<SearchOrdersRes> searchOrdersResPage = result.map(orders -> {
            return SearchOrdersRes.builder()
                    .ordersIdx(orders.getIdx())
                    .impUid(orders.getImpUid())
                    .name(orders.getCustomer().getName())
                    .email(orders.getCustomer().getEmail())
                    .address(orders.getCustomer().getAddress())
                    .phoneNumber(orders.getCustomer().getPhoneNumber())
                    .usedPoint(orders.getUsedPoint())
                    .totalPrice(orders.getTotalPrice())
                    .orderStatus(orders.getStatus())
                    .deliveryCost(orders.getDeliveryCost())
                    .createdAt(orders.getCreatedAt())
                    .updatedAt(orders.getUpdatedAt())
                    .build();
        });
        return searchOrdersResPage;
    }




}

