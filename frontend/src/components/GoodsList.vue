<template>
  <div class="ctn-list1">
    <img class="img-list" v-if="goods.searchGoodsImageResList && goods.searchGoodsImageResList.length" :src="goods.searchGoodsImageResList[0].goodsImageUrl" alt="N/A"/>
    
    <div class="ctn-listinfo1">
      <h1 class="txt-def1">{{ goods.goodsName }}</h1>
      <button class="btn-tagdefault"><Icon icon="iconoir:coin" width="20px" height="20px"/><span>{{ goods.goodsPrice }}원 </span></button>
      <button class="btn-tagdefault"><Icon icon="iconoir:box-iso" width="20px" height="20px"/><span>{{ goods.goodsAmount }}개 </span></button>
    </div>

    <!-- 굿즈 예약 페이지 용 -->
    <div v-if="showControl == true" class="ctn-listbuttons">
      <button class="btn-tagaction" @click="openModal"><Icon icon="iconoir:eye" width="20px" height="20px"/>상세 보기</button>
      <button class="btn-tagaction" @click="registerCart"><Icon icon="iconoir:cart" width="20px" height="20px"/>장바구니</button>
    </div>

    <!-- 스토어 상세 페이지 -->
    <div v-if="showControl == false" class="ctn-listbuttons">
      <button class="btn-tagaction" @click="openModal"><Icon icon="iconoir:eye" width="20px" height="20px"/>상세 보기</button>
    </div>

  </div>
  <GoodsModal v-if="isModalOpen" :goods="goods" :isModalOpen="isModalOpen" :closeModal="closeModal" />
</template>

<script setup>
import { defineProps, defineEmits, ref } from "vue";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/useAuthStore";
import { useCartStore } from "@/stores/useCartStore";
import GoodsModal from "@/components/GoodsModal.vue"; 

const props = defineProps({
  goods: Object,
  showControl: Boolean,
  storeIdx: Number,
});

const emit = defineEmits(['cartUpdated']);

const toast = useToast();
const authStore = useAuthStore();
const cartStore = useCartStore();
const isModalOpen = ref(false);

// 모달 열기
const openModal = () => {
  isModalOpen.value = true;
};

// 모달 닫기
const closeModal = () => {
  isModalOpen.value = false;
};

// 카트 등록
const registerCart = async () => {
  if (!authStore.isLoggedIn) {
    toast.error("로그인이 필요합니다.");
  } else {
    const req = {
      goodsIdx: props.goods.goodsIdx,
      storeIdx: props.storeIdx,
    }
    const res = await cartStore.register(req);
    if (res.success) {
      // toast.success(res.message);
      // 장바구니 업데이트 이벤트 발생
      emit('cartUpdated');
    } else {
      toast.error(res.message);
    }
  }
}

</script>

