<template>
  <div>
    <div class="lyt-child">
      <div class="ctn-split">
        <div></div>
        <div class="ctn-buttons">
          <button class="btn-normal" @click="router.back()">
            <Icon icon="iconoir:nav-arrow-left" width="20px" height="20px" />
          </button>
          <router-link class="btn-default" :to="`/mypage/company/reserve/register/${route.params.storeIdx}`">
            <Icon icon="iconoir:add-square" width="20px" height="20px"  style="color: #ffffff" />예약 등록
          </router-link>
        </div>
      </div>
      <div class="ctn-table" v-if="reserveList && reserveList.length">
        <ReserveTable :reserves="reserveList" :showControl="showControl" />
      </div>
      <div class="txt-null" v-else> <p>등록된 팝업 예약이 없습니다.</p> </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import ReserveTable from "@/components/ReserveTable.vue";
import AppPagination from "@/components/AppPagination.vue";
import { useRouter } from "vue-router";
import { onMounted, ref } from "vue";
import { useReserveStore } from "@/stores/useReserveStore";
import { useRoute } from "vue-router";

// reserve, router, route, toast
const reserveStore = useReserveStore(); 
const router = useRouter();
const route = useRoute();

// 변수(reserve)
const reserveList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const showControl = ref(true);

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