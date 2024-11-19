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
          <hr>
          <button @click="goStoreDetail" class="normal-btn">&nbsp;<p>상세 정보 보러가기</p>
            </button>
        </div>
      </div>
      <div class="main-page">
        <div class="search-container">
          <input class="search-input" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요"
            @keyup.enter="keywordSearchAll" />
          <button class="search-btn" @click="searchAllByKeyword"><img class="search-img"
              src="../../assets/img/search-none.png" alt=""></button>
          <button class="search-btn" @click="searchAll(0)"><img class="search-img"
              src="../../assets/img/reload-none.png" alt=""></button>
        </div>
        <div class="goods-list-grid" v-if="goodsList && goodsList.length">
          <GoodsCardComponent v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" />
        </div>
        <div v-else>
          <p>검색 결과에 해당하는 팝업 굿즈 목록이 없습니다.</p>
        </div>
        <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns"
          @page-changed="changePage" />
      </div>
    </div>
    <FooterComponent></FooterComponent>
  </div>
</template>


<script setup>
import GoodsCardComponent from "@/components/goods/GoodsCardComponent.vue";
import ImageSlider from "@/components/common/ImageSlider.vue";
import HeaderComponent from "@/components/common/HeaderComponent.vue";
import FooterComponent from "@/components/common/FooterComponent.vue";
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

// goods
const searchQuery = ref("");
const goodsList = ref([]);
const currentPage = ref(0);
const pageSize = ref(12);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);



onMounted(async () => {
  storeIdx.value = route.params.storeIdx;
  await search();
  await autoSet();

  await searchAll(currentPage.value, pageSize.value);
  goodsList.value = goodsStore.goodsList;
  totalElements.value = goodsStore.totalElements;
  totalPages.value = goodsStore.totalPages;
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

const goStoreDetail = () => {
  router.push(`/store/${storeIdx.value}`);
}

const changePage = async(newPage) => {
  currentPage.value = newPage;
  if (newPage >= 0 && searchQuery.value === "") {
    await searchAll();
  }  else {
    await  searchAllByKeyword();
  }
};

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


const searchAll = async (flag) => {
  if(flag === 0){
    currentPage.value = 0;
    searchQuery.value = "";
  }
  await goodsStore.searchAllGoodsByStoreIdx(storeIdx.value, currentPage.value, pageSize.value);
  totalElements.value = goodsStore.totalElements;
  totalPages.value = goodsStore.totalPages;
  goodsList.value = goodsStore.goodsList;
  hideBtns.value = false;
};

const searchAllByKeyword = async () => {
  currentPage.value = 0;
  const res = await goodsStore.searchAllGoodsByKeywordAndStoreIdx(storeIdx.value, searchQuery.value, currentPage.value, pageSize.value);
  if(res.success){
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
  background-color: #00c7ae;
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

.main-page {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  width: 65rem;
  padding: 1rem;
}

.hero-text {
  display: flex;
  justify-content: center;
  padding: 0.5rem 0;
  font-size: 1.75rem;
  font-weight: 700;
  color: #323232;
}

.search-container {
  display: flex;
  gap: 10px;
  justify-content: center;
  padding-bottom: 16px;
}

.search-input {
  border: 1px solid #e1e1e1;
  border-radius: 4px;
  display: block;
  padding: 1rem;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  width: 50%;
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
.pagination-move-btn:hover {
  opacity: 0.8;
}

.search-img {
  padding: 0 1.25rem;
}

.goods-list-grid {
  margin-top: 16px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  grid-auto-rows: 2fr;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 2rem;
}

.pagination-btn {
  padding: 0.5rem 1rem;
  color: black;
  border: 1px solid #fff;
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
</style>