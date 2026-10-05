<template>
  <div class="ctn-list1">
    <div class="ctn-listinfo1">

      <button class="btn-tagdefault">
        <Icon icon="iconoir:user" class="img-iconior"/>
        <span>{{ reserve.reservePeople }}명</span>
      </button>

      <button class="btn-tagdefault">
        <Icon icon="iconoir:calendar-plus" class="img-iconior"/>
        <span>{{ reserve.reserveStartDate }} </span>
      </button>

      <button class="btn-tagdefault">
        <Icon icon="iconoir:clock" class="img-iconior"/>
        <span>{{ formatTime(reserve.reserveStartTime) }} ~ {{ formatTime(reserve.reserveEndTime) }}</span>
      </button>

      <button class="btn-tagdefault">
        <CountDownTimer :targetTime="reserve.reserveStartTime" :flag="false"></CountDownTimer>
      </button>
        
    </div>
    
    <div v-if="showControl === 0" class="ctn-listbuttons">
      <button
        class="btn-tagaction"
        type="button"
        aria-label="예약 참여"
        title="예약 참여"
        :disabled="!isReserveActive(reserve)"
        :aria-disabled="!isReserveActive(reserve)"
        @click="goReserve"
      >
        <Icon icon="iconoir:bell" class="img-iconior"/>
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/authStore";
import CountDownTimer from "@/components/CountDownTimer.vue";

const router = useRouter();
const toast = useToast();
const authStore = useAuthStore();

// props 정의(reserve, showControl)
const props = defineProps({
  reserve: Object,
  showControl: Boolean,
});

// 문자열을 자르기: 'T' 이후 부분 가져오기
function formatTime(dateTimeString) {
  if (!dateTimeString) return "";
  const timePart = dateTimeString.split("T")[1];
  return timePart ? timePart.slice(0, 5) : "";
}

const goReserve = () => {
  if (!isReserveActive(props.reserve)) {
    toast.info("현재 진행 중인 예약이 아닙니다.");
    return;
  }
  if (!authStore.isLoggedIn) {
    toast.error("로그인이 필요합니다.");
    return;
  }
  router.push(`/reserve/${props.reserve.popupIdx}/${props.reserve.reserveIdx}`);
}

const isReserveActive = (reserve) => {
  const start = new Date(reserve?.reserveStartTime).getTime();
  const end = new Date(reserve?.reserveEndTime).getTime();
  const now = Date.now();
  return Number.isFinite(start) && Number.isFinite(end) && now >= start && now < end;
};

</script>
