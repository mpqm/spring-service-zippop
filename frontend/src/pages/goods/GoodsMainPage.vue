<template>
  <div class="lyt-page">
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <section class="ctn-discover">
        <div class="ctn-sectionheading">
          <div>
            <span class="txt-eyebrow">STOCK MARKET</span>
            <h2>남은 재고 굿즈</h2>
          </div>
          <span class="txt-sectionhint">팝업 스토어 기간이 끝나고 남은 재고 굿즈를 구매해보세요!</span>
        </div>
        <div class="ctn-inputsearch">
          <input class="ipt-default" v-model="searchQuery" type="text" aria-label="재고 마켓 검색" placeholder="브랜드, 지역, 카테고리로 검색" @keyup.enter="getPopups()" />
          <button class="btn-default" type="button" aria-label="검색" @click="getPopups()"><Icon icon="ic:search" width="20px" height="20px" /></button>
        </div>
      </section>
      <div class="lyt-cardgrid" v-if="popupList && popupList.length">
        <PopupCard v-for="popup in popupList" :key="popup.popupIdx" :popup="popup" :redirectToGoodsDetail="true" />
      </div>
      <AppEmptyState
        v-else
        title="검색 결과가 없습니다"
        description="다른 키워드로 남은 재고가 있는 팝업을 찾아보세요."
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

const searchQuery = ref("");
const popupList = ref([]);
const currentPage = ref(0);
const pageSize = ref(12);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

onMounted(async () => {
  await getPopups();
});

const getPopups = async (reset = false) => {
  if (reset) {
    currentPage.value = 0;
    searchQuery.value = "";
  }
  const res = await popupStore.getPopups("POPUP_END", searchQuery.value, currentPage.value, pageSize.value);
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
