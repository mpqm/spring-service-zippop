<template>
  <div>
    <div class="management-page">
      <div class="two-section-container">
        <div>

        </div>
        <div class="btn-container">
          <router-link class="default-btn" :to="`/mypage/company/reserve`">
            <Icon icon="iconoir:nav-arrow-left" width="20px" height="20px"  style="color: #ffffff" />
          </router-link>
          <router-link class="default-btn" :to="`/mypage/company/reserve/register/${route.params.storeIdx}`">
            <Icon icon="iconoir:add-square" width="20px" height="20px"  style="color: #ffffff" />예약 등록
          </router-link>
        </div>
      </div>
      <div class="list-container" v-if="reserveList && reserveList.length">
        <ReserveListComponent v-for="reserve in reserveList" :key="reserve.reserveIdx" :reserve="reserve" :showControl=1 />
      </div>
      <div class="empty-string" v-else> <p>등록된 팝업 예약이 없습니다.</p> </div>
      <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import ReserveListComponent from "@/components/reserve/ReserveListComponent.vue";
import PaginationComponent from "@/components/common/PaginationComponent.vue";
import { onMounted, ref } from "vue";
import { useReserveStore } from "@/stores/useReserveStore";
import { useRoute } from "vue-router";

// reserve, router, route, toast
const reserveStore = useReserveStore();
const route = useRoute();

// 변수(reserve)
const reserveList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

// onMounted
onMounted(async () => {
  await searchAll();
});

// 예약 목록 조회
const searchAll = async () => {
  const res = await reserveStore.searchAllReserveAsCompany(route.params.storeIdx, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = reserveStore.totalElements;
    totalPages.value = reserveStore.totalPages;
    reserveList.value = reserveStore.reserveList;
    hideBtns.value = false;
  } else {
    reserveList.value = null;
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