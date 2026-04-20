<template>
  <div>
    <div class="lyt-child">
      <div class="wrp-split">
        <div class="ctn-l60">
          <div class="wrp-list">
            <div class="ctn-list1" v-for="item in cartItemList" :key="item.goodsIdx">
              <img v-if="item.searchGoodsRes.getGoodsImageResList && item.searchGoodsRes.getGoodsImageResList.length > 0" :src="item.searchGoodsRes.getGoodsImageResList[0].goodsImageUrl" class="img-list" />
              <div class="ctn-listinfo1">
                <p class="txt-def0">{{ item.searchGoodsRes.goodsName }}</p>
                <p class="txt-def1">{{ item.price * item.count }}원 ({{ item.price }}원)</p>
              </div>
              <div class="ctn-listbuttons">
                <button class="btn-tagdefault" @click="updateCartItemQuantity(item.cartItemIdx, -1)"><Icon icon="iconoir:minus" width="20px" height="20px" /></button>
                <button class="btn-tagaction" type="text" readonly>{{ item.count }}</button>
                <button class="btn-tagdefault" @click="updateCartItemQuantity(item.cartItemIdx, 1)"><Icon icon="iconoir:plus" width="20px" height="20px" /></button>
                <button class="btn-tagdefault" @click="removeCartItem(item.cartItemIdx)"><Icon icon="iconoir:trash" width="20px" height="20px" /></button>
              </div>
            </div>
          </div>
        </div>

        <div class="ctn-r40">
          <div class="ctn-split">
            <h3 class="txt-def0">주문</h3>
            <button class="btn-small" @click="router.back()"><Icon icon="iconoir:nav-arrow-left" width="20px" height="20px" /></button>
          </div>
          <div class="ctn-split">
            <span class="txt-def1">총 상품 가격</span> {{ totalPrice }}원
          </div>
          <div class="ctn-split">
            <span class="txt-def1">총 할인</span> {{ totalDiscount }}원
          </div>
          <div class="ctn-split">
            <span class="txt-def1">총 배송비</span> {{ deliveryFee }}원
          </div>
          <div class="ctn-split">
            <span class="txt-def1"><input type="checkbox" v-model="usePoints">포인트 사용:</span>{{ userPoints }} points
          </div>
          <h3><span class="txt-def0">총 주문 금액</span> {{ finalOrderPrice }}원</h3>
          <button v-if="cartItemList.length > 0" class="btn-default" @click="setPaymentData">
            <Icon icon="iconoir:hand-card" width="20px" height="20px" />구매하기
          </button>
          <div class="ctn-noticereward">
            <Icon icon="iconoir:coin" width="16px" height="16px" />
            <span>포인트적립: 결제 금액의 10% 적립</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAccountStore } from '@/stores/accountStore';
import { useCartStore } from '@/stores/cartStore';
import { useOrdersStore } from '@/stores/ordersStore';
import { onMounted, ref, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useToast } from 'vue-toastification';
import { Icon } from '@iconify/vue';

const router = useRouter();
const route = useRoute();
const cartStore = useCartStore();
const accountStore = useAccountStore();
const ordersStore = useOrdersStore();
const toast = useToast();

const cartItemList = ref([]);
const totalPrice = ref(0);
const userPoints = ref(0);
const usePoints = ref(false);
const deliveryFee = ref(2500);
const totalDiscount = ref(0);
const finalOrderPrice = ref(0);

onMounted(async () => {
  await getCartItems();
});

watch(() => cartStore.cartItemList, async () => {
  updateTotalPrice();
  updateTotalDiscount();
  updateFinalOrderPrice();
}, { deep: true });

watch(() => usePoints.value, () => {
  updateTotalDiscount();
  updateFinalOrderPrice();
});

const getCartItems = async () => {
  const res = await cartStore.getCartItems(route.params.cartIdx);
  if (res.success) {
    await accountStore.getAccount();
    userPoints.value = accountStore.userInfo.point;
    cartItemList.value = cartStore.cartItemList;
    cartItemList.value.forEach(item => { item.itemTotalPrice = item.price * item.count; });
    updateTotalPrice();
    updateTotalDiscount();
    updateFinalOrderPrice();
  }
};

const updateCartItemQuantity = async (cartItemIdx, direction) => {
  const operation = direction === -1 ? "DECREASE" : "INCREASE";
  const res = await cartStore.updateCartItemQuantity(route.params.cartIdx, cartItemIdx, operation);
  if (res.success) {
    const item = cartItemList.value.find(i => i.cartItemIdx === cartItemIdx);
    if (item) {
      if (direction === -1 && item.count <= 1) {
        await removeCartItem(cartItemIdx);
      } else {
        item.count = direction === -1 ? item.count - 1 : item.count + 1;
        item.itemTotalPrice = item.price * item.count;
      }
    }
    updateTotalPrice();
    updateTotalDiscount();
    updateFinalOrderPrice();
  } else {
    toast.error(res.message);
  }
};

const removeCartItem = async (cartItemIdx) => {
  await cartStore.deleteCartItem(route.params.cartIdx, cartItemIdx);
  cartItemList.value = cartItemList.value.filter(i => i.cartItemIdx !== cartItemIdx);
  cartStore.cartItemList = cartItemList.value;
  usePoints.value = false;
  updateTotalPrice();
};

const updateTotalPrice = () => {
  totalPrice.value = cartItemList.value.reduce((acc, item) => acc + (item.itemTotalPrice || 0), 0);
};

const updateTotalDiscount = () => {
  totalDiscount.value = usePoints.value ? userPoints.value : 0;
};

const updateFinalOrderPrice = () => {
  finalOrderPrice.value = totalPrice.value - totalDiscount.value + deliveryFee.value;
};

const setPaymentData = async () => {
  const customData = cartItemList.value.map(item => ({ [item.searchGoodsRes.goodsIdx]: item.count }));
  const paymentData = {
    goodsList: cartItemList.value,
    customData,
    totalPrice: totalPrice.value,
    totalDiscount: totalDiscount.value,
    deliveryFee: deliveryFee.value,
    usePoints: usePoints.value,
    finalOrderPrice: finalOrderPrice.value,
    cartIdx: route.params.cartIdx,
  };
  await ordersStore.setPaymentData(paymentData);
  router.push('/orders');
};
</script>
