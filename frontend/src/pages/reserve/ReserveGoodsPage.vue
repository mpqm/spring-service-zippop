<template>
    <div>
        <div class="detail-page">
            <div class="detail-container">
                <div class="left-section">
                    <h2>{{ store.storeName }}</h2>
                    <p>{{ store.category }}</p>
                </div>
                <div class="right-section">
                    <button class="orders-btn" @click="goCart">결제하기</button>
                    <button class="cancel-btn" @click="cancel">예약취소</button>
                </div>
            </div>
            <div class="main-page">
                <div class="search-container">
                    <input class="search-input" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="keywordSearchAll" />
                    <button class="search-btn" @click="searchAllByKeyword"><img class="search-img" src="../../assets/img/search-none.png" alt=""></button>
                    <button class="search-btn" @click="searchAll(0)"><img class="search-img" src="../../assets/img/reload-none.png" alt=""></button>
                </div>
                <div class="goods-list-grid" v-if="goodsList && goodsList.length">
                    <GoodsCardComponent v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :storeIdx="store.storeIdx" :showControl="false" />
                </div>
                <div v-else>
                    <p>검색 결과에 해당하는 팝업 굿즈 목록이 없습니다.</p>
                </div>
                <PaginationComponent :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
            </div>
        </div>
    </div>
</template>


<script setup>
import GoodsCardComponent from "@/components/goods/GoodsCardComponent.vue";
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
    await access();
    await search();
    await searchAll();
});

const access = async () => {
    const res = await reserveStore.access(route.params.reserveIdx, route.params.storeIdx);
    if (res.success) {
        toast.success(res.message)
    } else {
        router.push("/")
        toast.error(res.message)
    }
}

const cancel = async () => {
    const res = await reserveStore.cancel(route.params.reserveIdx);
    if (res.success) {
        router.push("/")
        toast.success(res.message)
    } else {
        router.push("/")
        toast.error(res.message)
    }
}

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

// 매핑함수
const mapper = () => {
    if (storeStore.store.searchStoreImageResList && storeStore.store.searchStoreImageResList.length) {
        fileUrls.value = storeStore.store.searchStoreImageResList.map(image => image.storeImageUrl);
    }
}

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

// 장바구니로 이동
const goCart = () => {
    router.push(`/reserve/${route.params.storeIdx}/${route.params.reserveIdx}/cart`);
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
    align-items: center;
    column-gap: 10px;
    background-color: #fff;
    border-radius: 8px;
    border: 1px solid #00c7ae;
    margin: 0;
    width: 65rem;
}

.left-section {
    display: flex;
    margin-left: 10px;
    margin-right: auto;
    gap: 10px;
    align-items: center;
}

.right-section {
    display: flex;
    padding: 1rem;
    gap: 10px;
    align-items: center;
}

.cancel-btn {
    background-color: #ff4d4f;
    color: #fff;
    border: none;
    font-size: 0.8rem;
    padding: 0.8rem;
    font-weight: bold;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.orders-btn {
    background-color: #00c7ae;
    color: #fff;
    border: none;
    font-size: 0.8rem;
    padding: 0.8rem;
    font-weight: bold;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.orders-btn,
.cancel-btn:hover {
    opacity: 0.8;
}

.image-slider {
    width: 100%;
    height: 98%;
    padding: 5px;
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

.normal-btn:hover {
    opacity: 0.8;
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
</style>