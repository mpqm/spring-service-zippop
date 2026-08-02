<template>
  <div v-if="showQueueOverlay" class="queue-overlay">
    <div class="queue-modal">
      <h1 class="txt-maintitle">팝업 예약을 위한 대기열 입니다.</h1>
      <h1 class="txt-maintitle">불필요한 접속을 막기위해 예약 유효시간은 10분입니다.</h1>
      <button class="btn-tagstatus">{{ queueStatusMessage }}</button>
      <br>
      <button class="btn-big" @click="cancelReserve"><Icon icon="iconoir:nav-arrow-left" width="20px" height="20px"/>예약 취소</button>
    </div>
  </div>

  <div class="lyt-rootreserve">
    <div class="ctn-split">
      <button class="btn-tagdefault">{{ popup.popupName }}</button>
      <div class="ctn-buttons">
        <button class="btn-big" @click="cancelReserve"><Icon icon="iconoir:nav-arrow-left" width="20px" height="20px"/>예약취소</button>
      </div>
    </div>
    <div class="wrp-split">
      <div class="ctn-l50">
        <div class="ctn-inputsearch">
          <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="fetchGoodsList()" />
          <button class="btn-default" @click="fetchGoodsList()"><Icon icon="ic:search" width="20px" height="20px" /></button>
          <button class="btn-normal" @click="fetchGoodsList(true)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
        </div>
        <div class="wrp-list" v-if="goodsList && goodsList.length">
          <GoodsList v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :popupIdx="Number(popupIdx)" :showControl="true" @cartUpdated="handleCartUpdated" />
        </div>
        <AppEmptyState
          v-else
          title="검색된 예약 굿즈가 없습니다"
          description="검색어를 바꾸거나 전체 예약 굿즈를 다시 확인해 보세요."
          action-label="검색 초기화"
          @action="fetchGoodsList(true)"
        />
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
          <div class="ctn-list1" v-for="item in cartItemList" :key="item.cartItemIdx">
            <img v-if="item.getGoodsRes.getGoodsImageResList && item.getGoodsRes.getGoodsImageResList.length > 0" :src="item.getGoodsRes.getGoodsImageResList[0].goodsImageUrl" class="img-list" />
            <div class="ctn-listinfo1">
              <p class="txt-def0">{{ item.getGoodsRes.goodsName }}</p>
              <p class="txt-def1">{{ item.price * item.count }}원</p>
            </div>
            <div class="ctn-listbuttons">
              <button :value="item.count" class="btn-tagdefault" type="text" readonly>{{ item.count }}</button>
              <button class="btn-tagaction" @click="removeCartItem(item.cartItemIdx)"><Icon icon="iconoir:trash" width="16px" height="16px"/></button>
            </div>
          </div>
        </div>
        <div class="ctn-noticereward">
          <span>결제 요청 후 배송 확정 처리가 되면 환불이 불가능합니다.</span>
        </div>
        <div class="ctn-noticereward">
          <Icon icon="iconoir:coin" class="img-iconior"/>&nbsp;<span>포인트적립: 결제 금액의 10% 적립</span>
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
import { usePopupStore } from "@/stores/popupStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/goodsStore";
import { useReserveStore } from "@/stores/reserveStore";
import GoodsList from "@/components/GoodsList.vue";
import { useAccountStore } from '@/stores/accountStore';
import { useCartStore } from '@/stores/cartStore';
import { useOrdersStore } from '@/stores/ordersStore';
import { useAuthStore } from '@/stores/authStore';
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";
import { IAMPORT_NAME, IAMPORT_PG, IAMPORT_UID, BACKEND_URL, BACKEND_SOCKET_URL } from '@/config';
import { Icon } from '@iconify/vue';
import { isSocketLoginRequiredError, redirectToLogin } from '@/utils/errorHandling';

const goodsStore = useGoodsStore();
const popupStore = usePopupStore();
const reserveStore = useReserveStore();
const cartStore = useCartStore();
const accountStore = useAccountStore();
const ordersStore = useOrdersStore();
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

