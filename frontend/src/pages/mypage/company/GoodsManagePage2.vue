<template>
  <div>
    <div class="lyt-child">
      <div class="ctn-split">
        <div class="ctn-inputsearch">
          <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="getGoodsList()" />
          <button class="btn-default" @click="getGoodsList()">
            <Icon icon="ic:search" width="20px" height="20px" />굿즈 검색
          </button>
          <button class="btn-normal" @click="getGoodsList(true)">
            <Icon icon="ic:baseline-refresh" width="20px" height="20px" />
          </button>
        </div>
        <div class="ctn-buttons">
          <router-link class="btn-normal" :to="`/mypage/company/goods`">
            <Icon icon="iconoir:nav-arrow-left" width="20px" height="20px" />
          </router-link>
          <router-link class="btn-default" :to="`/mypage/company/goods/${route.params.popupIdx}/register`">
            <Icon icon="iconoir:add-square" width="20px" height="20px" class="ico-inverse" />팝업 굿즈 등록
          </router-link>
        </div>
      </div>
      <div class="ctn-table" v-if="goodsList && goodsList.length">
        <GoodsTable :goods="goodsList" :showControl="showControl" />
      </div>
      <div class="txt-null" v-else>
        <p>등록된 굿즈가 없습니다.</p>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import GoodsTable from "@/components/GoodsTable.vue";
import AppPagination from "@/components/AppPagination.vue";
import { useGoodsStore } from "@/stores/goodsStore";
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { Icon } from '@iconify/vue';

const goodsStore = useGoodsStore();
const route = useRoute();

const searchQuery = ref("");
const showControl = ref(true);
const goodsList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

onMounted(async () => {
  await getGoodsList();
});

const getGoodsList = async (reset = false) => {
  if (reset) {
    currentPage.value = 0;
    searchQuery.value = "";
  }
  const res = await goodsStore.getGoodsList(route.params.popupIdx, searchQuery.value || null, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = goodsStore.totalElements;
    totalPages.value = goodsStore.totalPages;
    goodsList.value = goodsStore.goodsList;
    hideBtns.value = false;
  } else {
    goodsList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const changePage = async (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return;
  currentPage.value = newPage;
  await getGoodsList();
};
</script>
