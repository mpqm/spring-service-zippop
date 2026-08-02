<template>
  <div class="ctn-table">
    <EasyDataTable
      :headers="headers"
      :items="props.payout"
      :sort-by="sortBy"
      :sort-type="sortType"
      class="customize-table"
      table-class-name="customize-table"
      header-text-direction="center"
      body-text-direction="center"
      alternating
      border-cell
      hide-footer
    >
      <template #item-payoutDate="item">
        <span>{{ item.payoutDate }}</span>
      </template>
      
      <template #item-revenue="item">
        <span>{{ item.revenue }}원</span>
      </template>
    </EasyDataTable>
  </div>
</template>
  
<script setup>
import { ref, defineProps } from "vue";
import EasyDataTable from 'vue3-easy-data-table';
import 'vue3-easy-data-table/dist/style.css';

// props 정의
const props = defineProps({
  payout: {
    type: Array,
    default: () => []
  }
});

// 반응형 데이터
const sortBy = ref('payoutDate');
const sortType = ref('desc');

// 테이블 헤더 정의
const headers = ref([
  { text: '정산일', value: 'payoutDate', sortable: true },
  { text: '정산금액', value: 'revenue', sortable: true }
]);
</script>
