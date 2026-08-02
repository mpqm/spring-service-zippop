<template>
  <div>
    <div class="ctn-split">
      <div class="ctn-inputsearch">
        <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="getCompanyPopups()" />
        <button class="btn-default" @click="getCompanyPopups()">
          <Icon icon="ic:search" width="20px" height="20px" />팝업 검색
        </button>
        <button class="btn-normal" @click="getCompanyPopups(true)">
          <Icon icon="ic:baseline-refresh" width="20px" height="20px" />
        </button>
      </div>
      <router-link class="btn-default" to="/mypage/company/popup/register">
        <Icon icon="iconoir:add-square" width="20px" height="20px" class="ico-inverse" />팝업스토어 등록
      </router-link>
    </div>
    <div class="lyt-child">
      <div v-if="popupList && popupList.length">
        <PopupTable :popups="popupList" :showControl="showControl" />
      </div>
      <div class="txt-null" v-else>
        <p>등록된 팝업 스토어가 없습니다.</p>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import PopupTable from "@/components/PopupTable.vue";
import AppPagination from "@/components/AppPagination.vue";
import { usePopupStore } from "@/stores/popupStore";
import { onMounted, ref } from "vue";
import { Icon } from '@iconify/vue';

const popupStore = usePopupStore();

const searchQuery = ref("");
const showControl = ref(0);
const popupList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

onMounted(async () => {
  await getCompanyPopups();
});

const getCompanyPopups = async (reset = false) => {
  if (reset) {
    currentPage.value = 0;
    searchQuery.value = "";
  }
  const res = await popupStore.getCompanyPopups(searchQuery.value, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = popupStore.totalElements;
    totalPages.value = popupStore.totalPages;
    popupList.value = popupStore.popupList;
    hideBtns.value = false;
  } else {
    popupList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const changePage = async (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return;
  currentPage.value = newPage;
  await getCompanyPopups();
};
</script>
