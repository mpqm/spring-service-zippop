<template>
    <div>
        <div class="store-management-page">
            <div class="store-list" v-if="ordersList && ordersList.length">
                <OrdersListComponent v-for="orders in ordersList" :key="orders.ordersIdx" :orders="orders" :storeIdx="storeIdx"
                    :showControl="showControl" />
            </div>
            <div class="notice" v-else>
                <p>등록된 주문 내역이 없습니다.</p>
            </div>
            <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns"
                @page-changed="changePage" />
        </div>
    </div>
</template>

<script setup>
import OrdersListComponent from "@/components/orders/OrdersListComponent.vue";
import PaginationComponent from "@/components/common/PaginationComponent.vue";
import { onMounted, ref } from "vue";
import { useOrdersStore } from "@/stores/useOrdersStore";
import { useRoute } from "vue-router";
const ordersStore = useOrdersStore();
const route = useRoute();
const ordersList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const storeIdx = ref(null);
const hideBtns = ref(false);
const showControl = ref(false);

onMounted(async () => {
    storeIdx.value = route.params.storeIdx;
    await searchAll(currentPage.value, pageSize.value);
});

const changePage = (newPage) => {
    if (newPage >= 0) {
        currentPage.value = newPage;
        searchAll(currentPage.value, pageSize.value);
    }
};

const searchAll = async (page) => {
    const res = await ordersStore.searchAllAsCompany(storeIdx.value, page, pageSize.value);
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


</script>

<style scoped>
.store-management-page {
    flex-direction: row;
    width: 65rem;
}

.store-control {
    padding: 5px;
    display: flex;
    justify-content: space-between;
    align-items: center;
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

.pagination {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin: 10px auto;
}

.pagination-btn {
    padding: 0.5rem 1rem;
    color: black;
    border: 1px solid #ddd;
    cursor: pointer;
    border-radius: 5px;
}

.pagination-move-btn {
    padding: 0.5rem 1rem;
    color: #fff;
    background-color: #00c7ae;
    border: none;
    cursor: pointer;
    border-radius: 5px;
}

.pagination-btn.active {
    background-color: #00c7ae;
    color: #fff;
}

.store-register-btn {
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
.pagination-btn:hover,
.pagination-move-btn:hover,
.store-register-btn:hover {
    opacity: 0.8;
}

.search-img {
    padding: 0 1.25rem;
}
</style>