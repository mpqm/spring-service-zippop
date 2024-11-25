<template>
  <div class="store-list-item">
    <img class="store-img" v-if="store.searchStoreImageResList && store.searchStoreImageResList.length"
      :src="store.searchStoreImageResList[0].storeImageUrl" alt="store image" />
    <div class="store-info1">
      <p class="t1">{{ store.storeName }}</p>
      <p class="t2">{{ store.category }}</p>
      <p class="t3">{{ store.storeStartDate }} ~ {{ store.storeEndDate }}</p>
    </div>
    <div class="store-info2">
      <img class="like-img" src="../../assets/img/like-fill.png" alt="" />&nbsp;{{ store.likeCount }}
      <img class="people-img" src="../../assets/img/people.png" alt="" />&nbsp;{{ store.totalPeople }}
    </div>
    <!-- StoreManagePage용 -->
    <div v-if="showControl === 0" class="btn-container">
      <router-link class="ud-btn" :to="store ? `/store/${store.storeIdx}` : '#'">보기</router-link>
      <router-link class="ud-btn" :to="store ? `/mypage/company/store/update/${store.storeIdx}` : '#'">수정</router-link>
      <button class="ud-btn" @click="deleteStore">삭제</button>
    </div>
    <!-- GoodsManage1Page용 -->
    <div v-if="showControl === 1" class="btn-container">
      <router-link class="ud-btn" :to="store ? `/mypage/company/goods/${store.storeIdx}` : '#'">굿즈 보기</router-link>
    </div>
    <!-- CompanyOrdersManage1Page1 -->
    <div v-if="showControl === 2" class="btn-container">
      <router-link class="ud-btn" :to="store ? `/mypage/company/orders/${store.storeIdx}` : '#'">거래내역 보기</router-link>
    </div>
    <!-- CartManagement1Page용 -->
    <div v-if="showControl === 3" class="btn-container">
      <router-link class="ud-btn" :to="store ? `/mypage/customer/cart/${store.storeIdx}` : '#'">카트 보기</router-link>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
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

// 스토어 삭제
const deleteStore = async () => {
  const res = await storeStore.deleteStore(props.store.storeIdx);
  if (res.success) {
    toast.success(res.message)
    router.go(0)
  } else {
    toast.error(res.message);
  }
}
</script>

<style scoped>
.store-list-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.store-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 12px;
}

.store-info1 {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.store-info2 {
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
  display: flex;
  align-items: center;
}

.t3 {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}

.like-img {
  object-fit: cover;
  width: auto;
  height: 20px;
  padding: 5px;
  vertical-align: middle;
}

.people-img {
  object-fit: cover;
  width: auto;
  height: 30px;
  vertical-align: middle;
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
  text-decoration: #000;
}
</style>
