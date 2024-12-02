<template>
  <div>
    <HeaderComponent></HeaderComponent>
    <div class="detail-page">
      <div class="detail-container">
        <div class="left-panel">
          <ImageSlider class="image-slider" :fileUrls="fileUrls" />
        </div>
        <div class="right-panel">
          <div class="title">
            <h2>{{ store.storeName }}</h2>
            <p>{{ store.category }}</p>
          </div>
          <p> {{ store.storeAddress }}</p>
          <p> {{ store.storeStartDate }} ~ {{ store.storeEndDate }}</p>
          <p> {{ store.storeContent }}</p>
          <p class="t2">
            <img class="like-img" src="../../assets/img/like-fill.png" alt="" />&nbsp;{{ store.likeCount }}
            <img class="people-img" src="../../assets/img/people.png" alt="" />&nbsp;{{ store.totalPeople }}
          </p>
          <CountDownTimer :targetTime="store.storeEndDate" :flag="true"></CountDownTimer>
          <hr>
          <!-- <button class="normal-btn" @click="goReserve"><img src="../../assets/img/reserve-none.png" alt="">&nbsp;<p>예약 참여</p></button> -->
        </div>
      </div>
      <div class="detail-header">
        <div class="menu-list">
          <button class="menu-link" :class="{ active: activeMenu === 'goods' }" @click="setActiveMenu('goods')">굿즈 보기</button>
          <button class="menu-link" :class="{ active: activeMenu === 'review' }" @click="setActiveMenu('review')"> 리뷰 보기</button>
          <button class="menu-link" :class="{ active: activeMenu === 'reserve' }" @click="setActiveMenu('reserve')"> 예약 확인</button>
        </div>
      </div>
      <div v-if="activeMenu == 'goods'" class="goods-list-container">
        <div class="goods-control">
          <div class="search-container">
            <input class="search-input" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="searchAllGoodsByKeyword" />
            <button class="search-btn" @click="searchAllGoodsByKeyword"><img class="search-img" src="../../assets/img/search-none.png" alt=""></button>
            <button class="search-btn" @click="searchAllGoods(0)"><img class="search-img" src="../../assets/img/reload-none.png" alt=""></button>
          </div>
        </div>
        <div class="goods-list" v-if="goodsList && goodsList.length">
          <GoodsListComponent v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :showControl="showControl" />
        </div>
        <div class="notice" v-else>
          <p>등록된 굿즈가 없습니다.</p>
        </div>
        <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>
      <div v-if="activeMenu == 'review'" class="review-list-container">
        <form class="register-form" @submit.prevent="registerReview">
            <input class="register-input" v-model="reviewTitle" type="text" placeholder="후기의 제목을 남겨주세요" />
            <input class="register-input" v-model="reviewContent" type="text" placeholder="후기의 내용을 남겨주세요" />
            <input class="register-input" v-model="reviewRating" type="number" min="1" max="5" step="0.1" id="rating" placeholder="평점을 남겨주세요" />
            <button type="submit" class="register-btn">리뷰 등록</button>
        </form>
        <div class="review-list" v-if="reviewList && reviewList.length">
          <ReviewListComponent v-for="review in reviewList" :key="review.reviewIdx" :review="review" />
        </div>
        <div class="notice" v-else>
          <p>등록된 리뷰가 없습니다.</p>
        </div>
        <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>
      <div v-if="activeMenu == 'reserve'">
        <div class="review-list" v-if="reserveList && reserveList.length">
          <ReserveListComponent v-for="reserve in reserveList" :key="reserve.reserveIdx" :reserve="reserve" :showControl="true" /> 
        </div>
        <div class="notice" v-else>
          <p>등록된 예약이 없습니다.</p>
        </div>
        <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>
    </div>
    <FooterComponent></FooterComponent>
  </div>
</template>


<script setup>
import ImageSlider from "@/components/common/ImageSlider.vue";
import HeaderComponent from "@/components/common/HeaderComponent.vue";
import FooterComponent from "@/components/common/FooterComponent.vue";
import CountDownTimer from "@/components/common/CountDownTimer.vue";
import GoodsListComponent from "@/components/goods/GoodsListComponent.vue";
import ReviewListComponent from "@/components/store/ReviewListComponent.vue";
import ReserveListComponent from "@/components/reserve/ReserveListComponent.vue";
import PaginationComponent from "@/components/common/PaginationComponent.vue";
import { ref, onMounted } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useReserveStore } from "@/stores/useReserveStore";

