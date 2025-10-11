<template>
  <div v-if="isModalOpen" class="lyt-modal" @click="closeModal">
    <div class="wrp-modal" @click.stop>
      <div class="ctn-modal">
        <div class="ctn-split">
          <span class="txt-def1">{{ goods.goodsName }}</span>
          <button @click="closeModal" class="btn-default">
            <Icon icon="iconoir:delete-circle" width="20px" height="20px" style="color: #ffffff" />
          </button>
        </div>
        <div class="ctn-split">
          <div class="ctn-l50">
            <ImageSlider :fileUrls="fileUrls"></ImageSlider>
          </div>
          <div class="ctn-r50">
            <p class="txt-def1">{{ goods.goodsName }}</p>
            <p class="txt-desc"> {{ goods.goodsContent }}</p>
            <div class="ctn-tagbutton">
              <button class="btn-tagdefault">{{ goods.storeName }}</button>
              <button class="btn-tagdefault"><Icon icon="iconoir:coin" width="20px" height="20px" style="color: #00c7ae" />{{ goods.goodsPrice }}원</button>
              <button class="btn-tagdefault"><Icon icon="iconoir:box-iso" width="20px" height="20px" style="color: #00c7ae" />{{ goods.goodsAmount }}개</button>
            </div>
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