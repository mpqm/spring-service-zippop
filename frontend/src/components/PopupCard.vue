<template>
  <div class="ctn-card">
    <div class="ctn-cardmedia">
      <img class="img-card" v-if="popup.getPopupImageResList && popup.getPopupImageResList.length" :src="popup.getPopupImageResList[0].popupImageUrl" :alt="popup.popupName" />
      <div class="img-card img-cardfallback" v-else><span>ZIPPOP</span></div>
      <span class="txt-cardcategory">{{ popup.category }}</span>
    </div>
    <div class="ctn-cardbody">
    <p class="txt-def1">{{ popup.popupName }}</p>
    <p class="txt-carddate">{{ popup.popupStartDate }} — {{ popup.popupEndDate }}</p>
    <div class="ctn-tagbutton">

      <button class="btn-tagdefault btn-like" :class="{ active: authStore.isLoggedIn && isLiked }" @click="toggleLike" aria-label="좋아요">
        <Icon :icon="authStore.isLoggedIn && isLiked ? 'heart-solid' : 'heart'" width="20px" height="20px" />{{ currentLikeCount }}
      </button>
      
      <span class="btn-tagdefault"><Icon icon="iconoir:user" width="20px" height="20px"/>{{ popup.totalPeople }}</span>
    </div>
      <div class="ctn-cardactions">
        <button
          v-if="showReserveAction"
          class="btn-tagaction btn-reserveaction"
          :class="{ active: isReservationActive }"
          type="button"
          :disabled="!isReservationActive"
          :aria-disabled="!isReservationActive"
          aria-label="예약 확인"
          title="예약 확인"
          @click="goReserveTab"
        >
          <Icon icon="iconoir:bell" width="20px" height="20px" />예약 확인
        </button>

        <button v-if="redirectToGoodsDetail" class="btn-tagaction" type="button" aria-label="상세보기" title="상세보기" @click="goGoodsDetail">
          <Icon icon="iconoir:eye" width="20px" height="20px" />상세보기
        </button>

        <button v-else class="btn-tagaction" type="button" aria-label="상세보기" title="상세보기" @click="goPopupDetail">
          <Icon icon="iconoir:eye" width="20px" height="20px" />상세보기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useNow } from "@/common";
const currentTime = useNow();
import { computed, defineProps, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { usePopupStore } from "@/stores/popupStore";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/authStore";
import { useAccountStore } from "@/stores/accountStore";

const props = defineProps({
  popup: Object,
  redirectToGoodsDetail: Boolean,
  mainTab: { type: String, default: '' },
  showReserveAction: { type: Boolean, default: false },
});

const router = useRouter();
const popupStore = usePopupStore();
const authStore = useAuthStore();
const accountStore = useAccountStore();
const toast = useToast();

const isLiked = ref(popupStore.likeList.some(p => p.popupIdx === props.popup?.popupIdx));
const currentLikeCount = ref(props.popup?.likeCount || 0);
const isReservationActive = computed(() => {
  if (!props.popup?.popupStartDate || !props.popup?.popupEndDate) return false;
  const start = new Date(`${props.popup.popupStartDate}T00:00:00`).getTime();
  const end = new Date(`${props.popup.popupEndDate}T23:59:59.999`).getTime();
  const now = currentTime.value;
  return Number.isFinite(start) && Number.isFinite(end)
    && now >= start
    && now <= end
    && props.popup.popupStatus !== 'POPUP_END';
});

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
  router.push({
    path: `/popup/${props.popup.popupIdx}`,
    query: props.mainTab ? { mainTab: props.mainTab } : {},
  });
};

const goGoodsDetail = () => {
  router.push(`/goods/${props.popup.popupIdx}`);
};

const goReserveTab = () => {
  router.push({
    path: `/popup/${props.popup.popupIdx}`,
    query: { mainTab: 'reserve' },
  });
};

const toggleLike = async () => {
  if (!authStore.isLoggedIn) {
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
