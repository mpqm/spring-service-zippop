package com.fiiiiive.zippop.orders.service;

import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.account.repository.CustomerRepository;
import com.fiiiiive.zippop.global.enums.GoodsStatus;
import com.fiiiiive.zippop.global.enums.OrdersStatus;
import com.fiiiiive.zippop.global.redis.RedisQueueService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.orders.event.RefundEvent;
import com.fiiiiive.zippop.orders.model.dto.CreateOrdersReq;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.policy.OrdersPolicy;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.popup.model.entity.Popup;
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

import java.time.LocalDate;
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
    @Mock RedisQueueService redisQueueService;

    private OrdersService ordersService;
    private CustomUserDetails user;

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
                redisQueueService
        );
        user = CustomUserDetails.builder().idx(3L).email("user@test.com").role("ROLE_CUSTOMER").build();
    }

    @Test
    void failedReservePaymentPublishesExactlyOneRollbackRefund() throws Exception {
        Popup popup = Popup.builder().idx(7L).build();
        Reserve reserve = Reserve.builder()
                .idx(9L)
                .workingUUID("working")
                .waitingUUID("waiting")
                .totalPeople(3)
                .startDate(LocalDate.now())
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
        when(redisQueueService.getOrder("working", "user@test.com")).thenReturn(0L);
        when(iamportClient.paymentByImpUid("imp-reserve")).thenReturn(providerResponse);
        when(providerResponse.getResponse()).thenReturn(payment);
        when(payment.getCustomData()).thenReturn("{\"11\":2}");
        when(customerRepository.findByCustomerIdx(3L)).thenReturn(Optional.of(customer));
        when(goodsRepository.findByGoodsIdx(11L)).thenReturn(Optional.of(goods));
        CreateOrdersReq request = CreateOrdersReq.builder()
                .impUid("imp-reserve")
                .popupIdx(7L)
                .reserveIdx(9L)
                .build();

        assertThatThrownBy(() -> ordersService.createReserveOrders(user, request))
                .isInstanceOf(com.fiiiiive.zippop.global.base.ServiceException.class);

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
                .deliveryCost(2500)
                .status(OrdersStatus.STOCK_READY)
                .customer(customer)
                .popup(popup)
                .build();
        Payment payment = mock(Payment.class);
        @SuppressWarnings("unchecked")
        IamportResponse<Payment> providerResponse = mock(IamportResponse.class);
        when(customerRepository.findByCustomerIdx(3L)).thenReturn(Optional.of(customer));
        when(ordersRepository.findByOrdersIdxAndCustomerIdx(13L, 3L)).thenReturn(Optional.of(orders));
        when(iamportClient.paymentByImpUid("imp-stock")).thenReturn(providerResponse);
        when(providerResponse.getResponse()).thenReturn(payment);
        when(payment.getCustomData()).thenReturn("{\"11\":2}");
        when(goodsRepository.findByGoodsIdx(11L)).thenReturn(Optional.of(goods));

        ordersService.cancelOrders(user, 13L);

        ArgumentCaptor<RefundEvent> event = ArgumentCaptor.forClass(RefundEvent.class);
        verify(eventPublisher).publishEvent(event.capture());
        assertThat(event.getValue().isRollbackOnly()).isFalse();
        assertThat(goods.getAmount()).isEqualTo(7);
        assertThat(customer.getPoint()).isEqualTo(1500);
        assertThat(orders.getStatus()).isEqualTo(OrdersStatus.STOCK_CANCEL);
    }
}
