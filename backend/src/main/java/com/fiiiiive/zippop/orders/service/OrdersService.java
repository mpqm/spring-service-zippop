package com.fiiiiive.zippop.orders.service;


import com.fiiiiive.zippop.auth.entity.Customer;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.entity.Goods;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.orders.dto.OrdersDto;
import com.fiiiiive.zippop.orders.entity.Orders;
import com.fiiiiive.zippop.orders.entity.OrdersDetail;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.store.entity.Store;
import com.fiiiiive.zippop.store.repository.StoreRepository;
import com.google.gson.Gson;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final ReserveRepository reserveRepository;

    // 고객 회원 확인 및 조회
    public Customer checkCustomer(CustomUserDetails customUserDetails) throws BaseException {
        if (!customUserDetails.getRole().equals("ROLE_CUSTOMER")) throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_INVALID_ROLE);
        return customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
        );
    }

    // 결제 정보 확인
    public Payment checkPaymentData(String impUid) throws BaseException, IamportResponseException, IOException {
        Payment payment = iamportClient.paymentByImpUid(impUid).getResponse();
        if (payment == null) {
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL);
        }
        return payment;
    }

    // 결제 굿즈 정보 확인
    public Map<String, Double> checkGoodsData(Payment payment) {
        String customData = payment.getCustomData();
        // customData Map 형태로 변환
        Map<String, Double> goodsMap = new Gson().fromJson(customData, Map.class);
        return goodsMap;
    }

    // 총 구매 금액 계산(예약용)
    public Integer getTotalPurchasePriceForReserve(Payment payment, Map<String, Double> goodsMap) throws BaseException {
        int totalPurchasePrice = 0;
        for (String key : goodsMap.keySet()) {

            // 결제 하려는 굿즈 수량 확인
            int purchaseGoodsAmount = goodsMap.get(key).intValue();

            // 굿즈 조회(goodsIdx)
            Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseMessage.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
            );

            // 예약 굿즈 구매 수량 확인 / 구매 항목개수가 1개 이상이면 결제 실패 후 환불
            if (purchaseGoodsAmount != 1) {
                refund(payment);
                throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
            }

            // 총 구매 가격
            totalPurchasePrice += purchaseGoodsAmount * goods.getPrice();
        }
        return totalPurchasePrice;
    }

    // 총 구매 금액 계산(재고용)
    public Integer getTotalPurchasePriceForStock(Payment payment, Map<String, Double> goodsMap) throws BaseException {

        int totalPurchasePrice = 0;
        for (String key : goodsMap.keySet()) {

            // 결제 하려는 굿즈 수량 확인
            int purchaseGoodsAmount = goodsMap.get(key).intValue();

            // 굿즈 조회(goodsIdx)
            Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseMessage.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
            );

            // 재고 굿즈 구매 수량 확인 / 구매한 항목 수가 굿즈의 남은 수량보다 크면 예외
            if (purchaseGoodsAmount > goods.getAmount()) {
                refund(payment);
                throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
            }

            // 총 구매 가격
            totalPurchasePrice += purchaseGoodsAmount * goods.getPrice();
        }

        return totalPurchasePrice;
    }

    // 총 구매 금액과 IamPort 결제 금액 비교
    public void comparePrice(Payment payment, Integer totalPurchasePrice) throws BaseException {
        // IamPort 결제 금액과 총 구매 금액 비교 불일치 시 환불
        Integer payedPrice = payment.getAmount().intValue();
        if (!payedPrice.equals(totalPurchasePrice)) {
            refund(payment);
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
        }
    }

    // 포인트 적립(예약용)
    public Integer addPointForReserve(Customer customer, Integer totalPurchasePrice) {
        // 포인트 적립 계산(총 구매 금액의 5%)
        int addPoint = 0;
        addPoint += (int) Math.round(totalPurchasePrice * 0.05);
        customer.setPoint(customer.getPoint() + addPoint);
        // 고객 포인트 갱신 저장
        customerRepository.save(customer);
        return addPoint;
    }

    // 포인트 적립(재고용)
    public Integer addPointForStock(Payment payment, Customer customer, Integer totalPurchasePrice) throws BaseException {
        int payedPrice = payment.getAmount().intValue();
        // 포인트 적립 계산(총 구매 금액의 5%)
        int usedPoint = (totalPurchasePrice + 2500) - payedPrice;
        // 포인트 유효성 검사 (3000포인트 이상부터 사용 가능)
        if (usedPoint != 0 && (customer.getPoint() < 3000 || customer.getPoint() < usedPoint || totalPurchasePrice < usedPoint)) {
            refund(payment);
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_POINT_EXCEEDED);
        }
        // 배송비 적용 및 최종 구매 금액 조정 및 갱신
        customer.setPoint(customer.getPoint() - usedPoint);
        customerRepository.save(customer);
        return usedPoint;
    }


    @Transactional
    public Goods adjustAmountWithLock(Long key, Integer purchaseGoodsAmount) throws BaseException {
        Goods goods = goodsRepository.findByGoodsIdx(key).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
        );
        // 수량 감소
        goods.setAmount(goods.getAmount() - purchaseGoodsAmount);
        goodsRepository.save(goods);
        return goods;
    }

    // 굿즈 차감 및 주문 상세 정보 저장
    public void adjustGoodsAmount(Orders orders, Map<String, Double> goodsMap) throws BaseException {
        for (String key : goodsMap.keySet()) {
            // 구매 수량
            Integer purchaseGoodsAmount = goodsMap.get(key).intValue();

            // 굿즈 조회(goodsIdx)
            Goods goods = adjustAmountWithLock(Long.parseLong(key), purchaseGoodsAmount);

            // 주문 상세 정보 생성 => 밖으로 빼야됨
            OrdersDetail ordersDetail = OrdersDto.CreateOrdersDetailReq.toEntity(orders, goods, goods.getPrice() * purchaseGoodsAmount);
            ordersDetailRepository.save(ordersDetail);
        }
    }

    // 환불 처리
    public void refund(Payment payment) throws BaseException {
        try {
            CancelData cancelData = new CancelData(payment.getImpUid(), true, payment.getAmount());
            iamportClient.cancelPaymentByImpUid(cancelData);
        } catch (IamportResponseException | IOException e) {
            throw new BaseException(BaseMessage.IAMPORT_ERROR);
        }
    }

    // 굿즈 수량 복구
    public void recoverGoodsAmount(Map<String, Double> goodsMap) throws BaseException {
        for (String key : goodsMap.keySet()) {
            Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
            Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_NOT_FOUND_GOODS)
            );
            goods.setAmount(goods.getAmount() + purchaseGoodsAmount);
            goodsRepository.save(goods);
        }
    }

    // 포인트 복구
    public void recoverAddedPoint(Customer customer, Orders orders) {
        customer.setPoint(customer.getPoint() + orders.getUsedPoint());
        customerRepository.save(customer);
    }

    // 결제 검증(예약용)
    @Transactional
    public OrdersDto.VerifyOrdersRes verifyOrdersReserve(CustomUserDetails customUserDetails, String impUid, Long storeIdx, Long reserveIdx) throws BaseException, IamportResponseException, IOException {
        Payment payment = null;
        try {
            // 결제 정보 확인
            payment = checkPaymentData(impUid);

            // 고객 회원 시스템 역할(ROLE)확인 및 조회
            Customer customer = checkCustomer(customUserDetails);

            // 결제 굿즈 정보 확인
            Map<String, Double> goodsMap = checkGoodsData(payment);

            // 총 구매 금액 계산 및 IamPort 결제 금액과 비교
            int totalPurchasePrice = getTotalPurchasePriceForReserve(payment, goodsMap);

            // 포인트 적립, 배송비 적용 x 포인트 사용 x , 최종 구매 금액 조정 및 갱신
            int addPoint = addPointForReserve(customer, totalPurchasePrice);

            // IamPort 결제 금액과 총 구매 금액 비교 불일치 시 환불
            comparePrice(payment, totalPurchasePrice);

            // 예약 굿즈 주문 생성: 배송비 0, 포인트 사용 x, 상태 RESERVE_READY
            Orders orders = OrdersDto.CreateReserveOrdersReq.toEntity(impUid, totalPurchasePrice, customer, storeIdx);
            ordersRepository.save(orders);

            // 굿즈 재고 차감 및 주문 상세 정보 저장
            adjustGoodsAmount(orders, goodsMap);

            // 예약 정보 갱신
            reserveRepository.decreaseTotalPeople(reserveIdx, 1);

            // DTO 반환
            return OrdersDto.VerifyOrdersRes.builder().ordersIdx(orders.getIdx()).build();
        } catch (Exception e) {
            refund(payment);
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL);
        }
    }

    // 결제 검증(재고용)
    @Transactional
    public OrdersDto.VerifyOrdersRes verifyOrdersStock(CustomUserDetails customUserDetails, String impUid, Long storeIdx) throws BaseException, IamportResponseException, IOException {

        // 고객 회원 시스템 역할(ROLE)확인 및 조회
        Customer customer = checkCustomer(customUserDetails);

        // 결제 정보 확인
        Payment payment = checkPaymentData(impUid);

        // 결제 굿즈 정보 확인
        Map<String, Double> goodsMap = checkGoodsData(payment);

        // 총 구매 금액 계산 및 IamPort 결제 금액과 비교

        int totalPurchasePrice = getTotalPurchasePriceForStock(payment, goodsMap);

        // 포인트 적립, 배송비 적용 2500, 포인트 사용 0 , 최종 구매 금액 조정 및 갱신
        int usedPoint = addPointForStock(payment, customer, totalPurchasePrice);

        // 총 구매 금액 재 계산
        totalPurchasePrice += 2500 - usedPoint;

        // IamPort 결제 금액과 총 구매 금액 비교 불일치 시 환불
        comparePrice(payment, totalPurchasePrice);

        // 주문 객체 생성 및 저장 재고 굿즈 구매 배송비 2500, 상태 STOCK_READY
        Orders orders = OrdersDto.CreateStockOrdersReq.toEntity(impUid, totalPurchasePrice, usedPoint, customer, storeIdx);
        ordersRepository.save(orders);

        // 굿즈 재고 차감 및 주문 상세 정보 저장
        adjustGoodsAmount(orders, goodsMap);

        // DTO 반환
        return OrdersDto.VerifyOrdersRes.builder().ordersIdx(orders.getIdx()).build();

    }

    // 결제 취소
    @Transactional
    public void cancelOrders(CustomUserDetails customUserDetails, Long ordersIdx) throws BaseException, IamportResponseException, IOException {

        // 고객 회원 시스템 역할(ROLE)확인 및 조회
        Customer customer = checkCustomer(customUserDetails);

        // 주문 정보 조회
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_NOT_FOUND)
        );

        // 배송 상태 확인 (배송 중인 경우 취소 불가)
        if(Objects.equals(orders.getStatus(), BaseStatus.STOCK_DELIVERY) || Objects.equals(orders.getStatus(), BaseStatus.RESERVE_DELIVERY)) throw new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_IS_DELIVERY);

        // 이미 취소된 주문인지 확인
        if(Objects.equals(orders.getStatus(), BaseStatus.STOCK_CANCEL) || Objects.equals(orders.getStatus(), BaseStatus.RESERVE_CANCEL)) throw new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_ALREADY_CANCEL);

        // 결제 정보 확인
        Payment payment = checkPaymentData(orders.getImpUid());

        // 결제 굿즈 정보 확인
        Map<String, Double> goodsMap = checkGoodsData(payment);

        // 굿즈 수량 복구
        recoverGoodsAmount(goodsMap);

        // 포인트 복구
        recoverAddedPoint(customer, orders);

        // 주문 상태 변경(STOCK_CANCEL, RSERVE_CANCEL)
        BaseStatus orderStatus = switch (orders.getStatus()) {
            case STOCK_READY, STOCK_COMPLETE -> BaseStatus.STOCK_CANCEL;
            case RESERVE_READY, RESERVE_COMPLETE -> BaseStatus.RESERVE_CANCEL;
            default -> throw new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_IS_DELIVERY);
        };
        orders.setStatus(orderStatus);
        ordersRepository.save(orders);

        // 환불 처리 진행
        refund(payment);

    }

    // 주문 확정
    @Transactional
    public void completeOrders(CustomUserDetails customUserDetails, Long storeIdx, Long ordersIdx) throws BaseException {
        if (Objects.equals(customUserDetails.getRole(), "ROLE_COMPANY")) {

            // 기업 회원인 경우(배송 완료 처리)
            Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                    () -> new BaseException(BaseMessage.STORE_SEARCH_FAIL_NOT_FOUND)
            );

            // 기업 회원 확인
            if (!store.getCompanyEmail().equals(customUserDetails.getEmail())) throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_INVALID_MEMBER);

            // 주문 조회(ordersIdx, storeIdx)
            Orders orders = ordersRepository.findByOrdersIdxAndStoreIdx(ordersIdx, storeIdx).orElseThrow(
                    () -> new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_NOT_FOUND)
            );

            // 주문 상태 변경 STOCK_DELIVERY, RESERVE_DELIVERY
            BaseStatus orderStatus = switch (orders.getStatus()) {
                case STOCK_READY, STOCK_COMPLETE -> BaseStatus.STOCK_DELIVERY;
                case RESERVE_READY, RESERVE_COMPLETE -> BaseStatus.RESERVE_DELIVERY;
                case RESERVE_DELIVERY, STOCK_DELIVERY -> throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_IS_DELIVERY);
                default -> throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_IS_CANCEL);
            };
            orders.setStatus(orderStatus);
            ordersRepository.save(orders);

        } else {

            // 고객회원일 경우 (구매 확정 처리)
            Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_NOT_FOUND)
            );

            // 주문 소유 확인
            if (!orders.getCustomer().getIdx().equals(customUserDetails.getIdx())) throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_INVALID_MEMBER);

            // 주문 상태 변경(STOCK_COMPLETE, RESERVE_COMPLETE)
            BaseStatus orderStatus = switch (orders.getStatus()) {
                case STOCK_READY -> BaseStatus.STOCK_COMPLETE;
                case RESERVE_READY -> BaseStatus.RESERVE_COMPLETE;
                case RESERVE_DELIVERY, STOCK_DELIVERY -> throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_IS_DELIVERY);
                default -> throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_IS_CANCEL);
            };
            orders.setStatus(orderStatus);
            ordersRepository.save(orders);
        }
    }

    // 고객 주문 상세 조회
    public OrdersDto.SearchOrdersRes searchOrdersAsCustomer(CustomUserDetails customUserDetails, Long ordersIdx) throws BaseException {

        // 주문 조회(ordersIdx)
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_FAIL_NOT_FOUND)
        );

        // Orders DTO 반환
        return orders.toDto();

    }

    // 고객 주문 목록 조회
    public Page<OrdersDto.SearchOrdersRes> searchAllOrdersAsCustomer(CustomUserDetails customUserDetails, int page, int size) throws BaseException {

        // 주문 조회(customerIdx)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = ordersRepository.findAllByCustomerIdx(customUserDetails.getIdx(), pageable).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        // Orders DTO 반환
        return Orders.toDtoPage(ordersPage);

    }

    // 기업 고객 주문 상세 조회
    public OrdersDto.SearchOrdersRes searchOrdersAsCompany(CustomUserDetails customUserDetails, Long storeIdx, Long ordersIdx) throws BaseException {

        // 스토어(storeIdx) 조회
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_FAIL_NOT_FOUND_STORE)
        );

        // 스토어 소유 확인
        if(!(store.getCompanyEmail().equals(customUserDetails.getEmail()))) throw new BaseException(BaseMessage.ORDERS_SEARCH_FAIL_INVALID_MEMBER);

        Orders orders = ordersRepository.findByOrdersIdxAndStoreIdx(ordersIdx, storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_FAIL_NOT_FOUND)
        );

        return orders.toDto();
    }

    // 기업 고객 주문 목록 조회
    public Page<OrdersDto.SearchOrdersRes> searchAllOrdersAsCompany(CustomUserDetails customUserDetails, Long storeIdx, int page, int size) throws BaseException {

        // 스토어(storeIdx) 조회
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND_STORE)
        );

        // 스토어 소유 확인
        if(!(store.getCompanyEmail().equals(customUserDetails.getEmail()))) throw new BaseException(BaseMessage.ORDERS_SEARCH_ALL_FAIL_INVALID_MEMBER);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = ordersRepository.findAllByStoreIdx(storeIdx, pageable).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        // Orders DTO 반환
        return Orders.toDtoPage(ordersPage);

    }

}

