<template>
<div>
    <div class="store-control">
        <div class="search-container">
            <input class="search-input" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="keywordSearchAll" />
            <button class="search-btn" @click="keywordSearchAll"><img class="search-img" src="../../assets/img/search-none.png" alt=""></button>
            <button class="search-btn" @click="searchAll(0)"><img class="search-img" src="../../assets/img/reload-none.png" alt=""></button>
        </div>
        <router-link class="store-register-btn" to="/mypage/company/store/register">팝업스토어 등록</router-link>
    </div>
    <div class="store-management-page">
        <div class="store-list" v-if="storeList && storeList.length">
            <StoreListComponent v-for="store in storeList" :key="store.storeIdx" :store="store" :showControl="showControl" />
        </div>
        <div v-else>
            <p>등록된 팝업 스토어가 없습니다.</p>
        </div>
        <PaginationComponent
            :currentPage="currentPage"
            :totalPages="totalPages"
            :hideBtns="hideBtns"
            @page-changed="changePage"
        />
    </div>
</div>
  </template>
  
  <script setup>
  import StoreListComponent from "@/components/StoreListComponent.vue";
  import PaginationComponent from "@/components/PaginationComponent.vue";
  import { useStoreStore } from "@/stores/useStoreStore";
  import { onMounted, ref } from "vue";
  
  const storeStore = useStoreStore();
  const searchQuery = ref("");
  const showControl = ref(true);
  const storeList = ref([]);
  const currentPage = ref(0);
  const pageSize = ref(8);
  const totalElements = ref(0);
  const totalPages = ref(0);
  const hideBtns = ref(false);
  
  onMounted(async () => {
    await searchAll(currentPage.value, pageSize.value);
  });
  
  const changePage = (newPage) => {
    if (newPage >= 0) {
      currentPage.value = newPage;
      searchAll(currentPage.value, pageSize.value);
    }
  };
  
  const searchAll = async (page) => {
    const res = await storeStore.searchAllAsCompany(page, pageSize.value);
    if(res.success){
    totalElements.value = storeStore.totalElements;
    totalPages.value = storeStore.totalPages;
    storeList.value = storeStore.storeList;
    hideBtns.value = false;
  } else {
    storeList.value = null;
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
  };

  const keywordSearchAll = async () => {
  currentPage.value = 0;
  const res = await storeStore.searchAllByKeywordAsCompany(searchQuery.value, currentPage.value, pageSize.value);
  if(res.success){
    totalElements.value = storeStore.totalElements;
    totalPages.value = storeStore.totalPages;
    storeList.value = storeStore.storeList;
    hideBtns.value = false;
  } else {
    storeList.value = null;
    totalElements.value = null;
    totalPages.value = null;
    hideBtns.value = true;
  }
};

  </script>
  
  <style scoped>
  .store-management-page {
    flex-direction: row;
    width: 65rem;
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
  
  .pagination {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin: 10px auto;
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

  .store-register-btn{
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

.search-btn:hover, .pagination-btn:hover, .pagination-move-btn:hover, .store-register-btn:hover{
  opacity: 0.8;
}

.search-img {
  padding: 0 1.25rem;
}
  </style>
  