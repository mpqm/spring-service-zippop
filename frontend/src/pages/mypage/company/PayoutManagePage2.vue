<template>
    <div>
      <div class="lyt-child">
        <div class="ctn-split">
          <button class="btn-tagdefault">총 판매 수익: {{ totalRevenueSum }}원</button>
          <div class="ctn-buttons">
            <button class="btn-normal" @click="router.back()">
              <Icon icon="iconoir:nav-arrow-left" width="20px" height="20px" />
            </button>
          </div>
        </div>
 
        <div v-if="payout && payout.length">
          <PayoutTable :payout="payout" />
        </div>
        <div class="txt-null" v-else>
          <p>정산 내역이 없습니다.</p>
        </div>
        <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>
    </div>
  </template>
  
  <script setup>
  import PayoutTable from "@/components/PayoutTable.vue";
  import AppPagination from "@/components/AppPagination.vue";
  import { computed, onMounted, ref } from "vue";
  import { useRouter } from "vue-router";
  import { usePayoutStore } from "@/stores/usePayoutStore";
  import { useRoute } from "vue-router";
  
  // payout, router, route, toast
  const payoutStore = usePayoutStore();
  const router = useRouter();
  const route = useRoute();
  
  // 변수(payout)
  const payout = ref([]);
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
  return payout.value.reduce((sum, item) => {
    return sum + (item.totalRevenue || 0); // totalRevenue가 없을 경우 0으로 처리
  }, 0);
});

  // 예약 목록 조회
  const searchAll = async () => {
    const res = await payoutStore.searchAllPayout(route.params.storeIdx, currentPage.value, pageSize.value);
    if (res.success) {
      totalElements.value = payoutStore.totalElements;
      totalPages.value = payoutStore.totalPages;
      payout.value = payoutStore.payout;
      hideBtns.value = false;
    } else {
      payout.value = null;
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
