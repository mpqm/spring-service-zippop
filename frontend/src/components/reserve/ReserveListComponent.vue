<template>
  <div class="store-list-item">
    <div class="store-info1">
      <p class="t1">예약 인원수 : {{ reserve.reservePeople }}</p>
      <p class="t2">예약 시작 날짜 : {{ reserve.reserveStartDate }} </p>
      <CountDownTimer :targetTime="reserve.reserveStartTime" :flag="false"></CountDownTimer>
    </div>
    <div v-if="showControl" class="btn-container">
      <button  class="normal-btn" @click="goReserve">
        <img src="../../assets/img/reserve-none.png" alt="">
        &nbsp;<p>예약 참여</p>
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useRouter } from "vue-router";
import CountDownTimer from "../common/CountDownTimer.vue";
const router = useRouter();
// props 정의(store, showControl)
const props = defineProps({
  reserve: Object,
  showControl: Boolean,
});

// function formatTime(dateTimeString) {
//   if (!dateTimeString) return "";
//   // 문자열을 자르기: 'T' 이후 부분 가져오기
//   const timePart = dateTimeString.split("T")[1];
//   return timePart ? timePart.slice(0, 5) : "";
// }

const goReserve = () => {
  router.push(`/reserve/${props.reserve.reserveIdx}`);
}
</script>

<style scoped>
.store-list-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.store-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 12px;
}

.store-info1 {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.store-info2 {
  padding: 10px;
  justify-content: space-between;
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.t1 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: bold;
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

.like-img {
  object-fit: cover;
  width: auto;
  height: 20px;
  padding: 5px;
  vertical-align: middle;
}

.people-img {
  object-fit: cover;
  width: auto;
  height: 30px;
  vertical-align: middle;
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
}

.normal-btn {
  display: flex;
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
  align-items: center;
  justify-content: center;
}
</style>