<template>
  <div>
    <div class="store-control">
      <div class="search-container">
        <input class="search-input" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="searchAllByKeyword" />
        <button class="search-btn" @click="searchAllByKeyword"><img class="search-img" src="../../../assets/img/search-none.png" alt=""></button>
        <button class="search-btn" @click="searchAll(0)"><img class="search-img" src="../../../assets/img/reload-none.png" alt=""></button>
      </div>
    </div>
    <div class="store-manage-page">
      <div class="store-list" v-if="storeList && storeList.length">
        <StoreListComponent v-for="store in storeList" :key="store.storeIdx" :store="store" :showControl="showControl" />
      </div>
      <div class="notice" v-else>
        <p>등록된 팝업 스토어가 없습니다.</p>
      </div>
      <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import StoreListComponent from "@/components/store/StoreListComponent.vue";
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
const showControl = ref(2);
const isKeywordSearch = ref(false);

// onMounted 
onMounted(async () => {
  await searchAll();
});

// 스토어 목록 조회
const searchAll = async (flag) => {
  if (flag === 0) {
    currentPage.value = 0;
    searchQuery.value = "";
    isKeywordSearch.value = false; // 일반 검색 상태로 전환
  }
  const res = await storeStore.searchAllStoreAsCompany(currentPage.value, pageSize.value);
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
  const res = await storeStore.searchAllStoreByKeywordAsCompany(searchQuery.value, currentPage.value, pageSize.value);
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
.store-manage-page {
  flex-direction: row;
  width: 65rem;
}

.notice {
  text-align: center;
}

.store-control {
  padding: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.store-list {
  width: auto;
  padding: 5px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.search-container {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.search-input {
  border: 1px solid #e1e1e1;
  border-radius: 4px;
  display: flex;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  padding: 0.5rem;
  width: 30rem;
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

.search-btn:hover,
.store-register-btn:hover {
  opacity: 0.8;
}

.search-img {
  padding: 0 1.25rem;
}
</style>