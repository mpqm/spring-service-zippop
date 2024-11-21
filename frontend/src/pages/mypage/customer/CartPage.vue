<template>
  <div>
    <div class="cart-page">
      <div class="cart-list">
        <table class="cart-table">
          <tbody>
            <tr v-for="item in cartList" :key="item.goodsIdx">
              <td>
                <img v-if="item.searchGoodsRes.searchGoodsImageResList && item.searchGoodsRes.searchGoodsImageResList.length > 0" 
                      :src="item.searchGoodsRes.searchGoodsImageResList[0].goodsImageUrl" class="cart_item_img" />
              </td>
              <td> {{ item.searchGoodsRes.goodsName }} </td>
              <td> {{ item.price * item.count }}원 ({{ item.price }}) </td>
              <td>
                <div class="quantity-container">
                  <img class="quantity-input" src="../../../assets/img/minus-none.png" @click="count(item.cartItemIdx, -1)" alt="">
                  <input :value="item.count" class="quantity-input" type="text" readonly />
                  <img class="quantity-input" src="../../../assets/img/plus-none.png" @click="count(item.cartItemIdx, 1)" alt="">
                  <a href="#" class="delete-option" @click="deleteCartItem(item.cartItemIdx)">삭제</a>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="predict-price-container">
          <h3 class="predict-price-title">주문 예상 금액</h3>
          <div class="predict-price-item"><span>총 상품 가격</span> {{ totalPrice }}원</div>
          <div class="predict-price-item"><span>총 할인</span> {{ totalDiscount }}원</div>
          <div class="predict-price-item"><span>총 배송비</span> {{ deliveryFee }}원</div>
          <div class="predict-price-item"><span><input type="checkbox" v-model="usePoints">포인트 사용:</span>{{ userPoints }} points </div>
          <h3 class="predict-total-price"><span>총 주문 금액</span> {{ finalOrderPrice }}원</h3>
          <div class="reward-area">
            <img class="reward-icon" src="../../../assets/img/point.png" alt="">&nbsp;
            <span>포인트적립: 결제 금액의 10% 적립</span><br>
          </div>
          <a v-if="cartList.length > 0" href="#" class="cart-btn" @click="setPaymentData">구매하기</a>
          <a href="#" class="cart-btn" @click="deleteAllCartItems">장바구니 비우기</a>
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
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';

// store, router, route, toast
const router = useRouter();
const cartStore = useCartStore();
const authStore = useAuthStore();
const ordersStore = useOrdersStore();
const toast = useToast();

// 변수(cart)
const cartList = ref([]);
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
watch(() => cartStore.cartList, async () => {
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
  const res = await cartStore.searchAll();
  if (res.success) {
    await authStore.getInfo();
    userPoints.value = authStore.userInfo.point;
    cartList.value = cartStore.cartList;
    cartList.value.forEach(item => { item.itemTotalPrice = item.price * item.count; }) // 아이템별 총 금액 계산
    await updateTotalPrice();  // 총 상품 가격 계산
    await updateTotalDiscount();  // 총 할인 계산
    await updateFinalOrderPrice();  // 총 주문 금액 계산
    toast.success(res.message);
  }
};

// 수량 조절
const count = async (cartItemIdx, operation) => {
  const operationValue = operation === -1 ? 1 : 0; // -1: 감소, 1: 증가
  const res = await cartStore.count(cartItemIdx, operationValue);
  if (res.success) {
    const item = cartList.value.find(item => item.cartItemIdx === cartItemIdx);
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
  cartList.value = cartList.value.filter(item => item.cartItemIdx !== cartItemIdx);
  cartStore.cartList = cartList.value
  usePoints.value = false
  await updateTotalPrice();
};

// 전체 카트 아이템 삭제
const deleteAllCartItems = async () => {
  await cartStore.deleteAllCartItems()
  cartList.value = [];
  cartStore.cartList = cartList.value
  usePoints.value = false
  await updateTotalPrice();
};

// 총 상품 가격 계산
const updateTotalPrice = async () => {
  totalPrice.value = cartList.value.reduce((acc, item) => acc + item.itemTotalPrice, 0);
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
  const customData = cartList.value.map(item => { return { [item.searchGoodsRes.goodsIdx]: item.count }; });
  const paymentData = {
    goodsList: cartList.value,
    customData: customData,
    totalPrice: totalPrice.value,
    totalDiscount: totalDiscount.value,
    deliveryFee: deliveryFee.value,
    usePoints: usePoints.value,
    finalOrderPrice: finalOrderPrice.value,
  };
  await ordersStore.setPaymentData(paymentData);
  // `ordersPage`로 데이터 전달 (라우터 사용)
  router.push('/orders')
};

</script>

<style scoped>
.cart-page {
  flex-direction: row;
  width: 65rem;
}

.cart-list {
  width: 100%;
  display: flex;
  border: 1px solid #00c7ae;
  border-radius: 8px
}

.cart-table {
  width: 70%;
  border-collapse: collapse;
  table-layout: auto;
  border-radius: 8px;
}

.predict-price-container {
  display: flex;
  flex-direction: column;
  padding: 10px;
  justify-content: center;
  width: 30%;
}

.cart-table td {
  padding: 5px;
  height: 150px;
}

.cart-table td:nth-child(1) {
  width: 150px;
  align-items: center;
}

.cart-table td:nth-child(2) {
  width: fit-content;
  justify-content: center;
}

.cart-table td:nth-child(3) {
  width: fit-content;
  justify-content: center;
}

.cart-table td:nth-child(4) {
  width: fit-content;
  justify-content: center;
}

.quantity-container {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.delete-option {
  display: inline-block;
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
  color: red;
  cursor: pointer;
  text-decoration: none;
  margin-left: 10px;
}

.quantity-input {
  width: 30px;
  height: 30px;
  line-height: 30px;
  border: none;
  border-radius: 8px;
  background-color: #00c7ae;
  text-align: center;
  cursor: pointer;
  display: block;
  font-size: 15px;
  font-weight: bold;
  color: #fff;
}

.cart-btn:hover,
.quantity-input:hover{
  opacity: 0.8;
}

.predict-price-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 20px;
}

.predict-price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.predict-total-price {
  font-size: 20px;
  display: flex;
  justify-content: space-between;
  font-weight: bold;
}

.cart_item_img {
  width: 140px;
  height: 140px;
}

.cart-btn {
  width: auto;
  margin: 5px;
  text-align: center;
  margin-top: 10px;
  background-color: #00c7ae;
  color: #fff;
  padding: 10px;
  text-decoration: none;
  border-radius: 5px;
  font-size: 16px;
}

.reward-area {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 5px;
  padding: 5px;
  border: 1px solid #00c7ae;
  border-radius: 8px;
  font-size: 14px;
}

.reward-icon {
  width: 20px;
  height: 20px;
}
</style>