<template>
  <div>
    <div class="two-section-container">
      <div class="input-search-container">
        <input class="default-input" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="searchAllByKeyword" />
        <button class="default-btn" @click="searchAllByKeyword">
          <Icon icon="ic:search" width="20px" height="20px" />
          팝업 검색
        </button>
        <button class="default-btn" @click="searchAll(0)">
          <Icon icon="ic:baseline-refresh" width="20px" height="20px" /> 
        </button>
      </div>
      <router-link class="default-btn" to="/mypage/company/store/register">
        <Icon icon="iconoir:add-square" width="20px" height="20px"  style="color: #ffffff" />팝업스토어 등록
      </router-link>
    </div>
    <div class="management-page">
      <div v-if="storeList && storeList.length">
        <StoreTable :stores="storeList" :showControl="showControl" />
      </div>
      <div class="empty-string" v-else>
        <p>등록된 팝업 스토어가 없습니다.</p>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import StoreTable from "@/components/StoreTable.vue";
import AppPagination from "@/components/AppPagination.vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { onMounted, ref } from "vue";
import { Icon } from '@iconify/vue';

// store, router, route, toast
const storeStore = useStoreStore();

// 변수(store)
const searchQuery = ref("");
const showControl = ref(0);
const storeList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
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
.management-page {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.empty-string {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
  background: #f8f9fa;
  border-radius: 8px;
}

.empty-string p {
  margin: 0;
  font-size: 16px;
}
</style>