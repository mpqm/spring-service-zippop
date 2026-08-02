<template>
  <div class="lyt-page">
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <h2 class="txt-maintitle txt-pagetitle">팝업 스토어 예약 일정을 확인하세요</h2>
      <div class="ctn-inputsearch">
        <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="getPopups()" />
        <button class="btn-default" @click="getPopups()"><Icon icon="ic:search" width="20px" height="20px" /></button>
        <button class="btn-normal" @click="getPopups(true)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
      </div>
      <div class="lyt-cardgrid" v-if="popupList && popupList.length">
        <PopupCard v-for="popup in popupList" :key="popup.popupIdx" :popup="popup" main-tab="reserve" />
      </div>
      <AppEmptyState
        v-else
        title="예약 가능한 팝업이 없습니다"
        description="검색어를 바꾸거나 전체 예약 일정을 다시 확인해 보세요."
        action-label="검색 초기화"
        @action="getPopups(true)"
      />
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
    <AppFooter></AppFooter>
  </div>
</template>

<script setup>
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import PopupCard from "@/components/PopupCard.vue";
import AppPagination from "@/components/AppPagination.vue";
import { usePopupStore } from "@/stores/popupStore";
import { onMounted, ref } from "vue";
import { Icon } from "@iconify/vue";

const popupStore = usePopupStore();

const popupList = ref([]);
const currentPage = ref(0);
const pageSize = ref(12);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const searchQuery = ref("");

onMounted(async () => {
  await getPopups();
});

const getPopups = async (reset = false) => {
  if (reset) {
    currentPage.value = 0;
    searchQuery.value = "";
  }
  const res = await popupStore.getPopups("POPUP_START", searchQuery.value, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = popupStore.totalElements;
    totalPages.value = popupStore.totalPages;
    popupList.value = popupStore.popupList;
    hideBtns.value = false;
  } else {
    popupList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const changePage = async (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return;
  currentPage.value = newPage;
  await getPopups();
};
</script>
