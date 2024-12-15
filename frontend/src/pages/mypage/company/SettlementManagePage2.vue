<template>
    <div>
      <div class="settlement-management-page">
        <div class="total-sum">
        <p>총 판매 수익: {{ totalRevenueSum }}원</p>
      </div>
        <div class="settlement-list" v-if="settlementList && settlementList.length">
          <SettlementComponent v-for="settlement in settlementList" :key="settlement.settlementIdx" :settlement="settlement" :showControl="false" />
        </div>
        <div class="notice" v-else> <p>등록된 팝업 예약이 없습니다.</p> </div>
        <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>
    </div>
  </template>
  
  <script setup>
import SettlementComponent from "@/components/settlement/SettlementComponent.vue";
  import PaginationComponent from "@/components/common/PaginationComponent.vue";
  import { computed, onMounted, ref } from "vue";
  import { useSettlementStore } from "@/stores/useSettlementStore";
  import { useRoute } from "vue-router";
  
  // settlement, router, route, toast
  const settlementStore = useSettlementStore();
  const route = useRoute();
  
  // 변수(settlement)
  const settlementList = ref([]);
  const currentPage = ref(0);
  const pageSize = ref(8);
  const totalElements = ref(0);
  const totalPages = ref(0);
  const hideBtns = ref(false);
  
  // onMounted
  onMounted(async () => {
    await searchAll();
  });
  
  const totalRevenueSum = computed(() => {
  return settlementList.value.reduce((sum, item) => {
    return sum + (item.totalRevenue || 0); // totalRevenue가 없을 경우 0으로 처리
  }, 0);
});

  // 예약 목록 조회
  const searchAll = async () => {
    const res = await settlementStore.searchAllSettlement(route.params.storeIdx, currentPage.value, pageSize.value);
    if (res.success) {
      totalElements.value = settlementStore.totalElements;
      totalPages.value = settlementStore.totalPages;
      settlementList.value = settlementStore.settlementList;
      hideBtns.value = false;
    } else {
      settlementList.value = null;
      totalElements.value = 0;
      totalPages.value = 0;
      hideBtns.value = true;
    }
  };
  
  // 페이지 네이션
  const changePage = async (newPage) => {
      if (newPage >= 0) {
          currentPage.value = newPage;
          await searchAll(currentPage.value, pageSize.value);
      }
  };
  
  </script>
  
  <style scoped>
  .settlement-management-page {
    flex-direction: row;
    width: 65rem;
  }
  
  .settlement-control {
    padding: 5px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .btn-container {
    display: flex;
    gap: 10px;
    justify-content: center;
  }
  
  .notice {
    text-align: center;
  }
  
  .settlement-list {
    width: auto;
    padding: 5px;
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .settlement-register-btn {
    display: block;
    text-align: center;
    width: auto;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.5rem;
    border-radius: 0.25rem;
    text-decoration: #000;
  }
  
  .search-container {
    display: flex;
    gap: 10px;
    justify-content: center;
  }
  
  .settlement-control {
    padding: 5px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .back-btn {
    display: block;
    text-align: center;
    width: auto;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.5rem;
    border-radius: 0.25rem;
    text-decoration: #000;
  }
  
  .btn-container {
    display: flex;
    gap: 10px;
    justify-content: center;
  }
  
  .register-btn {
    display: block;
    text-align: center;
    width: auto;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.5rem;
    border-radius: 0.25rem;
    text-decoration: #000;
  }
  
  .back-btn:hover,
  .register-btn:hover {
    opacity: 0.8;
  }
  </style>