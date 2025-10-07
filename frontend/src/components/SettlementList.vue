<template>
  <div class="list-container">
      <p> 정산 날짜 : {{ payout.payoutDate }} </p>
      <p> 일별 수익 : {{ payout.totalRevenue }} </p>
  </div>
</template>
  
<script setup>
import { defineProps } from "vue";

// props 정의(payout, showControl)
defineProps({
  payout: Object,
});
</script>