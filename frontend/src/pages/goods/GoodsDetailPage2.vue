<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <div class="wrp-split">
        <div class="ctn-l50">
          <ImageSlider class="image-slider" :fileUrls="fileUrls"></ImageSlider>
        </div>
        <div class="ctn-r50">
          <p class="txt-def1">{{ goods.goodsName }}</p>
          <p class="txt-desc">{{ goods.goodsContent }}</p>
          <div class="ctn-tagbutton">
            <button class="btn-tagdefault">{{ goods.popupName }}</button>
            <button class="btn-tagdefault"><Icon icon="iconoir:coin" width="20px" height="20px" class="ico-accent" />{{ goods.goodsPrice }}원</button>
            <button class="btn-tagdefault"><Icon icon="iconoir:box-iso" width="20px" height="20px" class="ico-accent" />{{ goods.goodsAmount }}개</button>
            <button class="btn-tagaction" @click="createCart"><Icon icon="iconoir:cart" width="20px" height="20px" />장바구니</button>
            <button class="btn-tagaction" @click="router.back()"><Icon icon="iconoir:nav-arrow-left" width="20px" height="20px" />뒤로가기</button>
          </div>
        </div>
      </div>
    </div>
    <AppFooter></AppFooter>
  </div>
</template>

<script setup>
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import ImageSlider from "@/components/ImageSlider.vue";
import { Icon } from "@iconify/vue";
import { ref, onMounted } from "vue";
import { useGoodsStore } from "@/stores/goodsStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/authStore";
import { useCartStore } from "@/stores/cartStore";

const goodsStore = useGoodsStore();
const authStore = useAuthStore();
const cartStore = useCartStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

const goods = ref({});
const fileUrls = ref([]);

onMounted(async () => {
  await getGoods(route.params.goodsIdx);
});

const getGoods = async (goodsIdx) => {
  const res = await goodsStore.getGoods(goodsIdx);
  if (res.success) {
    goods.value = goodsStore.goods;
    if (goodsStore.goods.getGoodsImageResList && goodsStore.goods.getGoodsImageResList.length) {
      fileUrls.value = goodsStore.goods.getGoodsImageResList.map(image => image.goodsImageUrl);
    }
  } else {
    router.push("/");
    toast.error(res.message);
  }
};

const createCart = async () => {
  if (!authStore.isLoggedIn) {
    toast.error("로그인이 필요합니다.");
    return;
  }
  const req = {
    goodsIdx: route.params.goodsIdx,
    popupIdx: route.params.popupIdx,
  };
  const res = await cartStore.createCart(req);
  if (res.success) {
    toast.success(res.message);
  } else {
    toast.error(res.message);
  }
};
</script>
