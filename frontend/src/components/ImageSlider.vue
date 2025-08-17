<template>
  <div v-if="fileUrls.length" class="image-slider-wrapper">
    <div class="image-slider-images">
        <img :src="fileUrls[currentIndex]" :alt="'Image ' + (currentIndex + 1)" /> 
    </div>
    <div class="image-slider-indicator-container">
      <button class="default-btn" @click="prevImage">
        <Icon icon="ic:round-navigate-before" width="16px" height="16px" />
      </button>
      <div class="image-slider-indicator-dots">
        <span v-for="(fileUrl, index) in fileUrls" :key="index" :class="{ 'active': index === currentIndex }" class="image-slider-indicator" @click="goToImage(index)"></span>
      </div>
      <button class="default-btn" @click="nextImage">
        <Icon icon="ic:round-navigate-next" width="16px" height="16px" />
      </button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, ref } from 'vue';

const props = defineProps({
  fileUrls: Array,
})
const currentIndex = ref(0);

// 전 이미지
const prevImage = () => { currentIndex.value = (currentIndex.value - 1 + props.fileUrls.length) % props.fileUrls.length; };

// 다음 이미지
const nextImage = () => { currentIndex.value = (currentIndex.value + 1) % props.fileUrls.length; };

// 인디케이터 조정
const goToImage = (index) => { currentIndex.value = index; };

</script>