const popupIdx = ref(route.params.popupIdx);
const reserveIdx = ref(route.params.reserveIdx);

const popup = ref({});
const cartItemList = ref([]);
const totalPrice = ref(0);
const currentCartIdx = ref(null);

const searchQuery = ref("");
const goodsList = ref([]);
const currentPage = ref(0);
const pageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const userInfo = ref({});

const stompClient = ref(null);
const showQueueOverlay = ref(true);
const queueStatusMessage = ref("대기열 연결 중...");
let statusInterval = null;
let reconnectTimer = null;
let leavingForAuthentication = false;

onMounted(async () => {
  const enrolled = await enrollReserve();
  if (!enrolled) return;
  connectWebSocket();

  await getPopup();
  await fetchGoodsList();
  await loadCartItems();
  updateTotalPrice();

  window.addEventListener('beforeunload', handleBeforeUnload);

  statusInterval = setInterval(() => {
    if (showQueueOverlay.value && stompClient.value?.connected && reserveIdx.value) {
      stompClient.value.send("/pub/reserve/status", {}, JSON.stringify({ reserveIdx: reserveIdx.value }));
    }
  }, 10000);

  userInfo.value = accountStore.userInfo;
});

onBeforeUnmount(() => {
  window.removeEventListener('beforeunload', handleBeforeUnload);
  if (stompClient.value?.connected) {
    stompClient.value.disconnect();
  }
  clearInterval(statusInterval);
  clearTimeout(reconnectTimer);
});

const handleBeforeUnload = () => {
  const isNavigatingToOrders = window.location.href.includes('/orders');
  if (!isNavigatingToOrders && reserveIdx.value) {
    fetch(`${BACKEND_URL}/reserves/${reserveIdx.value}/enrollment`, {
      method: 'DELETE',
      credentials: 'include',
      keepalive: true,
    });
  }
};

const connectWebSocket = () => {
  if (leavingForAuthentication) return;
  const socket = new SockJS(BACKEND_SOCKET_URL);
  stompClient.value = Stomp.over(socket);

  stompClient.value.connect({}, () => {
    const userEmail = accountStore.userInfo.email;

    stompClient.value.subscribe(`/user/${userEmail}/reserve/status`, (message) => {
      try {
        const data = JSON.parse(message.body);
        queueStatusMessage.value = data.statusMessage;

        if (data.access === 1) {
          if (data.wtoken) {
            document.cookie = `WTOKEN=${data.wtoken}; path=/; max-age=600; secure; samesite=strict`;
            toast.success("예약 승격! 상품을 선택하실 수 있습니다.");
          } else {
            toast.success("예약 접속 성공! 상품을 선택하실 수 있습니다.");
          }
          showQueueOverlay.value = false;
          reserveStore.access = true;
        } else if (data.access === 0) {
          showQueueOverlay.value = true;
        } else if (data.access === 2 || data.access === 3) {
          toast.error(data.statusMessage);
          router.push("/");
        }
      } catch (error) {
        console.error("메시지 파싱 실패:", error);
      }
    });

    if (stompClient.value?.connected && reserveIdx.value) {
      stompClient.value.send("/pub/reserve/status", {}, JSON.stringify({ reserveIdx: reserveIdx.value }));
    }
  }, async (error) => {
    console.error("WebSocket 연결 실패:", error);
    if (isSocketLoginRequiredError(error) || !authStore.isLoggedIn) {
      leavingForAuthentication = true;
      clearInterval(statusInterval);
      clearTimeout(reconnectTimer);
      reserveStore.access = false;
      await redirectToLogin(router, authStore, route.fullPath);
      return;
    }
    reconnectTimer = setTimeout(connectWebSocket, 5000);
  });
};

const enrollReserve = async () => {
  const res = await reserveStore.enrollReserve(reserveIdx.value);
  if (!res.success) {
    if (authStore.isLoggedIn) {
      toast.error(res.message || "예약 등록에 실패했습니다.");
      router.push("/");
    }
    return false;
  }
  return true;
};

