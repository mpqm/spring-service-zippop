package com.fiiiiive.zippop.popup.policy;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.enums.PopupStatus;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PopupPolicy {

    public void validateOwner(Popup popup, CustomUserDetails user) {
        // 팝업 소유 확인
        if (!Objects.equals(popup.getCompanyEmail(), user.getEmail())) {
            throw new BaseException(BaseMessage.STORE_OWN_FAIL_INVALID_MEMBER);
        }
    }

    public void validateReserveStatus(Popup popup, PopupStatus popupStatus) {
        // 팝업 상태 확인(종료 상태면 예약 생성 불가)
        if(Objects.equals(popup.getStatus(), popupStatus)) {
            throw new BaseException(BaseMessage.RESERVE_REGISTER_FAIL_STORE_ENDED);
        }
    }

}
