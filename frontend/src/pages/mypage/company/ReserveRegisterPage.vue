<template>
  <div class="lyt-child">
    <form class="ctn-rootform" @submit.prevent="createReserve">
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
import { useReserveStore } from "@/stores/reserveStore";
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";

const reserveStore = useReserveStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

const reservePeople = ref(1);
const reserveStartDate = ref("");
const reserveStartTime = ref("");
const reserveEndTime = ref("");

const createReserve = async () => {
  if (!reserveStartDate.value || !reserveStartTime.value || !reserveEndTime.value) {
    toast.error("모든 필드를 입력해주세요.");
    return;
  }
  if (Number(reservePeople.value) <= 0) {
    toast.error("예약 인원은 1명 이상이어야 합니다.");
    return;
  }
  if (reserveEndTime.value <= reserveStartTime.value) {
    toast.error("종료 시간은 시작 시간보다 늦어야 합니다.");
    return;
  }
  const req = {
    popupIdx: Number(route.params.popupIdx),
    reservePeople: Number(reservePeople.value),
    reserveStartDate: reserveStartDate.value,
    reserveStartTime: `${reserveStartDate.value}T${reserveStartTime.value}:00`,
    reserveEndTime: `${reserveStartDate.value}T${reserveEndTime.value}:00`,
  };
  const res = await reserveStore.createReserve(req);
  if (res.success) {
    router.push(`/mypage/company/reserve/${route.params.popupIdx}`);
    toast.success(res.message);
  } else {
    toast.error(res.message);
  }
};
</script>
