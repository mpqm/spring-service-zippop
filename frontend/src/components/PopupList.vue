<template>
  <div class="ctn-list1">
    
    <img class="img-list" v-if="popup.getPopupImageResList && popup.getPopupImageResList.length" :src="popup.getPopupImageResList[0].popupImageUrl" alt="popup image" />
    
    <div class="ctn-listinfo1">
      <h4 class="txt-def1">{{ popup.popupName }}</h4>
      <button class="btn-tagdefault">{{ popup.category }}</button>
      <button class="btn-tagdefault">{{ popup.popupStartDate }}<span class="divider">~</span>{{ popup.popupEndDate }}</button>
      <button class="btn-tagdefault"><Icon icon="iconoir:thumbs-up" width="20px" height="20px" class="ico-accent" /> {{ popup.likeCount }}</button>
      <button class="btn-tagdefault"><Icon icon="iconoir:user" width="20px" height="20px" class="ico-accent" /> {{ popup.totalPeople }}</button>
    </div>

    <!-- CartManagePage1 용 -->
    <div v-if="showControl === 3" class="ctn-listbuttons">
      <button class="btn-tagaction" @click="goCart">
        <Icon icon="iconoir:eye" width="20px" height="20px" class="ico-inverse" />
        카트 보기
      </button>
      <button class="btn-tagaction" @click="deleteCart">
        <Icon icon="iconoir:trash" width="20px" height="20px" class="ico-inverse" />
        비우기
      </button>
    </div>

    <!-- LikeManagePage 용 -->
    <div v-if="showControl === 6" class="ctn-listbuttons">
      <button class="btn-tagaction" @click="goPopup"><Icon icon="iconoir:eye" width="20px" height="20px"/>팝업 보기</button>
      <button class="btn-tagaction" @click="toggleLike"><Icon icon="iconoir:thumbs-up" width="20px" height="20px"/>좋아요 취소</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { usePopupStore } from "@/stores/popupStore";
import { useCartStore } from "@/stores/cartStore";
import { useToast } from "vue-toastification";
import { useRouter } from "vue-router";
import { Icon } from "@iconify/vue";

const props = defineProps({
  popup: Object,
  showControl: Number,
});

const toast = useToast();
const router = useRouter();
const popupStore = usePopupStore();
const cartStore = useCartStore();

const goCart = () => {
  router.push(`/mypage/customer/cart/${props.popup.cartIdx}`);
};

const goPopup = () => {
  router.push(`/popup/${props.popup.popupIdx}`);
};

const toggleLike = async () => {
  const res = await popupStore.togglePopupLike(props.popup.popupIdx);
  if (res.success) {
    toast.success(res.message);
  } else {
    toast.error(res.message);
  }
};

const deleteCart = async () => {
  const res = await cartStore.deleteCarts(props.popup.cartIdx);
  if (res.success) {
    router.go(0);
  } else {
    toast.error(res.message);
  }
};
</script>
