<template>
  <div>
    <div class="lyt-child">
      <div class="wrp-list" v-if="likeList && likeList.length">
        <PopupList v-for="popup in likeList" :key="popup.popupIdx" :popup="popup" :showControl="showControl" />
      </div>
      <AppEmptyState
        v-else-if="loadError"
        title="좋아요 목록을 불러오지 못했습니다"
        description="잠시 후 다시 시도해 주세요."
        action-label="다시 시도"
        @action="getMyLikedPopups()"
      />
      <AppEmptyState
        v-else
        title="좋아요한 팝업이 없습니다"
        description="관심 있는 팝업을 저장하면 이곳에서 빠르게 다시 확인할 수 있어요."
        to="/"
        action-label="팝업 둘러보기"
      />
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import PopupList from "@/components/PopupList.vue";
import AppPagination from "@/components/AppPagination.vue";
import { usePopupStore } from "@/stores/popupStore";
import { computed, onMounted, ref } from "vue";

const popupStore = usePopupStore();

const likeList = computed(() => popupStore.likePageList);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const loadError = ref(false);
const showControl = ref(6);

onMounted(async () => {
  await getMyLikedPopups();
});

const getMyLikedPopups = async () => {
  const res = await popupStore.getMyLikedPopups(currentPage.value, pageSize.value);
  if (res.success) {
    loadError.value = false;
    totalElements.value = popupStore.likeTotalElements;
    totalPages.value = popupStore.likeTotalPages;
    hideBtns.value = false;
  } else {
    loadError.value = true;
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const changePage = (newPage) => {
  if (newPage >= 0) {
    currentPage.value = newPage;
    getMyLikedPopups();
  }
};
</script>
