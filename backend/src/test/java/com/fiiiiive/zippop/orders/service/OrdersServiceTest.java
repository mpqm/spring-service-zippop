package com.fiiiiive.zippop.orders.service;

import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.goods.model.enums.GoodsStatus;
import com.fiiiiive.zippop.orders.model.enums.OrdersStatus;
import com.fiiiiive.zippop.reserve.infrastructure.redis.ReserveRedisService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.orders.event.RefundEvent;
import com.fiiiiive.zippop.orders.model.dto.CreateOrdersReq;
import com.fiiiiive.zippop.orders.model.dto.CreatePaymentIntentReq;
import com.fiiiiive.zippop.orders.model.dto.PaymentIntentGoodsReq;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.model.entity.PaymentIntent;
import com.fiiiiive.zippop.orders.model.entity.PaymentIntentStatus;
import com.fiiiiive.zippop.orders.policy.OrdersPolicy;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.orders.repository.PaymentIntentRepository;
import com.fiiiiive.zippop.orders.repository.PointLedgerRepository;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.model.enums.PopupStatus;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock IamportClient iamportClient;
    @Mock CustomerRepository customerRepository;
    @Mock OrdersDetailRepository ordersDetailRepository;
    @Mock OrdersRepository ordersRepository;
    @Mock GoodsRepository goodsRepository;
    @Mock PopupRepository popupRepository;
    @Mock ReserveRepository reserveRepository;
    @Mock ApplicationEventPublisher eventPublisher;
    @Mock OrdersPolicy ordersPolicy;
    @Mock
    ReserveRedisService reserveRedisService;
    @Mock PaymentIntentRepository paymentIntentRepository;
    @Mock PointLedgerRepository pointLedgerRepository;
    @Mock RefundOutboxService refundOutboxService;

    private OrdersService ordersService;
    private CustomUserDetails user;

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"ready", "cancelled", "failed"})
    void unpaidStockTransactionsAreNotOrderedOrRefunded(String status) throws Exception {
        Customer customer = Customer.builder().idx(3L).point(3000).build();
        when(customerRepository.findForUpdate(3L)).thenReturn(Optional.of(customer));
        Payment payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn(status);
        @SuppressWarnings("unchecked")
        IamportResponse<Payment> response = mock(IamportResponse.class);
        when(response.getResponse()).thenReturn(payment);
        when(iamportClient.paymentByImpUid("imp-unpaid")).thenReturn(response);
        when(paymentIntentRepository.findByMerchantUid("merchant-unpaid")).thenReturn(Optional.of(PaymentIntent.builder()
                .merchantUid("merchant-unpaid").accountIdx(99L).popupIdx(7L).goodsJson("{\"11\":1}")
                .usedPoint(0).totalPrice(3500).status(PaymentIntentStatus.READY)
                .expiresAt(LocalDateTime.now().plusMinutes(10)).build()));

        assertThatThrownBy(() -> ordersService.createStockOrders(user,
                CreateOrdersReq.builder().impUid("imp-unpaid").merchantUid("merchant-unpaid").popupIdx(7L).build()))
                .isInstanceOf(com.fiiiiive.zippop.global.api.ServiceException.class);
        org.mockito.Mockito.verifyNoInteractions(ordersRepository, goodsRepository, eventPublisher);
    }

    @Test void emptyGoodsCannotCreateAShippingOnlyOrder() throws Exception {
        Customer customer = Customer.builder().idx(3L).point(3000).build();
        when(customerRepository.findForUpdate(3L)).thenReturn(Optional.of(customer));
        Payment payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn("paid");
        when(payment.getMerchantUid()).thenReturn("merchant-empty");
        when(payment.getCustomData()).thenReturn("{}");
        when(payment.getAmount()).thenReturn(java.math.BigDecimal.valueOf(2500));
        PaymentIntent emptyIntent = PaymentIntent.builder()
                .merchantUid("merchant-empty").accountIdx(99L).popupIdx(7L).goodsJson("{}")
                .usedPoint(0).totalPrice(2500).status(PaymentIntentStatus.READY)
                .expiresAt(LocalDateTime.now().plusMinutes(10)).build();
        when(paymentIntentRepository.findForUpdate("merchant-empty")).thenReturn(Optional.of(emptyIntent));
        when(paymentIntentRepository.findByMerchantUid("merchant-empty")).thenReturn(Optional.of(emptyIntent));
        @SuppressWarnings("unchecked")
        IamportResponse<Payment> response = mock(IamportResponse.class);
        when(response.getResponse()).thenReturn(payment);
        when(iamportClient.paymentByImpUid("imp-empty")).thenReturn(response);

        assertThatThrownBy(() -> ordersService.createStockOrders(user,
                CreateOrdersReq.builder().impUid("imp-empty").merchantUid("merchant-empty").popupIdx(7L).build()))
                .isInstanceOf(com.fiiiiive.zippop.global.api.ServiceException.class);
        org.mockito.Mockito.verifyNoInteractions(ordersRepository, goodsRepository);
    }

    @Test void merchantIdsAreServerGeneratedAndDoNotRepeatWithinAMillisecond() {
        Popup popup = Popup.builder().idx(7L).build();
        Goods goods = Goods.builder().idx(11L).popup(popup).name("Sample goods").price(1000).amount(3000).status(GoodsStatus.GOODS_STOCK).build();
        Customer customer = Customer.builder().idx(3L).point(3000).build();
        when(customerRepository.findForUpdate(3L)).thenReturn(Optional.of(customer));
        when(popupRepository.findByPopupIdx(7L)).thenReturn(Optional.of(popup));
        when(goodsRepository.findByGoodsIdx(11L)).thenReturn(Optional.of(goods));
        CreatePaymentIntentReq request = CreatePaymentIntentReq.builder()
                .popupIdx(7L).goods(java.util.List.of(PaymentIntentGoodsReq.builder().goodsIdx(11L).count(1).build()))
                .usedPoint(0).build();
        java.util.Set<String> ids = new java.util.HashSet<>();
        for (int i = 0; i < 2000; i++) ids.add(ordersService.issueMerchantUid(user, request).getMerchantUid());
        assertThat(ids).hasSize(2000).allMatch(id -> id.matches("zippop_[0-9a-f]{32}"));
        ArgumentCaptor<PaymentIntent> issuedIntents = ArgumentCaptor.forClass(PaymentIntent.class);
        verify(paymentIntentRepository, times(2000)).save(issuedIntents.capture());
        assertThat(issuedIntents.getAllValues().get(0).getGoodsJson())
                .contains("\"quantity\":1", "\"unitPrice\":1000", "\"goodsName\":\"Sample goods\"");
    }

    @Test
    void anotherAccountsPaymentCannotCreateOrderChangeInventoryOrTriggerRefund() throws Exception {
        Customer customer = Customer.builder().idx(3L).point(3000).build();
        when(paymentIntentRepository.findByMerchantUid("merchant-owned-by-another-account"))
                .thenReturn(Optional.of(PaymentIntent.builder()
                        .merchantUid("merchant-owned-by-another-account")
                        .accountIdx(100L).popupIdx(7L).goodsJson("{\"11\":1}")
                        .usedPoint(0).totalPrice(3500).status(PaymentIntentStatus.READY).build()));

        assertThatThrownBy(() -> ordersService.createStockOrders(user,
                CreateOrdersReq.builder().impUid("imp-stolen").merchantUid("merchant-owned-by-another-account").popupIdx(7L).build()))
                .isInstanceOf(com.fiiiiive.zippop.global.api.ServiceException.class);

        org.mockito.Mockito.verifyNoInteractions(iamportClient, customerRepository, ordersRepository, goodsRepository,
                ordersDetailRepository, eventPublisher);
        assertThat(customer.getPoint()).isEqualTo(3000);
    }

    @Test
    void paymentWhoseProviderMerchantUidDiffersFromOwnedIntentIsNeverRefundedOrOrdered() throws Exception {
        Customer customer = Customer.builder().idx(3L).point(3000).build();
        PaymentIntent ownedIntent = PaymentIntent.builder()
                .merchantUid("merchant-b").accountIdx(99L).popupIdx(7L).goodsJson("{\"11\":1}")
                .usedPoint(0).totalPrice(3500).status(PaymentIntentStatus.READY)
                .expiresAt(LocalDateTime.now().plusMinutes(10)).build();
        when(paymentIntentRepository.findByMerchantUid("merchant-b")).thenReturn(Optional.of(ownedIntent));
        when(customerRepository.findForUpdate(3L)).thenReturn(Optional.of(customer));
        Payment payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn("paid");
        when(payment.getMerchantUid()).thenReturn("merchant-a");
        @SuppressWarnings("unchecked") IamportResponse<Payment> response = mock(IamportResponse.class);
        when(response.getResponse()).thenReturn(payment);
        when(iamportClient.paymentByImpUid("imp-a")).thenReturn(response);

        assertThatThrownBy(() -> ordersService.createStockOrders(user, CreateOrdersReq.builder()
                .impUid("imp-a").merchantUid("merchant-b").popupIdx(7L).build()))
                .isInstanceOf(com.fiiiiive.zippop.global.api.ServiceException.class);

        org.mockito.Mockito.verifyNoInteractions(goodsRepository, ordersRepository, ordersDetailRepository,
                eventPublisher, refundOutboxService);
        assertThat(customer.getPoint()).isEqualTo(3000);
    }

    @Test
    void completedIntentReturnsExistingOrderWithoutCallingProvider() {
        PaymentIntent completed = PaymentIntent.builder()
                .merchantUid("merchant-done").accountIdx(99L).popupIdx(7L)
                .impUid("imp-done").orderIdx(55L).status(PaymentIntentStatus.COMPLETED).build();
        when(paymentIntentRepository.findByMerchantUid("merchant-done")).thenReturn(Optional.of(completed));

        var result = ordersService.createStockOrders(user, CreateOrdersReq.builder()
                .impUid("imp-done").merchantUid("merchant-done").popupIdx(7L).build());

        assertThat(result.getOrdersIdx()).isEqualTo(55L);
        org.mockito.Mockito.verifyNoInteractions(iamportClient, customerRepository, goodsRepository,
                ordersRepository, eventPublisher, refundOutboxService);
    }

    @Test
    void expiredPaidIntentCreatesCompensationWithoutChangingOrderOrInventory() throws Exception {
        Customer customer = Customer.builder().idx(3L).point(3000).build();
        PaymentIntent expired = PaymentIntent.builder()
                .merchantUid("merchant-expired").accountIdx(99L).popupIdx(7L).goodsJson("{\"11\":1}")
                .usedPoint(0).totalPrice(3500).status(PaymentIntentStatus.READY)
                .expiresAt(LocalDateTime.now().minusSeconds(1)).build();
        when(paymentIntentRepository.findByMerchantUid("merchant-expired")).thenReturn(Optional.of(expired));
        when(paymentIntentRepository.findForUpdate("merchant-expired")).thenReturn(Optional.of(expired));
        when(customerRepository.findForUpdate(3L)).thenReturn(Optional.of(customer));
        Payment payment = mock(Payment.class);
        when(payment.getStatus()).thenReturn("paid");
        when(payment.getMerchantUid()).thenReturn("merchant-expired");
        @SuppressWarnings("unchecked") IamportResponse<Payment> response = mock(IamportResponse.class);
        when(response.getResponse()).thenReturn(payment);
        when(iamportClient.paymentByImpUid("imp-expired")).thenReturn(response);

        assertThatThrownBy(() -> ordersService.createStockOrders(user, CreateOrdersReq.builder()
                .impUid("imp-expired").merchantUid("merchant-expired").popupIdx(7L).build()))
                .isInstanceOf(com.fiiiiive.zippop.global.api.ServiceException.class);

        ArgumentCaptor<RefundEvent> event = ArgumentCaptor.forClass(RefundEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().isRollbackOnly()).isTrue();
        org.mockito.Mockito.verifyNoInteractions(goodsRepository, ordersRepository, ordersDetailRepository);
    }

    @Test
    void issuedQuoteUsesExplicitPointsInsteadOfInferringThemFromProviderAmount() {
        Popup popup = Popup.builder().idx(7L).build();
        Goods goods = Goods.builder().idx(11L).popup(popup).price(2000).amount(2).status(GoodsStatus.GOODS_STOCK).build();
        Customer customer = Customer.builder().idx(3L).point(4000).build();
        when(customerRepository.findForUpdate(3L)).thenReturn(Optional.of(customer));
        when(popupRepository.findByPopupIdx(7L)).thenReturn(Optional.of(popup));
        when(goodsRepository.findByGoodsIdx(11L)).thenReturn(Optional.of(goods));
        var request = CreatePaymentIntentReq.builder().popupIdx(7L).usedPoint(1000)
                .goods(java.util.List.of(PaymentIntentGoodsReq.builder().goodsIdx(11L).count(1).build())).build();

        var result = ordersService.issueMerchantUid(user, request);

        assertThat(result.getAmount()).isEqualTo(3500);
        ArgumentCaptor<PaymentIntent> intent = ArgumentCaptor.forClass(PaymentIntent.class);
        verify(paymentIntentRepository).save(intent.capture());
        assertThat(intent.getValue().getUsedPoint()).isEqualTo(1000);
        assertThat(intent.getValue().getTotalPrice()).isEqualTo(3500);
    }

    @BeforeEach
    void setUp() {
        ordersService = new OrdersService(
                iamportClient,
                customerRepository,
                ordersDetailRepository,
                ordersRepository,
                goodsRepository,
                popupRepository,
                reserveRepository,
                eventPublisher,
                ordersPolicy,
                reserveRedisService,
                paymentIntentRepository,
                pointLedgerRepository,
                refundOutboxService
        );
        user = CustomUserDetails.builder().accountId(99L).customerId(3L).email("user@test.com").role("ROLE_CUSTOMER").build();
    }

    @Test
    void failedReservePaymentPublishesExactlyOneRollbackRefund() throws Exception {
        Popup popup = Popup.builder().idx(7L).status(PopupStatus.POPUP_START).build();
        Reserve reserve = Reserve.builder()
                .idx(9L)
                .workingUUID("working")
                .waitingUUID("waiting")
                .totalPeople(3)
                .startTime(LocalDateTime.now().minusMinutes(5))
                .endTime(LocalDateTime.now().plusHours(1))
                .popup(popup)
                .build();
        Customer customer = Customer.builder().idx(3L).point(3000).build();
        Goods goods = Goods.builder()
                .idx(11L)
                .popup(popup)
                .name("예약 굿즈")
                .price(1000)
                .amount(10)
                .status(GoodsStatus.GOODS_RESERVED)
                .build();
        Payment payment = mock(Payment.class);
        @SuppressWarnings("unchecked")
        IamportResponse<Payment> providerResponse = mock(IamportResponse.class);
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));
        when(reserveRedisService.hasWorkingAccess("working", "user@test.com")).thenReturn(true);
        when(iamportClient.paymentByImpUid("imp-reserve")).thenReturn(providerResponse);
        when(providerResponse.getResponse()).thenReturn(payment);
        when(payment.getCustomData()).thenReturn("{\"11\":2}");
        when(payment.getMerchantUid()).thenReturn("merchant-reserve");
        when(payment.getAmount()).thenReturn(java.math.BigDecimal.valueOf(2000));
        org.mockito.Mockito.lenient().when(payment.getStatus()).thenReturn("paid");
        PaymentIntent reserveIntent = PaymentIntent.builder()
                .merchantUid("merchant-reserve").accountIdx(99L).popupIdx(7L).reserveIdx(9L)
                .goodsJson("{\"11\":{\"quantity\":2,\"unitPrice\":1000,\"goodsName\":\"예약 굿즈\"}}")
                .usedPoint(0).totalPrice(2000).status(PaymentIntentStatus.READY)
                .expiresAt(LocalDateTime.now().plusMinutes(10)).build();
        when(paymentIntentRepository.findForUpdate("merchant-reserve")).thenReturn(Optional.of(reserveIntent));
        when(paymentIntentRepository.findByMerchantUid("merchant-reserve")).thenReturn(Optional.of(reserveIntent));
        when(customerRepository.findForUpdate(3L)).thenReturn(Optional.of(customer));
        when(goodsRepository.findByGoodsIdx(11L)).thenReturn(Optional.of(goods));
        CreateOrdersReq request = CreateOrdersReq.builder()
                .impUid("imp-reserve")
                .merchantUid("merchant-reserve")
                .popupIdx(7L)
                .reserveIdx(9L)
                .build();

        assertThatThrownBy(() -> ordersService.createReserveOrders(user, request))
                .isInstanceOf(com.fiiiiive.zippop.global.api.ServiceException.class);

        ArgumentCaptor<RefundEvent> event = ArgumentCaptor.forClass(RefundEvent.class);
        verify(eventPublisher, times(1)).publishEvent(event.capture());
        assertThat(event.getValue().isRollbackOnly()).isTrue();
    }

    @Test
    void normalCancellationPublishesAnAfterCommitRefund() throws Exception {
        Popup popup = Popup.builder().idx(7L).build();
        Customer customer = Customer.builder().idx(3L).point(1000).build();
        Goods goods = Goods.builder().idx(11L).popup(popup).name("재고 굿즈").price(1000).amount(5).build();
        Orders orders = Orders.builder()
                .idx(13L)
                .impUid("imp-stock")
                .totalPrice(4500)
                .usedPoint(500)
                .earnedPoint(50)
                .deliveryCost(2500)
                .status(OrdersStatus.STOCK_READY)
                .customer(customer)
                .popup(popup)
                .build();
        Payment payment = mock(Payment.class);
        @SuppressWarnings("unchecked")
        IamportResponse<Payment> providerResponse = mock(IamportResponse.class);
        when(customerRepository.findForUpdate(3L)).thenReturn(Optional.of(customer));
        when(ordersRepository.findForUpdate(13L, 3L)).thenReturn(Optional.of(orders));
        when(iamportClient.paymentByImpUid("imp-stock")).thenReturn(providerResponse);
        when(providerResponse.getResponse()).thenReturn(payment);
        when(payment.getCustomData()).thenReturn("{\"11\":2}");
        when(goodsRepository.findByGoodsIdx(11L)).thenReturn(Optional.of(goods));

        ordersService.cancelOrders(user, 13L);

        verify(refundOutboxService).enqueueOrderRefund(orders, payment);
        assertThat(goods.getAmount()).isEqualTo(7);
        assertThat(customer.getPoint()).isEqualTo(1450);
        verify(pointLedgerRepository, times(2)).save(org.mockito.ArgumentMatchers.any());
        assertThat(orders.getStatus()).isEqualTo(OrdersStatus.STOCK_CANCEL);
    }
}
