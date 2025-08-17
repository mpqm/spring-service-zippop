<template>
  <div>
    <div class="management-page">
      <div class="two-section-container">
        <div class="input-search-container">
          <input class="default-input" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="searchAllByKeyword" />
          <button class="default-btn" @click="searchAllByKeyword">
          <Icon icon="ic:search" width="20px" height="20px" />굿즈 검색
          </button>
          <button class="default-btn" @click="searchAll(0)">
            <Icon icon="ic:baseline-refresh" width="20px" height="20px" /> 
          </button>
        </div>
        <div class="btn-container ">
          <router-link class="default-btn" :to="`/mypage/company/goods`">
            <Icon icon="iconoir:nav-arrow-left" width="20px" height="20px"  style="color: #ffffff" />
          </router-link>
          <router-link class="default-btn" :to="`/mypage/company/goods/${route.params.storeIdx}/register`">
            <Icon icon="iconoir:add-square" width="20px" height="20px"  style="color: #ffffff" />팝업 굿즈 등록
          </router-link>
        </div>
      </div>
      <div class="table-container" v-if="goodsList && goodsList.length">
        <GoodsTable :goods="goodsList" :showControl="showControl" />
      </div>
      <div class="empty-string" v-else> <p>등록된 굿즈가 없습니다.</p>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import GoodsTable from "@/components/GoodsTable.vue";
import AppPagination from "@/components/AppPagination.vue";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

// store, router, route, toast
const goodsStore = useGoodsStore();
const route = useRoute();

// 변수(goods)
const searchQuery = ref("");
const showControl = ref(true);
const goodsList = ref([]);
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

// 굿즈 목록 조회
const searchAll = async (flag) => {
  if (flag === 0) {
    currentPage.value = 0;
    searchQuery.value = "";
    isKeywordSearch.value = false; // 일반 검색 상태로 전환
  }
  const res = await goodsStore.searchAllByStoreIdx(route.params.storeIdx, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = goodsStore.totalElements;
    totalPages.value = goodsStore.totalPages;
    goodsList.value = goodsStore.goodsList;
    hideBtns.value = false;
  } else {
    goodsList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

// 굿즈 목록 조회(키워드 검색)
const searchAllByKeyword = async () => {
  if (!isKeywordSearch.value) {
    currentPage.value = 0; // 키워드 검색 상태로 진입 시 페이지를 초기화
    isKeywordSearch.value = true; // 키워드 검색 상태 활성화
  }
  const res = await goodsStore.searchAllByKeywordAndStoreIdx(searchQuery.value, route.params.storeIdx, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = goodsStore.totalElements;
    totalPages.value = goodsStore.totalPages;
    goodsList.value = goodsStore.goodsList;
    hideBtns.value = false;
  } else {
    goodsList.value = [];
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