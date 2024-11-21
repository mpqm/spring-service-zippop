<template>
  <div class="goods-card">
    <p class="t1">{{ goods.goodsName }}</p>
    <p class="t2">{{ goods.storeName }}</p>
    <p class="t2">
      <img class="coin-img" src="../../assets/img/coin.png" alt="" />{{ goods.goodsPrice }}원
      <img class="stock-img" src="../../assets/img/stock.png" alt="" />{{ goods.goodsAmount }}개
    </p>
    <img class="goods-img" v-if="goods.searchGoodsImageResList && goods.searchGoodsImageResList.length" :src="goods.searchGoodsImageResList[0].goodsImageUrl" alt="goods image" />
    <div class="btn-container">
      <button class="normal-btn" @click="goGoodsDetail"><img class="search-img" src="../../assets/img/search-none.png" alt=""></button>
      <button class="normal-btn" @click="registerCart"><img src="../../assets/img/cart-none.png" alt=""></button>
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
});

// store, router, route, toast
const router = useRouter();
const toast = useToast();
const cartStore = useCartStore();
const authStore = useAuthStore();

// 굿즈 상세 페이지 이동 함수
const goGoodsDetail = async () => {
  router.push(`/goods-detail/${props.goods.goodsIdx}`);
}

// 카트 등록
const registerCart = async () => {
  if (!authStore.isLoggedIn) {
    toast.error("로그인이 필요합니다.");
  } else {
    const req = {
      goodsIdx: props.goods.goodsIdx,
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

<style scoped>
.goods-card {
  width: auto;
  max-width: auto;
  border: 1px solid #ddd;
  padding: 12px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.t1 {
  font-size: 1.5rem;
  margin: 4px 0;
}

.t2 {
  margin: 4px 0;
}

.search-img {
  width: 20px;
  height: auto;
}

.goods-img {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  margin-top: 4px;
  object-fit: cover;
}

.coin-img {
  object-fit: cover;
  width: auto;
  height: 20px;
  vertical-align: middle;
}

.stock-img {
  object-fit: cover;
  width: auto;
  height: 20px;
  vertical-align: middle;
}

.btn-container {
  display: flex;
  width: auto;
  gap: 10px;
}

.normal-btn {
  margin-top: 10px;
  display: block;
  text-align: center;
  width: 100%;
  font-weight: 400;
  transition: opacity 0.2s ease-in-out;
  color: #fff;
  cursor: pointer;
  background-color: #00c7ae;
  border-color: #00c7ae;
  border: 0.0625rem solid transparent;
  padding: 0.5rem;
  border-radius: 0.25rem;
  text-decoration: #000;
}

.normal-btn:hover {
  opacity: 0.8;
}
</style>