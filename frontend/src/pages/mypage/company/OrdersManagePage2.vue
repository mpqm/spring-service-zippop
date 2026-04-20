<template>
    <div>
        <div class="lyt-child">
            <div class="ctn-table" v-if="ordersList && ordersList.length">
                <OrdersTable :orders="ordersList" :showControl="showControl" />
            </div>
            <div class="txt-null" v-else>
                <p>등록된 주문 내역이 없습니다.</p>
            </div>
            <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns"
                @page-changed="changePage" />
        </div>
    </div>
</template>

<script setup>
import OrdersTable from "@/components/OrdersTable.vue";
import AppPagination from "@/components/AppPagination.vue";
import { onMounted, ref } from "vue";
import { useOrdersStore } from "@/stores/ordersStore";
import { useRoute } from "vue-router";

// store, router, route, toast
const ordersStore = useOrdersStore();
const route = useRoute();

// 변수(orders)
const ordersList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const showControl = ref(false);

// onMounted 
onMounted(async () => {
    await getCompanyPopupOrdersList(currentPage.value, pageSize.value);
});

// 주문 목록 조회
const getCompanyPopupOrdersList = async () => {
    const res = await ordersStore.getCompanyPopupOrdersList(route.params.popupIdx, currentPage.value, pageSize.value);
    if (res.success) {
        totalElements.value = ordersStore.totalElements;
        totalPages.value = ordersStore.totalPages;
        ordersList.value = ordersStore.ordersList;
        hideBtns.value = false;
    } else {
        ordersList.value = null;
        totalElements.value = 0;
        totalPages.value = 0;
        hideBtns.value = true;
    }
};

// 페이지 네이션
const changePage = (newPage) => {
    if (newPage >= 0) {
        currentPage.value = newPage;
        getCompanyPopupOrdersList(currentPage.value, pageSize.value);
    }
};

</script>

