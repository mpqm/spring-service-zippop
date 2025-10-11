<template>
  <div>
    <div class="lyt-child">
      <div class="wrp-list" v-if="cartList && cartList.length">
        <StoreList v-for="cart in cartList" :key="cart.storeIdx" :store="cart" :showControl="showControl" />
      </div>
      <div class="txt-null" v-else>
        <p>등록된 팝업 스토어가 없습니다.</p>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import AppPagination from "@/components/AppPagination.vue";
import StoreList from "@/components/StoreList.vue";
import { useCartStore } from "@/stores/useCartStore";
import { onMounted, ref } from "vue";

// store, router, route, toast
const cartStore = useCartStore();

// 변수(store)
const cartList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const showControl = ref(3);

// onMounted 
onMounted(async () => {
  await searchAll();
});

// 스토어 목록 조회
const searchAll = async () => {
  const res = await cartStore.searchAll(currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = cartStore.totalElements;
    totalPages.value = cartStore.totalPages;
    cartList.value = cartStore.cartList;
    hideBtns.value = false;
  } else {
    cartList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

// 페이지 네이션
const changePage = (newPage) => {
  if (newPage >= 0) {
    currentPage.value = newPage;
    searchAll();
  }
};

</script>
