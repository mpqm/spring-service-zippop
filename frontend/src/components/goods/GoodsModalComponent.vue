<template>
  <div v-if="isModalOpen" class="modal-overlay" @click="closeModal">
    <div class="modal-container" @click.stop>
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ goods.goodsName }}</h2>
          <button @click="closeModal" class="close-btn">X</button>
        </div>
        <div class="modal-body">
          <div class="left-panel">
            <ImageSlider class="image-slider" :fileUrls="fileUrls"></ImageSlider>
          </div>
          <div class="right-panel">
            <div class="title">
              <h2>{{ goods.storeName }}</h2>
              <p>{{ goods.goodsName }}</p>
            </div>
            <p class="t2">
              <img class="people-img" src="../../assets/img/coin.png" alt="" />{{ goods.goodsPrice }}원
              <img class="like-img" src="../../assets/img/stock.png" alt="" />{{ goods.goodsAmount }}개
            </p>
            <p> {{ goods.goodsContent }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, onMounted } from "vue";
import ImageSlider from "@/components/common/ImageSlider.vue";

// props 정의(goods, 모달 여부, 모달 닫기 함수)
const props = defineProps({
  goods: Object,
  isModalOpen: Boolean,
  closeModal: Function,
});

// 변수
const fileUrls = ref([]);

// onMounted
onMounted(async () => {
  await mapper()
});

// 매핑 함수
const mapper = async() => {
  if (props.goods.searchGoodsImageResList && props.goods.searchGoodsImageResList.length) {
    fileUrls.value = props.goods.searchGoodsImageResList.map(image => image.goodsImageUrl);
  }
}

</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.modal-container {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  width: 80%;
  max-width: 900px;
  overflow-y: auto;
  max-height: 80vh;
}

.image-slider {
  width: 100%;
  height: 100%;
}

.modal-content {
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.close-btn {
  background-color: transparent;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.modal-body {
  display: flex;
  gap: 2rem;
}

.left-panel,
.right-panel {
  width: 50%;
}

.left-panel img {
  width: 100%;
  height: auto;
}

.right-panel {
  padding: 1rem;
}

.right-panel .btn-container {
  display: flex;
  gap: 10px;
}

.right-panel .ud-btn {
  background-color: #f08c4d;
}

.title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.t2 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 1rem 0;
}
</style>