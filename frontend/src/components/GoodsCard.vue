<template>
  <div class="card-container">
    <p class="t1">{{ goods.goodsName }}</p>
    <p class="t2">{{ goods.storeName }}</p>
    <p class="t2">
      <Icon icon="iconoir:coin" width="20px" height="20px" style="color: #00c7ae" />{{ goods.goodsPrice }}원
      <Icon icon="iconoir:box-iso" width="20px" height="20px" style="color: #00c7ae" />{{ goods.goodsAmount }}개
    </p>
    <img class="card-img" v-if="goods.searchGoodsImageResList && goods.searchGoodsImageResList.length" :src="goods.searchGoodsImageResList[0].goodsImageUrl" alt="goods image" />
    <div class="btn-container">
      <button v-if="showControl" class="default-btn" @click="goGoodsDetail">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
      </button>
      <button class="default-btn" @click="registerCart">
        <Icon icon="iconoir:cart" width="20px" height="20px" style="color: #ffffff" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "@/stores/useAuthStore";
import { useCartStore } from "@/stores/useCartStore";
import { defineProps } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";

// props 정의(goods)
const props = defineProps({
  goods: Object,
  storeIdx: Number,
  showControl: Boolean,
});

// store, router, route, toast
const router = useRouter();
const toast = useToast();
const cartStore = useCartStore();
const authStore = useAuthStore();

// 굿즈 상세 페이지 이동 함수
const goGoodsDetail = async () => {
  router.push(`/goods/${props.storeIdx}/${props.goods.goodsIdx}`);
}

// 카트 등록
const registerCart = async () => {
  if (!authStore.isLoggedIn) {
    toast.error("로그인이 필요합니다.");
  } else {
    const req = {
      goodsIdx: props.goods.goodsIdx,
      storeIdx: props.storeIdx,
    }
    const res = await cartStore.register(req);
    if (res.success) {
      toast.success(res.message);
    } else {
      toast.error(res.message);
    }
  }
}

</script>