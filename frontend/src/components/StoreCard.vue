<template>
  <div class="ctn-card">

    <p class="txt-def1">{{ store.storeName }}</p>

    <div class="ctn-tagbutton">

      <button class="btn-tagdefault">{{ store.category }}</button>

      <button class="btn-tagdefault" :class="{ active: isLiked }" @click="toggleLike"><Icon icon="iconoir:thumbs-up" width="20px" height="20px"  />{{ currentLikeCount }}</button>
      
      <button class="btn-tagdefault"><Icon icon="iconoir:user" width="20px" height="20px"/>{{ store.totalPeople }}</button>

      <button class="btn-tagdefault">{{ store.storeStartDate }}<span class="divider">~</span>{{ store.storeEndDate }}</button>
      
      <button v-if="redirecToGoodsDetail" class="btn-tagaction" @click="goGoodsDetail"><Icon icon="iconoir:eye" width="20px" height="20px"/>상세보기</button>

      <button v-else class="btn-tagaction" @click="goStoreDetail"><Icon icon="iconoir:eye" width="20px" height="20px" />상세보기</button>
    
    </div>

    <img class="img-card" v-if="store.searchStoreImageResList && store.searchStoreImageResList.length" :src="store.searchStoreImageResList[0].storeImageUrl" alt="N/A" />
    
  </div>
</template>

<script setup>
import { defineProps, ref, watch } from "vue";
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

// 좋아요 상태 관리
const isLiked = ref(storeStore.likeList.some(store => store.storeIdx === props.store.storeIdx));
const currentLikeCount = ref(props.store.likeCount || 0);

// props.store가 변경되면 좋아요 상태 및 수 업데이트
watch(() => props.store, (newStore) => {
  if (newStore) {
    isLiked.value = storeStore.likeList.some(store => store.storeIdx === newStore.storeIdx);
    currentLikeCount.value = newStore.likeCount || 0;
  }
}, { immediate: true, deep: true });

// storeStore의 좋아요 배열이 변경되면 상태 업데이트
watch(() => storeStore.likeList, () => {
  isLiked.value = storeStore.likeList.some(store => store.storeIdx === props.store.storeIdx);
}, { deep: true });

// 스토어 상세 페이지 이동
const goStoreDetail = () => {
  router.push(`/store/${props.store.storeIdx}`);
}

// 굿즈 상세 페이지 이동
const goGoodsDetail = () => {
  router.push(`/goods/${props.store.storeIdx}`);
}

// 좋아요 토글 (유튜브 스타일)
const toggleLike = async () => {
  if (!authStore.isLoggedIn) {
    router.push("/");
    toast.error("로그인이 필요합니다.");
    return;
  }

  // 기업 회원인 경우 좋아요 불가
  if (authStore.userInfo.role === "ROLE_COMPANY") {
    toast.error("고객 회원만 좋아요를 누를 수 있습니다.");
    return;
  }

  // 낙관적 업데이트 (Optimistic Update)
  const wasLiked = isLiked.value;
  isLiked.value = !isLiked.value;
  currentLikeCount.value += isLiked.value ? 1 : -1;

  // API 호출
  const res = await storeStore.registerLike(props.store.storeIdx);
  
  if (!res.success) {
    isLiked.value = wasLiked;
    currentLikeCount.value += wasLiked ? 1 : -1;
    toast.error(res.message);
  }
}

</script>