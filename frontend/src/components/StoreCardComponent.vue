<template>
  <div class="store-card">
    <p class="t1">{{ store.storeName }}</p>
    <p class="t2">{{ store.category }}</p>
    <p class="t2">
      <img class="like-img" src="../assets/img/like-fill.png" alt="" />{{ store.likeCount }}
      <img class="people-img" src="../assets/img/people.png" alt="" />{{ store.totalPeople }}
    </p>
    <p class="t3">{{ store.storeStartDate }} ~ {{ store.storeEndDate }}</p>
    <img
      class="store-img"
      v-if="store.searchStoreImageResList && store.searchStoreImageResList.length"
      :src="store.searchStoreImageResList[0].storeImageUrl"
      alt="store image"
    />
    <div class="btn-container">
      <button class="normal-btn" @click="goStoreDetail"><img class="search-img" src="../assets/img/search-none.png" alt=""></button>
      <button class="normal-btn" @click="like"><img class="search-img" src="../assets/img/like-none.png" alt=""></button>
    </div>
  </div>
</template>
  
  <script setup>
import { defineProps } from "vue";
import { useRouter } from "vue-router";
import { useStoreStore } from "@/stores/useStoreStore";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/useAuthStore";
const router = useRouter(); 
const storeStore = useStoreStore();
const authStore = useAuthStore();
const toast = useToast();
const props = defineProps({
  store: Object,
});
const goStoreDetail = () => {
  router.push(`/store/${props.store.storeIdx}`);
}

const like = async() => {
  if(authStore.isLoggedIn){
    const res = await storeStore.like(props.store.storeIdx);
    if(res.success){
      toast.error(res.message);
    } else {
      if(authStore.userInfo.role == "ROLE_COMPANY"){
        toast.error(res.message);
      } else {
        toast.error(res.message);
      }
    }
  } else {
    router.push("/");
    toast.error("로그인이 필요합니다.");
  }

}

</script>
  
<style scoped>
.store-card {
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

.t3 {
  font-size: 11px;
}

.store-img {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  margin-top: 4px;
}

.search-img {
  width: 20px;
  height: auto;
}

.like-img {
  object-fit: cover;
  width: auto;
  height: 20px;
  vertical-align: middle;
  padding-right: 5px;
}

.people-img {
  object-fit: cover;
  width: auto;
  height: 30px;
  vertical-align: middle;
}

.btn-container{
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
</style>
  