package com.fiiiiive.zippop.orders;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.fiiiiive.zippop.auth.model.entity.Customer;
import com.fiiiiive.zippop.auth.repository.CustomerRepository;
import com.fiiiiive.zippop.global.common.exception.BaseException;
import com.fiiiiive.zippop.global.security.CustomUserDetails;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.goods.repository.GoodsRepository;
import com.fiiiiive.zippop.orders.model.dto.VerifyOrdersRes;
import com.fiiiiive.zippop.orders.repository.OrdersDetailRepository;
import com.fiiiiive.zippop.orders.repository.OrdersRepository;
import com.fiiiiive.zippop.orders.service.OrdersService;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@ExtendWith(MockitoExtension.class)
public class OrdersServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private GoodsRepository goodsRepository;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private OrdersDetailRepository ordersDetailRepository;
    @Mock
    private IamportClient iamportClient;

    @InjectMocks
    private OrdersService ordersService;

    @Test
    void testVerifyOrders_Success() throws IamportResponseException, IOException, BaseException {
        // given
        Long userId = 1L;
        CustomUserDetails customUserDetails = new CustomUserDetails(userId, "user1@example.com", "ROLE_CUSTOMER", "user1");

        String impUid = "imp_12345";
        Payment payment = mock(Payment.class);
        when(payment.getAmount()).thenReturn(BigDecimal.valueOf(1000));  // 결제 금액
        when(payment.getCustomData()).thenReturn("{\"1\":1}");  // 굿즈 1개
        IamportResponse<Payment> response = mock(IamportResponse.class);
        when(response.getResponse()).thenReturn(payment);
        when(iamportClient.paymentByImpUid(impUid)).thenReturn(response);

        // 고객 정보 Mock
        Customer customer = mock(Customer.class);
        when(customerRepository.findByCustomerIdx(userId)).thenReturn(Optional.of(customer));
        when(customer.getPoint()).thenReturn(5000);  // 고객 포인트

        // 굿즈 정보 Mock
        Goods goods = mock(Goods.class);
        when(goodsRepository.findByGoodsIdx(1L)).thenReturn(Optional.of(goods));
        when(goods.getPrice()).thenReturn(1000);  // 굿즈 가격
        when(goods.getAmount()).thenReturn(10);  // 재고 10개

        // when
        VerifyOrdersRes result = ordersService.verifyOrders(customUserDetails, impUid, true);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getOrdersIdx());  // 주문 idx가 반환되었는지 확인
    }

    @Test
    void testVerifyOrders_Fail_InvalidRole() {
        // given
        CustomUserDetails customUserDetails = new CustomUserDetails(1L, "user1@example.com", "ROLE_ADMIN", "user1");

        // when & then
        assertThrows(BaseException.class, () ->
                ordersService.verifyOrders(customUserDetails, "imp_12345", true));
    }

    @Test
    void testVerifyOrders_Fail_NotFoundMember() throws IamportResponseException, IOException {
        // given
        Long userId = 1L;
        CustomUserDetails customUserDetails = new CustomUserDetails(userId, "user1@example.com", "ROLE_CUSTOMER", "user1");

        String impUid = "imp_12345";
        Payment payment = mock(Payment.class);
        when(payment.getAmount()).thenReturn(BigDecimal.valueOf(1000));  // 결제 금액
        when(payment.getCustomData()).thenReturn("{\"1\":1}");  // 굿즈 1개
        IamportResponse<Payment> response = mock(IamportResponse.class);
        when(response.getResponse()).thenReturn(payment);
        when(iamportClient.paymentByImpUid(impUid)).thenReturn(response);

        // 회원 정보가 없을 경우
        when(customerRepository.findByCustomerIdx(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BaseException.class, () ->
                ordersService.verifyOrders(customUserDetails, impUid, true));
    }

    @Test
    void testVerifyOrders_Fail_GoodsNotFound() throws IamportResponseException, IOException {
        // given
        Long userId = 1L;
        CustomUserDetails customUserDetails = new CustomUserDetails(userId, "user1@example.com", "ROLE_CUSTOMER", "user1");

        String impUid = "imp_12345";
        Payment payment = mock(Payment.class);
        when(payment.getAmount()).thenReturn(BigDecimal.valueOf(1000));  // 결제 금액
        when(payment.getCustomData()).thenReturn("{\"999\":1}");  // 굿즈 999번
        IamportResponse<Payment> response = mock(IamportResponse.class);
        when(response.getResponse()).thenReturn(payment);
        when(iamportClient.paymentByImpUid(impUid)).thenReturn(response);

        // 굿즈 정보가 없을 경우
        when(goodsRepository.findByGoodsIdx(999L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BaseException.class, () ->
                ordersService.verifyOrders(customUserDetails, impUid, true));
    }

    @Test
    void testVerifyOrders_Fail_InvalidTotalPrice() throws IamportResponseException, IOException {
        // given
        Long userId = 1L;
        CustomUserDetails customUserDetails = new CustomUserDetails(userId, "user1@example.com", "ROLE_CUSTOMER", "user1");

        String impUid = "imp_12345";
        Payment payment = mock(Payment.class);
        when(payment.getAmount()).thenReturn(BigDecimal.valueOf(500));  // 결제 금액
        when(payment.getCustomData()).thenReturn("{\"1\":1}");  // 굿즈 1개
        IamportResponse<Payment> response = mock(IamportResponse.class);
        when(response.getResponse()).thenReturn(payment);
        when(iamportClient.paymentByImpUid(impUid)).thenReturn(response);

        // 고객 정보 Mock
        Customer customer = mock(Customer.class);
        when(customerRepository.findByCustomerIdx(userId)).thenReturn(Optional.of(customer));
        when(customer.getPoint()).thenReturn(5000);  // 고객 포인트

        // 굿즈 정보 Mock
        Goods goods = mock(Goods.class);
        when(goodsRepository.findByGoodsIdx(1L)).thenReturn(Optional.of(goods));
        when(goods.getPrice()).thenReturn(1000);  // 가격이 1000인 굿즈
        when(goods.getAmount()).thenReturn(10);  // 재고 10개

        // when & then
        assertThrows(BaseException.class, () ->
                ordersService.verifyOrders(customUserDetails, impUid, true));
    }
}

