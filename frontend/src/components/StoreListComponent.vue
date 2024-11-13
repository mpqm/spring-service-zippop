<template>
  <div class="store-list-item">
    <img
      class="store-img"
      v-if="store.searchStoreImageResList && store.searchStoreImageResList.length"
      :src="store.searchStoreImageResList[0].storeImageUrl"
      alt="store image"
    />
    <div class="store-info1">
      <p class="t1">{{ store.storeName }}</p>
      <p class="t2">{{ store.category }}</p>
      <p class="t3">{{ store.storeStartDate }} ~ {{ store.storeEndDate }}</p>
    </div>
    <div class="store-info2">
        <img class="like-img" src="../assets/img/like-fill.png" alt="" />&nbsp;{{ store.likeCount }}
        <img class="people-img" src="../assets/img/people.png" alt="" />&nbsp;{{ store.totalPeople }}
      </div>
    <div v-if="showControl == true" class="btn-container">
        <router-link class="ud-btn" :to="store ? `/store/${store.storeIdx}` : '#'">보기</router-link>
        <router-link class="ud-btn" :to="store ? `/mypage/company/store/update/${store.storeIdx}` : '#'">수정</router-link>
        <button class="ud-btn" @click="deleteStore">삭제</button>
    </div>
    <div v-if="showControl == false" class="btn-container">
        <router-link class="ud-btn" :to="store ? `/mypage/company/goods/${store.storeIdx}` : '#'">굿즈 보기</router-link>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useToast } from "vue-toastification";
import { useRouter } from "vue-router";

const toast = useToast();
const router = useRouter();
const storeStore = useStoreStore();
const props = defineProps({
  store: Object,
  showControl: Boolean,
});

const deleteStore = async() => {
    const res = await storeStore.delete(props.store.storeIdx);
    if(res.success){
        toast.success("팝업 스토어 삭제에 성공했습니다.")
        router.go(0)
    }else {
        toast.error("팝업 스토어 삭제에 실패했습니다.");
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

.like-img,
.people-img {
  width: 20px;
  height: 20px;
  margin-right: 5px;
  vertical-align: middle;
}

.btn-container{
    margin-right: 0;
    margin-left: auto;
    display: flex;
    gap: 20px;
}

.ud-btn{
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
