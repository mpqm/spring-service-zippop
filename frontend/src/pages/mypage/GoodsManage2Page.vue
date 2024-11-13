<template>
  <div>
    <div class="goods-manage-page">
      <div class="goods-control">
        <div class="search-container">
          <input class="search-input" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요"
            @keyup.enter="keywordSearchAll" />
          <button class="search-btn" @click="keywordSearchAll"><img class="search-img"
              src="../../assets/img/search-none.png" alt=""></button>
          <button class="search-btn" @click="searchAll(0)"><img class="search-img"
              src="../../assets/img/reload-none.png" alt=""></button>
        </div>
        <div class="btn-container">
          <router-link class="register-btn" :to="`/mypage/company/goods`">&lt;</router-link>
          <router-link class="back-btn" :to="`/mypage/company/goods/${route.params.storeIdx}/register`">팝업 굿즈 등록</router-link>
        </div>
      </div>
      <div class="goods-list" v-if="goodsList && goodsList.length">
        <GoodsListComponent v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :showControl="showControl" />
      </div>
      <div v-else>
        <p>등록된 굿즈가 없습니다.</p>
      </div>
      <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns"
        @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import GoodsListComponent from "@/components/GoodsListComponent.vue";
import PaginationComponent from "@/components/PaginationComponent.vue";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const searchQuery = ref("");
const goodsStore = useGoodsStore();
const route = useRoute();

const showControl = ref(true);
const goodsList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

onMounted(async () => {
  await searchAll(route.params.storeIdx, currentPage.value, pageSize.value);
});

const changePage = (newPage) => {
  if (newPage >= 0) {
    currentPage.value = newPage;
    searchAll(currentPage.value, pageSize.value);
  }
};

const searchAll = async (storeIdx, page) => {
  const res = await goodsStore.searchAllByStoreIdx(storeIdx, page, pageSize.value);
  if (res.success) {
    totalElements.value = goodsStore.totalElements;
    totalPages.value = goodsStore.totalPages;
    goodsList.value = goodsStore.goodsList;
    hideBtns.value = false;
  } else {
    goodsList.value = null;
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const keywordSearchAll = async () => {
  currentPage.value = 0;
  const res = await goodsStore.searchAllByKeywordAndStoreIdx(searchQuery.value, route.params.storeIdx, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = goodsStore.totalElements;
    totalPages.value = goodsStore.totalPages;
    goodsList.value = goodsStore.goodsList;
    hideBtns.value = false;
  } else {
    goodsList.value = null;
    totalElements.value = null;
    totalPages.value = null;
    hideBtns.value = true;
  }
};

</script>

<style scoped>
.goods-manage-page {
  flex-direction: row;
  width: 65rem;
}

.goods-control {
  padding: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-list {
  width: auto;
  padding: 5px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin: 10px auto;
}

.back-btn {
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

.btn-container {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.register-btn {
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

.pagination-btn {
  padding: 0.5rem 1rem;
  color: black;
  border: 1px solid #ddd;
  cursor: pointer;
  border-radius: 5px;
}

.pagination-move-btn {
  padding: 0.5rem 1rem;
  color: #fff;
  background-color: #00c7ae;
  border: none;
  cursor: pointer;
  border-radius: 5px;
}

.pagination-btn.active {
  background-color: #00c7ae;
  color: #fff;
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
.pagination-btn:hover,
.pagination-move-btn:hover,
.goods-register-btn:hover {
  opacity: 0.8;
}

.search-img {
  padding: 0 1.25rem;
}
</style>