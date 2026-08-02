<template>
    <div>
        <div class="lyt-child">
            <div class="wrp-list" v-if="ordersList && ordersList.length">
                <OrdersList v-for="orders in ordersList" :key="orders.ordersIdx" :orders="orders" :showControl="showControl" />
            </div>
            <AppEmptyState
                v-else
                title="결제 내역이 없습니다"
                description="굿즈 결제를 완료하면 주문 상태와 상세 내역을 확인할 수 있어요."
                to="/goods"
                action-label="굿즈 둘러보기"
            />
            <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
        </div>
    </div>
</template>

<script setup>
import OrdersList from "@/components/OrdersList.vue";
import AppPagination from "@/components/AppPagination.vue";
import { onMounted, ref } from "vue";
import { useOrdersStore } from "@/stores/ordersStore";

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
    await getOrders();
});

// 주문 목록 조회
const getOrders = async () => {
    const res = await ordersStore.getOrders(currentPage.value, pageSize.value);
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
        await getOrders(currentPage.value, pageSize.value);
    }
};

</script>
