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
      <button class="btn-default" @click="goReserve"><Icon icon="iconoir:bell" class="img-iconior"/>예약 참여</button>
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

// 문자열을 자르기: 'T' 이후 부분 가져오기
function formatTime(dateTimeString) {
  if (!dateTimeString) return "";
  const timePart = dateTimeString.split("T")[1];
  return timePart ? timePart.slice(0, 5) : "";
}

const goReserve = () => {
  router.push(`/reserve/${props.reserve.storeIdx}/${props.reserve.reserveIdx}`);
}

</script>