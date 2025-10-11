<template>
    <div class="lyt-child">
      <form class="ctn-rootform" @submit.prevent="register">
        <div class="ctn-split">
          <h1 class="txt-def0">팝업 스토어 예약 등록</h1>
          <div class="ctn-buttons">
            <button type="submit" class="btn-default">등록</button>
            <button type="button" class="btn-normal" @click="router.back()">취소</button>
          </div>
        </div>
        
        <div class="ctn-inputdefault">
          <label class="ipt-default-label">팝업 스토어 예약 인원</label>
          <input class="ipt-default" v-model="reservePeople" type="number" min="1" placeholder="팝업 스토어 예약 인원을 입력해주세요." />
        </div>

        <div class="ctn-inputdefault">
          <label class="ipt-default-label">팝업스토어 예약 시작일</label>
          <input class="ipt-default" v-model="reserveStartDate" type="date" placeholder="시작일" />
        </div>
        
        <div class="ctn-inputdefault">
          <label class="ipt-default-label">팝업스토어 예약 시작/종료시간</label>
          <div class="ctn-split">
            <input class="ipt-default" v-model="reserveStartTime" type="time" placeholder="시작시간" />
            <input class="ipt-default" v-model="reserveEndTime" type="time" placeholder="종료시간" />
          </div>
        </div>
      </form>
    </div>
  </template>
  
  <script setup>
  import { useReserveStore } from "@/stores/useReserveStore";
import { ref } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import { useToast } from "vue-toastification";
  
  // reserve, router, route, toast
  const reserveStore = useReserveStore()
  const router = useRouter();
  const route = useRoute();
  const toast = useToast();
  
  // 변수(reserve)
  const reservePeople = ref(1);
  const reserveStartDate = ref("");
  const reserveStartTime = ref("");
  const reserveEndTime = ref("");

  // 스토어 등록
  const register = async () => {
    // 입력 값 검증
    if (!reserveStartDate.value || !reserveStartTime.value || !reserveEndTime.value) {
      toast.error("모든 필드를 입력해주세요.");
      return;
    }

    if (Number(reservePeople.value) <= 0) {
      toast.error("예약 인원은 1명 이상이어야 합니다.");
      return;
    }

    // 종료 시간이 시작 시간보다 늦은지 확인
    if (reserveEndTime.value <= reserveStartTime.value) {
      toast.error("종료 시간은 시작 시간보다 늦어야 합니다.");
      return;
    }

    // 날짜와 시간을 ISO 형식으로 변환 (Spring Boot가 자동 파싱)
    const req = {
        storeIdx: Number(route.params.storeIdx),
        reservePeople: Number(reservePeople.value),
        reserveStartDate: reserveStartDate.value,
        reserveStartTime: `${reserveStartDate.value}T${reserveStartTime.value}:00`,
        reserveEndTime: `${reserveStartDate.value}T${reserveEndTime.value}:00`
    };
    const res = await reserveStore.register(req);
    if (res.success) {
      router.push(`/mypage/company/reserve/${route.params.storeIdx}`);
      toast.success(res.message);
    } else {
      toast.error(res.message);
    }
  };
  
  </script>
  