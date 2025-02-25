package com.fiiiiive.zippop.global.security;


import com.fiiiiive.zippop.global.utils.RedisUtil;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class AccessControlService {
    public final RedisUtil redisUtil;
    public final ReserveRepository reserveRepository;

    public AuthorizationDecision hasReserveAccess(Supplier<Authentication> authentication, RequestAuthorizationContext object) {

        String reserveIdx = object.getRequest().getParameter("reserveIdx");
        boolean flag = false;
        // 인증되지 않은 사용자는 false 반환
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return new AuthorizationDecision(false);
        }

        // reserveIdx가 없으면 false 반환
        Optional<Reserve> reserveOpt = reserveRepository.findById(Long.valueOf(reserveIdx));
        if (reserveOpt.isEmpty()) {
            return new AuthorizationDecision(false);
        }

        Reserve reserve = reserveOpt.get();
        if (reserve.getEndTime().isBefore(LocalDateTime.now())) {
            return new AuthorizationDecision(false);
        }
        String userEmail = authentication.get().getName();
        // 작업큐에 유저가 있는지 확인
        Long existUser = redisUtil.getOrder(reserve.getWorkingUUID(), userEmail);

    if (existUser == null) {
            return new AuthorizationDecision(false);
        }
        // 인가
        return new AuthorizationDecision(true);
    }
}
