package com.fiiiiive.zippop.orders.service;


import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.orders.event.RefundEvent;
import com.fiiiiive.zippop.orders.model.dto.*;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import com.fiiiiive.zippop.global.enums.RoleType;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.orders.model.entity.OrdersDetail;
import com.fiiiiive.zippop.orders.policy.OrdersPolicy;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
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
    private final PopupRepository popupRepository;
    private final ReserveRepository reserveRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OrdersPolicy ordersPolicy;

    // 결제 검증(예약용)
    @Transactional
    public CreateOrdersRes createReserveOrders(CustomUserDetails user, CreateOrdersReq req) throws ServiceException {
        Payment payment = null;
        try {
            // 주문자 역할 검증
            ordersPolicy.validateOrderRole(user);

            // 결제 정보 확인
            payment = iamportClient.paymentByImpUid(req.getImpUid()).getResponse();
            if (payment == null) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
            }

            // 고객 회원 조회(idx)
            Customer customer = customerRepository.findByCustomerIdx(user.getIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
            );

            // 결제 굿즈 정보 확인 customData Map 형태로 변환
            Map<String, Double> goodsMap = new Gson().fromJson(payment.getCustomData(), new TypeToken<Map<String, Double>>() {}.getType());

            // 총 구매 금액 계산 및 IamPort 결제 금액과 비교
            int totalPurchasePrice = 0;
            for (String key : goodsMap.keySet()) {

                // 결제 하려는 굿즈 수량 확인
                int purchaseGoodsAmount = goodsMap.get(key).intValue();

                // 굿즈 조회(goodsIdx)
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                        () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
                );

                // 예약 굿즈 구매 수량 확인 / 구매 항목개수가 1개 이상이면 결제 실패 후 환불
                if (purchaseGoodsAmount != 1) {
                    eventPublisher.publishEvent(new RefundEvent(payment));
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }

                // 총 구매 가격
                totalPurchasePrice += purchaseGoodsAmount * goods.getPrice();
            }

            // 포인트 적립((총 구매 금액의 5%)), 배송비 적용 x 포인트 사용 x , 최종 구매 금액 조정 및 갱신
            customer.updatePoint(customer.getPoint() + (int) Math.round(totalPurchasePrice * 0.05));

            // IamPort 결제 금액과 총 구매 금액 비교 불일치 시 환불
            Integer payedPrice = payment.getAmount().intValue();
            if (!payedPrice.equals(totalPurchasePrice)) {
                eventPublisher.publishEvent(new RefundEvent(payment));
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
            }

            // 예약 굿즈 주문 생성: 배송비 0, 포인트 사용 x, 상태 RESERVE_READY
            Popup popup = popupRepository.findByPopupIdx(req.getPopupIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.RESERVE_REGISTER_FAIL_NOT_FOUND_STORE)
            );

            // 예약 굿즈 주문 저장
            Orders orders = Orders.createReserveOrders(
                    req.getImpUid(),
                    totalPurchasePrice,
                    customer,
                    popup);
            ordersRepository.save(orders);

            // 굿즈 재고 차감 및 주문 상세 정보 저장
            for (String key : goodsMap.keySet()) {
                // 구매 수량
                Integer purchaseGoodsAmount = goodsMap.get(key).intValue();

                // 굿즈 조회(goodsIdx)
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                        () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
                );

                // 재고 굿즈 구매 수량 확인 / 구매한 항목 수가 굿즈의 남은 수량보다 크면 예외
                if (goods.getAmount() < purchaseGoodsAmount){
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }

                // 수량 감소
                goods.updateAmount(goods.getAmount() - purchaseGoodsAmount);

                // 주문 상세 정보 생성
                OrdersDetail ordersDetail = OrdersDetail.create(
                        orders,
                        goods,
                        goods.getPrice() * purchaseGoodsAmount
                );
                ordersDetailRepository.save(ordersDetail);
            }

            // 예약 정보 갱신
            reserveRepository.decreaseTotalPeople(req.getReserveIdx(), 1);

            // DTO 반환
            return CreateOrdersRes.builder().ordersIdx(orders.getIdx()).build();

        } catch (IamportResponseException | IOException exception) {
            if (payment != null) {
                eventPublisher.publishEvent(new RefundEvent(payment));
            }
            throw new ServerException(ServerErrorCode.PAYMENT_PROVIDER_ERROR, exception);
        } catch (ServiceException exception) {
            if (payment != null) {
                eventPublisher.publishEvent(new RefundEvent(payment));
            }
            throw exception;
        } catch (Exception e) {
            if (payment != null) {
                eventPublisher.publishEvent(new RefundEvent(payment));
            }
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }
        
    }

    // 결제 검증(재고용)
    @Transactional
    public CreateOrdersRes createStockOrders(CustomUserDetails user, CreateOrdersReq req) throws ServiceException {
        Payment payment = null;
        try {
            // 주문자 역할 검증
            ordersPolicy.validateOrderRole(user);

            // 고객 회원 조회(idx)
            Customer customer = customerRepository.findByCustomerIdx(user.getIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
            );

            // 결제 정보 확인
            payment = iamportClient.paymentByImpUid(req.getImpUid()).getResponse();
            if (payment == null) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
            }

            // 결제 굿즈 정보 확인 customData Map 형태로 변환
            Map<String, Double> goodsMap = new Gson().fromJson(payment.getCustomData(), new TypeToken<Map<String, Double>>() {}.getType());

            // 총 구매 금액 계산 및 IamPort 결제 금액과 비교
            int totalPurchasePrice = 0;
            for (String key : goodsMap.keySet()) {

                // 결제 하려는 굿즈 수량 확인
                int purchaseGoodsAmount = goodsMap.get(key).intValue();

                // 굿즈 조회(goodsIdx)
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                        () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
                );

                // 재고 굿즈 구매 수량 확인 / 구매한 항목 수가 굿즈의 남은 수량보다 크면 예외
                if (purchaseGoodsAmount > goods.getAmount()) {
                    eventPublisher.publishEvent(new RefundEvent(payment));
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }

                // 총 구매 가격
                totalPurchasePrice += purchaseGoodsAmount * goods.getPrice();
            }

            // 포인트 적립, 배송비 적용 2500, 포인트 사용 0 , 최종 구매 금액 조정 및 갱신
            int usedPoint;
            Integer payedPrice = payment.getAmount().intValue();

            // 포인트 적립 계산(총 구매 금액의 5%)
            usedPoint = (totalPurchasePrice + 2500) - payedPrice;

            // 포인트 유효성 검사 (3000포인트 이상부터 사용 가능)
            if (usedPoint != 0 && (customer.getPoint() < 3000 || customer.getPoint() < usedPoint || totalPurchasePrice < usedPoint)) {
                eventPublisher.publishEvent(new RefundEvent(payment));
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_POINT_EXCEEDED);
            }

            // 배송비 적용 및 최종 구매 금액 조정 및 갱신
            customer.updatePoint(customer.getPoint() - usedPoint);

            // 총 구매 금액 재 계산
            totalPurchasePrice += 2500 - usedPoint;

            // IamPort 결제 금액과 총 구매 금액 비교 불일치 시 환불
            if (!payedPrice.equals(totalPurchasePrice)) {
                eventPublisher.publishEvent(new RefundEvent(payment));
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
            }

            // 주문 객체 생성 및 저장 재고 굿즈 구매 배송비 2500, 상태 STOCK_READY
            Popup popup = popupRepository.findByPopupIdx(req.getPopupIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.RESERVE_REGISTER_FAIL_NOT_FOUND_STORE)
            );

            Orders orders = Orders.createStockOrders(
                    req.getImpUid(),
                    totalPurchasePrice,
                    usedPoint, customer, popup);
            ordersRepository.save(orders);

            // 굿즈 재고 차감 및 주문 상세 정보 저장
            for (String key : goodsMap.keySet()) {
                // 구매 수량
                Integer purchaseGoodsAmount = goodsMap.get(key).intValue();

                // 굿즈 조회(goodsIdx)
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                        () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
                );

                // 재고 굿즈 구매 수량 확인 / 구매한 항목 수가 굿즈의 남은 수량보다 크면 예외
                if (goods.getAmount() < purchaseGoodsAmount){
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }

                // 수량 감소
                goods.updateAmount(goods.getAmount() - purchaseGoodsAmount);
                goodsRepository.save(goods);

                // 주문 상세 정보 생성
                OrdersDetail ordersDetail = OrdersDetail.create(orders, goods, goods.getPrice() * purchaseGoodsAmount);
                ordersDetailRepository.save(ordersDetail);
            }

            return CreateOrdersRes.builder().ordersIdx(orders.getIdx()).build();

        } catch (IamportResponseException | IOException exception) {
            if (payment != null) {
                eventPublisher.publishEvent(new RefundEvent(payment));
            }
            throw new ServerException(ServerErrorCode.PAYMENT_PROVIDER_ERROR, exception);
        } catch (ServiceException exception) {
            if (payment != null) {
                eventPublisher.publishEvent(new RefundEvent(payment));
            }
            throw exception;
        } catch (Exception e) {
            if (payment != null) {
                eventPublisher.publishEvent(new RefundEvent(payment));
            }
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }

    }

    // 주문 확정
    @Transactional
    public UpdateOrdersRes updateOrders(CustomUserDetails user, Long orderIdx, UpdateOrdersReq req) throws ServiceException {
        if (Objects.equals(user.getRole(), RoleType.ROLE_COMPANY.name())) {

            // 기업 회원인 경우(배송 완료 처리)
            Popup popup = popupRepository.findByPopupIdx(req.getPopupIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.STORE_SEARCH_FAIL_NOT_FOUND)
            );

            // 기업 회원 확인
            if (!popup.getCompanyEmail().equals(user.getEmail())) {
                throw new ServiceException(ServiceErrorCode.ORDERS_COMPLETE_FAIL_INVALID_MEMBER);
            }

            // 주문 조회(ordersIdx, popupIdx)
            Orders orders = ordersRepository.findByOrdersIdxAndPopupIdx(orderIdx, req.getPopupIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_COMPLETE_FAIL_NOT_FOUND)
            );

            // 주문 상태 변경 STOCK_DELIVERY, RESERVE_DELIVERY
            orders.changeToDelivery();

            return UpdateOrdersRes.builder().ordersIdx(orders.getIdx()).build();

        } else {

            // 고객회원일 경우 (구매 확정 처리)
            Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(orderIdx, user.getIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_COMPLETE_FAIL_NOT_FOUND)
            );

            // 주문 소유 확인
            if (!orders.getCustomer().getIdx().equals(user.getIdx())) throw new ServiceException(ServiceErrorCode.ORDERS_COMPLETE_FAIL_INVALID_MEMBER);

            // 주문 상태 변경(STOCK_COMPLETE, RESERVE_COMPLETE)
            orders.changeToComplete();

            return UpdateOrdersRes.builder().ordersIdx(orders.getIdx()).build();
        }
    }


    // 결제 취소
    @Transactional
    public UpdateOrdersRes cancelOrders(CustomUserDetails user, Long ordersIdx) throws ServiceException {

        // 주문자 역할 검증
        ordersPolicy.validateOrderRole(user);

        // 고객 회원 조회(idx)
        Customer customer = customerRepository.findByCustomerIdx(user.getIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
        );

        // 주문 정보 조회
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, user.getIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_CANCEL_FAIL_NOT_FOUND)
        );

        // 배송 상태 확인 (배송 중인 경우 취소 불가)
        orders.validateOrders();

        // 결제 정보 확인
        Payment payment;
        try {
            payment = iamportClient.paymentByImpUid(orders.getImpUid()).getResponse();
        } catch (IamportResponseException | IOException exception) {
            throw new ServerException(ServerErrorCode.PAYMENT_PROVIDER_ERROR, exception);
        }
        if (payment == null) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }

        // 결제 굿즈 정보 확인 customData Map 형태로 변환
        Map<String, Double> goodsMap = new Gson().fromJson(payment.getCustomData(), new TypeToken<Map<String, Double>>() {}.getType());
        // 굿즈 수량 복구
        for (String key : goodsMap.keySet()) {
            Integer purchaseGoodsAmount = goodsMap.get(key).intValue();
            Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_CANCEL_FAIL_NOT_FOUND_GOODS)
            );
            goods.updateAmount(goods.getAmount() + purchaseGoodsAmount);
        }
        // 포인트 복구
        customer.updatePoint(customer.getPoint() + orders.getUsedPoint());

        // 주문 상태 변경(STOCK_CANCEL, RSERVE_CANCEL)
        orders.updateOrderStatus();

        // 환불 처리 진행
        eventPublisher.publishEvent(new RefundEvent(payment));

        return UpdateOrdersRes.builder().ordersIdx(orders.getIdx()).build();
    }

    // 고객 주문 상세 조회
    public GetOrdersRes getOrder(CustomUserDetails user, Long ordersIdx) throws ServiceException {

        // 주문 조회(ordersIdx)
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, user.getIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_SEARCH_FAIL_NOT_FOUND)
        );

        // Orders DTO 반환
        return orders.toDto();

    }

    // 고객 주문 목록 조회
    public Page<GetOrdersRes> getOrders(CustomUserDetails user, int page, int size) throws ServiceException {

        // 주문 조회(customerIdx)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = ordersRepository.findAllByCustomerIdx(user.getIdx(), pageable).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return Orders.toDtoPage(ordersPage);

    }

}

