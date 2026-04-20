<template>
  <div>
    <div class="lyt-child">
      <div class="ctn-split">
        <div></div>
        <div class="ctn-buttons">
          <button class="btn-normal" @click="router.back()">
            <Icon icon="iconoir:nav-arrow-left" width="20px" height="20px" />
          </button>
          <router-link class="btn-default" :to="`/mypage/company/reserve/register/${route.params.popupIdx}`">
            <Icon icon="iconoir:add-square" width="20px" height="20px" style="color: #ffffff" />예약 등록
          </router-link>
        </div>
      </div>
      <div class="ctn-table" v-if="reserveList && reserveList.length">
        <ReserveTable :reserves="reserveList" :showControl="showControl" />
      </div>
      <div class="txt-null" v-else>
        <p>등록된 팝업 예약이 없습니다.</p>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import ReserveTable from "@/components/ReserveTable.vue";
import AppPagination from "@/components/AppPagination.vue";
import { useRouter, useRoute } from "vue-router";
import { onMounted, ref } from "vue";
import { useReserveStore } from "@/stores/reserveStore";
import { Icon } from '@iconify/vue';

const reserveStore = useReserveStore();
const router = useRouter();
const route = useRoute();

const reserveList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const showControl = ref(true);

onMounted(async () => {
  await getCompanyPopupReserves();
});

const getCompanyPopupReserves = async () => {
  const res = await reserveStore.getCompanyPopupReserves(route.params.popupIdx, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = reserveStore.totalElements;
    totalPages.value = reserveStore.totalPages;
    reserveList.value = reserveStore.reserveList;
    hideBtns.value = false;
  } else {
    reserveList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const changePage = async (newPage) => {
  if (newPage >= 0) {
    currentPage.value = newPage;
    await getCompanyPopupReserves();
  }
};
</script>
