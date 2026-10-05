<template>
  <div class="ctn-list1">
    <img class="img-list" v-if="goods.getGoodsImageResList && goods.getGoodsImageResList.length" :src="goods.getGoodsImageResList[0].goodsImageUrl" alt="N/A"/>
    
    <div class="ctn-listinfo1">
      <h1 class="txt-def1">{{ goods.goodsName }}</h1>
      <button class="btn-tagdefault"><Icon icon="iconoir:coin" width="20px" height="20px"/><span>{{ goods.goodsPrice }}원 </span></button>
      <button class="btn-tagdefault"><Icon icon="iconoir:box-iso" width="20px" height="20px"/><span>{{ goods.goodsAmount }}개 </span></button>
    </div>

    <!-- 굿즈 예약 페이지 용 -->
    <div v-if="showControl == true" class="ctn-listbuttons">
      <button class="btn-tagaction" type="button" aria-label="상세보기" title="상세보기" @click="goGoodsDetail"><Icon icon="iconoir:eye" width="20px" height="20px"/></button>
      <button class="btn-tagaction" type="button" aria-label="장바구니 담기" title="장바구니 담기" @click="addToCart"><Icon icon="iconoir:cart" width="20px" height="20px"/></button>
    </div>

    <!-- 팝업 상세 페이지 -->
    <div v-if="showControl == false" class="ctn-listbuttons">
      <button class="btn-tagaction" type="button" aria-label="상세보기" title="상세보기" @click="goGoodsDetail"><Icon icon="iconoir:eye" width="20px" height="20px"/></button>
    </div>

  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/authStore";
import { useCartStore } from "@/stores/cartStore";
import { Icon } from "@iconify/vue";

const props = defineProps({
  goods: Object,
  showControl: Boolean,
  popupIdx: Number,
});

const emit = defineEmits(['cartUpdated']);

const toast = useToast();
const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();

const goGoodsDetail = () => {
  router.push(`/goods/${props.popupIdx}/${props.goods.goodsIdx}`);
};

const addToCart = async () => {
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
    emit('cartUpdated');
  } else {
    toast.error(res.message);
  }
};
</script>
