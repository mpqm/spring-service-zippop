package com.fiiiiive.zippop.global.security.normal;


import com.fiiiiive.zippop.global.redis.RedisQueueService;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessControlService {

    public final RedisQueueService redisQueueService;
    public final ReserveRepository reserveRepository;

    public AuthorizationDecision hasReserveAccess(Supplier<Authentication> authentication, RequestAuthorizationContext object) {

        HttpServletRequest request = object.getRequest();
        String reserveIdx = request.getParameter("reserveIdx");

        // 인증되지 않은 사용자는 false 반환
        Authentication auth = authentication.get();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            return new AuthorizationDecision(false);
        }

        // WTOKEN 블랙리스트 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("WTOKEN".equals(cookie.getName())) {
                    String wToken = cookie.getValue();
                    if (wToken != null && !wToken.isEmpty()) {
                        if (redisQueueService.isReserveTokenBlacklisted(wToken)) {
                            log.warn("블랙리스트된 예약 토큰 사용 시도 - 사용자: {}", auth.getName());
                            return new AuthorizationDecision(false);
                        }
                    }
                    break;
                }
            }
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
        Long existUser = redisQueueService.getOrder(reserve.getWorkingUUID(), userEmail);

        if (existUser == null) {
            return new AuthorizationDecision(false);
        }

        return new AuthorizationDecision(true);
    }

}
