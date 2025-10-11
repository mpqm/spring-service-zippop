<template>
  <div v-if="fileUrls.length" class="wrp-slider">
    <!-- 이미지 영역 -->
    <div class="ctn-images">
      <img
        :src="fileUrls[currentIndex]"
        :alt="'상품 이미지 ' + (currentIndex + 1)"
      />
      <!-- 좌우 화살표 (이미지가 2개 이상일 때만 표시) -->
      <button v-if="fileUrls.length > 1" class="img-sliderarrow left" @click="prevImage">
        <Icon icon="iconoir:nav-arrow-left" width="18px" height="18px" />
      </button>
      <button v-if="fileUrls.length > 1" class="img-sliderarrow right" @click="nextImage">
        <Icon icon="iconoir:nav-arrow-right" width="18px" height="18px" />
      </button>
    </div>

    <!-- 인디케이터 (이미지가 2개 이상일 때만 표시) -->
    <div v-if="fileUrls.length > 1" class="ctn-indicator">
      <span
        v-for="(fileUrl, index) in fileUrls"
        :key="index"
        :class="['img-indicatordot', { active: index === currentIndex }]"
        @click="goToImage(index)"
      ></span>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps } from 'vue';
import { Icon } from '@iconify/vue';

const props = defineProps({
  fileUrls: {
    type: Array,
    default: () => [],
  },
});

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
.wrp-slider {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    max-width: 30rem;
    margin: 0 auto;
    background-color: #fff;
    overflow: hidden;
    transition: all 0.3s ease;
}

.ctn-images {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 320px;
    overflow: hidden;
    position: relative;
    border-radius: 12px;
    background-color: #f8f8f8;
}

.ctn-images img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s ease;
}

.ctn-images img:hover {
    transform: scale(1.03);
}

.ctn-indicator {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 10px 0;
    gap: 6px;
}

.img-indicatordot {
    width: 10px;
    height: 10px;
    background-color: #f3f3f3;
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.25s ease-in-out;
}

.img-indicatordot:hover {
    background-color: #00c7ae66;
    transform: scale(1.2);
}

.img-indicatordot.active {
    background-color: #00c7ae;
    transform: scale(1.3);
    box-shadow: 0 0 6px rgba(0, 199, 174, 0.4);
}

.img-sliderarrow {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    background-color: #f3f3f3;
    border: none;
    border-radius: 50%;
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    transition: all 0.25s ease;
}

.img-sliderarrow:hover {
    background-color: #00c7ae;
    color: #fff;
    transform: translateY(-50%) scale(1.05);
}

.img-sliderarrow.left {
    left: 10px;
}

.img-sliderarrow.right {
    right: 10px;
}
</style>