const cancelReserve = async () => {
  if (stompClient.value?.connected) {
    stompClient.value.disconnect();
  }
  clearInterval(statusInterval);
  clearTimeout(reconnectTimer);
  const res = await reserveStore.cancelReserve(reserveIdx.value);
  reserveStore.access = false;
  if (res.success) {
    router.push("/");
    toast.success(res.message);
  } else {
    router.push("/");
    toast.error(res.message);
  }
};

const getPopup = async () => {
  const res = await popupStore.getPopup(popupIdx.value);
  if (res.success) {
    popup.value = popupStore.popup;
  } else {
    router.push("/");
    toast.error(res.message);
  }
};

const fetchGoodsList = async (reset = false) => {
  if (reset) {
    currentPage.value = 0;
    searchQuery.value = "";
  }
  const res = await goodsStore.getGoodsList(popupIdx.value, searchQuery.value || null, currentPage.value, pageSize.value);
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

const changePage = async (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return;
  currentPage.value = newPage;
  await fetchGoodsList();
};

watch(() => cartStore.cartItemList, () => {
  updateTotalPrice();
}, { deep: true });

const loadCartItems = async () => {
  const cartsRes = await cartStore.getCarts(0, 100);
  if (cartsRes.success && cartStore.cartList.length) {
    const cart = cartStore.cartList.find(c => String(c.popupIdx) === String(popupIdx.value));
    if (cart) {
      currentCartIdx.value = cart.cartIdx;
      await accountStore.getAccount();
      const res = await cartStore.getCartItems(cart.cartIdx);
      if (res.success) {
        cartItemList.value = cartStore.cartItemList;
        cartItemList.value.forEach(item => { item.itemTotalPrice = item.price * item.count; });
        updateTotalPrice();
      }
    }
  }
};

const handleCartUpdated = async () => {
  await loadCartItems();
};

const updateTotalPrice = () => {
  totalPrice.value = cartItemList.value.reduce((acc, item) => acc + (item.itemTotalPrice || 0), 0);
};

const removeCartItem = async (cartItemIdx) => {
  if (currentCartIdx.value) {
    await cartStore.deleteCartItem(currentCartIdx.value, cartItemIdx);
    cartItemList.value = cartItemList.value.filter(i => i.cartItemIdx !== cartItemIdx);
    cartStore.cartItemList = cartItemList.value;
    updateTotalPrice();
  }
};

const transformGoodsList = (customData) => {
  return customData.reduce((acc, item) => {
    const [goodsIdx, count] = Object.entries(item)[0];
    acc[goodsIdx] = count;
    return acc;
  }, {});
};

const payment = () => {
  if (typeof window.IMP === 'undefined') { return; }
  const IMP = window.IMP;
  IMP.init(IAMPORT_UID);

  const customData = cartItemList.value.map(item => ({ [item.getGoodsRes.goodsIdx]: item.count }));

  IMP.request_pay({
    pg: IAMPORT_PG,
    merchant_uid: "order_no_" + new Date().getMilliseconds(),
    name: IAMPORT_NAME,
    amount: totalPrice.value,
    buyer_email: userInfo.value.email,
    buyer_name: userInfo.value.name,
    custom_data: transformGoodsList(customData),
  }, async (rsp) => {
    if (rsp.success) {
      const res = await ordersStore.createOrder({
        impUid: rsp.imp_uid,
        popupIdx: Number(route.params.popupIdx),
        reserveIdx: Number(route.params.reserveIdx),
      });
      if (res.success) {
        if (currentCartIdx.value) {
          await cartStore.deleteCarts(currentCartIdx.value);
        }
        await reserveStore.cancelReserve(route.params.reserveIdx);
        reserveStore.access = false;
        toast.success("결제를 처리했습니다.");
        router.push({ path: `/orders/${res.result.ordersIdx}`, query: { mainTab: 'reserve' } });
      } else {
        toast.error("결제를 처리하지 못했습니다.");
      }
    } else {
      toast.error("결제를 처리하지 못했습니다.");
    }
  });
};
</script>
