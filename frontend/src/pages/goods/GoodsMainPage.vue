<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <h2 class="txt-maintitle">팝업 스토어 예약이 끝나고 남은 재고 굿즈를 구매해보세요!</h2>
      <div class="ctn-inputsearch">
        <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="searchAllByKeyword" />
        
        <button class="btn-default" @click="searchAllByKeyword"><Icon icon="ic:search" width="20px" height="20px" /></button>

        <button class="btn-normal" @click="searchAll(0)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
      
      </div>
      <div class="lyt-cardgrid" v-if="storeList && storeList.length">
        <StoreCard v-for="store in storeList" :key="store.storeIdx" :store="store" :redirecToGoodsDetail="true" />
      </div>
      <div v-else>
        <p>검색 결과에 해당하는 팝업 스토어 목록이 없습니다.</p>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
    <AppFooter></AppFooter>
  </div>
</template>
<script setup>
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import StoreCard from "@/components/StoreCard.vue";
import AppPagination from "@/components/AppPagination.vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useAuthStore } from "@/stores/useAuthStore";
import { onMounted, ref } from "vue";

// store, router, route, toast
const storeStore = useStoreStore();
const authStore = useAuthStore();

// 변수(store)
const searchQuery = ref("");
const storeList = ref([]);
const currentPage = ref(0);
const pageSize = ref(12);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const isKeywordSearch = ref(false);

// onMounted 
onMounted(async () => {
  // 로그인한 고객 회원이면 좋아요 목록 로드
  if (authStore.isLoggedIn && authStore.userInfo.role === "ROLE_CUSTOMER") {
    await storeStore.searchAllLike();
  }
  await searchAll();
});

// 스토어 목록 조회
const searchAll = async (flag) => {
  if (flag === 0) {
    currentPage.value = 0;
    searchQuery.value = "";
    isKeywordSearch.value = false; // 일반 검색 상태로 전환
  }
  const res = await storeStore.searchAllStore("STORE_END", currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = storeStore.totalElements;
    totalPages.value = storeStore.totalPages;
    storeList.value = storeStore.storeList;
    hideBtns.value = false;
  } else {
    storeList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

// 스토어 키워드 검색
const searchAllByKeyword = async () => {
  if (!isKeywordSearch.value) {
    currentPage.value = 0; // 키워드 검색 상태로 진입 시 페이지를 초기화
    isKeywordSearch.value = true; // 키워드 검색 상태 활성화
  }
  
  const res = await storeStore.searchAllStoreByKeyword("STORE_END", searchQuery.value, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = storeStore.totalElements;
    totalPages.value = storeStore.totalPages;
    storeList.value = storeStore.storeList;
    hideBtns.value = false;
  } else {
    storeList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

// 페이지 네이션
const changePage = async (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return; // 유효한 페이지 번호인지 확인
  currentPage.value = newPage;
  
  if (isKeywordSearch.value) { // 키워드 검색 상태일 경우
    await searchAllByKeyword();
  } else { // 일반 검색 상태일 경우
    await searchAll();
  }
};

</script>