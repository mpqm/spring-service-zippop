<template>
    <div v-if="fileUrls.length" class="image-slider">
      <div class="slider-container">
        <button class="prev" @click="prevImage">&lt;</button>
        
        <div class="images">
          <img :src="fileUrls[currentIndex]" :alt="'Image ' + (currentIndex + 1)" />
        </div>
  
        <button class="next" @click="nextImage">&gt;</button>
      </div>
      
      <div class="indicators">
        <span v-for="(fileUrl, index) in fileUrls" :key="index" :class="{'active': index === currentIndex}" class="indicator" @click="goToImage(index)"></span>
      </div>
    </div>
  </template>
  
  <script setup>
  import { defineProps, ref } from 'vue';
  const props = defineProps({
    fileUrls: Array,
  })

  const currentIndex = ref(0);
  
  const prevImage = () => {
    currentIndex.value = (currentIndex.value - 1 + props.fileUrls.length) % props.fileUrls.length;
  };
  
  const nextImage = () => {
    currentIndex.value = (currentIndex.value + 1) % props.fileUrls.length;
  };
  
  const goToImage = (index) => {
    currentIndex.value = index;
  };
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
    align-items: center;
    justify-content: center;
    height: 100%;
    position: relative;
  }
  
  .images img {
    width: 100%;
    height: auto;
    object-fit: cover;
  }
  
  button {
    background-color: #00c7ae;
    color: white;
    height: 100%;
    border: none;
    padding: 1rem;
    border-radius: 8px;
    cursor: pointer;
    font-size: 1.5rem;
  }
  
  button:hover {
    opacity: 0.8;
  }
  
  .prev {
    position: absolute;
    left: 0;
  }
  
  .next {
    position: absolute;
    right: 0;
  }
  
  .indicators {
    text-align: center;
    z-index: 1;
  }
  
  .indicator {
    display: inline-block;
    width: 10px;
    height: 10px;
    margin: 0 5px;
    background-color: #ccc;
    border-radius: 50%;
    cursor: pointer;
  }
  
  .indicator.active {
    background-color: #00c7ae;
  }
  </style>
  