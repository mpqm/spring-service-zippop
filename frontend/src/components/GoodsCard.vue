<template>
  <div class="ctn-card">
    <div class="ctn-cardmedia">
      <img class="img-card" v-if="goods.getGoodsImageResList && goods.getGoodsImageResList.length" :src="goods.getGoodsImageResList[0].goodsImageUrl" :alt="goods.goodsName" />
      <div class="img-card img-cardfallback" v-else><span>ZIPPOP</span></div>
    </div>
    <div class="ctn-cardbody ctn-goodscardbody">
      <p class="txt-def1 txt-goodscardname">{{ goods.goodsName }}</p>
      <div class="ctn-goodscardmeta">
        <button class="btn-tagdefault"><Icon icon="iconoir:coin" width="20px" height="20px" class="ico-accent" />{{ goods.goodsPrice }}원</button>
        <button class="btn-tagdefault"><Icon icon="iconoir:box-iso" width="20px" height="20px" class="ico-accent" />{{ goods.goodsAmount }}개</button>
        <button class="btn-tagaction" type="button" aria-label="상세보기" title="상세보기" @click="goGoodsDetail"><Icon icon="iconoir:eye" width="20px" height="20px" /></button>
        <button class="btn-tagaction" type="button" aria-label="장바구니 담기" title="장바구니 담기" :disabled="!showCart" :aria-disabled="!showCart" @click="addToCart"><Icon icon="iconoir:cart" width="20px" height="20px" /></button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "@/stores/authStore";
import { useCartStore } from "@/stores/cartStore";
import { defineProps } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { Icon } from "@iconify/vue";

const props = defineProps({
  goods: Object,
  popupIdx: Number,
  showControl: Boolean,
  showCart: { type: Boolean, default: true },
});

const router = useRouter();
const toast = useToast();
const cartStore = useCartStore();
const authStore = useAuthStore();

const goGoodsDetail = async () => {
  router.push(`/goods/${props.popupIdx}/${props.goods.goodsIdx}`);
};

const addToCart = async () => {
  if (!props.showCart) return;
  if (!authStore.isLoggedIn) {
    toast.error("로그인이 필요합니다.");
    return;
  }
  const req = {
    goodsIdx: props.goods.goodsIdx,
    popupIdx: props.popupIdx,
  };
  const res = await cartStore.createCart(req);
  if (res.success) {
    toast.success(res.message);
  } else {
    toast.error(res.message);
  }
};
</script>
