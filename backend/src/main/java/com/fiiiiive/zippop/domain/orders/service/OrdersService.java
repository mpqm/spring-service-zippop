package com.fiiiiive.zippop.domain.orders.service;


import com.fiiiiive.zippop.domain.auth.entity.Customer;
import com.fiiiiive.zippop.domain.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.domain.orders.dto.RefundEvent;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.base.BaseStatus;
import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.domain.goods.entity.Goods;
import com.fiiiiive.zippop.domain.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.domain.orders.dto.OrdersDto;
import com.fiiiiive.zippop.domain.orders.entity.Orders;
import com.fiiiiive.zippop.domain.orders.entity.OrdersDetail;
import com.fiiiiive.zippop.domain.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.domain.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.domain.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.domain.store.entity.Store;
import com.fiiiiive.zippop.domain.store.repository.StoreRepository;
import com.google.gson.Gson;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
    private final ApplicationEventPublisher eventPublisher;

    // 결제 검증(예약용)
    @Transactional
    public OrdersDto.VerifyOrdersRes verifyOrdersReserve(CustomUserDetails customUserDetails, String impUid, Long storeIdx, Long reserveIdx) throws BaseException, IamportResponseException, IOException {

        Payment payment = null;
        try {
            // 결제 정보 확인
            payment = iamportClient.paymentByImpUid(impUid).getResponse();
            if (payment == null) {
                throw new BaseException(BaseMessage.ORDERS_PAY_FAIL);
            }

            // 고객 회원 시스템 역할(ROLE)확인
            if (!customUserDetails.getRole().equals(BaseStatus.ROLE_CUSTOMER.name())) {
                throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_INVALID_ROLE);
            }

            // 고객 회원 조회(idx)
            Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                    () -> new BaseException(BaseMessage.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
            );

            // 결제 굿즈 정보 확인 customData Map 형태로 변환
            Map<String, Double> goodsMap = new Gson().fromJson(payment.getCustomData(), Map.class);

            // 총 구매 금액 계산 및 IamPort 결제 금액과 비교
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
                    eventPublisher.publishEvent(new RefundEvent(payment));
                    throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }

                // 총 구매 가격
                totalPurchasePrice += purchaseGoodsAmount * goods.getPrice();
            }

            // 포인트 적립((총 구매 금액의 5%)), 배송비 적용 x 포인트 사용 x , 최종 구매 금액 조정 및 갱신
            int addPoint = 0;
            addPoint += (int) Math.round(totalPurchasePrice * 0.05);
            customer.setPoint(customer.getPoint() + addPoint);
            customerRepository.save(customer);

            // IamPort 결제 금액과 총 구매 금액 비교 불일치 시 환불
            Integer payedPrice = payment.getAmount().intValue();
            if (!payedPrice.equals(totalPurchasePrice)) {
                eventPublisher.publishEvent(new RefundEvent(payment));
                throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
            }

            // 예약 굿즈 주문 생성: 배송비 0, 포인트 사용 x, 상태 RESERVE_READY
            Orders orders = OrdersDto.CreateReserveOrdersReq.toEntity(impUid, totalPurchasePrice, customer, storeIdx);
            ordersRepository.save(orders);

            // 굿즈 재고 차감 및 주문 상세 정보 저장
            for (String key : goodsMap.keySet()) {
                // 구매 수량
                Integer purchaseGoodsAmount = goodsMap.get(key).intValue();

                // 굿즈 조회(goodsIdx)
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                        () -> new BaseException(BaseMessage.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
                );

                // 재고 굿즈 구매 수량 확인 / 구매한 항목 수가 굿즈의 남은 수량보다 크면 예외
                if (goods.getAmount() < purchaseGoodsAmount){
                    throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }

                // 수량 감소
                goods.setAmount(goods.getAmount() - purchaseGoodsAmount);
                goodsRepository.save(goods);

                // 주문 상세 정보 생성
                OrdersDetail ordersDetail = OrdersDto.CreateOrdersDetailReq.toEntity(orders, goods, goods.getPrice() * purchaseGoodsAmount);
                ordersDetailRepository.save(ordersDetail);
            }

            // 예약 정보 갱신
            reserveRepository.decreaseTotalPeople(reserveIdx, 1);

            // DTO 반환
            return OrdersDto.VerifyOrdersRes.builder().ordersIdx(orders.getIdx()).build();
        } catch (Exception e) {
            if (payment != null) {
                eventPublisher.publishEvent(new RefundEvent(payment));
            }
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL);
        }
    }

    // 결제 검증(재고용)
    @Transactional
    public OrdersDto.VerifyOrdersRes verifyOrdersStock(CustomUserDetails customUserDetails, String impUid, Long storeIdx) throws BaseException, IamportResponseException, IOException {

        // 고객 회원 시스템 역할(ROLE)확인
        if (!customUserDetails.getRole().equals(BaseStatus.ROLE_CUSTOMER.name())) {
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_INVALID_ROLE);
        }

        // 고객 회원 조회(idx)
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
        );

        // 결제 정보 확인
        Payment payment = iamportClient.paymentByImpUid(impUid).getResponse();
        if (payment == null) {
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL);
        }

        // 결제 굿즈 정보 확인 customData Map 형태로 변환
        Map<String, Double> goodsMap = new Gson().fromJson(payment.getCustomData(), Map.class);

        // 총 구매 금액 계산 및 IamPort 결제 금액과 비교
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
                eventPublisher.publishEvent(new RefundEvent(payment));
                throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
            }

            // 총 구매 가격
            totalPurchasePrice += purchaseGoodsAmount * goods.getPrice();
        }

        // 포인트 적립, 배송비 적용 2500, 포인트 사용 0 , 최종 구매 금액 조정 및 갱신
        int usedPoint = 0;
        Integer payedPrice = payment.getAmount().intValue();
        // 포인트 적립 계산(총 구매 금액의 5%)
        usedPoint = (totalPurchasePrice + 2500) - payedPrice;
        // 포인트 유효성 검사 (3000포인트 이상부터 사용 가능)
        if (usedPoint != 0 && (customer.getPoint() < 3000 || customer.getPoint() < usedPoint || totalPurchasePrice < usedPoint)) {
            eventPublisher.publishEvent(new RefundEvent(payment));
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_POINT_EXCEEDED);
        }
        // 배송비 적용 및 최종 구매 금액 조정 및 갱신
        customer.setPoint(customer.getPoint() - usedPoint);
        customerRepository.save(customer);


        // 총 구매 금액 재 계산
        totalPurchasePrice += 2500 - usedPoint;

        // IamPort 결제 금액과 총 구매 금액 비교 불일치 시 환불
        if (!payedPrice.equals(totalPurchasePrice)) {
            eventPublisher.publishEvent(new RefundEvent(payment));
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
        }

        // 주문 객체 생성 및 저장 재고 굿즈 구매 배송비 2500, 상태 STOCK_READY
        Orders orders = OrdersDto.CreateStockOrdersReq.toEntity(impUid, totalPurchasePrice, usedPoint, customer, storeIdx);
        ordersRepository.save(orders);

        // 굿즈 재고 차감 및 주문 상세 정보 저장
        for (String key : goodsMap.keySet()) {
            // 구매 수량
            Integer purchaseGoodsAmount = goodsMap.get(key).intValue();

            // 굿즈 조회(goodsIdx)
            Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseMessage.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
            );

            // 재고 굿즈 구매 수량 확인 / 구매한 항목 수가 굿즈의 남은 수량보다 크면 예외
            if (goods.getAmount() < purchaseGoodsAmount){
                throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
            }

            // 수량 감소
            goods.setAmount(goods.getAmount() - purchaseGoodsAmount);
            goodsRepository.save(goods);

            // 주문 상세 정보 생성 => 밖으로 빼야됨
            OrdersDetail ordersDetail = OrdersDto.CreateOrdersDetailReq.toEntity(orders, goods, goods.getPrice() * purchaseGoodsAmount);
            ordersDetailRepository.save(ordersDetail);
        }

        return OrdersDto.VerifyOrdersRes.builder().ordersIdx(orders.getIdx()).build();

    }

    // 결제 취소
    @Transactional
    public void cancelOrders(CustomUserDetails customUserDetails, Long ordersIdx) throws BaseException, IamportResponseException, IOException {

        // 고객 회원 시스템 역할(ROLE)확인
        if (!customUserDetails.getRole().equals(BaseStatus.ROLE_CUSTOMER.name())) {
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL_INVALID_ROLE);
        }

        // 고객 회원 조회(idx)
        Customer customer = customerRepository.findByCustomerIdx(customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
        );

        // 주문 정보 조회
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, customUserDetails.getIdx()).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_NOT_FOUND)
        );

        // 배송 상태 확인 (배송 중인 경우 취소 불가)
        if(Objects.equals(orders.getStatus(), BaseStatus.STOCK_DELIVERY) || Objects.equals(orders.getStatus(), BaseStatus.RESERVE_DELIVERY)) {
            throw new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_IS_DELIVERY);
        }

        // 이미 취소된 주문인지 확인
        if(Objects.equals(orders.getStatus(), BaseStatus.STOCK_CANCEL) || Objects.equals(orders.getStatus(), BaseStatus.RESERVE_CANCEL)) {
            throw new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_ALREADY_CANCEL);
        }

        // 결제 정보 확인
        Payment payment = iamportClient.paymentByImpUid(orders.getImpUid()).getResponse();
        if (payment == null) {
            throw new BaseException(BaseMessage.ORDERS_PAY_FAIL);
        }

        // 결제 굿즈 정보 확인 customData Map 형태로 변환
        Map<String, Double> goodsMap = new Gson().fromJson(payment.getCustomData(), Map.class);

        // 굿즈 수량 복구
        for (String key : goodsMap.keySet()) {
            Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
            Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_NOT_FOUND_GOODS)
            );
            goods.setAmount(goods.getAmount() + purchaseGoodsAmount);
            goodsRepository.save(goods);
        }
        // 포인트 복구
        customer.setPoint(customer.getPoint() + orders.getUsedPoint());
        customerRepository.save(customer);

        // 주문 상태 변경(STOCK_CANCEL, RSERVE_CANCEL)
        BaseStatus orderStatus = switch (orders.getStatus()) {
            case STOCK_READY, STOCK_COMPLETE -> BaseStatus.STOCK_CANCEL;
            case RESERVE_READY, RESERVE_COMPLETE -> BaseStatus.RESERVE_CANCEL;
            default -> throw new BaseException(BaseMessage.ORDERS_CANCEL_FAIL_IS_DELIVERY);
        };
        orders.setStatus(orderStatus);
        ordersRepository.save(orders);

        // 환불 처리 진행
        eventPublisher.publishEvent(new RefundEvent(payment));

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
            if (!store.getCompanyEmail().equals(customUserDetails.getEmail())) {
                throw new BaseException(BaseMessage.ORDERS_COMPLETE_FAIL_INVALID_MEMBER);
            }

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

        return Orders.toDtoPage(ordersPage);

    }

    // 기업 고객 주문 상세 조회
    public OrdersDto.SearchOrdersRes searchOrdersAsCompany(CustomUserDetails customUserDetails, Long storeIdx, Long ordersIdx) throws BaseException {

        // 스토어(storeIdx) 조회
        Store store = storeRepository.findByStoreIdx(storeIdx).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_FAIL_NOT_FOUND_STORE)
        );

        // 스토어 소유 확인
        if(!(store.getCompanyEmail().equals(customUserDetails.getEmail()))) {
            throw new BaseException(BaseMessage.ORDERS_SEARCH_FAIL_INVALID_MEMBER);
        }

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
        if(!(store.getCompanyEmail().equals(customUserDetails.getEmail()))) {
            throw new BaseException(BaseMessage.ORDERS_SEARCH_ALL_FAIL_INVALID_MEMBER);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = ordersRepository.findAllByStoreIdx(storeIdx, pageable).orElseThrow(
                () -> new BaseException(BaseMessage.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return Orders.toDtoPage(ordersPage);

    }

}

