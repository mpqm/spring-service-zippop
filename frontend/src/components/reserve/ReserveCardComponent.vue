<template>
    <div class="reserve-card">
        <p class="t1">{{ reserve.searchStoreRes.storeName }}</p>
        <p class="t2">{{ reserve.searchStoreRes.category }}</p>
        <p class="t2">
            <img class="like-img" src="../../assets/img/like-fill.png" alt="" />{{ reserve.searchStoreRes.likeCount }}
            <img class="people-img" src="../../assets/img/people.png" alt="" />{{ reserve.searchStoreRes.totalPeople }}
        </p>
        <div class="store-info1">
            <p class="t3">예약 인원수 : {{ reserve.reservePeople }}</p>
            <p class="t3">예약 시작 날짜 : {{ reserve.reserveStartDate }} </p>
            <p class="t4"> 예약 기간 : {{ formatTime(reserve.reserveStartTime) }} ~ {{ formatTime(reserve.reserveEndTime) }}
            </p>
        </div>
        <div class="btn-container">
            <button v-if="redirecToGoodsDetail" class="normal-btn" @click="goGoodsDetail"><img class="search-img" src="../../assets/img/search-none.png" alt=""></button>
            <button v-else class="normal-btn" @click="goreserveDetail"><img class="search-img" src="../../assets/img/search-none.png" alt=""></button>
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
const goreserveDetail = () => {
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

<style scoped>
.reserve-card {
    width: auto;
    max-width: auto;
    border: 1px solid #ddd;
    padding: 12px;
    border-radius: 8px;
    background-color: #fff;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
}

.t1 {
    font-size: 1.5rem;
    margin: 4px 0;
}

.t2 {
    margin: 4px 0;
}

.t3 {
    font-size: 11px;
}

.t4 {
    color: red;
    font-size: 13px;
}

.reserve-img {
    width: 100%;
    height: 200px;
    border-radius: 8px;
    margin-top: 4px;
    object-fit: cover;
}


.search-img {
    width: 20px;
    height: auto;
}

.like-img {
    object-fit: cover;
    width: auto;
    height: 20px;
    vertical-align: middle;
    padding-right: 5px;
}

.people-img {
    object-fit: cover;
    width: auto;
    height: 30px;
    vertical-align: middle;
}

.btn-container {
    display: flex;
    width: auto;
    gap: 10px;
}

.normal-btn {
    margin-top: 10px;
    display: block;
    text-align: center;
    width: 100%;
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
</style>