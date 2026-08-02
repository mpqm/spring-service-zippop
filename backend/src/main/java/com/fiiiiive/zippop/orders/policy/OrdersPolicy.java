package com.fiiiiive.zippop.orders.policy;

import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.enums.RoleType;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrdersPolicy {

    public void validateOrderRole(CustomUserDetails user) {
        // 고객만 주문 가능
        if (!user.getRole().equals(RoleType.ROLE_CUSTOMER.name())) {
            throw new ServiceException(ServiceErrorCode.ORDERS_PAY_FAIL_INVALID_ROLE);
        }
    }

}
