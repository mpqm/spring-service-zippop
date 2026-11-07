package com.fiiiiive.zippop.orders.service;


import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.orders.event.RefundEvent;
import com.fiiiiive.zippop.orders.model.dto.*;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.model.entity.PaymentIntent;
import com.fiiiiive.zippop.orders.model.entity.PaymentIntentGoodsSnapshot;
import com.fiiiiive.zippop.orders.model.entity.PaymentIntentStatus;
import com.fiiiiive.zippop.orders.model.entity.PointLedger;
import com.fiiiiive.zippop.orders.model.entity.PointLedgerType;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.base.ServerErrorCode;
import com.fiiiiive.zippop.global.base.ServerException;
import com.fiiiiive.zippop.account.model.enums.RoleType;
import com.fiiiiive.zippop.global.enums.GoodsStatus;
import com.fiiiiive.zippop.global.redis.RedisQueueService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.orders.model.entity.OrdersDetail;
import com.fiiiiive.zippop.orders.policy.OrdersPolicy;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.orders.repository.PaymentIntentRepository;
import com.fiiiiive.zippop.orders.repository.PointLedgerRepository;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
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
import com.fiiiiive.zippop.global.web.RequestValidation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;


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
    private final RedisQueueService redisQueueService;
    private final PaymentIntentRepository paymentIntentRepository;
    private final PointLedgerRepository pointLedgerRepository;
    private final RefundOutboxService refundOutboxService;

    @Transactional
    public CreatePaymentIntentRes issueMerchantUid(CustomUserDetails user, CreatePaymentIntentReq req) {
        ordersPolicy.validateOrderRole(user);
        Customer customer = customerRepository.findForUpdate(user.getCustomerId()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER));

        Popup popup = popupRepository.findByPopupIdx(req.getPopupIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.STORE_SEARCH_FAIL_NOT_FOUND));
        boolean reserveOrder = req.getReserveIdx() != null;
        if (reserveOrder) {
            Reserve reserve = reserveRepository.findById(req.getReserveIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.RESERVE_ENROLL_FAIL_NOT_FOUND));
            if (!reserve.getPopup().getIdx().equals(req.getPopupIdx())
                    || LocalDateTime.now().isBefore(reserve.getStartTime())
                    || !LocalDateTime.now().isBefore(reserve.getEndTime())
                    || reserve.getTotalPeople() <= 0
                    || reserve.getPopup().getStatus() != com.fiiiiive.zippop.global.enums.PopupStatus.POPUP_START
                    || !redisQueueService.hasWorkingAccess(reserve.getWorkingUUID(), user.getEmail())) {
                throw new ServiceException(ServiceErrorCode.RESERVE_ACCESS_FAIL);
            }
            if (req.getUsedPoint() != 0) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_POINT_EXCEEDED);
            }
        }

        TreeMap<Long, Integer> goods = new TreeMap<>();
        for (PaymentIntentGoodsReq item : req.getGoods()) {
            if (goods.putIfAbsent(item.getGoodsIdx(), item.getCount()) != null) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
            }
        }
        int subtotal = 0;
        Map<String, PaymentIntentGoodsSnapshot> snapshots = new java.util.LinkedHashMap<>();
        GoodsStatus expectedStatus = reserveOrder ? GoodsStatus.GOODS_RESERVED : GoodsStatus.GOODS_STOCK;
        for (Map.Entry<Long, Integer> entry : goods.entrySet()) {
            Goods item = goodsRepository.findByGoodsIdx(entry.getKey()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS));
            int quantity = entry.getValue();
            if (!item.getPopup().getIdx().equals(popup.getIdx()) || item.getStatus() != expectedStatus) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
            }
            if ((reserveOrder && quantity != 1) || quantity > item.getAmount()) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
            }
            subtotal = Math.addExact(subtotal, Math.multiplyExact(quantity, item.getPrice()));
            snapshots.put(String.valueOf(entry.getKey()),
                    new PaymentIntentGoodsSnapshot(quantity, item.getPrice(), item.getName()));
        }

        int usedPoint = req.getUsedPoint();
        if (!reserveOrder && usedPoint != 0
                && (customer.getPoint() < 3000 || customer.getPoint() < usedPoint || subtotal < usedPoint)) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_POINT_EXCEEDED);
        }
        int totalPrice = reserveOrder ? subtotal : Math.addExact(subtotal, 2500 - usedPoint);
        if (totalPrice < 0) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
        }

        String merchantUid = "zippop_" + java.util.UUID.randomUUID().toString().replace("-", "");
        paymentIntentRepository.save(PaymentIntent.ready(merchantUid, user.getAccountId(), popup.getIdx(),
                req.getReserveIdx(), new Gson().toJson(snapshots), usedPoint, totalPrice));
        return CreatePaymentIntentRes.builder().merchantUid(merchantUid).amount(totalPrice).build();
    }

    @Transactional
    public CreateOrdersRes reconcilePayment(PaymentWebhookReq webhook) {
        PaymentIntent intent = paymentIntentRepository.findByMerchantUid(webhook.getMerchantUid()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL));
        Customer customer = customerRepository.findByAccountIdx(intent.getAccountIdx()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER));
        CustomUserDetails owner = CustomUserDetails.from(customer.getAccount(), customer.getIdx(), null);
        CreateOrdersReq request = CreateOrdersReq.builder()
                .impUid(webhook.getImpUid()).merchantUid(webhook.getMerchantUid())
                .popupIdx(intent.getPopupIdx()).reserveIdx(intent.getReserveIdx()).build();
        return intent.getReserveIdx() == null
                ? createStockOrders(owner, request)
                : createReserveOrders(owner, request);
    }

    // 결제 검증(예약용)
    @Transactional
    public CreateOrdersRes createReserveOrders(CustomUserDetails user, CreateOrdersReq req) throws ServiceException {
        Payment payment = null;
        PaymentIntent paymentIntent = null;
        try {
            // 주문자 역할 검증
            ordersPolicy.validateOrderRole(user);
            PaymentIntent preparedIntent = loadOwnedPaymentIntentBeforeProvider(user, req);
            if (preparedIntent.getStatus() == PaymentIntentStatus.COMPLETED) {
                return completedOrder(preparedIntent, req);
            }

            // 결제 정보 확인
            payment = iamportClient.paymentByImpUid(req.getImpUid()).getResponse();
            if (payment == null || !"paid".equals(payment.getStatus())) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
            }

            paymentIntent = loadOwnedPaymentIntent(user, req, payment);
            validatePaymentIntent(paymentIntent, req, payment);

            // 결제가 확인된 뒤 예약 유효성을 검사한다. 실패 시 아래 보상 outbox 경로가 결제를 환불한다.
            Reserve reserve = reserveRepository.findById(req.getReserveIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.RESERVE_ENROLL_FAIL_NOT_FOUND)
            );
            if (!reserve.getPopup().getIdx().equals(req.getPopupIdx())
                    || LocalDateTime.now().isBefore(reserve.getStartTime())
                    || !LocalDateTime.now().isBefore(reserve.getEndTime())
                    || reserve.getTotalPeople() <= 0
                    || reserve.getPopup().getStatus() != com.fiiiiive.zippop.global.enums.PopupStatus.POPUP_START
                    || !redisQueueService.hasWorkingAccess(reserve.getWorkingUUID(), user.getEmail())) {
                throw new ServiceException(ServiceErrorCode.RESERVE_ACCESS_FAIL);
            }

            // 고객 회원 조회(idx)
            Customer customer = customerRepository.findForUpdate(user.getCustomerId()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
            );

            // 결제 굿즈 정보 확인 customData Map 형태로 변환
            Map<String, Double> goodsMap = new Gson().fromJson(payment.getCustomData(), new TypeToken<Map<String, Double>>() {}.getType());
            Map<String, PaymentIntentGoodsSnapshot> snapshots = readGoodsSnapshots(paymentIntent);

            if (goodsMap == null || goodsMap.isEmpty()) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
            }

            // 총 구매 금액 계산 및 IamPort 결제 금액과 비교
            int totalPurchasePrice = 0;
            for (String key : goodsMap.keySet()) {

                // 결제 하려는 굿즈 수량 확인
                Double purchaseGoodsValue = goodsMap.get(key);
                if (purchaseGoodsValue == null || !Double.isFinite(purchaseGoodsValue)
                        || purchaseGoodsValue <= 0 || purchaseGoodsValue > Integer.MAX_VALUE || purchaseGoodsValue % 1 != 0) {
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }
                int purchaseGoodsAmount = purchaseGoodsValue.intValue();

                // 굿즈 조회(goodsIdx)
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                        () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
                );
                if (!goods.getPopup().getIdx().equals(req.getPopupIdx()) || goods.getStatus() != GoodsStatus.GOODS_RESERVED) {
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
                }

                // 예약 굿즈 구매 수량 확인 / 구매 항목개수가 1개 이상이면 결제 실패 후 환불
                if (purchaseGoodsAmount != 1) {
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }

                // 총 구매 가격
                PaymentIntentGoodsSnapshot snapshot = snapshots.get(key);
                if (snapshot == null || snapshot.quantity() != purchaseGoodsAmount) {
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
                }
                totalPurchasePrice = Math.addExact(totalPurchasePrice,
                        Math.multiplyExact(purchaseGoodsAmount, snapshot.unitPrice()));
            }

            // 포인트 적립((총 구매 금액의 5%)), 배송비 적용 x 포인트 사용 x , 최종 구매 금액 조정 및 갱신
            int earnedPoint = (int) Math.round(totalPurchasePrice * 0.05);
            customer.updatePoint(customer.getPoint() + earnedPoint);

            // IamPort 결제 금액과 총 구매 금액 비교 불일치 시 환불
            Integer payedPrice = payment.getAmount().intValueExact();
            if (!payedPrice.equals(totalPurchasePrice)) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
            }

            // 예약 굿즈 주문 생성: 배송비 0, 포인트 사용 x, 상태 RESERVE_READY
            Popup popup = reserve.getPopup();

            // 예약 굿즈 주문 저장
            Orders orders = Orders.createReserveOrders(
                    req.getImpUid(),
                    totalPurchasePrice,
                    earnedPoint,
                    customer,
                    popup);
            ordersRepository.save(orders);
            pointLedgerRepository.save(PointLedger.create(customer, orders, PointLedgerType.ORDER_EARN,
                    earnedPoint, customer.getPoint()));

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
                PaymentIntentGoodsSnapshot snapshot = snapshots.get(key);
                OrdersDetail ordersDetail = OrdersDetail.create(
                        orders,
                        goods,
                        purchaseGoodsAmount,
                        snapshot.unitPrice(),
                        snapshot.goodsName()
                );
                ordersDetailRepository.save(ordersDetail);
            }

            // 예약 정보 갱신
            if (reserveRepository.decreaseTotalPeople(req.getReserveIdx(), 1) != 1) {
                throw new ServiceException(ServiceErrorCode.RESERVE_ACCESS_FAIL);
            }

            paymentIntent.complete(req.getImpUid(), orders.getIdx());

            // DTO 반환
            return CreateOrdersRes.builder().ordersIdx(orders.getIdx()).build();

        } catch (IamportResponseException | IOException exception) {
            if (paymentIntent != null && payment != null && "paid".equals(payment.getStatus())) {
                eventPublisher.publishEvent(new RefundEvent(payment, true));
            }
            throw new ServerException(ServerErrorCode.PAYMENT_PROVIDER_ERROR, exception);
        } catch (ServiceException exception) {
            if (paymentIntent != null && payment != null && "paid".equals(payment.getStatus())) {
                eventPublisher.publishEvent(new RefundEvent(payment, true));
            }
            throw exception;
        } catch (Exception e) {
            if (paymentIntent != null && payment != null && "paid".equals(payment.getStatus())) {
                eventPublisher.publishEvent(new RefundEvent(payment, true));
            }
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }
        
    }

    // 결제 검증(재고용)
    @Transactional
    public CreateOrdersRes createStockOrders(CustomUserDetails user, CreateOrdersReq req) throws ServiceException {
        Payment payment = null;
        PaymentIntent paymentIntent = null;
        try {
            // 주문자 역할 검증
            ordersPolicy.validateOrderRole(user);
            PaymentIntent preparedIntent = loadOwnedPaymentIntentBeforeProvider(user, req);
            if (preparedIntent.getStatus() == PaymentIntentStatus.COMPLETED) {
                return completedOrder(preparedIntent, req);
            }

            // 고객 회원 조회(idx)
            Customer customer = customerRepository.findForUpdate(user.getCustomerId()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
            );

            // 결제 정보 확인
            payment = iamportClient.paymentByImpUid(req.getImpUid()).getResponse();
            if (payment == null || !"paid".equals(payment.getStatus())) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
            }

            paymentIntent = loadOwnedPaymentIntent(user, req, payment);
            validatePaymentIntent(paymentIntent, req, payment);

            // 결제 굿즈 정보 확인 customData Map 형태로 변환
            Map<String, Double> goodsMap = new Gson().fromJson(payment.getCustomData(), new TypeToken<Map<String, Double>>() {}.getType());
            Map<String, PaymentIntentGoodsSnapshot> snapshots = readGoodsSnapshots(paymentIntent);

            if (goodsMap == null || goodsMap.isEmpty()) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
            }

            // 총 구매 금액 계산 및 IamPort 결제 금액과 비교
            int totalPurchasePrice = 0;
            for (String key : goodsMap.keySet()) {

                // 결제 하려는 굿즈 수량 확인
                Double purchaseGoodsValue = goodsMap.get(key);
                if (purchaseGoodsValue == null || !Double.isFinite(purchaseGoodsValue)
                        || purchaseGoodsValue <= 0 || purchaseGoodsValue > Integer.MAX_VALUE || purchaseGoodsValue % 1 != 0) {
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }
                int purchaseGoodsAmount = purchaseGoodsValue.intValue();

                // 굿즈 조회(goodsIdx)
                Goods goods = goodsRepository.findByGoodsIdx(Long.parseLong(key)).orElseThrow(
                        () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS)
                );
                if (!goods.getPopup().getIdx().equals(req.getPopupIdx()) || goods.getStatus() != GoodsStatus.GOODS_STOCK) {
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
                }

                // 재고 굿즈 구매 수량 확인 / 구매한 항목 수가 굿즈의 남은 수량보다 크면 예외
                if (purchaseGoodsAmount > goods.getAmount()) {
                    throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_LIMIT_EXCEEDED);
                }

                // 총 구매 가격
                totalPurchasePrice = Math.addExact(totalPurchasePrice, Math.multiplyExact(purchaseGoodsAmount, goods.getPrice()));
            }

            // 포인트 적립, 배송비 적용 2500, 포인트 사용 0 , 최종 구매 금액 조정 및 갱신
            int usedPoint = paymentIntent.getUsedPoint();
            Integer payedPrice = payment.getAmount().intValueExact();

            // 포인트 유효성 검사 (3000포인트 이상부터 사용 가능)
            if (usedPoint != 0 && (customer.getPoint() < 3000 || customer.getPoint() < usedPoint || totalPurchasePrice < usedPoint)) {
                throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_POINT_EXCEEDED);
            }

            // 배송비 적용 및 최종 구매 금액 조정 및 갱신
            customer.updatePoint(customer.getPoint() - usedPoint);

            // 총 구매 금액 재 계산
            totalPurchasePrice += 2500 - usedPoint;

            // IamPort 결제 금액과 총 구매 금액 비교 불일치 시 환불
            if (!payedPrice.equals(totalPurchasePrice)) {
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
            if (usedPoint > 0) {
                pointLedgerRepository.save(PointLedger.create(customer, orders, PointLedgerType.ORDER_USE,
                        -usedPoint, customer.getPoint()));
            }

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
                PaymentIntentGoodsSnapshot snapshot = snapshots.get(key);
                OrdersDetail ordersDetail = OrdersDetail.create(orders, goods, purchaseGoodsAmount,
                        snapshot.unitPrice(), snapshot.goodsName());
                ordersDetailRepository.save(ordersDetail);
            }

            paymentIntent.complete(req.getImpUid(), orders.getIdx());

            return CreateOrdersRes.builder().ordersIdx(orders.getIdx()).build();

        } catch (IamportResponseException | IOException exception) {
            if (paymentIntent != null && payment != null && "paid".equals(payment.getStatus())) {
                eventPublisher.publishEvent(new RefundEvent(payment, true));
            }
            throw new ServerException(ServerErrorCode.PAYMENT_PROVIDER_ERROR, exception);
        } catch (ServiceException exception) {
            if (paymentIntent != null && payment != null && "paid".equals(payment.getStatus())) {
                eventPublisher.publishEvent(new RefundEvent(payment, true));
            }
            throw exception;
        } catch (Exception e) {
            if (paymentIntent != null && payment != null && "paid".equals(payment.getStatus())) {
                eventPublisher.publishEvent(new RefundEvent(payment, true));
            }
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }

    }

    private PaymentIntent loadOwnedPaymentIntentBeforeProvider(CustomUserDetails user, CreateOrdersReq req) {
        PaymentIntent intent = paymentIntentRepository.findByMerchantUid(req.getMerchantUid()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL));
        if (!intent.getAccountIdx().equals(user.getAccountId())) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }
        return intent;
    }

    private CreateOrdersRes completedOrder(PaymentIntent intent, CreateOrdersReq req) {
        if (!Objects.equals(intent.getImpUid(), req.getImpUid())
                || !Objects.equals(intent.getPopupIdx(), req.getPopupIdx())
                || !Objects.equals(intent.getReserveIdx(), req.getReserveIdx())
                || intent.getOrderIdx() == null) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }
        return CreateOrdersRes.builder().ordersIdx(intent.getOrderIdx()).build();
    }

    private PaymentIntent loadOwnedPaymentIntent(CustomUserDetails user, CreateOrdersReq req, Payment payment) {
        if (!req.getMerchantUid().equals(payment.getMerchantUid())) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }
        PaymentIntent intent = paymentIntentRepository.findForUpdate(req.getMerchantUid()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL));
        if (!intent.getAccountIdx().equals(user.getAccountId())) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }
        return intent;
    }

    private void validatePaymentIntent(PaymentIntent intent, CreateOrdersReq req, Payment payment) {
        if (intent.getStatus() != PaymentIntentStatus.READY
                || LocalDateTime.now().isAfter(intent.getExpiresAt())
                || !intent.getPopupIdx().equals(req.getPopupIdx())
                || !Objects.equals(intent.getReserveIdx(), req.getReserveIdx())) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL);
        }

        int paidAmount;
        try {
            paidAmount = payment.getAmount().intValueExact();
        } catch (RuntimeException exception) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
        }
        if (paidAmount != intent.getTotalPrice()) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_INVALID_TOTAL_PRICE);
        }

        Map<String, Double> providerGoods;
        Map<String, Double> intendedGoods;
        try {
            providerGoods = new Gson().fromJson(payment.getCustomData(), new TypeToken<Map<String, Double>>() {}.getType());
            Map<String, PaymentIntentGoodsSnapshot> snapshots = readGoodsSnapshots(intent);
            intendedGoods = new java.util.HashMap<>();
            snapshots.forEach((id, snapshot) -> intendedGoods.put(id, (double) snapshot.quantity()));
        } catch (RuntimeException exception) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
        }
        if (providerGoods == null || intendedGoods == null || !providerGoods.equals(intendedGoods)) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
        }
    }

    private Map<String, PaymentIntentGoodsSnapshot> readGoodsSnapshots(PaymentIntent intent) {
        Map<String, PaymentIntentGoodsSnapshot> snapshots = new Gson().fromJson(intent.getGoodsJson(),
                new TypeToken<Map<String, PaymentIntentGoodsSnapshot>>() {}.getType());
        if (snapshots == null || snapshots.isEmpty() || snapshots.values().stream().anyMatch(snapshot ->
                snapshot == null || snapshot.quantity() <= 0 || snapshot.unitPrice() < 0 || snapshot.goodsName() == null)) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_GOODS);
        }
        return snapshots;
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
            Orders orders = ordersRepository.findForUpdateByPopup(orderIdx, req.getPopupIdx()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_COMPLETE_FAIL_NOT_FOUND)
            );

            // 주문 상태 변경 STOCK_DELIVERY, RESERVE_DELIVERY
            orders.changeToDelivery();

            return UpdateOrdersRes.builder().ordersIdx(orders.getIdx()).build();

        } else {

            // 고객회원일 경우 (구매 확정 처리)
            Orders orders = ordersRepository.findForUpdate(orderIdx, user.getCustomerId()).orElseThrow(
                    () -> new ServiceException(ServiceErrorCode.ORDERS_COMPLETE_FAIL_NOT_FOUND)
            );

            // 주문 소유 확인
            if (!orders.getCustomer().getIdx().equals(user.getCustomerId())) throw new ServiceException(ServiceErrorCode.ORDERS_COMPLETE_FAIL_INVALID_MEMBER);

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
        Customer customer = customerRepository.findForUpdate(user.getCustomerId()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_NOT_FOUND_MEMBER)
        );

        // 주문 정보 조회
        Orders orders = ordersRepository.findForUpdate(ordersIdx, user.getCustomerId()).orElseThrow(
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
        if (orders.getUsedPoint() > 0) {
            customer.updatePoint(customer.getPoint() + orders.getUsedPoint());
            pointLedgerRepository.save(PointLedger.create(customer, orders, PointLedgerType.CANCEL_RETURN_USED,
                    orders.getUsedPoint(), customer.getPoint()));
        }
        if (orders.getEarnedPoint() > 0) {
            // 이미 적립 포인트를 사용했더라도 음수 잔액(포인트 채무)으로 회수한다.
            customer.updatePoint(customer.getPoint() - orders.getEarnedPoint());
            pointLedgerRepository.save(PointLedger.create(customer, orders, PointLedgerType.CANCEL_RECLAIM_EARNED,
                    -orders.getEarnedPoint(), customer.getPoint()));
        }

        // 주문 상태 변경(STOCK_CANCEL, RSERVE_CANCEL)
        orders.updateOrderStatus();

        // 환불 처리 진행
        refundOutboxService.enqueueOrderRefund(orders, payment);

        return UpdateOrdersRes.builder().ordersIdx(orders.getIdx()).build();
    }

    // 고객 주문 상세 조회
    public GetOrdersRes getOrder(CustomUserDetails user, Long ordersIdx) throws ServiceException {

        // 주문 조회(ordersIdx)
        Orders orders = ordersRepository.findByOrdersIdxAndCustomerIdx(ordersIdx, user.getCustomerId()).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_SEARCH_FAIL_NOT_FOUND)
        );

        // Orders DTO 반환
        return orders.toDto();

    }

    // 고객 주문 목록 조회
    public Page<GetOrdersRes> getOrders(CustomUserDetails user, int page, int size) throws ServiceException {

        // 주문 조회(customerIdx)
        Pageable pageable = RequestValidation.page(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Orders> ordersPage = ordersRepository.findAllByCustomerIdx(user.getCustomerId(), pageable).orElseThrow(
                () -> new ServiceException(ServiceErrorCode.ORDERS_SEARCH_ALL_FAIL_NOT_FOUND)
        );

        return Orders.toDtoPage(ordersPage);

    }

}

