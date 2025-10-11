<template>
    <div class="ctn-list1">
        <img class="img-list" v-if="orders.searchordersImageResList && orders.searchordersImageResList.length" :src="orders.searchordersImageResList[0].ordersImageUrl" alt="orders image" />
        <div class="ctn-listinfo2">
            <p class="txt-def2">주문번호 : {{ orders.impUid }}</p>
            <p class="txt-def2">고객이름 : {{ orders.name }}</p>
            <p class="txt-def2">전화번호 : {{ orders.phoneNumber }}</p>
            <p class="txt-def2">주문날짜 : {{ formatedDate }}</p>

        </div>
        <div class="ctn-listinfo2">
            <p class="txt-def2">사용포인트 : {{ orders.usedPoint }} points</p>
            <p class="txt-def2">배송비용 : {{ orders.deliveryCost }} 원</p>
            <p class="txt-def2">총 가격 : {{ orders.totalPrice }} 원</p>
            <p class="txt-def2">배송주소 : {{ orders.address }}</p>
        </div>
        <div v-if="showControl == true" class="ctn-listbuttons">
            <p :class="getStatusClass(orders.orderStatus)">{{ formatedOrderStatus }}</p>
            <button class="btn-tagaction" :disabled="isCancelDisabled" @click="cancelOrders">
                <Icon icon="iconoir:trash" width="20px" height="20px" style="color: #ffffff" />
                주문 취소
            </button>
            <button class="btn-tagaction" @click="completeOrders">
                <Icon icon="iconoir:check" width="20px" height="20px" style="color: #ffffff" />
                주문 확정
            </button>
            <button class="btn-tagaction" @click="goOrders">
                <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
                정보 보기
            </button>
        </div>
        <div v-if="showControl == false" class="ctn-buttons">
            <button class="btn-tagaction" @click="completeOrders">
                <Icon icon="iconoir:check" width="20px" height="20px" style="color: #ffffff" />
                배송 확정
            </button>
            <router-link v-if="orders && route.params.storeIdx" class="btn-tagaction" :to="`/orders/${orders.ordersIdx}?storeIdx=${route.params.storeIdx}`">
                <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
                정보 보기
            </router-link>
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

// 주문 정보 보기
const goOrders = () => {
    router.push(`/orders/${props.orders.ordersIdx}`);
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
  if (statusString === "STOCK_READY" || statusString === "RESERVE_READY") return "btn-wating";
  else if (statusString === "STOCK_CANCEL" || statusString === "RESERVE_CANCEL") return "btn-cancel";
  else if (statusString === "STOCK_COMPLETE" || statusString === "RESERVE_COMPLETE") return "btn-complete";
  else if (statusString === "STOCK_DELIVERY" || statusString === "RESERVE_DELIVERY") return "btn-active";
  return "";
};

</script>