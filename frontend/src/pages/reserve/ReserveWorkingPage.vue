<template>
hihihihihihihihi
<button @click="cancel">예약취소</button>
</template>
<script setup>
import { useReserveStore } from '@/stores/useReserveStore';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
const reserveStore = useReserveStore();
const router = useRouter()
const route = useRoute()
const toast = useToast()

const cancel = async() => {
  const res = await reserveStore.cancel(route.params.reserveIdx);
  if(res.success) {
    router.push("/")
    toast.success(res.message)
  } else {
    router.push("/")
    toast.error(res.message)
  }
}
</script>
<style>
</style>
