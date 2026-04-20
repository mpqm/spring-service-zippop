<template>
  <div>
    <div class="lyt-child">
      <div class="wrp-list" v-if="cartList && cartList.length">
        <PopupList v-for="cart in cartList" :key="cart.cartIdx" :popup="cart" :showControl="showControl" />
      </div>
      <div class="txt-null" v-else>
        <p>장바구니가 비어있습니다.</p>
      </div>
      <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
    </div>
  </div>
</template>

<script setup>
import AppPagination from "@/components/AppPagination.vue";
import PopupList from "@/components/PopupList.vue";
import { useCartStore } from "@/stores/cartStore";
import { onMounted, ref } from "vue";

const cartStore = useCartStore();

const cartList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const showControl = ref(3);

onMounted(async () => {
  await getCarts();
});

const getCarts = async () => {
  const res = await cartStore.getCarts(currentPage.value, pageSize.value);
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

const changePage = (newPage) => {
  if (newPage >= 0) {
    currentPage.value = newPage;
    getCarts();
  }
};
</script>
