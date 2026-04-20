<template>
    <div class="ctn-card">
        <p class="txt-def1">{{ reserve.searchStoreRes.storeName }}</p>
        <div class="ctn-tagbutton">
            <button class="btn-tagdefault">{{ reserve.searchStoreRes.category }}</button>
            <button class="btn-tagdefault">
                <Icon icon="iconoir:thumbs-up" width="20px" height="20px" style="color: #00c7ae" />
                {{ reserve.searchStoreRes.likeCount }}
            </button>
            <button class="btn-tagdefault">
            <Icon icon="iconoir:user" width="20px" height="20px" style="color: #00c7ae" />{{ reserve.searchStoreRes.totalPeople }}</button>
            <button class="btn-tagdefault"><Icon icon="iconoir:fire-flame" width="20px" height="20px" style="color: #00c7ae" />{{ reserve.reservePeople }}</button>
            <button class="btn-tagdefault"> <Icon icon="iconoir:calendar-plus" width="20px" height="20px" style="color: #00c7ae" />{{ reserve.reserveStartDate }}</button>
            <button class="btn-tagdefault"> <Icon icon="iconoir:clock" width="20px" height="20px" style="color: #00c7ae" />{{ formatTime(reserve.reserveStartTime) }} ~ {{ formatTime(reserve.reserveEndTime) }}</button>
            <button v-if="redirecToGoodsDetail" class="btn-tagaction" @click="goGoodsDetail"><Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" /></button>
            <button v-else class="btn-tagaction" @click="goReserveDetail"><Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" /></button>
        
    </div>
    <div class="ctn-buttons">

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
    router.push(`/reserve/${props.reserve.popupIdx}/${props.reserve.reserveIdx}`);
}

function formatTime(dateTimeString) {
    if (!dateTimeString) return "";
    // 문자열을 자르기: 'T' 이후 부분 가져오기
    const timePart = dateTimeString.split("T")[1];
    return timePart ? timePart.slice(0, 5) : "";
}

// 굿즈 상세 페이지 이동
const goGoodsDetail = () => {
    router.push(`/goods/${props.reserve.popupIdx}`);
}

</script>