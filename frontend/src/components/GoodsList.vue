<template>
  <div class="list-container">
    <img
      class="list-img"
      v-if="goods.searchGoodsImageResList && goods.searchGoodsImageResList.length"
      :src="goods.searchGoodsImageResList[0].goodsImageUrl"
      alt="N/A"
    />
    <div class="list-info-container">
      <div class="list-info1">
        <p>{{ goods.goodsName }}</p>
      </div>
      <div class="list-info1">
        <div class="list-info2">
          <Icon icon="iconoir:coin" width="20px" height="20px"  style="color: #00c7ae" />
          <span> &nbsp;{{ goods.goodsPrice }}원 </span>
          &nbsp;
          <Icon icon="iconoir:box-iso" width="20px" height="20px"  style="color: #00c7ae" />
          <span> &nbsp;{{ goods.goodsAmount }}개 </span>
        </div>
      </div>
    </div>
    <div v-if="showControl == true" class="list-btn-container">
      <router-link class="list-btn " :to="goods ? `/goods/${route.params.storeIdx}/${goods.goodsIdx}` : '#'">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
        굿즈 보기
      </router-link>
      <router-link class="list-btn " :to="goods ? `/mypage/company/goods/${route.params.storeIdx}/update/${goods.goodsIdx}` : '#'">
        <Icon icon="iconoir:edit-pencil" width="20px" height="20px" style="color: #ffffff" />
        굿즈 수정
      </router-link>
      <button class="list-btn " @click="deleteGoods">
        <Icon icon="iconoir:trash" width="20px" height="20px" style="color: #ffffff" />
        굿즈 삭제
      </button>
    </div>
    <div v-if="showControl == false" class="list-btn-container">
      <button class="list-btn " @click="openModal">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
        상세 보기
      </button>
    </div>
  </div>
  <GoodsModal v-if="isModalOpen" :goods="goods" :isModalOpen="isModalOpen" :closeModal="closeModal" />
</template>

<script setup>
import { defineProps, ref } from "vue";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useToast } from "vue-toastification";
import { useRoute, useRouter } from "vue-router";
import GoodsModal from "@/components/GoodsModal.vue"; 

const props = defineProps({
  goods: Object,
  showControl: Boolean,
});
const toast = useToast();
const router = useRouter();
const route = useRoute();
const goodsStore = useGoodsStore();
const isModalOpen = ref(false);

// 굿즈 삭제
const deleteGoods = async () => {
  const res = await goodsStore.delete(props.goods.goodsIdx);
  if (res.success) {
    toast.success(res.message);
    router.go(0);
  } else {
    toast.error(res.message);
  }
};

// 모달 열기
const openModal = () => {
  isModalOpen.value = true;
};

// 모달 닫기
const closeModal = () => {
  isModalOpen.value = false;
};

</script>

