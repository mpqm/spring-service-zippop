<template>
  <div>
    <div class="lyt-child">
      <div class="wrp-list" v-if="likeList && likeList.length">
        <PopupList v-for="popup in likeList" :key="popup.popupIdx" :popup="popup" :showControl="showControl" />
      </div>
      <div class="txt-null" v-else>
        <p>좋아요한 팝업 스토어가 없습니다.</p>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import PopupList from "@/components/PopupList.vue";
import AppPagination from "@/components/AppPagination.vue";
import { usePopupStore } from "@/stores/popupStore";
import { onMounted, ref } from "vue";

const popupStore = usePopupStore();

const likeList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const showControl = ref(6);

onMounted(async () => {
  await getMyLikedPopups();
});

const getMyLikedPopups = async () => {
  const res = await popupStore.getMyLikedPopups(currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = popupStore.totalElements;
    totalPages.value = popupStore.totalPages;
    likeList.value = popupStore.likeList;
    hideBtns.value = false;
  } else {
    likeList.value = [];
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
