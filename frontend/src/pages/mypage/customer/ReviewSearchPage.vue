<template>
  <div>
    <div class="review-manage-page">
      <div class="review-list" v-if="reviewList && reviewList.length">
        <ReviewListComponent v-for="review in reviewList" :key="review.reviewIdx" :review="review" :hideStoreName="true" />
      </div>
      <div class="notice" v-else>
        <p>등록된 리뷰가 없습니다.</p>
      </div>
      <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import ReviewListComponent from "@/components/store/ReviewListComponent.vue";
import PaginationComponent from "@/components/common/PaginationComponent.vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { onMounted, ref } from "vue";

// store, router, route, toast
const storeStore = useStoreStore();

// 변수(review)
const reviewList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

// onMounted
onMounted(async () => {
  await searchAll(currentPage.value, pageSize.value);
});

// 리뷰 목록 조회
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

// 페이지 네이션
const changePage = async (newPage) => {
  if (newPage >= 0) {
    currentPage.value = newPage;
    await searchAll();
  }
};

</script>

<style scoped>
.review-manage-page {
  flex-direction: row;
  width: 65rem;
}

.notice {
  text-align: center;
}

.review-list {
  width: auto;
  padding: 5px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

</style>