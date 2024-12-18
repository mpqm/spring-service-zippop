<template>
  <div>
    <HeaderComponent></HeaderComponent>
    <div class="main-page">
      <h2 class="hero-text">여러가지 팝업 정보를 단 한 곳에서</h2>
      <div class="search-container">
        <input class="search-input" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="searchAllByKeyword" />
        <button class="search-btn" @click="searchAllByKeyword"><img class="search-img" src="../../assets/img/search-none.png" alt=""></button>
        <button class="search-btn" @click="searchAll(0)"><img class="search-img" src="../../assets/img/reload-none.png" alt=""></button>
      </div>
      <div class="store-list-grid" v-if="storeList && storeList.length">
        <StoreCardComponent v-for="store in storeList" :key="store.storeIdx" :store="store" />
      </div>
      <div v-else>
        <p>검색 결과에 해당하는 팝업 스토어 목록이 없습니다.</p>
      </div>
      <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
    <FooterComponent></FooterComponent>
  </div>
</template>
<script setup>
import HeaderComponent from "@/components/common/HeaderComponent.vue";
import FooterComponent from "@/components/common/FooterComponent.vue";
import StoreCardComponent from "@/components/store/StoreCardComponent.vue";
import PaginationComponent from "@/components/common/PaginationComponent.vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { onMounted, ref } from "vue";

// store, router, route, toast
const storeStore = useStoreStore();

// 변수(store)
const searchQuery = ref("");
const storeList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const isKeywordSearch = ref(false);

// onMounted
onMounted(async () => {
  await searchAll(currentPage.value, pageSize.value);
});

// 스토어 목록 조회
const searchAll = async (flag) => {
  if (flag === 0) {
    currentPage.value = 0;
    searchQuery.value = "";
    isKeywordSearch.value = false; // 일반 검색 상태로 전환
  }
  const res = await storeStore.searchAllStore("STORE_START", currentPage.value, pageSize.value);
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

// 스토어 목록 조회(키워드 검색)
const searchAllByKeyword = async () => {
  if (!isKeywordSearch.value) {
    currentPage.value = 0; // 키워드 검색 상태로 진입 시 페이지를 초기화
    isKeywordSearch.value = true; // 키워드 검색 상태 활성화
  }
  const res = await storeStore.searchAllStoreByKeyword("STORE_START", searchQuery.value, currentPage.value, pageSize.value);
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

<style scoped>
.main-page {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  width: 65rem;
  padding: 1rem;
}

.hero-text {
  display: flex;
  justify-content: center;
  padding: 0.5rem 0;
  font-size: 1.75rem;
  font-weight: 700;
  color: #323232;
}

.search-container {
  display: flex;
  gap: 10px;
  justify-content: center;
  padding-bottom: 16px;
}

.search-input {
  border: 1px solid #e1e1e1;
  border-radius: 4px;
  display: block;
  padding: 1rem;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  width: 50%;
  box-sizing: border-box;
  color: #323232;
  background-color: #fff;
}

.search-btn {
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

.search-btn:hover {
  opacity: 0.8;
}

.search-img {
  padding: 0 1.25rem;
}

.store-list-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  grid-auto-rows: 2fr;
}
</style>
