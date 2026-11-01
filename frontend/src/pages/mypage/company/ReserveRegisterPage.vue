<template>
  <div class="lyt-child">
    <form class="ctn-rootform ctn-reviewform ctn-reserveform" @submit.prevent="createReserve">
      <div class="ctn-reviewheading">
        <div>
          <span class="txt-eyebrow">RESERVATION SCHEDULE</span>
          <h2>팝업 스토어 예약 등록</h2>
          <p>방문 가능한 날짜와 시간, 회차별 예약 인원을 설정해주세요.</p>
        </div>
        <div class="ctn-buttons">
          <button type="button" class="btn-normal btn-reviewsubmit" @click="router.back()">취소</button>
          <button type="submit" class="btn-default btn-reviewsubmit">등록</button>
        </div>
      </div>

      <div class="ctn-inputdefault">
        <label class="ipt-default-label" for="reserve-people">예약 가능 인원</label>
        <input id="reserve-people" class="ipt-default" v-model="reservePeople" type="number" min="1" placeholder="예약 가능 인원을 입력해주세요." required />
      </div>

      <div class="ctn-inputdefault">
        <label class="ipt-default-label" for="reserve-start-datetime">예약 시작 날짜</label>
        <input id="reserve-start-datetime" class="ipt-default" v-model="reserveStartDateTime" type="datetime-local" required />
      </div>

      <div class="ctn-inputdefault">
        <div class="ctn-labelwithhelp">
          <label class="ipt-default-label" for="reserve-end-datetime">예약 종료 날짜</label>
          <span class="txt-formhelp">종료 시간은 시작 시간보다 늦게 설정해주세요.</span>
        </div>
        <input id="reserve-end-datetime" class="ipt-default" v-model="reserveEndDateTime" type="datetime-local" required />
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useReserveStore } from "@/stores/reserveStore";

const reserveStore = useReserveStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();
const reservePeople = ref(1);
const reserveStartDateTime = ref("");
const reserveEndDateTime = ref("");

const createReserve = async () => {
  if (!reserveStartDateTime.value || !reserveEndDateTime.value) {
    toast.error("모든 예약 정보를 입력해주세요.");
    return;
  }
  if (Number(reservePeople.value) < 1) {
    toast.error("예약 인원은 1명 이상이어야 합니다.");
    return;
  }
  if (new Date(reserveEndDateTime.value).getTime() <= new Date(reserveStartDateTime.value).getTime()) {
    toast.error("종료 시간은 시작 시간보다 늦어야 합니다.");
    return;
  }

  const req = {
    popupIdx: Number(route.params.popupIdx),
    reservePeople: Number(reservePeople.value),
    reserveStartDate: reserveStartDateTime.value.split('T')[0],
    reserveStartTime: `${reserveStartDateTime.value}:00`,
    reserveEndTime: `${reserveEndDateTime.value}:00`,
  };
  const res = await reserveStore.createReserve(req);
  if (res.success) {
    toast.success(res.message);
    router.push(`/mypage/company/reserve/${route.params.popupIdx}`);
  } else {
    toast.error(res.message);
  }
};
</script>
