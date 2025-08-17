<template>
  <div v-if="isModalOpen" class="modal-overlay" @click="closeModal">
    <div class="modal-container" @click.stop>
      <div class="modal-content">
        <div class="modal-header">
          <span class="t1">{{ goods.goodsName }}</span>
          <button @click="closeModal" class="default-btn">
            <Icon icon="iconoir:delete-circle" width="20px" height="20px" style="color: #ffffff" />
          </button>
        </div>
        <div class="modal-body">
          <div class="modal-body-left">
            <ImageSlider :fileUrls="fileUrls"></ImageSlider>
          </div>
          <div class="modal-body-right">
            <div class="modal-info-container">
              <span class="t1">{{ goods.storeName }}</span>
              <span class="t1">{{ goods.goodsName }}</span>
            </div>
            <div class="modal-info-container">
                <Icon icon="iconoir:coin" width="20px" height="20px" style="color: #00c7ae" />{{ goods.goodsPrice }}원
                <Icon icon="iconoir:box-iso" width="20px" height="20px" style="color: #00c7ae" />{{ goods.goodsAmount }}개
            </div>
            <span class="t1"> {{ goods.goodsContent }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, defineProps, onMounted } from "vue";
import ImageSlider from "@/components/ImageSlider.vue";

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

</style>