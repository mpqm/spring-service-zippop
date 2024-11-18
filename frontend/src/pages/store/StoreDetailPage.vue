<template>
    <div>
        <HeaderComponent></HeaderComponent>
        <div class="detail-page">
            <div class="detail-container">
                <div class="left-panel">
                  <ImageSlider class="image-slider" :fileUrls="fileUrls"/>
                </div>
                <div class="right-panel">
                    <div class="title">
                        <h2>{{ storeName }}</h2>
                        <p>{{ category }}</p>
                    </div>
                    <p> {{ address }} / {{ addressDetail }}</p>
                    <p> {{ storeStartDate }} ~ {{ storeEndDate }}</p>
                    <p> {{ storeContent }}</p>
                    <p class="t2">
                        <img class="like-img" src="../../assets/img/like-fill.png" alt="" />&nbsp;{{ likeCount }}
                        <img class="people-img" src="../../assets/img/people.png" alt="" />&nbsp;{{ totalPeople }}
                    </p>
                    <CountDownTimer></CountDownTimer>
                    <hr>
                    <button class="normal-btn"><img src="../../assets/img/reserve-none.png" alt="">&nbsp;<p>예약 참여</p></button>
                </div>
            </div>
            <div class="detail-header">
                <div class="menu-list">
                    <button  class="menu-link" :class="{ active: activeMenu === 'goods' }"  @click="setActiveMenu('goods')">굿즈 보기</button>
                    <button  class="menu-link" :class="{ active: activeMenu === 'review' }"  @click="setActiveMenu('review')"> 리뷰 보기</button>
                    <button  class="menu-link" :class="{ active: activeMenu === 'question' }"  @click="setActiveMenu('review')"> 문의 채팅</button>
                </div>
            </div>
            <div v-if="activeMenu=='goods'" class="goods-list-container">
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
            <div v-if="activeMenu=='review'" class="review-list-container">
                <div class="review-list" v-if="reviewList && reviewList.length">
                    <ReviewListComponent v-for="review in reviewList" :key="review.reviewIdx" :review="review" />
                </div>
                <div class="notice" v-else>
                    <p>등록된 리뷰가 없습니다.</p>
                </div>
                <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
            </div>
            <div v-if="activeMenu=='chat'">
                dd
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
import PaginationComponent from "@/components/common/PaginationComponent.vue";
import { ref, onMounted } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/useGoodsStore";

const goodsStore = useGoodsStore();
const storeStore = useStoreStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

// store
const storeIdx = ref(null);
const storeName = ref("");
const storeContent = ref("");
const category = ref("");
const totalPeople = ref(0);
const likeCount = ref(0);
const address = ref("");
const addressDetail = ref("");
const storeStartDate = ref("");
const storeEndDate = ref("");
const fileUrls = ref([]);
const store = ref({});
const activeMenu = ref('');

// goods
const searchQuery = ref("");
const showControl = ref(false);
const goodsList = ref([]);
const currentPage = ref(0);
const goodsPageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);


// review
const reviewList = ref([]);
const reviewPageSize = ref(8);

onMounted(async () => {
  storeIdx.value = route.params.storeIdx;
    await search();
    await autoSet();
});

const search = async () => {
    const res = await storeStore.searchStore(storeIdx.value);
    if (res.success) {
        store.value = storeStore.store;
        await autoSet();
    } else {
        router.push("/")
        toast.error(res.message);
    }
}

const autoSet = async () => {
    storeIdx.value = storeStore.store.storeIdx;
    storeName.value = storeStore.store.storeName;
    storeContent.value = storeStore.store.storeContent;
    category.value = storeStore.store.category;
    totalPeople.value = storeStore.store.totalPeople;
    likeCount.value = storeStore.store.likeCount;
    address.value = storeStore.store.storeAddress.split(',')[0];
    addressDetail.value = storeStore.store.storeAddress.split(',')[1] || '';
    storeStartDate.value = storeStore.store.storeStartDate;
    storeEndDate.value = storeStore.store.storeEndDate;
    if (storeStore.store.searchStoreImageResList && storeStore.store.searchStoreImageResList.length) {
        fileUrls.value = storeStore.store.searchStoreImageResList.map(image => image.storeImageUrl);
    }
    goodsList.value = storeStore.store.searchGoodsResList;
}

