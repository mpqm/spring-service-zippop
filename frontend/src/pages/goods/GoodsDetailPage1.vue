<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <div class="wrp-split">
        <div class="ctn-l50">
          <ImageSlider class="image-slider" :fileUrls="fileUrls" />
        </div>
        <div class="ctn-r50">
          <p class="txt-def1">{{ store.storeName }}</p>
          <p class="txt-desc"> {{ store.storeContent }}</p>
          <div class="ctn-tagbutton">

            <button class="btn-tagdefault">{{ store.category }}</button>
            <button class="btn-tagdefault">{{ store.storeStartDate }}<span class="divider">~</span>{{ store.storeEndDate }}</button>

            <button class="btn-tagdefault"> {{ store.storeAddress }}</button>
            <button class="btn-tagdefault" :class="{ active: isLiked }" @click="toggleLike">
              <Icon icon="iconoir:thumbs-up" width="20px" height="20px" />&nbsp;{{ currentLikeCount }}
            </button>
            <button class="btn-tagdefault"><Icon icon="iconoir:user" width="20px" height="20px"/>&nbsp;{{ store.totalPeople }}</button>
            <button @click="goStoreDetail" class="btn-tagaction"><Icon icon="iconoir:eye" width="20px" height="20px"/>이전 팝업 정보 보기</button>
          </div>
        </div>
      </div>
      
      <div>
        <div class="ctn-inputsearch">
          <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="keywordSearchAll" />
          <button class="btn-default" @click="searchAllByKeyword"><Icon icon="ic:search" width="20px" height="20px" /></button>
          <button class="btn-normal" @click="searchAll(0)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
        </div>  
        <div class="lyt-cardgrid" v-if="goodsList && goodsList.length">
          <GoodsCard v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :storeIdx="store.storeIdx" />
        </div>
        <div v-else>
          <p>검색 결과에 해당하는 팝업 굿즈 목록이 없습니다.</p>
        </div>
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
import { useStoreStore } from "@/stores/useStoreStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useAuthStore } from "@/stores/useAuthStore";

// store, router, route, toast
const goodsStore = useGoodsStore();
const storeStore = useStoreStore();
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

// 변수(store)
const fileUrls = ref([]);
const store = ref({});

// 좋아요 상태 관리
const isLiked = ref(false);
const currentLikeCount = ref(0);

// 변수(goods)
const searchQuery = ref("");
const goodsList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const isKeywordSearch = ref(false);

// onMounted 
onMounted(async () => {
  await search();
  await searchAll();
});

// 스토어 조회 
const search = async () => {
  const res = await storeStore.searchStore(route.params.storeIdx);
  if (res.success) {
    store.value = storeStore.store;
    mapper();

  } else {
    router.push("/")
    toast.error(res.message);
  }
}

// 스토어 상세 정보 이동
const goStoreDetail = () => {
  router.push(`/store/${route.params.storeIdx}`);
}

// 매핑함수
const mapper = () => {
  if (storeStore.store.searchStoreImageResList && storeStore.store.searchStoreImageResList.length) {
      fileUrls.value = storeStore.store.searchStoreImageResList.map(image => image.storeImageUrl);
    }
  // 좋아요 상태 초기화
  isLiked.value = storeStore.likeList.some(s => s.storeIdx === storeStore.store.storeIdx);
  currentLikeCount.value = storeStore.store.likeCount || 0;
}

// storeStore의 좋아요 배열이 변경되면 상태 업데이트
watch(() => storeStore.likeList, () => {
  if (store.value.storeIdx) {
    isLiked.value = storeStore.likeList.some(s => s.storeIdx === store.value.storeIdx);
  }
}, { deep: true });

// store가 변경되면 좋아요 상태 업데이트
watch(() => store.value, (newStore) => {
  if (newStore && newStore.storeIdx) {
    isLiked.value = storeStore.likeList.some(s => s.storeIdx === newStore.storeIdx);
    currentLikeCount.value = newStore.likeCount || 0;
  }
}, { immediate: true, deep: true });

// 굿즈 목록 조회
const searchAll = async (flag) => {
  if (flag === 0) {
    currentPage.value = 0;
    searchQuery.value = "";
    isKeywordSearch.value = false; // 일반 검색 상태로 전환
  }
  const res = await goodsStore.searchAllByStoreIdx(route.params.storeIdx, currentPage.value, pageSize.value);
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

// 굿즈 목록 조회 (키워드 검색) 
const searchAllByKeyword = async () => {
  if (!isKeywordSearch.value) {
    currentPage.value = 0; // 키워드 검색 상태로 진입 시 페이지를 초기화
    isKeywordSearch.value = true; // 키워드 검색 상태 활성화
  }
  const res = await goodsStore.searchAllByKeywordAndStoreIdx(searchQuery.value, route.params.storeIdx, currentPage.value, pageSize.value);
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

// 페이지 네이션
const changePage = async (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return; // 유효한 페이지 번호인지 확인
  currentPage.value = newPage;
  if (isKeywordSearch.value) { // 키워드 검색 상태일 경우
    await searchAllByKeyword();
  } else { // 일반 검색 상태일 경우
    await searchAll();
  }
};

// 좋아요 토글 (유튜브 스타일)
const toggleLike = async () => {
  if (!authStore.isLoggedIn) {
    router.push("/");
    toast.error("로그인이 필요합니다.");
    return;
  }

  // 기업 회원인 경우 좋아요 불가
  if (authStore.userInfo.role === "ROLE_COMPANY") {
    toast.error("고객 회원만 좋아요를 누를 수 있습니다.");
    return;
  }

  // 낙관적 업데이트 (Optimistic Update)
  const wasLiked = isLiked.value;
  isLiked.value = !isLiked.value;
  currentLikeCount.value += isLiked.value ? 1 : -1;

  // API 호출
  const res = await storeStore.registerLike(store.value.storeIdx);
  
  if (!res.success) {
    // 실패 시 롤백
    isLiked.value = wasLiked;
    currentLikeCount.value += wasLiked ? 1 : -1;
    toast.error(res.message);
  }
}

</script>
