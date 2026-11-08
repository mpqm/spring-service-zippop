<template>
  <span :class="flag ? 'countdown-timer' : 'countdown-timer2'">{{ countdownText }}</span>
</template>

<script setup>
import { computed, defineProps } from 'vue';
import { useNow } from '@/common';

const props = defineProps({
  targetTime: { type: String, default: '' },
  startTime: { type: String, default: '' },
  endTime: { type: String, default: '' },
  flag: { type: Boolean, default: false },
});

const now = useNow();

const countdownText = computed(() => {
  let label = '종료까지';
  let target;

  if (props.startTime && props.endTime) {
    const start = new Date(props.startTime).getTime();
    const end = new Date(props.endTime).getTime();
    if (!Number.isFinite(start) || !Number.isFinite(end) || end <= start) return '예약 시간 정보 오류';
    if (now.value < start) {
      label = '시작까지';
      target = start;
    } else if (now.value < end) {
      target = end;
    } else {
      return '예약 종료';
    }
  } else {
    const value = /^\d{4}-\d{2}-\d{2}$/.test(props.targetTime)
      ? `${props.targetTime}T23:59:59.999`
      : props.targetTime;
    target = new Date(value).getTime();
    if (!Number.isFinite(target)) return '시간 정보 없음';
    if (now.value >= target) return '종료';
  }

  const totalSeconds = Math.ceil((target - now.value) / 1000);
  const days = Math.floor(totalSeconds / 86400);
  const hours = Math.floor((totalSeconds % 86400) / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;
  return `${label} ${days}일 ${hours}시간 ${minutes}분 ${seconds}초`;
});
</script>
