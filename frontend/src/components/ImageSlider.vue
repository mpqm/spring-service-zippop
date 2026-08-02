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
