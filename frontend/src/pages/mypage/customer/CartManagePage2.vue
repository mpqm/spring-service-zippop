<template>
    <div>
      <div class="lyt-child">
        <div class="wrp-split">
          <div class="ctn-l60">
            <div class="wrp-list">
              <div class="ctn-list1" v-for="item in cartItemList" :key="item.goodsIdx">
                <img v-if="item.searchGoodsRes.searchGoodsImageResList && item.searchGoodsRes.searchGoodsImageResList.length > 0" :src="item.searchGoodsRes.searchGoodsImageResList[0].goodsImageUrl" class="img-list" />
                <div class="ctn-listinfo1">
                  <p class="txt-def0"> {{ item.searchGoodsRes.goodsName }} </p>
                  <p class="txt-def1"> {{ item.price * item.count }}원 ({{ item.price }}) </p>
                </div>
                <div class="ctn-listbuttons">
                  <button class="btn-tagdefault" @click="count(item.cartItemIdx, -1)"> <Icon icon="iconoir:minus" width="20px" height="20px"/> </button>
                  <button :value="item.count" class="btn-tagaction" type="text" readonly> {{ item.count }} </button>
                  <button class="btn-tagdefault" @click="count(item.cartItemIdx, 1)"> <Icon icon="iconoir:plus" width="20px" height="20px"/> </button>
                  <button class="btn-tagdefault" @click="deleteCartItem(item.cartItemIdx)"> <Icon icon="iconoir:trash" width="20px" height="20px"/> </button>
                </div>
              </div>
            </div>
          </div>
          
          <div class="ctn-r40">
            <div class="ctn-split">
              <h3 class="txt-def0">주문</h3>
              <button class="btn-small" @click="router.back()"><Icon icon="iconoir:nav-arrow-left" width="20px" height="20px"/></button>
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

            <button v-if="cartItemList.length > 0" class="btn-default" @click="setPaymentData"><Icon icon="iconoir:hand-card" width="20px" height="20px"/>구매하기</button>
            <div class="ctn-noticereward">
              <Icon icon="iconoir:coin" width="16px" height="16px"/> 
              <span>포인트적립: 결제 금액의 10% 적립</span><br>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { useAuthStore } from '@/stores/useAuthStore';
  import { useCartStore } from '@/stores/useCartStore';
  import { useOrdersStore } from '@/stores/useOrdersStore';
  import { onMounted, ref, watch } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import { useToast } from 'vue-toastification';
  
  // store, router, route, toast
  const router = useRouter();
  const route = useRoute();
  const cartStore = useCartStore();
  const authStore = useAuthStore();
  const ordersStore = useOrdersStore();
  const toast = useToast();
  
  // 변수(cart)
  const cartItemList = ref([]);
  const totalPrice = ref(0);
  const userPoints = ref(0);
  const usePoints = ref(false);
  const deliveryFee = ref(2500);
  const totalDiscount = ref(0);
  const finalOrderPrice = ref(0);
  
  // onMounted
  onMounted(async () => {
    await searchAll();
    await updateTotalPrice();  // 총 상품 가격 계산
    await updateTotalDiscount();  // 총 할인 계산
    await updateFinalOrderPrice();  // 총 주문 금액 계산
  });
  
  // 수량 변경 시 자동으로 totalPrice와 finalOrderPrice 업데이트
  watch(() => cartStore.cartItemList, async () => {
    await updateTotalPrice();  // 총 상품 가격 계산
    await updateTotalDiscount();  // 총 할인 계산
    await updateFinalOrderPrice();  // 총 주문 금액 계산
  }, { deep: true });
  
  // usePoints가 변경될 때마다 totalDiscount와 finalOrderPrice 업데이트
  watch(() => usePoints.value, async () => {
    await updateTotalDiscount();  // 포인트 사용 여부에 따라 할인 계산
    await updateFinalOrderPrice();  // 총 주문 금액 계산
  });
  
  // 카트 목록 조회
  const searchAll = async () => {
    const res = await cartStore.itemSearchAll(route.params.storeIdx);
    if (res.success) {
      await authStore.getInfo();
      userPoints.value = authStore.userInfo.point;
      cartItemList.value = cartStore.cartItemList;
      cartItemList.value.forEach(item => { item.itemTotalPrice = item.price * item.count; }) // 아이템별 총 금액 계산
      await updateTotalPrice();  // 총 상품 가격 계산
      await updateTotalDiscount();  // 총 할인 계산
      await updateFinalOrderPrice();  // 총 주문 금액 계산
    }
  };
  
  // 수량 조절
  const count = async (cartItemIdx, operation) => {
    const operationValue = operation === -1 ? 1 : 0; // -1: 감소, 1: 증가
    const res = await cartStore.itemCount(cartItemIdx, operationValue);
    if (res.success) {
      const item = cartItemList.value.find(item => item.cartItemIdx === cartItemIdx);
      if (item) {
        if (operation === -1 && item.count <= 1) {
          await deleteCartItem(cartItemIdx);  // 수량이 0이 되면 삭제
        } else {
          item.count = operation === -1 ? item.count - 1 : item.count + 1;
          item.itemTotalPrice = item.price * item.count; // 수량 변경 시 총 가격 업데이트
        }
      }
      // 수량 변경 후 합계 다시 계산
      await updateTotalPrice();  // 총 상품 가격 계산
      await updateTotalDiscount();  // 총 할인 계산
      await updateFinalOrderPrice();  // 총 주문 금액 계산
    } else {
      toast.error(res.message);
    }
  };
  
  // 카트 아이템 삭제
  const deleteCartItem = async (cartItemIdx) => {
    await cartStore.deleteCartItem(cartItemIdx)
    cartItemList.value = cartItemList.value.filter(item => item.cartItemIdx !== cartItemIdx);
    cartStore.cartItemList = cartItemList.value
    usePoints.value = false
    await updateTotalPrice();
  };
  
  // 총 상품 가격 계산
  const updateTotalPrice = async () => {
    totalPrice.value = cartItemList.value.reduce((acc, item) => acc + item.itemTotalPrice, 0);
  };
  
  // 총 할인 계산 (포인트 사용 여부에 따라 할인액 결정)
  const updateTotalDiscount = async () => {
    totalDiscount.value = usePoints.value ? userPoints.value : 0;
  };
  
  // 최종 주문 금액 계산
  const updateFinalOrderPrice = async () => {
    finalOrderPrice.value = totalPrice.value - totalDiscount.value + deliveryFee.value;
  };
  
  // 결제 정보 저장 및 결제 페이지로 이동
  const setPaymentData = async () => {
    const customData = cartItemList.value.map(item => { return { [item.searchGoodsRes.goodsIdx]: item.count }; });
    const paymentData = {
      goodsList: cartItemList.value,
      customData: customData,
      totalPrice: totalPrice.value,
      totalDiscount: totalDiscount.value,
      deliveryFee: deliveryFee.value,
      usePoints: usePoints.value,
      finalOrderPrice: finalOrderPrice.value,
      storeIdx: route.params.storeIdx,
    };
    await ordersStore.setPaymentData(paymentData);
    // `ordersPage`로 데이터 전달 (라우터 사용)
    router.push('/orders')
  };
  
  </script>
