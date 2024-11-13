<template>
  <div class="goods-list-item">
    <img
      class="goods-img"
      v-if="goods.searchGoodsImageResList && goods.searchGoodsImageResList.length"
      :src="goods.searchGoodsImageResList[0].goodsImageUrl"
      alt="goods image"
    />
    <div class="goods-info1">
      <p class="t1">{{ goods.goodsName }}</p>
      <p class="t2">{{ goods.goodsPrice }}원</p>
      <p class="t3">재고: {{ goods.goodsAmount }}개</p>
    </div>
    <div v-if="showControl" class="btn-container">
      <router-link class="ud-btn" :to="goods ? `/goods/${goods.goodsIdx}` : '#'">보기</router-link>
      <router-link class="ud-btn" :to="goods ? `/mypage/company/goods/${route.params.storeIdx}/update/${goods.goodsIdx}` : '#'">수정</router-link>
      <button class="ud-btn" @click="deleteGoods">삭제</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useToast } from "vue-toastification";
import { useRoute, useRouter } from "vue-router";

const toast = useToast();
const router = useRouter();
const route = useRoute();
const goodsStore = useGoodsStore();

const props = defineProps({
  goods: Object,
  showControl: Boolean,
});

const deleteGoods = async () => {
  const res = await goodsStore.delete(props.goods.goodsIdx);
  if (res.success) {
    toast.success("굿즈 삭제에 성공했습니다.");
    router.go(0);
  } else {
    toast.error("굿즈 삭제에 실패했습니다.");
  }
};
</script>

<style scoped>
.goods-list-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.goods-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 12px;
}

.goods-info1 {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-info2 {
  padding: 10px;
  justify-content: space-between;
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.t1 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: bold;
}

.t2 {
  margin: 0;
  font-size: 1.1rem;
  color: #333;
}

.t3 {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.goods-content {
  font-size: 0.9rem;
  color: #666;
}

.btn-container {
  margin-right: 0;
  margin-left: auto;
  display: flex;
  gap: 20px;
}

.ud-btn {
  display: block;
  text-align: center;
  width: auto;
  font-weight: 400;
  transition: opacity 0.2s ease-in-out;
  color: #fff;
  cursor: pointer;
  background-color: #00c7ae;
  border-color: #00c7ae;
  border: 0.0625rem solid transparent;
  padding: 0.5rem;
  border-radius: 0.25rem;
  text-decoration: none;
}
</style>
