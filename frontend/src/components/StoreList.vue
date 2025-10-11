<template>
  <div class="ctn-list1">
    
    <img class="img-list" v-if="store.searchStoreImageResList && store.searchStoreImageResList.length" :src="store.searchStoreImageResList[0].storeImageUrl" alt="store image" />
    
    <div class="ctn-listinfo1">
      <h4 class="txt-def1">{{ store.storeName }}</h4>
      <button class="btn-tagdefault">{{ store.category }}</button>
      <button class="btn-tagdefault">{{ store.storeStartDate }}<span class="divider">~</span>{{ store.storeEndDate }}</button>
      <button class="btn-tagdefault"><Icon icon="iconoir:thumbs-up" width="20px" height="20px" style="color: #00c7ae" /> {{ store.likeCount }}</button>
      <button class="btn-tagdefault"><Icon icon="iconoir:user" width="20px" height="20px" style="color: #00c7ae" /> {{ store.totalPeople }}</button>
    </div>

    <!-- CartManagement1Page용 -->
    <div v-if="showControl === 3" class="ctn-listbuttons">
      <button class="btn-tagaction" @click="goCart">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
        카트 보기
      </button>
      <button class="btn-tagaction" @click="deleteCart">
        <Icon icon="iconoir:trash" width="20px" height="20px" style="color: #ffffff" />
        비우기
      </button>
    </div>

    <!-- LikeManagePage용 -->
    <div v-if="showControl === 6" class="ctn-listbuttons">
      <button class="btn-tagaction" @click="goStore"><Icon icon="iconoir:eye" width="20px" height="20px"/>팝업 보기</button>
      <button class="btn-tagaction" @click="registerLike"><Icon icon="iconoir:thumbs-up" width="20px" height="20px"/>좋아요 취소</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useCartStore } from "@/stores/useCartStore";
import { useToast } from "vue-toastification";
import { useRouter } from "vue-router";

// props 정의(store, showControl)
const props = defineProps({
  store: Object,
  showControl: Number,
});

// store, router, route, toast
const toast = useToast();
const router = useRouter();
const storeStore = useStoreStore();
const cartStore = useCartStore();

// 카트 보기
const goCart = () => {
  router.push(`/mypage/customer/cart/${props.store.storeIdx}`);
}

// 스토어 보기
const goStore = () => {
  router.push(`/store/${props.store.storeIdx}`);
}

// 좋아요 등록/취소
const registerLike = async () => {
  const res = await storeStore.registerLike(props.store.storeIdx);
  if (res.success) {
    toast.success(res.message)
  } else {
    toast.error(res.message);
  }
}

  // 카트 삭제(=전체 카트 아이템 삭제) 삭제
const deleteCart = async () => {
  const res = await cartStore.deleteCart(props.store.storeIdx)
  if (res.success) {
    router.go(0);
  } else {
    toast.error(res.message);
  }
  
};

</script>


