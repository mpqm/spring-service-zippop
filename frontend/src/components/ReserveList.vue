<template>
  <div class="ctn-list1">
    <div class="ctn-listinfo1">
      <Icon icon="iconoir:user" width="20px" height="20px" style="color: #00c7ae" />
      <span>{{ reserve.reservePeople }}명</span>
      <Icon icon="iconoir:calendar-plus" width="20px" height="20px" style="color: #00c7ae" />
      <span>{{ reserve.reserveStartDate }} </span>
      <Icon icon="iconoir:clock" width="20px" height="20px" style="color: #00c7ae" />
      <span>{{ formatTime(reserve.reserveStartTime) }} ~ {{ formatTime(reserve.reserveEndTime) }}</span>
      <CountDownTimer :targetTime="reserve.reserveStartTime" :flag="false"></CountDownTimer>
    </div>
    <div v-if="showControl === 0" class="ctn-listbuttons">
      <button class="btn-default" @click="goReserve">
        <Icon icon="iconoir:bell" width="24" height="24" />
        예약 참여
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useRouter } from "vue-router";
import CountDownTimer from "@/components/CountDownTimer.vue";

const router = useRouter();

// props 정의(reserve, showControl)
const props = defineProps({
  reserve: Object,
  showControl: Boolean,
});

function formatTime(dateTimeString) {
  if (!dateTimeString) return "";
  // 문자열을 자르기: 'T' 이후 부분 가져오기
  const timePart = dateTimeString.split("T")[1];
  return timePart ? timePart.slice(0, 5) : "";
}

const goReserve = () => {
  router.push(`/reserve/${props.reserve.storeIdx}/${props.reserve.reserveIdx}`);
}

</script>