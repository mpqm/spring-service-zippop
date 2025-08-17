<template>
    <div class="card-container">
        <p class="t1">{{ reserve.searchStoreRes.storeName }}</p>
        <p class="t2">{{ reserve.searchStoreRes.category }}</p>
        <p class="t2">
            <Icon icon="iconoir:thumbs-up" width="20px" height="20px" style="color: #00c7ae" />
            {{ reserve.searchStoreRes.likeCount }}
            <Icon icon="iconoir:user" width="20px" height="20px" style="color: #00c7ae" />
            {{ reserve.searchStoreRes.totalPeople }}
        </p>
        <div>
            <p class="t3">예약 인원수 : {{ reserve.reservePeople }}</p>
            <p class="t3">예약 시작 날짜 : {{ reserve.reserveStartDate }} </p>
            <p class="t4"> 예약 기간 : {{ formatTime(reserve.reserveStartTime) }} ~ {{ formatTime(reserve.reserveEndTime) }}</p>
        </div>
        <div class="btn-container">
            <button v-if="redirecToGoodsDetail" class="default-btn" @click="goGoodsDetail">
                <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
            </button>
            <button v-else class="default-btn" @click="goReserveDetail">
                <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
            </button>
        </div>
    </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useRouter } from "vue-router";


// props 정의 (reserve, 상세페이지 이동)
const props = defineProps({
    reserve: Object,
    redirecToGoodsDetail: Boolean,
});

// reserve, router, route, toast
const router = useRouter();

// 스토어 상세 페이지 이동
const goReserveDetail = () => {
    router.push(`/store/${props.reserve.storeIdx}`);
}

function formatTime(dateTimeString) {
    if (!dateTimeString) return "";
    // 문자열을 자르기: 'T' 이후 부분 가져오기
    const timePart = dateTimeString.split("T")[1];
    return timePart ? timePart.slice(0, 5) : "";
}

// 굿즈 상세 페이지 이동
const goGoodsDetail = () => {
    router.push(`/goods/${props.storeIdx}`);
}

</script>