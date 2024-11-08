package com.fiiiiive.zippop.orders.service;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import com.fiiiiive.zippop.member.repository.CompanyRepository;
import com.fiiiiive.zippop.member.repository.CustomerRepository;
import com.fiiiiive.zippop.member.model.entity.Company;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.member.model.entity.Customer;
import com.fiiiiive.zippop.orders.model.entity.CompanyOrders;
import com.fiiiiive.zippop.orders.model.entity.CustomerOrders;
import com.fiiiiive.zippop.orders.model.entity.CustomerOrdersDetail;
import com.fiiiiive.zippop.orders.model.entity.CompanyOrdersDetail;
import com.fiiiiive.zippop.orders.model.dto.*;
import com.fiiiiive.zippop.goods.PopupGoodsRepository;
import com.fiiiiive.zippop.goods.model.entity.PopupGoods;
import com.fiiiiive.zippop.orders.repository.CompanyOrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.CompanyOrdersRepository;
import com.fiiiiive.zippop.orders.repository.CustomerOrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.CustomerOrdersRepository;
import com.fiiiiive.zippop.store.model.dto.SearchStoreRes;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import com.fiiiiive.zippop.store.model.entity.Store;
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
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class OrdersService {
    private final IamportClient iamportClient;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final CustomerOrdersDetailRepository customerOrdersDetailRepository;
    private final CompanyOrdersDetailRepository companyOrdersDetailRepository;
    private final CustomerOrdersRepository customerOrdersRepository;
    private final CompanyOrdersRepository companyOrdersRepository;
    private final PopupGoodsRepository popupGoodsRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public VerifyOrdersRes verify(CustomUserDetails customUserDetails, String impUid, Integer operation) throws BaseException, IamportResponseException, IOException {
        // 기업회원 등록 수수료 결제(operation = 0 )
        if (customUserDetails.getRole().equals("ROLE_COMPANY") && operation == 0) {
            Company company = companyRepository.findByCompanyEmail(customUserDetails.getEmail())
            .orElseThrow(() -> new BaseException(POPUP_STORE_PAY_FAIL_NOT_FOUND_COMPANY));
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
            Payment payment = response.getResponse();
            if (payment == null) {
                throw new BaseException(POPUP_STORE_PAY_FAIL_NOT_FOUND_PAYINFO);
            }
            Integer payedPrice = payment.getAmount().intValue();
            String customData = payment.getCustomData();
            Gson gson = new Gson();
            Map<String, Object > customDataMap = gson.fromJson(customData, Map.class);
            if (customDataMap.get("storeIdx") == null) {
                throw new BaseException(POPUP_STORE_PAY_FAIL_INCORRECT_REQUEST);
            }
            Long storeIdx = ((Double)customDataMap.get("storeIdx")).longValue();
            Store store = storeRepository.findById(storeIdx)
            .orElseThrow(() -> new BaseException(POPUP_STORE_PAY_FAIL_NOT_FOUND_STORE));
            Integer totalPeople = store.getTotalPeople();
            Integer expectedPrice = totalPeople * 1500;
            if (!payedPrice.equals(expectedPrice)) {
                throw new BaseException(BaseResponseMessage.POPUP_STORE_PAY_FAIL_VALIDATION_ERROR);
            }
            CompanyOrders companyOrders = CompanyOrders.builder()
                    .impUid(impUid)
                    .company(company)
                    .build();
            companyOrdersRepository.save(companyOrders);
            CompanyOrdersDetail companyOrdersDetail = CompanyOrdersDetail.builder()
                    .companyOrders(companyOrders)
                    .totalPrice(expectedPrice)
                    .store(store)
                    .build();
            companyOrdersDetailRepository.save(companyOrdersDetail);
            return VerifyOrdersRes.builder()
                    .impUid(impUid)
                    .storeIdx(storeIdx)
                    .totalPrice(expectedPrice)
                    .totalPeople(totalPeople)
                    .build();
        }
        else if (customUserDetails.getRole().equals("ROLE_CUSTOMER")) {
            Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
            .orElseThrow(() -> new BaseException(POPUP_GOODS_PAY_FAIL_NOT_FOUND_MEMBER));
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
            Payment payment = response.getResponse();
            if (payment == null) {
                throw new BaseException(POPUP_STORE_PAY_FAIL);
            }
            Integer payedPrice = payment.getAmount().intValue();
            String customData = payment.getCustomData();
            if (customData == null) {
                throw new BaseException(POPUP_STORE_PAY_FAIL);
            }
            Gson gson = new Gson();
            Map<String, Double> goodsMap = gson.fromJson(customData, Map.class);
            Integer totalPurchasePrice = 0;
            Integer addPoint = 0;
            Integer usedPoint = 0;
            List<PopupGoods> popupGoodsList = new ArrayList<>();
            // 사전 굿즈 구매(operation = 1)
            if (operation == 1) {
                for (String key : goodsMap.keySet()) {
                    Integer purchaseGoodsPrice = goodsMap.get(key).intValue();
                    PopupGoods popupGoods = popupGoodsRepository.findByIdx(Long.parseLong(key))
                    .orElseThrow(() -> new BaseException(POPUP_GOODS_PAY_GOODS_NULL));
                    if (purchaseGoodsPrice != 1) {
                        throw new BaseException(POPUP_GOODS_PAY_FAIL_LIMIT_EXCEEDED);
                    }
                    popupGoodsList.add(popupGoods);
                    totalPurchasePrice += purchaseGoodsPrice * popupGoods.getProductPrice();
                }
                addPoint += (int) Math.round(totalPurchasePrice * 0.05);
                usedPoint = (totalPurchasePrice + 500) - payedPrice;
                if (customer.getPoint() < 3000 || customer.getPoint() - usedPoint < 0) {
                    throw new BaseException(POPUP_GOODS_PAY_FAIL_POINT_EXCEEDED);
                }
                totalPurchasePrice += 500 - usedPoint;
                customer.setPoint(customer.getPoint() - usedPoint + addPoint);
            } else { // 재고 굿즈 구매 (operation = 2)
                for (String key : goodsMap.keySet()) {
                    Integer purchaseGoodsPrice = goodsMap.get(key).intValue();
                    PopupGoods popupGoods = popupGoodsRepository.findByIdx(Long.parseLong(key))
                    .orElseThrow(() -> new BaseException(POPUP_GOODS_PAY_GOODS_NULL));
                    if (purchaseGoodsPrice > popupGoods.getProductAmount()) {
                        throw new BaseException(POPUP_GOODS_PAY_FAIL_EXCEEDED);
                    }
                    popupGoodsList.add(popupGoods);
                    totalPurchasePrice += purchaseGoodsPrice * popupGoods.getProductPrice();
                }
                usedPoint = (totalPurchasePrice + 2500) - payedPrice;
                if (customer.getPoint() < 3000 || customer.getPoint() - usedPoint < 0) {
                    throw new BaseException(POPUP_GOODS_PAY_FAIL_POINT_EXCEEDED);
                }
                totalPurchasePrice += 2500 - usedPoint;
                customer.setPoint(customer.getPoint() - usedPoint);
            }
            if (!payedPrice.equals(totalPurchasePrice)){
                throw new BaseException(POPUP_GOODS_PAY_FAIL_VALIDATION_ERROR);
            }
            else {
                customerRepository.save(customer);
                CustomerOrders customerOrders = null;
                if(operation == 1){
                    customerOrders = CustomerOrders.builder()
                            .impUid(impUid)
                            .totalPrice(totalPurchasePrice)
                            .orderState("in reserve")
                            .deliveryCost(0)
                            .usedPoint(usedPoint)
                            .customer(customer)
                            .build();
                } else {
                    customerOrders = CustomerOrders.builder()
                            .impUid(impUid)
                            .totalPrice(totalPurchasePrice)
                            .orderState("in delivery")
                            .deliveryCost(2500)
                            .usedPoint(usedPoint)
                            .customer(customer)
                            .build();
                }
                customerOrdersRepository.save(customerOrders);
                for (String key : goodsMap.keySet()) {
                    Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
                    PopupGoods popupGoods = popupGoodsRepository.findById(Long.parseLong(key))
                    .orElseThrow(() -> new BaseException(POPUP_GOODS_PAY_GOODS_NULL));
                    popupGoods.setProductAmount(popupGoods.getProductAmount() - purchaseGoodsAmount);
                    popupGoodsRepository.save(popupGoods);
                    // 배송지 정보는 없음
                    String uuid = UUID.randomUUID().toString();
                    CustomerOrdersDetail customerOrdersDetail = CustomerOrdersDetail.builder()
                            .eachPrice(popupGoods.getProductPrice() * purchaseGoodsAmount)
                            .trackingNumber(uuid)
                            .customerOrders(customerOrders)
                            .popupGoods(popupGoods)
                            .build();
                    customerOrdersDetailRepository.save(customerOrdersDetail);
                }
                return VerifyOrdersRes.builder()
                        .impUid(impUid)
                        .productIdxMap(goodsMap)
                        .totalPrice(totalPurchasePrice)
                        .build();
            }
        } else {
            throw new BaseException(POPUP_PAY_FAIL_NOT_INVALID);
        }
    }

    public List<GetCustomerOrdersRes> searchCustomer(CustomUserDetails customUserDetails) throws BaseException {
        Customer customer = customerRepository.findByCustomerEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(POPUP_PAY_FAIL_NOT_INVALID));
        List<CustomerOrders> customerOrdersList = customerOrdersRepository.findByCustomerIdx(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(POPUP_PAY_SEARCH_FAIL_NOT_FOUND));
        List<GetCustomerOrdersRes> getCustomerOrdersResList = new ArrayList<>();
        for(CustomerOrders customerOrders : customerOrdersList){
            List<CustomerOrdersDetail> customerOrdersDetailList = customerOrders.getCustomerOrdersDetailList();
            List<GetCustomerOrdersDetailRes> getCustomerOrdersDetailResList = new ArrayList<>();
            for(CustomerOrdersDetail customerOrdersDetail : customerOrdersDetailList){
                PopupGoods popupGoods = customerOrdersDetail.getPopupGoods();
                SearchGoodsRes searchGoodsRes = SearchGoodsRes.builder()
                        .productIdx(popupGoods.getProductIdx())
                        .productName(popupGoods.getProductName())
                        .productPrice(popupGoods.getProductPrice())
                        .productContent(popupGoods.getProductContent())
                        .productAmount(popupGoods.getProductAmount())
                        .build();
                GetCustomerOrdersDetailRes getCustomerOrdersDetailRes = GetCustomerOrdersDetailRes.builder()
                        .companyOrdersDetailIdx(customerOrdersDetail.getCustomerOrderDetailIdx())
                        .eachPrice(customerOrdersDetail.getEachPrice())
                        .trackingNumber(customerOrdersDetail.getTrackingNumber())
                        .searchGoodsRes(searchGoodsRes)
                        .build();
                getCustomerOrdersDetailResList.add(getCustomerOrdersDetailRes);
            }
            GetCustomerOrdersRes getCustomerOrdersRes = GetCustomerOrdersRes.builder()
                    .customerOrdersIdx(customerOrders.getCustomerOrdersIdx())
                    .impUid(customerOrders.getImpUid())
                    .usedPoint(customerOrders.getUsedPoint())
                    .totalPrice(customerOrders.getTotalPrice())
                    .orderState(customerOrders.getOrderState())
                    .deliveryCost(customerOrders.getDeliveryCost())
                    .createdAt(customerOrders.getCreatedAt())
                    .updatedAt(customerOrders.getUpdatedAt())
                    .getCustomerOrdersDetailResList(getCustomerOrdersDetailResList)
                    .build();
            getCustomerOrdersResList.add(getCustomerOrdersRes);
        }
        return getCustomerOrdersResList;
    }

    public List<GetCompanyOrdersRes> searchCompany(CustomUserDetails customUserDetails) throws BaseException {
        Company company = companyRepository.findByCompanyEmail(customUserDetails.getEmail())
        .orElseThrow(() -> new BaseException(BaseResponseMessage.POPUP_PAY_SEARCH_FAIL_INVALID_MEMBER));
        List<CompanyOrders> companyOrdersList = companyOrdersRepository.findByCompanyIdx(customUserDetails.getIdx())
        .orElseThrow(() -> new BaseException(POPUP_PAY_SEARCH_FAIL_NOT_FOUND));
        List<GetCompanyOrdersRes> getCompanyOrdersResList = new ArrayList<>();
        for(CompanyOrders companyOrders : companyOrdersList){
            List<CompanyOrdersDetail> companyOrdersDetailList = companyOrders.getCompanyOrdersDetailList();
            List<GetCompanyOrdersDetailRes> getCompanyOrdersDetailResList = new ArrayList<>();
            for(CompanyOrdersDetail companyOrdersDetail : companyOrdersDetailList) {
                Store store = companyOrdersDetail.getStore();
                SearchStoreRes searchStoreRes = SearchStoreRes.builder()
                        .storeIdx(store.getStoreIdx())
                        .companyEmail(store.getCompanyEmail())
                        .storeName(store.getStoreName())
                        .storeContent(store.getStoreContent())
                        .storeAddress(store.getStoreAddress())
                        .category(store.getCategory())
                        .likeCount(store.getLikeCount())
                        .totalPeople(store.getTotalPeople())
                        .storeStartDate(store.getStoreStartDate())
                        .storeEndDate(store.getStoreEndDate())
                        .build();
                GetCompanyOrdersDetailRes getCompanyOrdersDetailRes = GetCompanyOrdersDetailRes.builder()
                        .companyOrdersDetailIdx(companyOrdersDetail.getCompanyOrderDetailIdx())
                        .totalPrice(companyOrdersDetail.getTotalPrice())
                        .searchStoreRes(searchStoreRes)
                        .createdAt(companyOrdersDetail.getCreatedAt())
                        .updatedAt(companyOrdersDetail.getUpdatedAt())
                        .build();
                getCompanyOrdersDetailResList.add(getCompanyOrdersDetailRes);
            }
            GetCompanyOrdersRes getCompanyOrdersRes = GetCompanyOrdersRes.builder()
                    .companyOrdersIdx(companyOrders.getCompanyOrdersIdx())
                    .impUid(companyOrders.getImpUid())
                    .createdAt(companyOrders.getCreatedAt())
                    .updatedAt(companyOrders.getUpdatedAt())
                    .getCompanyOrdersDetailResList(getCompanyOrdersDetailResList)
                    .build();
            getCompanyOrdersResList.add(getCompanyOrdersRes);
        }
        return getCompanyOrdersResList;
    }
}

