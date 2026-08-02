<template>
  <div>
    <AppHeader></AppHeader>
    <main class="lyt-root lyt-home">
      <section class="ctn-hero">
        <div class="ctn-herocopy">
          <span class="txt-eyebrow">POP-UP, WITHOUT THE WAIT</span>
          <h1 class="txt-maintitle">기다림은 줄이고,<br>팝업의 기회는 <em>더 길게.</em></h1>
          <p class="txt-herodesc">진행 중인 팝업은 사전 예약과 굿즈 선구매로 편하게 방문하고, 종료된 팝업의 남은 재고는 온라인에서 다시 만나보세요.</p>
        </div>
        <div class="ctn-herobadge" aria-hidden="true">
          <span>WAIT</span><strong>LESS!</strong>
        </div>
      </section>
      <section class="ctn-discover">
        <div class="ctn-sectionheading">
          <div><span class="txt-eyebrow">OPEN NOW</span><h2>지금 열려 있는 팝업</h2></div>
          <span class="txt-sectionhint">예약부터 굿즈 구매까지 한 번에</span>
        </div>
        <div class="ctn-inputsearch">
          <input class="ipt-default" v-model="searchQuery" type="text" aria-label="팝업 검색" placeholder="브랜드, 지역, 카테고리로 검색" @keyup.enter="getPopups()" />
          <button class="btn-default" @click="getPopups()"><Icon icon="ic:search" width="20px" height="20px" /></button>
          <button class="btn-normal btn-reset" aria-label="검색 초기화" @click="getPopups(true)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
        </div>
      </section>
      <div class="lyt-cardgrid" v-if="popupList && popupList.length">
        <PopupCard v-for="popup in popupList" :key="popup.popupIdx" :popup="popup" />
      </div>
      <div class="ctn-empty" v-else>
        <Icon icon="iconoir:search-window" width="42px" height="42px" />
        <p>검색 결과에 해당하는 팝업 스토어가 없습니다.</p>
        <span>다른 키워드로 다시 찾아보세요.</span>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </main>
    <AppFooter></AppFooter>
  </div>
</template>

<script setup>
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import PopupCard from "@/components/PopupCard.vue";
import AppPagination from "@/components/AppPagination.vue";
import { usePopupStore } from "@/stores/popupStore";
import { useAuthStore } from "@/stores/authStore";
import { onMounted, ref } from "vue";
import { Icon } from "@iconify/vue";

const popupStore = usePopupStore();
const authStore = useAuthStore();

const searchQuery = ref("");
const popupList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

onMounted(async () => {
  if (authStore.isLoggedIn) {
    await popupStore.getMyLikedPopups(0, 100);
  }
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
