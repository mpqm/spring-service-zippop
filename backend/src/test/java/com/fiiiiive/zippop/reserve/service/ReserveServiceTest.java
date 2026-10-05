package com.fiiiiive.zippop.reserve.service;

import com.fiiiiive.zippop.global.base.ServiceErrorCode;
import com.fiiiiive.zippop.global.base.ServiceException;
import com.fiiiiive.zippop.global.crypto.JwtService;
import com.fiiiiive.zippop.global.redis.RedisQueueService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.policy.PopupPolicy;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.fiiiiive.zippop.reserve.model.dto.CreateReserveReq;
import com.fiiiiive.zippop.reserve.model.dto.GetReserveQueueRes;
import com.fiiiiive.zippop.reserve.model.dto.GetReserveQueueReq;
import com.fiiiiive.zippop.reserve.model.entity.Reserve;
import com.fiiiiive.zippop.reserve.repository.ReserveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReserveServiceTest {

    @Mock ReserveRepository reserveRepository;
    @Mock PopupRepository popupRepository;
    @Mock PopupPolicy popupPolicy;
    @Mock JwtService jwtService;
    @Mock RedisQueueService redisQueueService;
    @Mock SimpMessagingTemplate messagingTemplate;

    private ReserveService reserveService;
    private CustomUserDetails user;

    @BeforeEach
    void setUp() {
        reserveService = new ReserveService(
                reserveRepository,
                popupRepository,
                popupPolicy,
                jwtService,
                redisQueueService,
                messagingTemplate
        );
        user = CustomUserDetails.builder().idx(3L).email("user@test.com").role("ROLE_CUSTOMER").build();
    }

    @Test
    void queueManagementRecreatesOnlyMissingQueues() {
        Reserve reserve = activeReserve();
        when(reserveRepository.findAllByStartDate(LocalDate.now())).thenReturn(List.of(reserve));
        when(redisQueueService.existQueue("working")).thenReturn(false);
        when(redisQueueService.existQueue("waiting")).thenReturn(true);

        reserveService.managementReserveQueue();

        verify(redisQueueService).createQueue(org.mockito.ArgumentMatchers.eq("working"), anyLong());
        verify(redisQueueService, never()).createQueue(org.mockito.ArgumentMatchers.eq("waiting"), anyLong());
    }

    @Test
    void statusRequestFromAUserOutsideBothQueuesFailsCleanly() {
        Reserve reserve = activeReserve();
        GetReserveQueueReq request = new GetReserveQueueReq();
        request.setReserveIdx(9L);
        Principal principal = () -> "user@test.com";
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));
        when(redisQueueService.getSize("working")).thenReturn("0");
        when(redisQueueService.getSize("waiting")).thenReturn("0");
        when(redisQueueService.getOrder("working", "user@test.com")).thenReturn(null);
        when(redisQueueService.getOrder("waiting", "user@test.com")).thenReturn(null);

        assertThatThrownBy(() -> reserveService.status(principal, request))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.RESERVE_ACCESS_FAIL));
    }

    @Test
    void enrollmentBeforeTheConfiguredStartTimeIsRejected() {
        Reserve reserve = Reserve.builder()
                .idx(9L)
                .workingUUID("working")
                .waitingUUID("waiting")
                .totalPeople(3)
                .startDate(LocalDate.now().plusDays(1))
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));

        assertThatThrownBy(() -> reserveService.enrollReserve(new MockHttpServletResponse(), user, 9L))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.RESERVE_ENROLL_FAIL_NOT_OPEN));
        verify(redisQueueService, never()).enrollQueueWithLock(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(), anyLong(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void cancellingAWorkingUserPromotesAndNotifiesTheFirstWaitingUser() {
        Reserve reserve = activeReserve();
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));
        when(redisQueueService.getOrder("working", "user@test.com")).thenReturn(0L);
        when(redisQueueService.firstWaitingUserToWorking("working", "waiting", 3))
                .thenReturn("waiting@test.com");
        when(jwtService.createReserveToken(9L, "waiting@test.com")).thenReturn("promoted-token");
        when(redisQueueService.getSize("working")).thenReturn("3");
        when(redisQueueService.getSize("waiting")).thenReturn("0");
        when(redisQueueService.getOrder("working", "waiting@test.com")).thenReturn(2L);

        reserveService.cancelReserve(
                new MockHttpServletRequest(),
                new MockHttpServletResponse(),
                user,
                9L
        );

        verify(redisQueueService).remove("working", "user@test.com");
        verify(redisQueueService).firstWaitingUserToWorking("working", "waiting", 3);
        verify(messagingTemplate).convertAndSendToUser(
                eq("waiting@test.com"),
                eq("/queue/reserve/status"),
                any(GetReserveQueueRes.class)
        );
    }

    @Test
    void reserveCreationCannotAllocateMorePeopleThanThePopupHas() {
        Popup popup = Popup.builder().idx(7L).totalPeople(2).build();
        CreateReserveReq request = CreateReserveReq.builder()
                .popupIdx(7L)
                .reservePeople(3)
                .reserveStartDate(LocalDate.now().plusDays(1))
                .reserveStartTime(LocalDateTime.now().plusDays(1))
                .reserveEndTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();
        when(popupRepository.findById(7L)).thenReturn(Optional.of(popup));

        assertThatThrownBy(() -> reserveService.createReserve(user, request))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.RESERVE_REGISTER_FAIL_LIMIT_EXCEEDED));
        verify(reserveRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    private Reserve activeReserve() {
        return Reserve.builder()
                .idx(9L)
                .workingUUID("working")
                .waitingUUID("waiting")
                .totalPeople(3)
                .startDate(LocalDate.now())
                .startTime(LocalDateTime.now().minusMinutes(5))
                .endTime(LocalDateTime.now().plusHours(1))
                .popup(Popup.builder().idx(7L).name("팝업").build())
                .build();
    }
}
