<template>
    <!-- 대기열 오버레이 (대기 중일 때만 표시) -->
    <div v-if="showQueueOverlay" class="queue-overlay">
        <div class="queue-modal">
            <h1 class="txt-maintitle">팝업 예약을 위한 대기열 입니다.</h1>
            <h1 class="txt-maintitle">불필요한 접속을 막기위해 예약 유효시간은 10분입니다.</h1>
            <button class="btn-tagstatus">{{ queueStatusMessage }}</button>
            <br>
            <button class="btn-big" @click="cancel"><Icon icon="iconoir:nav-arrow-left" width="20px" height="20px"/>예약 취소</button>
        </div>
    </div>

    <div class="lyt-rootreserve">
        <div class="ctn-split">
            <button class="btn-tagdefault">{{ store.storeName }}</button>
            <div class="ctn-buttons">
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
                    <GoodsList v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :storeIdx="storeIdx" :showControl="true" @cartUpdated="handleCartUpdated" />
                </div>
                <div class="txt-null" v-else>
                    <p>검색 결과에 해당하는 팝업 굿즈 목록이 없습니다.</p>
                </div>
                <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
            </div>
                <div class="ctn-r50">
                    <div class="ctn-split">
                        <h3 class="txt-def0">예약 상품 정보</h3>
                    </div>
                    <div class="ctn-defaultinfo">
                    <div class="ctn-split">
                        <span class="txt-def2">구매자 이름</span> 
                        <span class="txt-def2">{{ userInfo.name }}</span>
                    </div>
                    <div class="ctn-split">
                        <span class="txt-def2">구매자 이메일</span> 
                        <span class="txt-def2">{{ userInfo.email }}</span>
                    </div>
                    <div class="ctn-split">
                        <span class="txt-def2">구매자 휴대전화번호</span> 
                        <span class="txt-def2">{{ userInfo.phoneNumber }}</span>
                    </div>
                    <div class="ctn-split">
                        <span class="txt-def2">배송지 주소</span> 
                        <span class="txt-def2">{{ userInfo.address }}</span>
                    </div>
                </div>
                <h3 class="txt-def0">상품 구매 정보</h3>
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
                <div class="ctn-noticereward">
                <span>결제 요청 후 배송 확정 처리가 되면 환불이 불가능합니다.</span><br>
            </div>
            <div class="ctn-noticereward">
                    <Icon icon="iconoir:coin" class="img-iconior"/>&nbsp;<span>포인트적립: 결제 금액의 10% 적립</span><br>
            </div>
            <button type="button" @click="payment" class="btn-default">
                <Icon icon="iconoir:hand-card" width="20px" height="20px"/>결제하기
            </button>
        </div>
    </div>
</div>
</template>


<script setup>
import AppPagination from "@/components/AppPagination.vue";
import { ref, onMounted, onBeforeUnmount, watch } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useReserveStore } from "@/stores/useReserveStore";
import GoodsList from "@/components/GoodsList.vue";
import { useAuthStore } from '@/stores/useAuthStore';
import { useCartStore } from '@/stores/useCartStore';
import { useOrdersStore } from '@/stores/useOrdersStore';
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { IAMPORT_NAME, IAMPORT_PG, IAMPORT_UID } from '@/config';
import { Icon } from '@iconify/vue';

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

// 변수(route params 저장 - beforeunload에서 사용)
const storeIdx = ref(route.params.storeIdx);
const reserveIdx = ref(route.params.reserveIdx);

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

// 변수(orders)
const userInfo = ref({});

// 변수(WebSocket & Queue)
const stompClient = ref(null);
const showQueueOverlay = ref(true); // 처음에는 대기열 표시
const queueStatusMessage = ref("대기열 연결 중...");
let statusInterval = null;

// onMounted 
onMounted(async () => {
    // 예약 등록 및 WebSocket 연결
    await enrollReserve();
    connectWebSocket();
    
    // 상품 및 장바구니 데이터 로드
    await search();
    await searchAllGoods();
    await searchAllCart();
    await updateTotalPrice();
    await updateFinalOrderPrice();
    
    // 브라우저 종료 시 이벤트 리스너
    window.addEventListener('beforeunload', handleBeforeUnload);
    
    // 10초마다 상태 확인 (대기 중일 때만)
    statusInterval = setInterval(() => {
        if (showQueueOverlay.value) {
            if (stompClient.value && stompClient.value.connected && reserveIdx.value) {
                stompClient.value.send("/pub/reserve/status", {}, JSON.stringify({ reserveIdx: reserveIdx.value }));
            }
        }
    }, 10000);

    userInfo.value = authStore.userInfo
});

// 컴포넌트 언마운트 시 이벤트 리스너 제거, 소켓종료
onBeforeUnmount(() => {
    window.removeEventListener('beforeunload', handleBeforeUnload);
    if (stompClient.value && stompClient.value.connected) {
        stompClient.value.disconnect(() => {
            console.log("WebSocket 연결 해제됨");
        });
    }
    clearInterval(statusInterval);
});

// 브라우저 종료 시 예약 자동 취소 (단, 결제 페이지로 이동하는 경우 제외)
const handleBeforeUnload = () => {
    // 결제 페이지로 이동하는 경우 취소하지 않음
    const isNavigatingToOrders = window.location.href.includes('/orders');
    
    if (!isNavigatingToOrders && storeIdx.value && reserveIdx.value) {
        // 브라우저 종료 또는 다른 페이지 이동 시에만 취소
        navigator.sendBeacon(`${import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080'}/api/v1/reserve/cancel?reserveIdx=${reserveIdx.value}&storeIdx=${storeIdx.value}`);
    }
};

