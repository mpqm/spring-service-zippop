<template>
  <div>
    <div class="reserve-management-page">
      <div class="reserve-control">
        <div>

        </div>
        <div class="btn-container">
          <router-link class="register-btn" :to="`/mypage/company/reserve`">&lt;</router-link>
          <router-link class="back-btn" :to="`/mypage/company/reserve/register/${route.params.storeIdx}`">예약 등록</router-link>
        </div>
      </div>
      <div class="reserve-list" v-if="reserveList && reserveList.length">
        <ReserveListComponent v-for="reserve in reserveList" :key="reserve.reserveIdx" :reserve="reserve" :showControl="false" />
      </div>
      <div class="notice" v-else> <p>등록된 팝업 예약이 없습니다.</p> </div>
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

<style scoped>
.reserve-management-page {
  flex-direction: row;
  width: 65rem;
}

.reserve-control {
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

.reserve-list {
  width: auto;
  padding: 5px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.reserve-register-btn {
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

.reserve-control {
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