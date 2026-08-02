<template>
  <div>
    <div class="lyt-child">
      <div class="wrp-list" v-if="reviewList && reviewList.length">
        <ReviewList v-for="review in reviewList" :key="review.reviewIdx" :review="review" :hideStoreName="true" />
      </div>
      <AppEmptyState
        v-else
        title="등록한 리뷰가 없습니다"
        description="방문한 팝업의 경험을 남기면 이곳에서 리뷰를 관리할 수 있어요."
        to="/"
        action-label="팝업 둘러보기"
      />
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import ReviewList from "@/components/ReviewList.vue";
import AppPagination from "@/components/AppPagination.vue";
import { usePopupStore } from "@/stores/popupStore";
import { onMounted, ref } from "vue";

const popupStore = usePopupStore();

const reviewList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

onMounted(async () => {
  await getMyReviews();
});

const getMyReviews = async () => {
  const res = await popupStore.getMyReviews(currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = popupStore.totalElements;
    totalPages.value = popupStore.totalPages;
    reviewList.value = popupStore.reviewList;
    hideBtns.value = false;
  } else {
    reviewList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const changePage = async (newPage) => {
  if (newPage >= 0) {
    currentPage.value = newPage;
    await getMyReviews();
  }
};
</script>
