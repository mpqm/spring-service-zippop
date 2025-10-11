<template>
    <div class="lyt-rootreserve">
        <div class="ctn-split">
            <button class="btn-tagdefault">{{ store.storeName }}</button>
            <div class="ctn-buttons">
                <button v-if="cartItemList.length > 0" class="btn-default" @click="setPaymentData"><Icon icon="iconoir:hand-card" width="20px" height="20px"/>구매하기</button>
                <button class="btn-big" @click="cancel"><Icon icon="iconoir:nav-arrow-left" width="20px" height="20px"/>예약취소</button>
            </div>
        </div>
        <div class="wrp-split">
            <div class="ctn-l50">
                <div class="ctn-inputsearch">
                    <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="keywordSearchAll" />
                    <button class="btn-default" @click="searchAllByKeyword"><Icon icon="ic:search" width="20px" height="20px" /></button>
                    <button class="btn-normal" @click="searchAllGoods(0)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
                </div>
                
                <div class="wrp-list" v-if="goodsList && goodsList.length">
                    <GoodsList v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :storeIdx="store.storeIdx" :showControl="true" @cartUpdated="handleCartUpdated" />
                </div>
                <div class="txt-null" v-else>
                    <p>검색 결과에 해당하는 팝업 굿즈 목록이 없습니다.</p>
                </div>
                <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
            </div>
            <div class="ctn-r50">

                <div class="ctn-split">
                    <h3 class="txt-def0">총 주문 가격</h3> 
                    <span class="txt-def0">{{ totalPrice }}원</span>
                </div>
                <div class="wrp-list">
                    <div class="ctn-list1" v-for="item in cartItemList" :key="item.goodsIdx">
                        <img v-if="item.searchGoodsRes.searchGoodsImageResList && item.searchGoodsRes.searchGoodsImageResList.length > 0" :src="item.searchGoodsRes.searchGoodsImageResList[0].goodsImageUrl" class="img-list" />
                        <div class="ctn-listinfo1">
                            <p class="txt-def0"> {{ item.searchGoodsRes.goodsName }} </p>
                            <p class="txt-def1"> {{ item.price * item.count }}원</p>
                        </div>
                        <div class="ctn-listbuttons">
                            <button :value="item.count" class="btn-tagdefault" type="text" readonly> {{ item.count }} </button>
                            <button class="btn-tagaction" @click="deleteCartItem(item.cartItemIdx)"> <Icon icon="iconoir:trash" width="16px" height="16px"/> </button>
                        </div>
                    </div>
                </div>
        </div>
    </div>
</div>
</template>


<script setup>
import AppPagination from "@/components/AppPagination.vue";
import { ref, onMounted, watch } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useReserveStore } from "@/stores/useReserveStore";
import GoodsList from "@/components/GoodsList.vue";
import { useAuthStore } from '@/stores/useAuthStore';
import { useCartStore } from '@/stores/useCartStore';
import { useOrdersStore } from '@/stores/useOrdersStore';

// store, router, route, toast
const goodsStore = useGoodsStore();
const storeStore = useStoreStore();
const reserveStore = useReserveStore();
const cartStore = useCartStore();
const authStore = useAuthStore();
const ordersStore = useOrdersStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

// 변수(store)
const fileUrls = ref([]);
const store = ref({});

// 변수(cart)
const cartItemList = ref([]);
const totalPrice = ref(0);
const userPoints = ref(0);
const finalOrderPrice = ref(0);

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
    await accessConfirm();
    await search();
    await searchAllGoods();
    await searchAllCart();
    await updateTotalPrice();  // 총 상품 가격 계산
    await updateFinalOrderPrice();  // 총 주문 금액 계산
});

const accessConfirm = async () => {
    const res = await reserveStore.accessConfirm(route.params.reserveIdx, route.params.storeIdx);
    if (!res.success && !reserveStore.access) {
        router.push("/")
        toast.error(res.message)
    }
    return true;
}

const cancel = async () => {
    // 예약 취소 전에 장바구니 삭제
    await cartStore.deleteCart(route.params.storeIdx);
    const res = await reserveStore.cancel(route.params.reserveIdx);
    reserveStore.access = false; // access를 false로 설정하여 router guard 우회
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
const searchAllGoods = async (flag) => {
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
        await searchAllGoods();
    }
};

// 수량 변경 시 자동으로 totalPrice와 finalOrderPrice 업데이트
watch(() => cartStore.cartItemList, async () => {
    await updateTotalPrice();  // 총 상품 가격 계산
    await updateFinalOrderPrice();  // 총 주문 금액 계산
}, { deep: true });

// 카트 목록 조회
const searchAllCart = async () => {
    const res = await cartStore.itemSearchAll(route.params.storeIdx);
    if (res.success) {
        await authStore.getInfo();
        userPoints.value = authStore.userInfo.point;
        cartItemList.value = cartStore.cartItemList;
        cartItemList.value.forEach(item => { item.itemTotalPrice = item.price * item.count; }) // 아이템별 총 금액 계산
        await updateTotalPrice();  // 총 상품 가격 계산
        await updateFinalOrderPrice();  // 총 주문 금액 계산
    }
};

// 장바구니 업데이트 핸들러
const handleCartUpdated = async () => {
    await searchAllCart();
};

// 총 상품 가격 계산
const updateTotalPrice = async () => {
    totalPrice.value = cartItemList.value.reduce((acc, item) => acc + item.itemTotalPrice, 0);
};

// 최종 주문 금액 계산
const updateFinalOrderPrice = async () => {
    finalOrderPrice.value = totalPrice.value;
};

// 결제 정보 저장 및 결제 페이지로 이동
const setPaymentData = async () => {
    const customData = cartItemList.value.map(item => { return { [item.searchGoodsRes.goodsIdx]: item.count }; });
    const paymentData = {
        goodsList: cartItemList.value,
        customData: customData,
        totalPrice: totalPrice.value,
        finalOrderPrice: finalOrderPrice.value,
    };
    await ordersStore.setPaymentData(paymentData);
    router.push(`/reserve/${route.params.storeIdx}/${route.params.reserveIdx}/orders`)
};

// 카트 아이템 삭제
const deleteCartItem = async (cartItemIdx) => {
    await cartStore.deleteCartItem(cartItemIdx)
    cartItemList.value = cartItemList.value.filter(item => item.cartItemIdx !== cartItemIdx);
    cartStore.cartItemList = cartItemList.value
    await updateTotalPrice();
};

</script>