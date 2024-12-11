package com.fiiiiive.zippop.orders.service;


import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.common.responses.BaseResponseMessage;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsImageRes;
import com.fiiiiive.zippop.goods.model.dto.SearchGoodsRes;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.model.entity.GoodsImage;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.model.entity.OrdersDetail;
import com.fiiiiive.zippop.orders.model.dto.*;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.settlement.repository.SettlementRepository;
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
import org.springframework.data.domain.Sort;
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
    private final CustomerRepository customerRepository;
    private final OrdersDetailRepository ordersDetailRepository;
    private final OrdersRepository ordersRepository;
    private final GoodsRepository goodsRepository;
    private final StoreRepository storeRepository;
    private final SettlementRepository settlementRepository;

    // 결제 검증(예약용)
    @Transactional
    public VerifyOrdersRes verifyOrdersReserve(CustomUserDetails customUserDetails, String impUid) throws BaseException, IamportResponseException, IOException {
        // 1. 고객 회원만 굿즈 구매 가능
        if (!customUserDetails.getRole().equals("ROLE_CUSTOMER")){
            throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_INVALID_ROLE);
        }

        // 2. 회원 정보 조회
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
        );

        // 3. 결제 정보 조회
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
        Payment payment = response.getResponse();
        if (payment == null) {
            throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL);
        }

        // 4. 결제 금액과 customData 확인 및 Map 형태로 변환
        Integer payedPrice = payment.getAmount().intValue();
        String customData = payment.getCustomData();
        if (customData == null) {
            throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL);
        }
        Gson gson = new Gson();
        Map<String, Double> goodsMap = gson.fromJson(customData, Map.class);

        // 5. 초기 변수 설정
        int totalPurchasePrice = 0;
        int addPoint = 0;
        int usedPoint;
        List<Goods> goodsList = new ArrayList<>();

        // 6. 예약 굿즈 구매
        Goods goods = null;
        // 6-1. 결제하려는 굿즈 리스트 생성, 재고 수량 확인, 총 구매 금액 계산
        for (String key : goodsMap.keySet()) {
            int purchaseGoodsAmount = goodsMap.get(key).intValue();
            goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
            );
            // 구매 항목개수가 1개 이상이면 결제 실패
            if (purchaseGoodsAmount != 1) {
                refund(payment.getImpUid(), payment);
                throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
            }
            goodsList.add(goods);
            totalPurchasePrice += purchaseGoodsAmount * goods.getPrice();
        }
        // 6-2. 포인트 적립 계산(총 구매 금액의 5%)
        addPoint += (int) Math.round(totalPurchasePrice * 0.05);
        usedPoint = 0;
        // 6-4. 배송비 적용 x 포인트 사용 x , 최종 구매 금액 조정 및 갱신
        customer.setPoint(customer.getPoint() - usedPoint + addPoint);
        // 8. 결제 금액 검증 / 불일치 시 환불 처리
        if (!payedPrice.equals(totalPurchasePrice) && goods ==  null) {
            refund(payment.getImpUid(), payment);
            throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
        }

        // 8-1. 고객 포인트 갱신 저장
        customerRepository.save(customer);
        Orders orders = null;
        // 8-2. 주문 객체 생성 및 저장 예약 굿즈 구매 배송비 X, 상태 RESERVE_READY
        orders = Orders.builder()
                .impUid(impUid)
                .totalPrice(totalPurchasePrice)
                .status("RESERVE_READY")
                .deliveryCost(0)
                .usedPoint(usedPoint)
                .customer(customer)
                .storeIdx(goods.getStore().getIdx())
                .build();
        ordersRepository.save(orders);

        // 8-3. 굿즈 재고 차감 및 주문 상세 정보 저장
        for (String key : goodsMap.keySet()) {
            Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
            goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
            );
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
    }

    // 결제 검증(재고용)
    @Transactional
    public VerifyOrdersRes verifyOrdersStock(CustomUserDetails customUserDetails, String impUid) throws BaseException, IamportResponseException, IOException {
        // 1. 고객 회원만 굿즈 구매 가능
        if (!customUserDetails.getRole().equals("ROLE_CUSTOMER")){
            throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_INVALID_ROLE);
        }

        // 2. 회원 정보 조회
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
        );

        // 3. 결제 정보 조회
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(impUid);
        Payment payment = response.getResponse();
        if (payment == null) {
            throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL);
        }

        // 4. 결제 금액과 customData 확인 및 Map 형태로 변환
        Integer payedPrice = payment.getAmount().intValue();
        String customData = payment.getCustomData();
        if (customData == null) {
            throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL);
        }
        Gson gson = new Gson();
        Map<String, Double> goodsMap = gson.fromJson(customData, Map.class);

        // 5. 초기 변수 설정
        int totalPurchasePrice = 0;
        int addPoint = 0;
        int usedPoint;
        List<Goods> goodsList = new ArrayList<>();
        // 6. 예약 굿즈 구매(flag = true)
        Goods goods = null;
        // 7. 재고 굿즈 구매
        // 7-1. 결제하려는 굿즈 리스트 생성, 총 구매 금액 계산
        for (String key : goodsMap.keySet()) {
            Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
            goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
            );
            // 구매한 항목 수가 굿즈의 남은 수량보다 크면 예외
            if (purchaseGoodsAmount > goods.getAmount()) {
                refund(payment.getImpUid(), payment);
                throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_POINT_EXCEEDED);
            }
            goodsList.add(goods);
            totalPurchasePrice += purchaseGoodsAmount * goods.getPrice();
        }
        // 7-2. 사용된 포인트 계산 (적립 X)
        usedPoint = (totalPurchasePrice + 2500) - payedPrice;
        // 7-3 포인트 유효성 검사 (3000포인트 이상부터 사용 가능)
        if (usedPoint != 0 && (customer.getPoint() < 3000 || customer.getPoint() - usedPoint < 0)) {
            refund(payment.getImpUid(), payment);
            throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_POINT_EXCEEDED);
        }
        // 7-4. 배송비 적용 및 최종 구매 금액 조정 및 갱신
        totalPurchasePrice += 2500 - usedPoint;
        customer.setPoint(customer.getPoint() - usedPoint);
        // 8. 결제 금액 검증 / 불일치 시 환불 처리
        if (!payedPrice.equals(totalPurchasePrice) && goods ==  null){
            refund(payment.getImpUid(), payment);
            throw new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
        }
        // 8-1. 고객 포인트 갱신 저장
        customerRepository.save(customer);

        // 8-2. 주문 객체 생성 및 저장 재고 굿즈 구매 배송비 2500, 상태 STOCK_READY
        Orders orders = Orders.builder()
                .impUid(impUid)
                .totalPrice(totalPurchasePrice)
                .status("STOCK_READY")
                .deliveryCost(2500)
                .usedPoint(usedPoint)
                .customer(customer)
                .storeIdx(goods.getStore().getIdx())
                .build();
        ordersRepository.save(orders);
        // 8-3. 굿즈 재고 차감 및 주문 상세 정보 저장
        for (String key : goodsMap.keySet()) {
            Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
            goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
            );
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
    }

    // 결제 취소 및 환불
    @Transactional
    public void cancelOrders(CustomUserDetails customUserDetails, Long ordersIdx) throws BaseException, IamportResponseException, IOException {
        // 1. 기업 회원은 주문 취소 불가능
        if (customUserDetails.getRole().equals("ROLE_COMPANY")){
            throw new BaseException(BaseResponseMessage.ORDERS_CANCEL_FAIL_INVALID_ROLE);
        }

        // 2. 고객 정보 조회
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_CANCEL_FAIL_NOT_FOUND_MEMBER)
        );

        // 3. 주문 정보 조회
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_CANCEL_FAIL_NOT_FOUND)
        );

        // 4. 배송 상태 확인 (배송 중인 경우 취소 불가)
        if(Objects.equals(orders.getStatus(), "STOCK_DELIVERY") || Objects.equals(orders.getStatus(), "RESERVE_DELIVERY")) {
            throw new BaseException(BaseResponseMessage.ORDERS_CANCEL_FAIL_IS_DELIVERY);
        }

        // 5. 이미 취소된 주문인지 확인
        if(Objects.equals(orders.getStatus(), "STOCK_CANCEL") || Objects.equals(orders.getStatus(), "STOCK_CANCEL")){
            throw new BaseException(BaseResponseMessage.ORDERS_CANCEL_FAIL_ALREADY_CANCEL);
        }

        // 6. 결제 정보 조회
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(orders.getImpUid());
        Payment payment = response.getResponse();
        String customData = payment.getCustomData();
        if (customData == null) {
            throw new BaseException(BaseResponseMessage.ORDERS_CANCEL_FAIL_NOT_FOUND);
        }

        // 7. 굿즈 수량 복구
        Gson gson = new Gson();
        Map<String, Double> goodsMap = gson.fromJson(customData, Map.class);
        for (String key : goodsMap.keySet()) {
            Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
            Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.ORDERS_CANCEL_FAIL_NOT_FOUND_GOODS)
            );
            goods.setAmount(goods.getAmount() + purchaseGoodsAmount);
            goodsRepository.save(goods);
        }

        // 8. 포인트 복구
        customer.setPoint(customer.getPoint() + orders.getUsedPoint());
        customerRepository.save(customer);

        // 9. 주문 상태를 취소로 변경 및 저장
        String orderStatus = switch (orders.getStatus()) {
            case "STOCK_READY", "STOCK_COMPLETE" -> "STOCK_CANCEL";
            case "RESERVE_READY", "RESERVE_COMPLETE" -> "RESERVE_CANCEL";
            default -> throw new BaseException(BaseResponseMessage.ORDERS_CANCEL_FAIL_IS_DELIVERY);
        };
        orders.setStatus(orderStatus);
        ordersRepository.save(orders);
        // 환불 처리 진행
        refund(payment.getImpUid(), payment);
    }

    // 주문 확정
    @Transactional
    public void completeOrders(CustomUserDetails customUserDetails, Long storeIdx, Long ordersIdx) throws BaseException, IamportResponseException, IOException {
        // 1. 주문 확정
        // if : 기업회원일 경우 (배송 완료 처리)
        // else : 고객회원일 경우 (구매 확정 처리)
        if (Objects.equals(customUserDetails.getRole(), "ROLE_COMPANY")) {
            // 1-1. 기업 회원이 속한 상점 확인 및 소유 검증
            Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.STORE_SEARCH_FAIL_NOT_FOUND)
            );
            if (!store.getCompanyEmail().equals(customUserDetails.getEmail())) {
                throw new BaseException(BaseResponseMessage.ORDERS_COMPLETE_FAIL_INVALID_MEMBER);
            }
            // 1-2. 주문 조회 및 주문 상태에 따라 배송 상태로 변경 저장
            Orders orders = ordersRepository.findByOrdersIdxAndStoreIdx(ordersIdx, storeIdx).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.ORDERS_COMPLETE_FAIL_NOT_FOUND)
            );
            //
            String orderStatus = switch (orders.getStatus()) {
                case "STOCK_READY", "STOCK_COMPLETE" -> "STOCK_DELIVERY";
                case "RESERVE_READY", "RESERVE_COMPLETE" -> "RESERVE_DELIVERY";
                case "RESERVE_DELIVERY", "STOCK_DELIVERY" -> throw new BaseException(BaseResponseMessage.ORDERS_COMPLETE_FAIL_IS_DELIVERY);
                default -> throw new BaseException(BaseResponseMessage.ORDERS_COMPLETE_FAIL_IS_CANCEL);
            };
            orders.setStatus(orderStatus);
            ordersRepository.save(orders);
        } else {
            // 2-1. 주문 조회 및 소유 검증
            Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseResponseMessage.ORDERS_COMPLETE_FAIL_NOT_FOUND)
            );
            if (!orders.getCustomer().getIdx().equals(customUserDetails.getIdx())) {
                throw new BaseException(BaseResponseMessage.ORDERS_COMPLETE_FAIL_INVALID_MEMBER);
            }
            // 2-2. 주문 상태에 따라 구매 확정 상태로 변경 저장
            String orderStatus = switch (orders.getStatus()) {
                case "STOCK_READY" -> "STOCK_COMPLETE";
                case "RESERVE_READY" -> "RESERVE_COMPLETE";
                case "RESERVE_DELIVERY", "STOCK_DELIVERY" -> throw new BaseException(BaseResponseMessage.ORDERS_COMPLETE_FAIL_IS_DELIVERY);
                default -> throw new BaseException(BaseResponseMessage.ORDERS_COMPLETE_FAIL_IS_CANCEL);
            };
            orders.setStatus(orderStatus);
            ordersRepository.save(orders);
        }
    }

    // 환불 처리
    public IamportResponse refund(String impUid, Payment info) throws BaseException {
        CancelData cancelData = new CancelData(impUid, true, info.getAmount());
        try {
            return iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (IamportResponseException | IOException e) {
            throw new BaseException(BaseResponseMessage.IAMPORT_ERROR);
        }
    }

    // 고객 주문 상세 조회
    public SearchOrdersRes searchOrdersAsCustomer(CustomUserDetails customUserDetails, Long ordersIdx) throws BaseException {
        // 1. 주문, 고객 인덱스로 조회 및 예외
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_SEARCH_FAIL_NOT_FOUND)
        );

        // 2. Orders DTO List로 반환
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
                .orderStatus(orders.getStatus())
                .deliveryCost(orders.getDeliveryCost())
                .createdAt(orders.getCreatedAt())
                .updatedAt(orders.getUpdatedAt())
                .searchOrdersDetailResList(searchOrdersDetailResList)
                .build();
    }

    // 고객 주문 목록 조회
    public Page<SearchOrdersRes> searchAllOrdersAsCustomer(CustomUserDetails customUserDetails, int page, int size) throws BaseException {
        // 1. 고객 인덱스로 페이징 조회 및 예외 처리
        Page<Orders> ordersPage = ordersRepository.findAllByCustomerIdx(customUserDetails.getIdx(),PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        // 2. Orders DTO page 반환
        Page<SearchOrdersRes> searchOrdersResPage = ordersPage.map(orders -> {
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

    // 기업 고객 주문 상세 조회
    public SearchOrdersRes searchOrdersAsCompany(CustomUserDetails customUserDetails, Long storeIdx, Long ordersIdx) throws BaseException {
        // 1. 예외: 해당 스토어의 소유자가 아닐 때 / 스토어, 주문 인덱스로 조회 결과가 없을 때
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_SEARCH_FAIL_NOT_FOUND_STORE)
        );
        if(!(store.getCompanyEmail().equals(customUserDetails.getEmail()))){
            throw new BaseException(BaseResponseMessage.ORDERS_SEARCH_FAIL_INVALID_MEMBER);
        }
        Orders orders = ordersRepository.findByOrdersIdxAndStoreIdx(ordersIdx, storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_SEARCH_FAIL_NOT_FOUND)
        );

        // 2. Orders DTO List 반환
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

    // 기업 고객 주문 목록 조회
    public Page<SearchOrdersRes> searchAllOrdersAsCompany(CustomUserDetails customUserDetails, Long storeIdx, int page, int size) throws BaseException {
        // 1. 예외: 해당 스토어의 소유자가 아닐 때 / 스토어, 주문 인덱스로 조회 결과가 없을 때
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND_STORE)
        );
        if(!(store.getCompanyEmail().equals(customUserDetails.getEmail()))){
            throw new BaseException(BaseResponseMessage.ORDERS_SEARCH_ALL_FAIL_INVALID_MEMBER);
        }
        Page<Orders> ordersPage = ordersRepository.findAllByStoreIdx(storeIdx, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"))).orElseThrow(
                () -> new BaseException(BaseResponseMessage.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND)
        );
        // 2. Orders DTO Page 반환
        Page<SearchOrdersRes> searchOrdersResPage = ordersPage.map(orders -> {
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

