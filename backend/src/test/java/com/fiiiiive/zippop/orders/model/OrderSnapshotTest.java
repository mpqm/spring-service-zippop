package com.fiiiiive.zippop.orders.model;

import com.fiiiiive.zippop.account.model.entity.Account;
import com.fiiiiive.zippop.account.model.entity.Customer;
import com.fiiiiive.zippop.goods.model.entity.Goods;
import com.fiiiiive.zippop.orders.model.entity.Orders;
import com.fiiiiive.zippop.orders.model.entity.OrdersDetail;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderSnapshotTest {
    @Test
    void orderAndDetailKeepCheckoutTimeCustomerAndGoodsValues() {
        Account account = Account.builder().name("구매자").email("buyer@test.com")
                .phoneNumber("01012345678").address("서울시 원래 주소").build();
        Customer customer = Customer.builder().idx(1L).account(account).point(0).build();
        Popup popup = Popup.builder().idx(2L).build();
        Goods goods = Goods.builder().idx(3L).name("원래 상품명").price(1200).popup(popup).build();
        Orders orders = Orders.createStockOrders("imp", 4900, 0, customer, popup);
        OrdersDetail detail = OrdersDetail.create(orders, goods, 2);

        assertThat(orders.getRecipientName()).isEqualTo("구매자");
        assertThat(orders.getShippingAddress()).isEqualTo("서울시 원래 주소");
        assertThat(detail.getGoodsName()).isEqualTo("원래 상품명");
        assertThat(detail.getUnitPrice()).isEqualTo(1200);
        assertThat(detail.getQuantity()).isEqualTo(2);
        assertThat(detail.getEachPrice()).isEqualTo(2400);
    }
}
