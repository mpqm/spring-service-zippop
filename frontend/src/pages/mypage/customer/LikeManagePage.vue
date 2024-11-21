<template>
  <div>
    <div class="like-management-page">
      <div class="like-list" v-if="likeList && likeList.length">
        <LikeListComponent v-for="store in likeList" :key="store.storeIdx" :store="store" />
      </div>
      <div class="notice" v-else>
        <p>좋아요한 팝업 스토어가 없습니다.</p>
      </div>
      <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import LikeListComponent from "@/components/store/LikeListComponent.vue";
import PaginationComponent from "@/components/common/PaginationComponent.vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { onMounted, ref } from "vue";

// store, router, route, toast
const storeStore = useStoreStore();

// 변수(like)
const likeList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

// onMounted
onMounted(async () => {
  await searchAll();
});

// 페이지 네이션
const changePage = (newPage) => {
  if (newPage >= 0) {
    currentPage.value = newPage;
    searchAll();
  }
};

// 좋아요 목록 조회
const searchAll = async () => {
  const res = await storeStore.searchAllLike(currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = storeStore.totalElements;
    totalPages.value = storeStore.totalPages;
    likeList.value = storeStore.likeList;
    hideBtns.value = false;
  } else {
    likeList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

</script>

<style scoped>
.like-management-page {
  flex-direction: row;
  width: 65rem;
}

.notice {
  text-align: center;
}

.like-list {
  width: auto;
  padding: 5px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>