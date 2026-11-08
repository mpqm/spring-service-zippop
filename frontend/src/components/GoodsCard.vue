<template>
  <div class="ctn-card">
    <div class="ctn-cardmedia">
      <img class="img-card" v-if="goods.getGoodsImageResList && goods.getGoodsImageResList.length" :src="goods.getGoodsImageResList[0].goodsImageUrl" :alt="goods.goodsName" />
      <div class="img-card img-cardfallback" v-else><span>ZIPPOP</span></div>
    </div>
    <div class="ctn-cardbody ctn-goodscardbody">
      <p class="txt-def1 txt-goodscardname">{{ goods.goodsName }}</p>
      <div class="ctn-goodscardmeta">
        <span class="btn-tagdefault"><Icon icon="iconoir:coin" width="20px" height="20px" class="ico-accent" />{{ goods.goodsPrice }}원</span>
        <span class="btn-tagdefault"><Icon icon="iconoir:box-iso" width="20px" height="20px" class="ico-accent" />{{ goods.goodsAmount }}개</span>
      </div>
      <div class="ctn-cardactions">
        <button class="btn-tagaction" type="button" aria-label="장바구니 담기" title="장바구니 담기" :disabled="!canAddToCart" :aria-disabled="!canAddToCart" @click="addToCart"><Icon icon="iconoir:cart" width="20px" height="20px" />장바구니</button>
        <button class="btn-tagaction" type="button" aria-label="상세보기" title="상세보기" @click="goGoodsDetail"><Icon icon="iconoir:eye" width="20px" height="20px" />상세보기</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "@/stores/authStore";
import { useAccountStore } from "@/stores/accountStore";
import { useCartStore } from "@/stores/cartStore";
import { computed, defineProps } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";

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
const accountStore = useAccountStore();
const canAddToCart = computed(() => props.showCart &&
  (!authStore.isLoggedIn || accountStore.userInfo?.role === "ROLE_CUSTOMER"));

const goGoodsDetail = async () => {
  router.push(`/goods/${props.popupIdx}/${props.goods.goodsIdx}`);
};

const addToCart = async () => {
  if (!canAddToCart.value) return;
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
