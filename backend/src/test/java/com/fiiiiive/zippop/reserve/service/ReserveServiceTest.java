package com.fiiiiive.zippop.reserve.service;

import com.fiiiiive.zippop.global.api.ServiceErrorCode;
import com.fiiiiive.zippop.global.api.ServiceException;
import com.fiiiiive.zippop.global.security.crypto.JwtService;
import com.fiiiiive.zippop.global.security.normal.CustomUserDetails;
import com.fiiiiive.zippop.popup.model.entity.Popup;
import com.fiiiiive.zippop.popup.model.enums.PopupStatus;
import com.fiiiiive.zippop.popup.repository.PopupRepository;
import com.fiiiiive.zippop.reserve.infrastructure.redis.ReserveRedisService;
import com.fiiiiive.zippop.reserve.model.enums.EnrollResult;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
    @Mock JwtService jwtService;
    @Mock
    ReserveRedisService reserveRedisService;
    @Mock SimpMessagingTemplate messagingTemplate;

    private ReserveService reserveService;
    private CustomUserDetails user;

    @Test void maintenanceCleansAnOvernightReservationAfterItEnds() {
        Reserve reserve = Reserve.builder().idx(9L).workingUUID("working").waitingUUID("waiting")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusMinutes(1)).popup(Popup.builder().name("Popup").build()).build();
        when(reserveRepository.findQueuesForMaintenance(any(LocalDateTime.class))).thenReturn(List.of(reserve));
        reserveService.managementReserveQueue();
        verify(reserveRedisService).deleteQueue("working", "waiting");
    }

    @Test void closedPopupCannotBeEnteredEvenWhileItsReservationIsActive() {
        Reserve reserve = activeReserve();
        reserve.getPopup().setStatus(PopupStatus.POPUP_END);
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));
        assertThatThrownBy(() -> reserveService.enrollReserve(new MockHttpServletResponse(), user, 9L))
                .isInstanceOfSatisfying(ServiceException.class, ex ->
                        assertThat(ex.getErrorCode()).isEqualTo(ServiceErrorCode.RESERVE_ENROLL_FAIL_CLOSED));
        verify(reserveRedisService, never()).enrollWithLock(any(), any(), any(), anyLong(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test void allRemainingCapacityCanBeAllocatedToOneReservation() {
        Popup popup = Popup.builder().idx(7L).totalPeople(1).companyEmail("user@test.com").startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(2)).build();
        var start = LocalDateTime.now().plusDays(1);
        var request = CreateReserveReq.builder().popupIdx(7L).reservePeople(1)
                .reserveStartTime(start).reserveEndTime(start.plusHours(1)).build();
        when(popupRepository.findForUpdate(7L)).thenReturn(Optional.of(popup));
        TransactionSynchronizationManager.initSynchronization();
        try {
            reserveService.createReserve(user, request);
            assertThat(popup.getTotalPeople()).isZero();
            verify(reserveRepository).save(any());
            verify(reserveRedisService, never()).createQueue(any(), any());
            for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
                synchronization.afterCommit();
            }
            verify(reserveRedisService, org.mockito.Mockito.times(2)).createQueue(any(), eq(request.getReserveEndTime()));
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @BeforeEach
    void setUp() {
        reserveService = new ReserveService(
                reserveRepository,
                popupRepository,
                jwtService,
                reserveRedisService,
                messagingTemplate
        );
        user = CustomUserDetails.builder().accountId(99L).customerId(3L).email("user@test.com").role("ROLE_CUSTOMER").build();
    }

    @Test
    void queueManagementRecreatesOnlyMissingQueues() {
        Reserve reserve = activeReserve();
        when(reserveRepository.findQueuesForMaintenance(any(LocalDateTime.class))).thenReturn(List.of(reserve));
        when(reserveRedisService.existQueue("working")).thenReturn(false);
        when(reserveRedisService.existQueue("waiting")).thenReturn(true);

        reserveService.managementReserveQueue();

        verify(reserveRedisService).createQueue("working", reserve.getEndTime());
        verify(reserveRedisService, never()).createQueue("waiting", reserve.getEndTime());
        verify(reserveRedisService, never()).promoteFirstMember(any(), any(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void promotionJobMovesAndNotifiesWaitingUser() {
        Reserve reserve = activeReserve();
        when(reserveRepository.findActiveQueuesForPromotion(any(LocalDateTime.class))).thenReturn(List.of(reserve));
        when(reserveRedisService.promoteFirstMember("working", "waiting", 3))
                .thenReturn("waiting@test.com", (String) null);
        when(reserveRedisService.getSize("working")).thenReturn("1");
        when(reserveRedisService.getSize("waiting")).thenReturn("0");
        when(reserveRedisService.getMemberExpireAt("working", "waiting@test.com")).thenReturn(System.currentTimeMillis() + 600_000);
        when(jwtService.createReserveToken(9L, "waiting@test.com")).thenReturn("promoted-token");

        reserveService.promoteWaitingMembers();

        verify(reserveRedisService, org.mockito.Mockito.times(2)).promoteFirstMember("working", "waiting", 3);
        org.mockito.ArgumentCaptor<GetReserveQueueRes> response = org.mockito.ArgumentCaptor.forClass(GetReserveQueueRes.class);
        verify(messagingTemplate).convertAndSendToUser(eq("waiting@test.com"), eq("/queue/reserve/status"), response.capture());
        assertThat(response.getValue().getAccess()).isEqualTo(1);
        assertThat(response.getValue().getWtoken()).isEqualTo("promoted-token");
    }

    @Test
    void statusRequestFromAUserOutsideBothQueuesFailsCleanly() {
        Reserve reserve = activeReserve();
        GetReserveQueueReq request = new GetReserveQueueReq();
        request.setReserveIdx(9L);
        Principal principal = () -> "user@test.com";
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));
        when(reserveRedisService.getSize("working")).thenReturn("0");
        when(reserveRedisService.getSize("waiting")).thenReturn("0");
        when(reserveRedisService.getOrder("working", "user@test.com")).thenReturn(null);
        when(reserveRedisService.getOrder("waiting", "user@test.com")).thenReturn(null);

        reserveService.status(principal, request);
        org.mockito.ArgumentCaptor<GetReserveQueueRes> response = org.mockito.ArgumentCaptor.forClass(GetReserveQueueRes.class);
        verify(messagingTemplate).convertAndSendToUser(eq("user@test.com"), eq("/queue/reserve/status"), response.capture());
        assertThat(response.getValue().getAccess()).isEqualTo(4);
    }

    @Test
    void enrollmentBeforeTheConfiguredStartTimeIsRejected() {
        Reserve reserve = Reserve.builder()
                .idx(9L)
                .workingUUID("working")
                .waitingUUID("waiting")
                .totalPeople(3)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));

        assertThatThrownBy(() -> reserveService.enrollReserve(new MockHttpServletResponse(), user, 9L))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.RESERVE_ENROLL_FAIL_NOT_OPEN));
        verify(reserveRedisService, never()).enrollWithLock(
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyLong(), org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void waitingEnrollmentRequiresAnActualWaitingMembership() {
        Reserve reserve = activeReserve();
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));
        when(reserveRedisService.enrollWithLock(eq("working"), eq("waiting"),
                eq("user@test.com"), anyLong(), eq(3)))
                .thenReturn(EnrollResult.WAITING);
        when(reserveRedisService.getOrder("waiting", "user@test.com")).thenReturn(null);

        assertThatThrownBy(() -> reserveService.enrollReserve(new MockHttpServletResponse(), user, 9L))
                .isInstanceOfSatisfying(ServiceException.class, exception ->
                        assertThat(exception.getErrorCode()).isEqualTo(ServiceErrorCode.RESERVE_ACCESS_FAIL));
    }

    @Test
    void waitingEnrollmentReturnsTheStoredQueuePosition() {
        Reserve reserve = activeReserve();
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));
        when(reserveRedisService.enrollWithLock(eq("working"), eq("waiting"),
                eq("user@test.com"), anyLong(), eq(3)))
                .thenReturn(EnrollResult.WAITING);
        when(reserveRedisService.getOrder("waiting", "user@test.com")).thenReturn(1L);

        assertThat(reserveService.enrollReserve(new MockHttpServletResponse(), user, 9L)
                .getResponse()).contains("대기 순번: 2");
    }

    @Test
    void cancellingAWorkingUserPromotesAndNotifiesTheFirstWaitingUser() {
        Reserve reserve = activeReserve();
        when(reserveRepository.findById(9L)).thenReturn(Optional.of(reserve));
        when(reserveRedisService.getOrder("working", "user@test.com")).thenReturn(0L);
        when(reserveRedisService.promoteFirstMember("working", "waiting", 3))
                .thenReturn("waiting@test.com");
        when(jwtService.createReserveToken(9L, "waiting@test.com")).thenReturn("promoted-token");
        when(reserveRedisService.getSize("working")).thenReturn("3");
        when(reserveRedisService.getSize("waiting")).thenReturn("0");
        when(reserveRedisService.getOrder("working", "waiting@test.com")).thenReturn(2L);

        reserveService.cancelReserve(
                new MockHttpServletResponse(),
                user,
                9L
        );

        verify(reserveRedisService).remove("working", "user@test.com");
        verify(reserveRedisService).promoteFirstMember("working", "waiting", 3);
        verify(messagingTemplate).convertAndSendToUser(
                eq("waiting@test.com"),
                eq("/queue/reserve/status"),
                any(GetReserveQueueRes.class)
        );
    }

    @Test
    void reserveCreationCannotAllocateMorePeopleThanThePopupHas() {
        Popup popup = Popup.builder().idx(7L).totalPeople(2).companyEmail("user@test.com").build();
        CreateReserveReq request = CreateReserveReq.builder()
                .popupIdx(7L)
                .reservePeople(3)
                .reserveStartTime(LocalDateTime.now().plusDays(1))
                .reserveEndTime(LocalDateTime.now().plusDays(1).plusHours(1))
                .build();
        when(popupRepository.findForUpdate(7L)).thenReturn(Optional.of(popup));

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
                .startTime(LocalDateTime.now().minusMinutes(5))
                .endTime(LocalDateTime.now().plusHours(1))
                .popup(Popup.builder().idx(7L).name("팝업").status(PopupStatus.POPUP_START).build())
                .build();
    }
}
