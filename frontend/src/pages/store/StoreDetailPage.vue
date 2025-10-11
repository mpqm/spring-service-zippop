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
            <button class="btn-tagdefault"> {{ store.storeAddress }}</button>
            
            <button class="btn-tagdefault">{{ store.storeStartDate }}<span class="divider">~</span>{{ store.storeEndDate }}</button>
            <button class="btn-tagdefault" :class="{ active: isLiked }" @click="toggleLike"><Icon icon="iconoir:thumbs-up" width="20px" height="20px" />&nbsp;{{ currentLikeCount }} </button>
            <button class="btn-tagdefault"><Icon icon="iconoir:user" width="20px" height="20px"/>&nbsp;{{ store.totalPeople }}</button>
            <CountDownTimer :targetTime="store.storeEndDate" :flag="true"></CountDownTimer>
          </div>
          <!-- <button class="normal-btn" @click="goReserve"><img src="../../assets/img/reserve-none.png" alt="">&nbsp;<p>예약 참여</p></button> -->
        </div>
      </div>
      <div class="wrp-subheader">
        <div class="ctn-subheader">
          <a class="lnk-subheader" :class="{ active: activeMenu === 'goods' }" @click="setActiveMenu('goods')">굿즈 보기</a>
          <a class="lnk-subheader" :class="{ active: activeMenu === 'review' }" @click="setActiveMenu('review')"> 리뷰 보기</a>
          <a class="lnk-subheader" :class="{ active: activeMenu === 'reserve' }" @click="setActiveMenu('reserve')"> 예약 확인</a>
        </div>
      </div>
      <div class="lyt-child" v-if="activeMenu == 'goods'">
        <div class="ctn-inputsearch">
          <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="searchAllGoodsByKeyword" />
          <button class="btn-default" @click="searchAllGoodsByKeyword"><Icon icon="ic:search" width="20px" height="20px" /></button>
          <button class="btn-normal" @click="searchAllGoods(0)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
        </div>
        <br/>
        <div class="wrp-list" v-if="goodsList && goodsList.length">
          <GoodsList v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :showControl="showControl" :storeIdx="store.storeIdx"/>
        </div>
        <div class="notice" v-else>
          <p>등록된 굿즈가 없습니다.</p>
        </div>
        <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>
      <div v-if="activeMenu == 'review'" class="lyt-child">

        <form class="ctn-rootform" @submit.prevent="registerReview">
          <div class="ctn-split">
            <h1 class="txt-def0">후기 등록</h1>
            <button type="submit" class="btn-default">등록</button>
          </div>
            <label class="ipt-default-label">제목</label>
            <input class="ipt-default" v-model="reviewTitle" type="text" placeholder="후기의 제목을 남겨주세요" />
            <label class="ipt-default-label">내용</label>
            <input class="ipt-default" v-model="reviewContent" type="text" placeholder="후기의 내용을 남겨주세요" />
            <label class="ipt-default-label">평점</label>
            <input class="ipt-default" v-model="reviewRating" type="number" min="1" max="5" step="0.1" id="rating" placeholder="평점을 남겨주세요" />
        </form>
        <br>
        <div class="wrp-list" v-if="reviewList && reviewList.length">
          <ReviewList v-for="review in reviewList" :key="review.reviewIdx" :review="review" />
        </div>
        <div class="notice" v-else>
          <p>등록된 리뷰가 없습니다.</p>
        </div>
        <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>

      <div v-if="activeMenu == 'reserve'" class="lyt-child">
        <div class="wrp-list" v-if="reserveList && reserveList.length">
          <ReserveList v-for="reserve in reserveList" :key="reserve.reserveIdx" :reserve="reserve" :showControl=0 /> 
        </div>
        <div class="notice" v-else>
          <p>등록된 예약이 없습니다.</p>
          </div>
          <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
        </div>
    </div>
    <AppFooter></AppFooter>
  </div>
</template>


<script setup>
import ImageSlider from "@/components/ImageSlider.vue";
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import CountDownTimer from "@/components/CountDownTimer.vue";
import GoodsList from "@/components/GoodsList.vue";
import ReviewList from "@/components/ReviewList.vue";
import ReserveList from "@/components/ReserveList.vue";
import AppPagination from "@/components/AppPagination.vue";
import { ref, onMounted, watch } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useReserveStore } from "@/stores/useReserveStore";
import { useAuthStore } from "@/stores/useAuthStore";

