<template>
    <div>
        <div class="store-management-page">
            <div class="store-list" v-if="ordersList && ordersList.length">
                <OrdersListComponent v-for="orders in ordersList" :key="orders.ordersIdx" :orders="orders" :showControl="showControl" />
            </div>
            <div class="notice" v-else>
                <p>등록된 주문 내역이 없습니다.</p>
            </div>
            <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
        </div>
    </div>
</template>

<script setup>
import OrdersListComponent from "@/components/orders/OrdersListComponent.vue";
import PaginationComponent from "@/components/common/PaginationComponent.vue";
import { onMounted, ref } from "vue";
import { useOrdersStore } from "@/stores/useOrdersStore";

// store, router, route, toast
const ordersStore = useOrdersStore();

// 변수(orders)
const ordersList = ref([]);
const currentPage = ref(0);
const pageSize = ref(5);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const showControl = ref(true);

// onMounted
onMounted(async () => {
    await searchAll();
});

// 주문 목록 조회
const searchAll = async () => {
    const res = await ordersStore.searchAllAsCustomer(currentPage.value, pageSize.value);
    if (res.success) {
        totalElements.value = ordersStore.totalElements;
        totalPages.value = ordersStore.totalPages;
        ordersList.value = ordersStore.ordersList;
        hideBtns.value = false;
    } else {
        ordersList.value = [];
        totalElements.value = 0;
        totalPages.value = 0;
        hideBtns.value = true;
    }
};

// 페이지 네이션
const changePage = async (newPage) => {
    if (newPage >= 0) {
        currentPage.value = newPage;
        await searchAll(currentPage.value, pageSize.value);
    }
};

</script>

<style scoped>
.store-management-page {
    flex-direction: row;
    width: 65rem;
}

.notice {
    text-align: center;
}

.store-list {
    width: auto;
    padding: 5px;
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.store-register-btn:hover {
    opacity: 0.8;
}

</style>