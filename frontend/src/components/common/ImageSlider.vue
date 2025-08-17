<template>
  <div v-if="fileUrls.length" class="image-slider">
    <div class="slider-container">
      <div class="images"> 
        <img :src="fileUrls[currentIndex]" :alt="'Image ' + (currentIndex + 1)" /> 
      </div>
    </div>
    <div class="indicators">
      <button class="prev" @click="prevImage">&lt;</button>
      <div class="indicator-dots">
        <span v-for="(fileUrl, index) in fileUrls" :key="index" :class="{ 'active': index === currentIndex }" class="indicator" @click="goToImage(index)"></span>
      </div>
      <button class="next" @click="nextImage">&gt;</button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref } from 'vue';

// props 정의
const props = defineProps({
  fileUrls: Array,
})

// 변수
const currentIndex = ref(0);

// 전 이미지
const prevImage = () => { currentIndex.value = (currentIndex.value - 1 + props.fileUrls.length) % props.fileUrls.length; };

// 다음 이미지
const nextImage = () => { currentIndex.value = (currentIndex.value + 1) % props.fileUrls.length; };

// 인디케이터 조정
const goToImage = (index) => { currentIndex.value = index; };

</script>

<style scoped>
.image-slider {
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-width: 30rem;
}

.slider-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.images {
  width: 100%;
  height: 300px;
  overflow: hidden;
  border-radius: 8px;
}

.images img {
  width: 100%;
  height: 100%; 
  object-fit: cover;
}

.indicators {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 0.5rem;
}

.indicator-dots {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1;
}

button {
  background-color: #00c7ae;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1.2rem;
  min-width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

button:hover {
  opacity: 0.8;
}

.prev {
  margin-right: 1rem;
}

.next {
  margin-left: 1rem;
}

.indicator {
  display: inline-block;
  width: 12px;
  height: 12px;
  margin: 0 6px;
  background-color: #ccc;
  border-radius: 50%;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.indicator:hover {
  background-color: #999;
}

.indicator.active {
  background-color: #00c7ae;
}
</style>