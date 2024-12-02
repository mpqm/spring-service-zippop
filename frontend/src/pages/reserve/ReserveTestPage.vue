<template>
    <HeaderComponent></HeaderComponent>
<button @click="register">예약 등록</button>
<button @click="enroll">예약 신청</button>
<button @click="status">예약 상태</button>
<button @click="cancel">예약 취소</button>
{{ reserveStatus }}
</template>
<script setup>
import { useReserveStore } from '@/stores/useReserveStore';
import HeaderComponent from '@/components/common/HeaderComponent.vue';
import { ref } from 'vue';
// import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
const reserveStroe = useReserveStore();
const toast = useToast();
const reserveStatus = ref("");

const register = async() => {
    const req = {
        storeIdx: 1,
        reservePeople: 2,
        expireTime: 10,
    }
    const res = await reserveStroe.register(req);
    if(res.success){
        toast.success(res.message);
    } else {
        toast.error(res.message);
    }
}

const status = async() => {
    const res = await reserveStroe.status(14);
    if(res.success){
        status.value = res.message;
    } else {
        toast.error(res.message);
    }
}

const enroll = async() => {
    const res = await reserveStroe.enroll(14);
    if(res.success){
        toast.success(res.message);
    } else {
        toast.error(res.message);
    }
}

const cancel = async() => {
    const res = await reserveStroe.cancel(14);
    if(res.success){
        toast.success(res.message);
    } else {
        toast.error(res.message);
    }
}

</script>
<style>
</style>
