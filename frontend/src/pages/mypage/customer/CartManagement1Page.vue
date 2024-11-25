<template>
  <div>
    <div class="store-manage-page">
      <div class="store-list" v-if="cartList && cartList.length">
        <StoreListComponent v-for="cart in cartList" :key="cart.storeIdx" :store="cart" :showControl="showControl" />
      </div>
      <div class="notice" v-else>
        <p>등록된 팝업 스토어가 없습니다.</p>
      </div>
      <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import PaginationComponent from "@/components/common/PaginationComponent.vue";
import StoreListComponent from "@/components/store/StoreListComponent.vue";
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

<style scoped>
.store-manage-page {
  flex-direction: row;
  width: 65rem;
}

.notice {
  text-align: center;
}

.store-control {
  padding: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.store-list {
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
.store-register-btn:hover {
  opacity: 0.8;
}

.search-img {
  padding: 0 1.25rem;
}
</style>