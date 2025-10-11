<template>
  <button class="countdown-timer" v-if="flag">
    종료까지 {{ days }}일 {{ hours }}시간 {{ minutes }}분 {{ seconds }}초
  </button>
  <div class="countdown-timer2" v-if="flag==false">
    {{ days }}일 {{ hours }}시간 {{ minutes }}분 {{ seconds }}초
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { defineProps } from "vue";

const props = defineProps({
  targetTime: String,
  flag: Boolean,
});
const days = ref(0);
const hours = ref(0);
const minutes = ref(0);
const seconds = ref(0);
const intervalId = ref(null);

// 시간 차이 계산
const calculateTimeDifference = () => {
  const targetDate = new Date(props.targetTime); // 문자열을 Date 객체로 변환
  const currentTime = Date.now(); // 현재 시간 (밀리초)
  const diff = targetDate - currentTime; // 시간 차이 계산

  if (diff > 0) {
    days.value = Math.floor(diff / (1000 * 60 * 60 * 24));
    hours.value = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    minutes.value = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    seconds.value = Math.floor((diff % (1000 * 60)) / 1000);
  } else {
    // 남은 시간이 없으면 모두 0으로 설정
    days.value = 0;
    hours.value = 0;
    minutes.value = 0;
    seconds.value = 0;
    clearInterval(intervalId.value); // 타이머 중지
  }
};

// 컴포넌트가 마운트되면 1초마다 시간을 업데이트
onMounted(() => {
  calculateTimeDifference();
  intervalId.value = setInterval(calculateTimeDifference, 1000);
});

// 컴포넌트가 언마운트될 때 타이머 정리
onUnmounted(() => {
  clearInterval(intervalId.value);
});

</script>

<style scoped>
.countdown-timer {
    font-weight: bold;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 0.3rem 0.75rem;
    font-size: 0.85rem;
    font-weight: 500;
    color: #e63946;
    background-color: #f0fffc;
    border: 1px solid #00c7ae;
    border-radius: 9999px; /* pill 모양 */
    cursor: pointer;
    transition: all 0.25s ease-in-out;
    white-space: nowrap;
    gap: 4px;
    margin: 0;
    max-height: 30px;
}

.countdown-timer2 {
    font-size: 12px;
    color: #e63946;
    font-weight: bold;
}
</style>