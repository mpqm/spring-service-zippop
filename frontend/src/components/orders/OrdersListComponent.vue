<template>
    <div class="orders-list-item">
        <img class="orders-img" v-if="orders.searchordersImageResList && orders.searchordersImageResList.length" :src="orders.searchordersImageResList[0].ordersImageUrl" alt="orders image" />
        <div class="orders-info1">
            <p class="t3">주문 번호 : {{ orders.impUid }}</p>
            <p class="t3">주문자 : {{ orders.name }}</p>
            <p class="t3">전화 번호 : {{ orders.phoneNumber }}</p>
            <p class="t3">{{ formatedDate }}</p>
        </div>
        <div class="orders-info2">
            <p class="t2">사용포인트 : {{ orders.usedPoint }} points</p>
            <p class="t2">배송비 : {{ orders.deliveryCost }} 원</p>
            <p class="t2">총 가격 : {{ orders.totalPrice }} 원</p>
            <p class="t3">배송 주소 : {{ orders.address }}</p>
            <p class="t4" :class="getStatusClass(orders.orderStatus)">상태 : {{ formatedOrderStatus }}</p>
        </div>
        <div v-if="showControl == true" class="btn-container">
            <button class="orders-cancel-btn" :disabled="isCancelDisabled" @click="cancelOrders">주문 취소</button>
            <button class="orders-complete-btn" @click="completeOrders">주문 확정</button>
            <router-link class="ud-btn" :to="orders ? `/orders/${orders.ordersIdx}` : '#'">정보 보기</router-link>
        </div>
        <div v-if="showControl == false" class="btn-container">
            <button class="orders-complete-btn" @click="completeOrders">배송 확정</button>
            <router-link v-if="orders && route.params.storeIdx" class="ud-btn" :to="`/orders/${orders.ordersIdx}?storeIdx=${route.params.storeIdx}`">정보 보기</router-link>
        </div>
    </div>
</template>

<script setup>
import { computed, defineProps, onMounted, ref } from "vue";
import { useToast } from "vue-toastification";
import { useRouter, useRoute } from "vue-router";
import { useOrdersStore } from "@/stores/useOrdersStore";

// props 정의(주문, 스토어 인덱스, showControl)
const props = defineProps({
    orders: Object,
    storeIdx: String,
    showControl: Boolean,
});

// store, router, route, toast
const ordersStore = useOrdersStore();
const toast = useToast();
const route = useRoute();
const router = useRouter();

// 변수
const formatedDate = ref("");
const formatedOrderStatus = ref("");

// onMounted
onMounted(async () => {
    formatedDate.value = formatDate(props.orders.createdAt)
    formatedOrderStatus.value = formaOrdersStatus(props.orders.orderStatus)
});

// 주문 취소
const cancelOrders = async () => {
    const res = await ordersStore.cancel(props.orders.ordersIdx);
    if (res.success) {
        toast.success(res.message)
        router.go(0)
    } else {
        toast.error(res.message);
    }
}

// 주문 확정(고객), 배송 확정(기업)
const completeOrders = async () => {
    if (props.showControl) {
        const res = await ordersStore.completeAsCustomer(props.orders.ordersIdx);
        if (res.success) {
            toast.success(res.message)
            router.go(0)
        } else {
            toast.error(res.message);
        }
    } else {
        const res = await ordersStore.completeAsCompany(route.params.storeIdx, props.orders.ordersIdx);
        if (res.success) {
            toast.success(res.message)
            router.go(0)
        } else {
            toast.error(res.message);
        }
    }
}

// 날짜 포맷 함수
const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
}

// 주문 상태 포맷 함수
const formaOrdersStatus = (statusString) => {
    if (statusString === "STOCK_READY") return "재고 굿즈 결제 완료";
    else if (statusString === "STOCK_CANCEL") return "재고 굿즈 결제 취소";
    else if (statusString === "STOCK_COMPLETE") return "재고 굿즈 주문 확정";
    else if (statusString === "STOCK_DELIVERY") return "재고 굿즈 배달 중";
    else if (statusString === "RESERVE_READY") return "예약 굿즈 결제 완료";
    else if (statusString === "RESERVE_CANCEL") return "예약 굿즈 결제 취소";
    else if (statusString === "RESERVE_COMPLETE") return "예약 굿즈 주문 확정";
    else if (statusString === "RESERVE_DELIVERY") return "예약 굿즈 배달 중";
}

// 주문 확정인 경우 취소 버튼 비활성화
const isCancelDisabled = computed(() => {
  return props.orders.orderStatus === "STOCK_COMPLETE" || props.orders.orderStatus === "RESERVE_COMPLETE";
});

// 상태별 클래스 반환 함수
const getStatusClass = (statusString) => {
  if (statusString === "STOCK_READY" || statusString === "RESERVE_READY") return "status-ready";
  else if (statusString === "STOCK_CANCEL" || statusString === "RESERVE_CANCEL") return "status-cancel";
  else if (statusString === "STOCK_COMPLETE" || statusString === "RESERVE_COMPLETE") return "status-complete";
  else if (statusString === "STOCK_DELIVERY" || statusString === "RESERVE_DELIVERY") return "status-delivery";
  return "";
};

</script>

<style scoped>
.orders-list-item {
    display: flex;
    align-items: center;
    padding: 12px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.orders-img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 8px;
    margin-right: 12px;
}

.orders-info1 {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.orders-info2 {
    padding: 10px;
    justify-content: space-between;
    margin: 0;
    font-size: 0.9rem;
    color: #666;
}

.t2 {
    margin: 0;
    display: flex;
    align-items: center;
}

.t3 {
    margin: 0;
    font-size: 0.9rem;
    color: #666;
}

.t4 {
    margin: 0;
    font-size: 0.9rem;
    color: green;
}

.btn-container {
    margin-right: 0;
    margin-left: auto;
    display: flex;
    gap: 20px;
}

.ud-btn {
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
    font-size: 14px;
}

.orders-cancel-btn {
    display: block;
    text-align: center;
    width: auto;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: red;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.5rem;
    border-radius: 0.25rem;
    text-decoration: #000;
}

.orders-complete-btn {
    display: block;
    text-align: center;
    width: auto;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: green;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.5rem;
    border-radius: 0.25rem;
    text-decoration: #000;
}

.status-ready {
  color: #f39c12;
}

.status-cancel {
  color: #e74c3c;
}

.status-complete {
  color: #3498db;
}

.status-delivery {
  color: #27ae60;
}

</style>