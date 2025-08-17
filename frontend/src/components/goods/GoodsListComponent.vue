<template>
  <div class="default-list">
    <img
      class="list-item-img"
      v-if="goods.searchGoodsImageResList && goods.searchGoodsImageResList.length"
      :src="goods.searchGoodsImageResList[0].goodsImageUrl"
      alt="goods image"
    />
    <div class="list-item-info">
      <div class="list-item-info1">
        <p class="fs17fw800">{{ goods.goodsName }}</p>
      </div>
      <div class="list-item-info1">
        <div class="list-item-info2">
          <Icon icon="iconoir:coin" width="20px" height="20px"  style="color: #00c7ae" />
          <span class="fs13fw400"> &nbsp;{{ goods.goodsPrice }} </span>
          &nbsp;
          <Icon icon="iconoir:box-iso" width="20px" height="20px"  style="color: #00c7ae" />
          <span class="fs13fw400"> &nbsp;{{ goods.goodsAmount }} </span>
        </div>
      </div>
    </div>
    <div v-if="showControl == true" class="list-item-btn-container">
      <router-link class="list-item-btn " :to="goods ? `/goods/${route.params.storeIdx}/${goods.goodsIdx}` : '#'">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
        굿즈 보기
      </router-link>
      <router-link class="list-item-btn " :to="goods ? `/mypage/company/goods/${route.params.storeIdx}/update/${goods.goodsIdx}` : '#'">
        <Icon icon="iconoir:edit-pencil" width="20px" height="20px" style="color: #ffffff" />
        굿즈 수정
      </router-link>
      <button class="list-item-btn " @click="deleteGoods">
        <Icon icon="iconoir:trash" width="20px" height="20px" style="color: #ffffff" />
        굿즈 삭제
      </button>
    </div>
    <div v-if="showControl == false" class="list-item-btn-container">
      <button class="list-item-btn " @click="openModal">상세 보기</button>
    </div>
  </div>
  <GoodsModalComponent v-if="isModalOpen" :goods="goods" :isModalOpen="isModalOpen" :closeModal="closeModal" />
</template>

<script setup>
import { defineProps, ref } from "vue";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useToast } from "vue-toastification";
import { useRoute, useRouter } from "vue-router";
import GoodsModalComponent from "@/components/goods/GoodsModalComponent.vue"; 

// store, router, route, toast
const toast = useToast();
const router = useRouter();
const route = useRoute();
const goodsStore = useGoodsStore();

// props 정의(goods, showControl)
const props = defineProps({
  goods: Object,
  showControl: Boolean,
});

// 변수
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

