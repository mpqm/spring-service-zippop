package com.fiiiiive.zippop.orders.application;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.enums.OrdersStatus;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.OrdersDto;
import com.fiiiiive.zippop.orders.service.OrdersService;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

// 주문 도메인 파사드.
@Service
@RequiredArgsConstructor
public class OrdersFacade {

    private final OrdersService ordersService;

    // 재고용, 예약용 분기 처리
    public OrdersDto.CreateOrdersRes createOrders(CustomUserDetails user, OrdersDto.CreateOrdersReq req) throws BaseException, IamportResponseException, IOException {
        OrdersDto.CreateOrdersRes res = null;
        if (req.getReserveIdx() != null) {
            res = ordersService.createReserveOrders(user, req);
        } else {
            res = ordersService.createStockOrders(user, req);
        }
        return res;
    }

    // 주문 취소, 확정 분기 처리
    public OrdersDto.UpdateOrdersRes updateOrders(CustomUserDetails user, Long orderIdx, OrdersDto.UpdateOrdersReq req) throws IamportResponseException, IOException {
        OrdersDto.UpdateOrdersRes res = null;
        if (Objects.equals(req.getStatus(), OrdersStatus.STOCK_CANCEL.getName()) || Objects.equals(req.getStatus(), OrdersStatus.RESERVE_CANCEL.getName())) {
            res = ordersService.cancelOrders(user, orderIdx, req);
        } else {
            res = ordersService.updateOrders(user, orderIdx, req);
        }
        return res;
    }

}
