<template>
    <div class="register-page">
      <form class="register-form" @submit.prevent="register">
        <div>
          <label>팝업 스토어 예약 인원</label>
          <input class="register-input" v-model="reservePeople" type="numver" placeholder="팝업 스토어 예약 인원을 입력해주세요." />
        </div>
        <div>
          <label>팝업스토어 예약 시작일</label>
          <div class="date-picker">
            <input class="register-input" v-model="reserveStartDate" type="date" placeholder="시작일" />
          </div>
        </div>
        <div>
          <label>팝업스토어 예약 시작시간/종료시간</label>
          <div class="date-picker">
            <input class="register-input" :min="'09:00'"  v-model="reserveStartTime" type="time" placeholder="시작시간" />
            <input class="register-input" v-model="reserveEndTime" type="time" placeholder="종료시간" />
          </div>
        </div>
        <div class="btn-container">
          <button type="submit" class="register-btn">등록</button>
          <router-link :to="`/mypage/company/reserve/${route.params.storeIdx}`" class="register-btn">취소</router-link>
        </div>
      </form>
    </div>
  </template>
  
  <script setup>
  import { useReserveStore } from "@/stores/useReserveStore";
import { ref } from "vue";
  import { useRoute, useRouter } from "vue-router";
  import { useToast } from "vue-toastification";
  
  // reserve, router, route, toast
  const reserveStore = useReserveStore()
  const router = useRouter();
  const route = useRoute();
  const toast = useToast();
  
  // 변수(reserve)
  const reservePeople = ref(0);
  const reserveStartDate = ref("");
  const reserveStartTime = ref("");
  const reserveEndTime = ref("");

  // 스토어 등록
  const register = async () => {
    const formattedStartTime = `${reserveStartDate.value}T${reserveStartTime.value}:00`;
    const formattedEndTime = `${reserveStartDate.value}T${reserveEndTime.value}:00`;

    const req = {
        storeIdx: route.params.storeIdx,
        reservePeople: reservePeople.value,
        reserveStartDate: reserveStartDate.value,
        reserveStartTime: formattedStartTime,
        reserveEndTime: formattedEndTime
    };
    const res = await reserveStore.register(req);
    if (res.success) {
      router.push(`/mypage/company/reserve/${route.params.storeIdx}`);
      toast.success(res.message);
    } else {
      toast.error(res.message);
    }
  };
  
  </script>
  
  <style scoped>
  .register-page {
    padding: 5px;
    border-radius: 8px;
  }
  
  .register-form {
    padding: 10px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    row-gap: 1rem;
  }
  
  .register-input {
    border: 1px solid #e1e1e1;
    border-radius: 4px;
    display: block;
    padding: 1rem;
    font-size: 1rem;
    font-weight: 400;
    line-height: 1.5;
    width: 100%;
    box-sizing: border-box;
    color: #323232;
    background-color: #fff;
    resize: none;
  }
  
  #file {
    display: none;
  }
  
  .file-preview {
    display: flex;
    align-self: center;
    width: fit-content;
    height: fit-content;
  }
  
  .file-upload-btn {
    background-color: #00c7ae;
    color: white;
    padding: 0.75rem;
    text-align: center;
    cursor: pointer;
    border-radius: 5px;
    margin-top: 1rem;
  }
  
  .btn-container {
    display: flex;
    gap: 10px;
  }
  
  .register-btn {
    display: inline-block;
    text-align: center;
    vertical-align: middle;
    width: 100%;
    user-select: none;
    margin-top: 0.75rem;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.6875rem 0.75rem;
    font-size: 1rem;
    line-height: 1.5;
    border-radius: 0.25rem;
    text-decoration: none;
  }
  
  .file-preview {
    display: flex;
    flex-wrap: wrap;
    margin-top: 1rem;
    gap: 10px;
  }
  
  .address {
    position: relative;
    display: flex;
    column-gap: 10px;
    flex-direction: row;
  }
  
  .file-preview-item img {
    width: 100px;
    height: 100px;
    object-fit: cover;
  }
  
  .date-picker {
    display: flex;
    gap: 10px;
  }
  
  .file-upload-btn:hover,
  .register-btn:hover {
    opacity: 0.8;
  }
  </style>
  