// store, router, route, toast
const goodsStore = useGoodsStore();
const storeStore = useStoreStore();
const reserveStore = useReserveStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

// 변수(store)
const fileUrls = ref([]);
const store = ref({});
const activeMenu = ref('');

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

</script>

<style scoped>
.detail-page {
  display: flex;
  padding: 1rem;
  width: 65rem;
  flex-direction: column;
  margin: 10px auto;
  width: 65rem;
  gap: 10px;
}

.detail-container {
  display: flex;
  column-gap: 10px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #00c7ae;
  margin: 0;
  width: 65rem;
}

.image-slider {
  width: 100%;
  height: 98%;
  padding: 5px;
  ;
}

.notice {
  text-align: center;
}

.left-panel,
.right-panel {
  width: 50%;
}

.right-panel {
  width: 50%;
  padding: 1rem;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.file-preview-item img {
  width: 100%;
  max-height: 300px;
  object-fit: cover;
  border-radius: 8px;
}

.like-img {
  object-fit: cover;
  width: auto;
  height: 24px;
  margin-right: 5px;
  vertical-align: middle;
}

.people-img {
  object-fit: cover;
  width: auto;
  height: 30px;
  vertical-align: middle;
}

.normal-btn {
  display: flex;
  text-align: center;
  width: 100%;
  font-weight: 400;
  transition: opacity 0.2s ease-in-out;
  color: #fff;
  cursor: pointer;
  background-color: #00c7ae;
  border-color: #00c7ae;
  border: 0.0625rem solid transparent;
  padding: 0.5rem;
  border-radius: 0.25rem;
  text-decoration: #000;
  align-items: center;
  justify-content: center;
}

.detail-header {
  display: flex;
  column-gap: 10px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #00c7ae;
  margin: 0;
  width: 65rem;
  flex-direction: column;
}

.menu-list {
  display: flex;
  width: fit-content;
}

.menu-link {
  border: 0;
  background-color: transparent;
  position: relative;
  padding: 1rem;
  text-decoration: none;
  color: #000;
  font-weight: bold;
  border-right: 1px solid #00c7ae;
}

.menu-link.active {
  color: #fff;
  font-weight: bold;
  background-color: #00c7ae;
}

.goods-list-container {
  flex-direction: row;
  width: 65rem;
  margin: 10px auto;
  padding: 5px;
}

.review-list-container {
  flex-direction: row;
  width: 65rem;
  margin: 10px auto;
}

.goods-control {
  padding: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-list {
  width: auto;
  padding: 5px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.review-list {
  width: auto;
  padding: 5px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}


.search-container {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.search-input {
  border: 1px solid #e1e1e1;
  border-radius: 4px;
  display: flex;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  padding: 0.5rem;
  width: 30rem;
  box-sizing: border-box;
  color: #323232;
  background-color: #fff;
}

.search-btn {
  display: block;
  text-align: center;
  width: auto;
  font-weight: 400;
  transition: opacity 0.2s ease-in-out;
  color: #fff;
  cursor: pointer;
  background-color: #00c7ae;
  border-color: #00c7ae;
  border: 0.0625rem solid transparent;
  padding: 0.5rem;
  border-radius: 0.25rem;
  text-decoration: #000;
}

.search-btn:hover,
.goods-register-btn:hover {
  opacity: 0.8;
}

.search-img {
  padding: 0 1.25rem;
}

.register-form {
  padding: 5px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  row-gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.register-input {
  border: 1px solid #e1e1e1;
  border-radius: 4px;
  display: block;
  padding: 1rem;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  width: 100%;
  box-sizing: border-box;
  color: #323232;
  background-color: #fff;
}

.form-btn {
  display: flex;
  justify-content: flex-end; /* 버튼을 오른쪽 끝으로 정렬 */
  align-items: center; /* 수직 중앙 정렬 */
  margin-top: 1.5rem; /* 상단 여백 */
}

.register-btn {
  padding: 0.8rem 1.5rem;
  font-size: 1.2rem;
  background-color: #00c7ae;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  height: 100%; /* 버튼의 높이를 다른 입력 필드와 동일하게 설정 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-btn:hover {
  background-color: #009f92;
}

</style>