<template>
  <div class="ctn-card">

    <p class="txt-def1">{{ popup.popupName }}</p>

    <div class="ctn-tagbutton">

      <button class="btn-tagdefault">{{ popup.category }}</button>

      <button class="btn-tagdefault" :class="{ active: isLiked }" @click="toggleLike">
        <Icon icon="iconoir:thumbs-up" width="20px" height="20px" />{{ currentLikeCount }}
      </button>
      
      <button class="btn-tagdefault"><Icon icon="iconoir:user" width="20px" height="20px"/>{{ popup.totalPeople }}</button>

      <button class="btn-tagdefault">{{ popup.popupStartDate }}<span class="divider">~</span>{{ popup.popupEndDate }}</button>
      
      <button v-if="redirectToGoodsDetail" class="btn-tagaction" @click="goGoodsDetail">
        <Icon icon="iconoir:eye" width="20px" height="20px"/>상세보기
      </button>

      <button v-else class="btn-tagaction" @click="goPopupDetail">
        <Icon icon="iconoir:eye" width="20px" height="20px" />상세보기
      </button>
    
    </div>

    <img class="img-card" v-if="popup.getPopupImageResList && popup.getPopupImageResList.length" :src="popup.getPopupImageResList[0].popupImageUrl" alt="N/A" />
    
  </div>
</template>

<script setup>
import { defineProps, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { usePopupStore } from "@/stores/popupStore";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/authStore";
import { useAccountStore } from "@/stores/accountStore";
import { Icon } from "@iconify/vue";

const props = defineProps({
  popup: Object,
  redirectToGoodsDetail: Boolean,
});

const router = useRouter();
const popupStore = usePopupStore();
const authStore = useAuthStore();
const accountStore = useAccountStore();
const toast = useToast();

const isLiked = ref(popupStore.likeList.some(p => p.popupIdx === props.popup?.popupIdx));
const currentLikeCount = ref(props.popup?.likeCount || 0);

watch(() => props.popup, (newPopup) => {
  if (newPopup) {
    isLiked.value = popupStore.likeList.some(p => p.popupIdx === newPopup.popupIdx);
    currentLikeCount.value = newPopup.likeCount || 0;
  }
}, { immediate: true, deep: true });

watch(() => popupStore.likeList, () => {
  isLiked.value = popupStore.likeList.some(p => p.popupIdx === props.popup?.popupIdx);
}, { deep: true });

const goPopupDetail = () => {
  router.push(`/popup/${props.popup.popupIdx}`);
};

const goGoodsDetail = () => {
  router.push(`/goods/${props.popup.popupIdx}`);
};

const toggleLike = async () => {
  if (!authStore.isLoggedIn) {
    router.push("/");
    toast.error("로그인이 필요합니다.");
    return;
  }

  if (accountStore.userInfo.role === "ROLE_COMPANY") {
    toast.error("고객 회원만 좋아요를 누를 수 있습니다.");
    return;
  }

  const wasLiked = isLiked.value;
  isLiked.value = !isLiked.value;
  currentLikeCount.value += isLiked.value ? 1 : -1;

  const res = await popupStore.togglePopupLike(props.popup.popupIdx);
  
  if (!res.success) {
    isLiked.value = wasLiked;
    currentLikeCount.value += wasLiked ? 1 : -1;
    toast.error(res.message);
  }
};
</script>
