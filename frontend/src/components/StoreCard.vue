<template>
  <div class="card-container">
    <div class="card-info-container">
      <p>{{ store.storeName }}</p>
      <p>{{ store.category }}</p>
    </div>
    <p>{{ store.storeStartDate }} ~ {{ store.storeEndDate }}</p>
    <div class="card-info-container">
        <Icon icon="iconoir:thumbs-up" width="20px" height="20px" style="color: #00c7ae" />{{ store.likeCount }}
        <Icon icon="iconoir:user" width="20px" height="20px" style="color: #00c7ae" />{{ store.totalPeople }}
    </div>
    <img class="card-img" v-if="store.searchStoreImageResList && store.searchStoreImageResList.length" :src="store.searchStoreImageResList[0].storeImageUrl" alt="N/A" />
    <div class="btn-container">
      <button v-if="redirecToGoodsDetail" class="default-btn" @click="goGoodsDetail">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
      </button>
      <button v-else class="default-btn" @click="goStoreDetail">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
      </button>
      <button class="default-btn" @click="like">
        <Icon icon="iconoir:thumbs-up" width="20px" height="20px" style="color: #ffffff" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useRouter } from "vue-router";
import { useStoreStore } from "@/stores/useStoreStore";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/useAuthStore";

// props 정의 (store, 상세페이지 이동)
const props = defineProps({
  store: Object,
  redirecToGoodsDetail: Boolean,
});

// store, router, route, toast
const router = useRouter();
const storeStore = useStoreStore();
const authStore = useAuthStore();
const toast = useToast();

// 스토어 상세 페이지 이동
const goStoreDetail = () => {
  router.push(`/store/${props.store.storeIdx}`);
}

// 굿즈 상세 페이지 이동
const goGoodsDetail = () => {
  router.push(`/goods/${props.store.storeIdx}`);
}

// 좋아요 등록
const like = async () => {
  if (authStore.isLoggedIn) {
    const res = await storeStore.registerLike(props.store.storeIdx);
    if (res.success) {
      toast.success(res.message);
    } else {
      if (authStore.userInfo.role == "ROLE_COMPANY") {
        toast.error(res.message);
      } else {
        toast.error(res.message);
      }
    }
  } else {
    router.push("/");
    toast.error("로그인이 필요합니다.");
  }
}

</script>