// store, router, route, toast
const goodsStore = useGoodsStore();
const storeStore = useStoreStore();
const reserveStore = useReserveStore();
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

// 변수(store)
const fileUrls = ref([]);
const store = ref({});
const activeMenu = ref('');

// 좋아요 상태 관리
const isLiked = ref(false);
const currentLikeCount = ref(0);

// 변수(goods)
const searchQuery = ref("");
const showControl = ref(false);
const goodsList = ref([]);
const currentPage = ref(0);
const goodsPageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const isKeywordSearch = ref(false);

// 변수(review)
const reviewList = ref([]);
const reviewPageSize = ref(8);
const reviewContent = ref('');
const reviewTitle = ref('');
const reviewRating = ref(0);

// 변수(reserve)
const reserveList = ref([]);

// onMounted
onMounted(async () => {
  await search();
});

// 스토어 단일 검색
const search = async () => {
  const res = await storeStore.searchStore(route.params.storeIdx);
  if (res.success) {
    store.value = storeStore.store;
    await mapper();
  } else {
    router.push("/")
    toast.error(res.message);
  }
}

// 매핑 함수
const mapper = async () => {
  if (storeStore.store.searchStoreImageResList && storeStore.store.searchStoreImageResList.length) {
    fileUrls.value = storeStore.store.searchStoreImageResList.map(image => image.storeImageUrl);
  }
  goodsList.value = storeStore.store.searchGoodsResList;
  
  // 좋아요 상태 초기화
  isLiked.value = storeStore.likeList.some(s => s.storeIdx === storeStore.store.storeIdx);
  currentLikeCount.value = storeStore.store.likeCount || 0;
}

// 메뉴 선택 함수
const setActiveMenu = (menu) => {
  activeMenu.value = menu;
  if (menu == 'goods') {
    currentPage.value = 0;
    searchAllGoods();
  } else if (menu == 'review') {
    searchAllReview();
  } else if (menu == 'reserve') {
    searchAllReserve();
  }
}

// 페이지네이션
const changePage = (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return; // 유효한 페이지 번호인지 확인
  currentPage.value = newPage;
  if (activeMenu.value == 'goods') {
    if (isKeywordSearch.value) { // 키워드 검색 상태일 경우
      searchAllGoods();
    } else {
      searchAllGoodsByKeyword();
    }
  } else if (newPage >= 0 && activeMenu.value == 'review') {
    searchAllReview();
  }
};

// 굿즈 목록 조회
const searchAllGoods = async (flag) => {
  if (flag === 0) {
    currentPage.value = 0;
    searchQuery.value = "";
    isKeywordSearch.value = false; // 일반 검색 상태로 전환
  }
  const res = await goodsStore.searchAllByStoreIdx(route.params.storeIdx, currentPage.value, goodsPageSize.value);
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

// 굿즈 목록 조회(키워드)
const searchAllGoodsByKeyword = async () => {
  if (!isKeywordSearch.value) {
    currentPage.value = 0; // 키워드 검색 상태로 진입 시 페이지를 초기화
    isKeywordSearch.value = true; // 키워드 검색 상태 활성화
  }
  const res = await goodsStore.searchAllByKeywordAndStoreIdx(searchQuery.value, route.params.storeIdx, currentPage.value, goodsPageSize.value);
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

// 리뷰 등록
const registerReview = async () => {
  const req = {
    reviewTitle: reviewTitle.value,
    reviewContent: reviewContent.value,
    reviewRating: reviewRating.value,
  };
  const res = await storeStore.registerReview(route.params.storeIdx, req);
  if (res.success) {
    toast.success(res.message);
  } else {
    toast.error(res.message);
  }
};

// 리뷰 목록 조회
const searchAllReview = async () => {
  const res = await storeStore.searchAllReview(route.params.storeIdx, currentPage.value, reviewPageSize.value);
  if (res.success) {
    reviewList.value = storeStore.reviewList;
    totalElements.value = storeStore.totalElements;
    totalPages.value = storeStore.totalPages;
  } else {
    reviewList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
  }
}

const searchAllReserve = async () => {
  const res = await reserveStore.searchAllReserveByStoreIdx(route.params.storeIdx, currentPage.value, reviewPageSize.value);
  if (res.success) {
    reserveList.value = reserveStore.reserveList;
    totalElements.value = reserveStore.totalElements;
    totalPages.value = reserveStore.totalPages;
  } else {
    reserveList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
  }
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