const setActiveMenu = (menu) => {
    activeMenu.value = menu;
    if(menu == 'goods') {
        currentPage.value = 0;
        searchAllGoods();
    } else if(menu == 'review') {
      searchAllReview();
    }
}

const changePage = (newPage) => {
  currentPage.value = newPage;
  console.log("A")
  if (newPage >= 0 && activeMenu.value == 'goods') {
    if(searchQuery.value === ""){
      searchAllGoods();
    } else {
      searchAllGoodsByKeyword();
    }
  } else if (newPage >= 0 && activeMenu.value == 'review') {
    searchAllReview();
  }
};

const searchAllGoods = async (flag) => {
  if(flag == 0) {
    currentPage.value = 0;
    searchQuery.value = "";
  }
  const res = await goodsStore.searchAllGoodsByStoreIdx(storeIdx.value, currentPage.value, goodsPageSize.value);
  if (res.success) {
    totalElements.value = goodsStore.totalElements;
    totalPages.value = goodsStore.totalPages;
    goodsList.value = goodsStore.goodsList;
    hideBtns.value = false;
  } else {
    goodsList.value = null;
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const searchAllGoodsByKeyword = async () => {
  const res = await goodsStore.searchAllGoodsByKeywordAndStoreIdx(searchQuery.value, storeIdx.value, currentPage.value, goodsPageSize.value);
  if (res.success) {
    totalElements.value = goodsStore.totalElements;
    totalPages.value = goodsStore.totalPages;
    goodsList.value = goodsStore.goodsList;
    hideBtns.value = false;
  } else {
    goodsList.value = null;
    totalElements.value = null;
    totalPages.value = null;
    hideBtns.value = true;
  }
};

const searchAllReview = async () => {
  const res = await storeStore.searchAllReview(storeIdx.value, currentPage.value, reviewPageSize.value);
  if (res.success) {
    reviewList.value = storeStore.reviewList;
    totalElements.value = storeStore.totalElements;
    totalPages.value = storeStore.totalPages;
  } else {
    reviewList.value = null;
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
    padding: 5px;;
}

.notice{
    text-align: center;
  }
.left-panel, .right-panel {
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
.file-preview {
    display: flex;
    overflow-x: auto;
    gap: 10px;
    padding: 1rem 0;
}
.file-preview-item img {
    width: 100%;
    max-height: 300px;
    object-fit: cover;
    border-radius: 8px;
}
.store-img {
  width: 100%;
  height: fit-content;
  border-radius: 8px;
  margin-top: 4px;
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
    background-color:#00c7ae;
  }

.goods-list-container {
  flex-direction: row;
  width: 65rem;
  border: 1px solid #00c7ae;
  margin: 10px auto;
  border-radius: 8px;
}

.review-list-container {
  flex-direction: row;
  width: 65rem;
  border: 1px solid #00c7ae;
  margin: 10px auto;
  border-radius: 8px;
}

.goods-control {
  padding: 5px;
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin: 10px auto;
}

.back-btn {
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

.btn-container {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.register-btn {
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

.pagination-btn {
  padding: 0.5rem 1rem;
  color: black;
  border: 1px solid #ddd;
  cursor: pointer;
  border-radius: 5px;
}

.pagination-move-btn {
  padding: 0.5rem 1rem;
  color: #fff;
  background-color: #00c7ae;
  border: none;
  cursor: pointer;
  border-radius: 5px;
}

.pagination-btn.active {
  background-color: #00c7ae;
  color: #fff;
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
.pagination-btn:hover,
.pagination-move-btn:hover,
.goods-register-btn:hover {
  opacity: 0.8;
}

.search-img {
  padding: 0 1.25rem;
}
</style>