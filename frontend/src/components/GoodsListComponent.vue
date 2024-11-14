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
    </div>
    <div class="goods-info2">
        <img class="coin-img" src="../assets/img/coin.png" alt="" />&nbsp;{{ goods.goodsPrice }}
        <img class="stock-img" src="../assets/img/stock.png" alt="" />&nbsp;{{ goods.goodsAmount }}
      </div>
    <div v-if="showControl == true" class="btn-container">
      <router-link class="ud-btn" :to="goods ? `/goods/${goods.goodsIdx}` : '#'">보기</router-link>
      <router-link class="ud-btn" :to="goods ? `/mypage/company/goods/${route.params.storeIdx}/update/${goods.goodsIdx}` : '#'">수정</router-link>
      <button class="ud-btn" @click="deleteGoods">삭제</button>
    </div>
    <div v-if="showControl == false" class="btn-container">
      <button class="ud-btn" @click="openModal">상세 보기</button>
    </div>
  </div>

  <!-- 모달 컴포넌트 사용 -->
  <GoodsModalComponent v-if="isModalOpen" :goods="goods" :isModalOpen="isModalOpen" :closeModal="closeModal" />
</template>

<script setup>
import { defineProps, ref } from "vue";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useToast } from "vue-toastification";
import { useRoute, useRouter } from "vue-router";
import GoodsModalComponent from "@/components/GoodsModalComponent.vue"; // 모달 컴포넌트 임포트

const toast = useToast();
const router = useRouter();
const route = useRoute();
const goodsStore = useGoodsStore();

const props = defineProps({
  goods: Object,
  showControl: Boolean,
});

const isModalOpen = ref(false);

const deleteGoods = async () => {
  const res = await goodsStore.delete(props.goods.goodsIdx);
  if (res.success) {
    toast.success(res.message);
    router.go(0);
  } else {
    toast.error(res.message);
  }
};

const openModal = () => {
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};
</script>

<style scoped>
/* 기존 스타일 그대로 유지 */
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
</style>
