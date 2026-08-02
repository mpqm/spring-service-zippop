package com.fiiiiive.zippop.orders.application;

import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.enums.OrdersStatus;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.orders.model.dto.CreateOrdersReq;
import com.fiiiiive.zippop.orders.model.dto.CreateOrdersRes;
import com.fiiiiive.zippop.orders.model.dto.UpdateOrdersReq;
import com.fiiiiive.zippop.orders.model.dto.UpdateOrdersRes;
import com.fiiiiive.zippop.orders.service.OrdersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

// 주문 도메인 파사드.
@Service
@RequiredArgsConstructor
public class OrdersFacade {

    private final OrdersService ordersService;

    // 재고용, 예약용 분기 처리
    public CreateOrdersRes createOrders(CustomUserDetails user, CreateOrdersReq req) throws ServiceException {
        CreateOrdersRes res;
        if (req.getReserveIdx() != null) {
            res = ordersService.createReserveOrders(user, req);
        } else {
            res = ordersService.createStockOrders(user, req);
        }
        return res;
    }

    // 주문 취소, 확정 분기 처리
    public UpdateOrdersRes updateOrders(CustomUserDetails user, Long orderIdx, UpdateOrdersReq req) {
        UpdateOrdersRes res;
        if (Objects.equals(req.getStatus(), OrdersStatus.STOCK_CANCEL.getName()) || Objects.equals(req.getStatus(), OrdersStatus.RESERVE_CANCEL.getName())) {
            res = ordersService.cancelOrders(user, orderIdx);
        } else {
            res = ordersService.updateOrders(user, orderIdx, req);
        }
        return res;
    }

}
