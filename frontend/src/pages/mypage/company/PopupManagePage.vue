<template>
  <div>
    <div class="ctn-split">
      <div class="ctn-inputsearch">
        <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="submitSearch()" />
        <button class="btn-default" @click="submitSearch()">
          <Icon icon="ic:search" width="20px" height="20px" />팝업 검색
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
      <AppEmptyState
        v-else-if="loadError"
        title="팝업 목록을 불러오지 못했습니다"
        description="잠시 후 다시 시도해 주세요."
        action-label="다시 시도"
        @action="getCompanyPopups()"
      />
      <AppEmptyState
        v-else-if="searchQuery.trim()"
        title="검색 결과가 없습니다"
        description="검색어를 바꾸거나 전체 팝업 목록으로 돌아가세요."
        action-label="검색 초기화"
        @action="getCompanyPopups(true)"
      />
      <AppEmptyState
        v-else
        title="등록한 팝업이 없습니다"
        description="팝업을 등록하면 이곳에서 정보와 운영 상태를 관리할 수 있어요."
        to="/mypage/company/popup/register"
        action-label="팝업 등록하기"
      />
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import PopupTable from "@/components/PopupTable.vue";
import AppEmptyState from "@/components/AppEmptyState.vue";
import AppPagination from "@/components/AppPagination.vue";
import { usePopupStore } from "@/stores/popupStore";
import { onMounted, ref } from "vue";

const popupStore = usePopupStore();

const searchQuery = ref("");
const showControl = ref(0);
const popupList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const loadError = ref(false);

onMounted(async () => {
  await getCompanyPopups();
});

const submitSearch = () => {
  currentPage.value = 0;
  return getCompanyPopups();
};

const getCompanyPopups = async (reset = false) => {
  if (reset) {
    currentPage.value = 0;
    searchQuery.value = "";
  }
  const res = await popupStore.getCompanyPopups(searchQuery.value, currentPage.value, pageSize.value);
  if (res.success) {
    loadError.value = false;
    totalElements.value = popupStore.totalElements;
    totalPages.value = popupStore.totalPages;
    popupList.value = popupStore.popupList;
    hideBtns.value = false;
  } else {
    loadError.value = true;
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
