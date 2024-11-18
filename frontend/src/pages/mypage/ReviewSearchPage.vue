<template>
    <div>
      <div class="review-manage-page">
        <div class="review-list" v-if="reviewList && reviewList.length">
          <ReviewListComponent v-for="review in reviewList" :key="review.reviewIdx" :review="review" :hideStoreName="true" />
        </div>
        <div class="notice" v-else>
          <p>등록된 리뷰가 없습니다.</p>
        </div>
        <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns"
          @page-changed="changePage" />
      </div>
    </div>
  </template>
  
  <script setup>
  import ReviewListComponent from "@/components/store/ReviewListComponent.vue";
  import PaginationComponent from "@/components/common/PaginationComponent.vue";
  import { useStoreStore } from "@/stores/useStoreStore";
  import { onMounted, ref } from "vue";
  
  const storeStore = useStoreStore();
  
  const reviewList = ref([]);
  const currentPage = ref(0);
  const pageSize = ref(8);
  const totalElements = ref(0);
  const totalPages = ref(0);
  const hideBtns = ref(false);
  
  onMounted(async () => {
    await searchAll(currentPage.value, pageSize.value);
  });
  
  const changePage = async(newPage) => {
    if (newPage >= 0) {
      currentPage.value = newPage;
      await searchAll();
    }
  };
  
  const searchAll = async () => {
    const res = await storeStore.searchAllReviewAsCustomer(currentPage.value, pageSize.value);
    if (res.success) {
      totalElements.value = storeStore.totalElements;
      totalPages.value = storeStore.totalPages;
      reviewList.value = storeStore.reviewList;
      hideBtns.value = false;
    } else {
      reviewList.value = null;
      totalElements.value = 0;
      totalPages.value = 0;
      hideBtns.value = true;
    }
  };
  </script>
  
  <style scoped>
  .review-manage-page {
    flex-direction: row;
    width: 65rem;
  }
  .notice{
      text-align: center;
    }
  .review-control {
    padding: 5px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .review-list {
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
  .review-register-btn:hover {
    opacity: 0.8;
  }
  
  .search-img {
    padding: 0 1.25rem;
  }
  </style>