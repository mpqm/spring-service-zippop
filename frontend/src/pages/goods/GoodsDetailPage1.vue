<template>
  <div class="lyt-page">
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <div class="wrp-split">
        <div class="ctn-l50">
          <ImageSlider class="image-slider" :fileUrls="fileUrls" />
        </div>
        <div class="ctn-r50">
          <p class="txt-def1">{{ popup.popupName }}</p>
          <p class="txt-desc">{{ popup.popupContent }}</p>
          <div class="ctn-tagbutton">
            <button class="btn-tagdefault">{{ popup.category }}</button>
            <button class="btn-tagdefault">{{ popup.popupStartDate }}<span class="divider">~</span>{{ popup.popupEndDate }}</button>
            <button class="btn-tagdefault">{{ popup.popupAddress }}</button>
            <button class="btn-tagdefault" :class="{ active: isLiked }" @click="toggleLike">
              <Icon icon="iconoir:thumbs-up" width="20px" height="20px" />&nbsp;{{ currentLikeCount }}
            </button>
            <button class="btn-tagdefault"><Icon icon="iconoir:user" width="20px" height="20px"/>&nbsp;{{ popup.totalPeople }}</button>
            <button @click="goPopupDetail" class="btn-tagaction"><Icon icon="iconoir:eye" width="20px" height="20px"/>이전 팝업 정보 보기</button>
          </div>
        </div>
      </div>

      <div>
        <div class="ctn-inputsearch">
          <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="getGoodsList()" />
          <button class="btn-default" @click="getGoodsList()"><Icon icon="ic:search" width="20px" height="20px" /></button>
          <button class="btn-normal" @click="getGoodsList(true)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
        </div>
        <div class="lyt-cardgrid" v-if="goodsList && goodsList.length">
          <GoodsCard v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :popupIdx="popup.popupIdx" />
        </div>
        <AppEmptyState
          v-else
          title="검색된 굿즈가 없습니다"
          description="다른 상품명으로 검색하거나 전체 굿즈를 확인해 보세요."
          action-label="검색 초기화"
          @action="getGoodsList(true)"
        />
        <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>
    </div>
    <AppFooter></AppFooter>
  </div>
</template>

<script setup>
import GoodsCard from "@/components/GoodsCard.vue";
import ImageSlider from "@/components/ImageSlider.vue";
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import AppPagination from "@/components/AppPagination.vue";
import { Icon } from "@iconify/vue";
import { ref, onMounted, watch } from "vue";
import { usePopupStore } from "@/stores/popupStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/goodsStore";
import { useAuthStore } from "@/stores/authStore";

const goodsStore = useGoodsStore();
const popupStore = usePopupStore();
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

const popup = ref({});
const fileUrls = ref([]);
const isLiked = ref(false);
const currentLikeCount = ref(0);
const searchQuery = ref("");
const goodsList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

onMounted(async () => {
  await getPopup();
  await getGoodsList();
});

const getPopup = async () => {
  const res = await popupStore.getPopup(route.params.popupIdx);
  if (res.success) {
    popup.value = popupStore.popup;
    if (popupStore.popup.getPopupImageResList && popupStore.popup.getPopupImageResList.length) {
      fileUrls.value = popupStore.popup.getPopupImageResList.map(image => image.popupImageUrl);
    }
    isLiked.value = popupStore.likeList.some(s => s.popupIdx === popupStore.popup.popupIdx);
    currentLikeCount.value = popupStore.popup.likeCount || 0;
  } else {
    router.push("/");
    toast.error(res.message);
  }
};

const getGoodsList = async (reset = false) => {
  if (reset) {
    currentPage.value = 0;
    searchQuery.value = "";
  }
  const res = await goodsStore.getGoodsList(route.params.popupIdx, searchQuery.value || null, currentPage.value, pageSize.value);
  if (res.success) {
    totalElements.value = goodsStore.totalElements;
    totalPages.value = goodsStore.totalPages;
    goodsList.value = goodsStore.goodsList;
    hideBtns.value = false;
  } else {
    goodsList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const goPopupDetail = () => {
  router.push({ path: `/popup/${route.params.popupIdx}`, query: { mainTab: 'goods' } });
};

watch(() => popupStore.likeList, () => {
  if (popup.value.popupIdx) {
    isLiked.value = popupStore.likeList.some(s => s.popupIdx === popup.value.popupIdx);
  }
}, { deep: true });

watch(() => popup.value, (newPopup) => {
  if (newPopup && newPopup.popupIdx) {
    isLiked.value = popupStore.likeList.some(s => s.popupIdx === newPopup.popupIdx);
    currentLikeCount.value = newPopup.likeCount || 0;
  }
}, { immediate: true, deep: true });

const changePage = async (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return;
  currentPage.value = newPage;
  await getGoodsList();
};

const toggleLike = async () => {
  if (!authStore.isLoggedIn) {
    router.push("/");
    toast.error("로그인이 필요합니다.");
    return;
  }
  if (authStore.userInfo?.role === "ROLE_COMPANY") {
    toast.error("고객 회원만 좋아요를 누를 수 있습니다.");
    return;
  }

  const wasLiked = isLiked.value;
  isLiked.value = !isLiked.value;
  currentLikeCount.value += isLiked.value ? 1 : -1;

  const res = await popupStore.togglePopupLike(popup.value.popupIdx);
  if (!res.success) {
    isLiked.value = wasLiked;
    currentLikeCount.value += wasLiked ? 1 : -1;
    toast.error(res.message);
  }
};
</script>