// WebSocket 연결
const connectWebSocket = () => {
    const socket = new SockJS("http://localhost:8080/ws");
    stompClient.value = Stomp.over(socket);
    
    stompClient.value.connect(
        {},
        () => {
            console.log("WebSocket 연결 성공");
            const userEmail = authStore.userInfo.email;
            
            stompClient.value.subscribe(`/user/${userEmail}/reserve/status`, (message) => {
                try {
                    const data = JSON.parse(message.body);
                    queueStatusMessage.value = data.statusMessage;
                    
                    console.log("WebSocket 메시지 수신 - access:", data.access);
                    
                    if (data.access === 1) {
                        // 예약 큐에 진입 → 오버레이 숨김
                        console.log("✅ 예약 큐 진입 - 상품 선택 가능");
                        
                        if (data.wtoken) {
                            document.cookie = `WTOKEN=${data.wtoken}; path=/; max-age=600; secure; samesite=strict`;
                            toast.success("🎉 예약 승격! 상품을 선택하실 수 있습니다.");
                        } else {
                            toast.success("✅ 예약 접속 성공! 상품을 선택하실 수 있습니다.");
                        }
                        
                        showQueueOverlay.value = false;
                        reserveStore.access = true;
                        
                    } else if (data.access === 0) {
                        // 대기 큐 → 오버레이 유지
                        console.log("⏳ 대기 큐에 있음");
                        showQueueOverlay.value = true;
                        
                    } else if (data.access === 2 || data.access === 3) {
                        // 예약 종료 or 마감
                        toast.error(data.statusMessage);
                        router.push("/");
                    }
                } catch (error) {
                    console.error("메시지 파싱 실패:", error);
                }
            });
            
            // 초기 상태 요청
            if (stompClient.value && stompClient.value.connected && reserveIdx.value) {
                stompClient.value.send("/pub/reserve/status", {}, JSON.stringify({ reserveIdx: reserveIdx.value }));
            }
        },
        (error) => {
            console.error("WebSocket 연결 실패:", error);
            setTimeout(() => connectWebSocket(), 5000);
        }
    );
};

// 예약 등록
const enrollReserve = async () => {
    const res = await reserveStore.enroll(reserveIdx.value);
    if (!res.success) {
        toast.error("예약 등록에 실패했습니다.");
        router.push("/");
    }
};

// 예약 취소
const cancel = async () => {
    // WebSocket 연결 해제
    if (stompClient.value && stompClient.value.connected) {
        stompClient.value.disconnect(() => {
            console.log("WebSocket 연결 해제됨");
        });
    }
    clearInterval(statusInterval);
    
    // 장바구니 삭제는 백엔드에서 처리하므로 제거
    const res = await reserveStore.cancel(reserveIdx.value, storeIdx.value);
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
    const res = await storeStore.searchStore(storeIdx.value);
    if (res.success) {
        store.value = storeStore.store;
        if (storeStore.store.searchStoreImageResList && storeStore.store.searchStoreImageResList.length) {
            fileUrls.value = storeStore.store.searchStoreImageResList.map(image => image.storeImageUrl);
        }

    } else {
        router.push("/")
        toast.error(res.message);
    }
}

// 굿즈 목록 조회
const searchAllGoods = async (flag) => {
    if (flag === 0) {
        currentPage.value = 0;
        searchQuery.value = "";
        isKeywordSearch.value = false; // 일반 검색 상태로 전환
    }
    const res = await goodsStore.searchAllByStoreIdx(storeIdx.value, currentPage.value, pageSize.value);
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
    const res = await goodsStore.searchAllByKeywordAndStoreIdx(searchQuery.value, storeIdx.value, currentPage.value, pageSize.value);
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
    const res = await cartStore.itemSearchAll(storeIdx.value);
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

// 카트 아이템 삭제
const deleteCartItem = async (cartItemIdx) => {
    await cartStore.deleteCartItem(cartItemIdx)
    cartItemList.value = cartItemList.value.filter(item => item.cartItemIdx !== cartItemIdx);
    cartStore.cartItemList = cartItemList.value
    await updateTotalPrice();
};

// 굿즈 목록 내역에 맞게 CustomData로 바꾸는 함수
const transformGoodsList = (customData) => {
    return customData.reduce((acc, item) => {
        const [goodsIdx, count] = Object.entries(item)[0];
        acc[goodsIdx] = count;
        return acc;
    }, {});
};

// 결제 처리
const payment = () => {
    if (typeof window.IMP === 'undefined') { return; }
    const IMP = window.IMP;
    IMP.init(IAMPORT_UID);
    
    // customData 생성
    const customData = cartItemList.value.map(item => { 
        return { [item.searchGoodsRes.goodsIdx]: item.count }; 
    });
    
    IMP.request_pay(
        {
            pg: IAMPORT_PG,
            merchant_uid: "order_no_" + new Date().getMilliseconds(),
            name: IAMPORT_NAME,
            amount: finalOrderPrice.value,
            buyer_email: userInfo.value.email,
            buyer_name: userInfo.value.name,
            custom_data: transformGoodsList(customData), // 생성한 customData 전달
        },
        async (rsp) => {
            if (rsp.success) {
                const res = await ordersStore.verifyReserve(route.params.storeIdx, rsp.imp_uid, route.params.reserveIdx);
                if (res.success) {
                    await cartStore.deleteCart(route.params.storeIdx);
                    await reserveStore.cancel(route.params.reserveIdx);
                    reserveStore.access = false; // access를 false로 설정하여 router guard 우회
                    toast.success("결제를 처리했습니다.");
                    router.push("/")
                } else {
                    toast.error("결제를 처리하지 못했습니다.");
                }
            } else {
                toast.error("결제를 처리하지 못했습니다.");
            }
        }
    );
};

</script>