<template>
  <div class="countdown-timer">
    종료까지 남은 시간: {{ days }}일 {{ hours }}시간 {{ minutes }}분 {{ seconds }}초
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { defineProps } from "vue";

// Props 정의
const props = defineProps({
  targetTime: String, // 문자열로 된 목표 시간 (예: "2024-11-21")
});

// 남은 시간을 표시할 ref 변수
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
  calculateTimeDifference(); // 초기 시간 계산
  intervalId.value = setInterval(calculateTimeDifference, 1000);
});

// 컴포넌트가 언마운트될 때 타이머 정리
onUnmounted(() => {
  clearInterval(intervalId.value);
});

</script>

<style scoped>
.countdown-timer {
  font-size: 1.2rem;
  color: #e63946;
  font-weight: bold;
}
</style>
