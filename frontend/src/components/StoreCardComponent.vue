<template>
  <div class="store-card">
    <p class="t1">{{ store.storeName }}</p>
    <p class="t2">{{ store.category }}</p>
    <p class="t2">
      <img class="like-img" src="../assets/img/like-fill.png" alt="" />&nbsp;{{ store.likeCount }}
      <img class="people-img" src="../assets/img/people.png" alt="" />&nbsp;{{ store.totalPeople }}
    </p>
    <p class="t3">{{ store.storeStartDate }} ~ {{ store.storeEndDate }}</p>
    <img
      class="store-img"
      v-if="store.searchStoreImageResList && store.searchStoreImageResList.length"
      :src="store.searchStoreImageResList[0].storeImageUrl"
      alt="store image"
    />
  </div>
</template>
  
  <script setup>
import { defineProps } from "vue";

defineProps({
  store: Object,
});
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
  height: fit-content;
  border-radius: 8px;
  margin-top: 4px;
}

.like-img {
  object-fit: cover;
  width: auto;
  height: 24px;
  margin-right: 5px;
  vertical-align: middle;
}

.people-img {
  object-fit: cover;
  width: auto;
  height: 30px;
  vertical-align: middle;
}
</style>
  