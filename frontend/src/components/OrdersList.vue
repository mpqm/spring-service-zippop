<template>
    <div class="ctn-list1">
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
        <!-- 고객 주문 관리 -->
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
        <!-- 기업 주문 관리 -->
        <div v-if="showControl == false" class="ctn-buttons">
            <button class="btn-tagaction" @click="completeOrders">
                <Icon icon="iconoir:check" width="20px" height="20px" style="color: #ffffff" />
                배송 확정
            </button>
            <router-link v-if="orders && route.params.popupIdx" class="btn-tagaction" :to="`/orders/${orders.ordersIdx}?popupIdx=${route.params.popupIdx}`">
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
import { useOrdersStore } from "@/stores/ordersStore";
import { Icon } from "@iconify/vue";

const props = defineProps({
    orders: Object,
    showControl: Boolean,
});

const ordersStore = useOrdersStore();
const toast = useToast();
const route = useRoute();
const router = useRouter();

const formatedDate = ref("");
const formatedOrderStatus = ref("");

onMounted(async () => {
    formatedDate.value = formatDate(props.orders.createdAt);
    formatedOrderStatus.value = formatOrdersStatus(props.orders.orderStatus);
});

const cancelOrders = async () => {
    const cancelStatus = props.orders.orderStatus?.startsWith('STOCK') ? 'STOCK_CANCEL' : 'RESERVE_CANCEL';
    const res = await ordersStore.updateOrders(props.orders.ordersIdx, { status: cancelStatus });
    if (res.success) {
        toast.success(res.message);
        router.go(0);
    } else {
        toast.error(res.message);
    }
};

const completeOrders = async () => {
    const completeStatus = props.orders.orderStatus === 'STOCK_READY' ? 'STOCK_COMPLETE' : 'RESERVE_COMPLETE';
    if (props.showControl) {
        // 고객: 주문 확정
        const res = await ordersStore.updateOrders(props.orders.ordersIdx, { status: completeStatus });
        if (res.success) {
            toast.success(res.message);
            router.go(0);
        } else {
            toast.error(res.message);
        }
    } else {
        // 기업: 배송 확정
        const res = await ordersStore.updateOrders(props.orders.ordersIdx, { popupIdx: Number(route.params.popupIdx), status: completeStatus });
        if (res.success) {
            toast.success(res.message);
            router.go(0);
        } else {
            toast.error(res.message);
        }
    }
};

const goOrders = () => {
    router.push(`/orders/${props.orders.ordersIdx}`);
};

const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
};

const formatOrdersStatus = (statusString) => {
    if (statusString === "STOCK_READY") return "재고 굿즈 결제 완료";
    else if (statusString === "STOCK_CANCEL") return "재고 굿즈 결제 취소";
    else if (statusString === "STOCK_COMPLETE") return "재고 굿즈 주문 확정";
    else if (statusString === "STOCK_DELIVERY") return "재고 굿즈 배달 중";
    else if (statusString === "RESERVE_READY") return "예약 굿즈 결제 완료";
    else if (statusString === "RESERVE_CANCEL") return "예약 굿즈 결제 취소";
    else if (statusString === "RESERVE_COMPLETE") return "예약 굿즈 주문 확정";
    else if (statusString === "RESERVE_DELIVERY") return "예약 굿즈 배달 중";
    return statusString;
};

const isCancelDisabled = computed(() => {
    return props.orders.orderStatus === "STOCK_COMPLETE" || props.orders.orderStatus === "RESERVE_COMPLETE";
});

const getStatusClass = (statusString) => {
    if (statusString === "STOCK_READY" || statusString === "RESERVE_READY") return "btn-wating";
    else if (statusString === "STOCK_CANCEL" || statusString === "RESERVE_CANCEL") return "btn-cancel";
    else if (statusString === "STOCK_COMPLETE" || statusString === "RESERVE_COMPLETE") return "btn-complete";
    else if (statusString === "STOCK_DELIVERY" || statusString === "RESERVE_DELIVERY") return "btn-active";
    return "";
};
</script>
