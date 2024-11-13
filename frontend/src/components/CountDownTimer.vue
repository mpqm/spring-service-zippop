<template>
    <div class="countdown-timer">
      남은 시간: {{ days }}일 {{ hours }}시간 {{ minutes }}분 {{ seconds }}초
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted, onUnmounted } from "vue";
  
  // 타겟 시간 설정 (예: 2024년 12월 31일 23:59:59)
  const targetTime = new Date("2024-12-31T23:59:59").getTime();
  
  // 남은 시간을 표시할 ref 변수들
  const days = ref(0);
  const hours = ref(0);
  const minutes = ref(0);
  const seconds = ref(0);
  
  const calculateTimeDifference = () => {
    const currentTime = Date.now();
    const diff = targetTime - currentTime;
    days.value = Math.floor(diff / (1000 * 60 * 60 * 24));
    hours.value = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    minutes.value = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    seconds.value = Math.floor((diff % (1000 * 60)) / 1000);
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
  
  const intervalId = ref(null);
  </script>
  
  <style scoped>
  .countdown-timer {
    font-size: 1.2rem;
    color: #e63946;
    font-weight: bold;
  }
  </style>
  