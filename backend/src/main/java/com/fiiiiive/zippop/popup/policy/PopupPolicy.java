package com.fiiiiive.zippop.popup.policy;

import com.fiiiiive.zippop.global.base.BaseException;
import com.fiiiiive.zippop.global.base.BaseMessage;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.popup.model.Popup;
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

}
