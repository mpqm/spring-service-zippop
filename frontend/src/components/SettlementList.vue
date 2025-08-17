<template>
  <div class="list-container">
      <p> 정산 날짜 : {{ settlement.settlementDate }} </p>
      <p> 일별 수익 : {{ settlement.totalRevenue }} </p>
  </div>
</template>
  
<script setup>
import { defineProps } from "vue";

// props 정의(settlement, showControl)
defineProps({
  settlement: Object,
});
</script>