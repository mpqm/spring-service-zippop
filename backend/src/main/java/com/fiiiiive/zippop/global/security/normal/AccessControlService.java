package com.fiiiiive.zippop.global.security.normal;


import com.fiiiiive.zippop.global.service.RedisService;
import com.fiiiiive.zippop.reserve.entity.Reserve;
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

    public final RedisService redisService;
    public final ReserveRepository reserveRepository;

    public AuthorizationDecision hasReserveAccess(Supplier<Authentication> authentication, RequestAuthorizationContext object) {

        String reserveIdx = object.getRequest().getParameter("reserveIdx");

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
        Long existUser = redisService.getOrder(reserve.getWorkingUUID(), userEmail);

        if (existUser == null) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(true);
    }

}
