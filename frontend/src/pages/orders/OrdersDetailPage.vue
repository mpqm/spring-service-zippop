<template>
  <div>
    <HeaderComponent></HeaderComponent>
    <div class="payment-page">
      <hr>
      <h3 class="t1">배송지 정보</h3>
      <div class="userinfo-container">
        <div class="userinfo-item"><span>구매자 이름</span> {{ orders.name }}</div>
        <div class="userinfo-item"><span>구매자 이메일</span> {{ orders.email }}</div>
        <div class="userinfo-item"><span>구매자 휴대전화번호</span> {{ orders.phoneNumber }}</div>
        <div class="userinfo-item"><span>배송지 주소</span> {{ orders.address }}</div>
      </div>
      <hr>
      <h3 class="t1">상품 구매 정보</h3>
      <table class="cart-table">
        <tbody>
          <th>상품이미지</th>
          <th>상품명</th>
          <th>상품가격</th>
          <th>상품수량</th>
          <tr v-for="item in orders.searchOrdersDetailResList" :key="item.ordersDetailIdx">
            <td>
              <img
                v-if="item.searchGoodsRes.searchGoodsImageResList && item.searchGoodsRes.searchGoodsImageResList.length > 0"
                :src="item.searchGoodsRes.searchGoodsImageResList[0].goodsImageUrl" class="cart_item_img" />
            </td>
            <td>
              {{ item.searchGoodsRes.goodsName }}
            </td>
            <td>
              {{ item.eachPrice }}원 ({{ item.searchGoodsRes.goodsPrice }})
            </td>
            <td>{{ item.eachPrice / item.searchGoodsRes.goodsPrice }}</td>
          </tr>
        </tbody>
      </table>
      <hr>
      <h3 class="t1">주문 정보</h3>
      <div class="predict-price-container">
        <div class="predict-price-item"><span>총 상품 가격</span> {{ orders.totalPrice - orders.deliveryCost + orders.usedPoint }}원</div>
        <div class="predict-price-item"><span>사용 포인트</span> {{ orders.usedPoint }}원</div>
        <div class="predict-price-item"><span>총 배송비</span> {{ orders.deliveryCost }}원</div>
        <h3 class="predict-total-price"><span>주문 상태</span> {{ formatOrdersStatus(orders.orderStatus) }}</h3>
        <h3 class="predict-total-price"><span>총 주문 금액</span> {{ orders.totalPrice }}원</h3>
      </div>
      <div class="reward-area">
        <span>결제 요청 후 배송 확정 처리가 되면 환불이 불가능합니다.</span><br>
      </div>
      <button type="button" @click="goBack" class="back-btn">뒤로가기</button>
    </div>
    <FooterComponent></FooterComponent>
  </div>
</template>

<script setup>
import HeaderComponent from '@/components/common/HeaderComponent.vue';
import FooterComponent from "@/components/common/FooterComponent.vue";
import { useOrdersStore } from '@/stores/useOrdersStore';
import { onMounted, ref } from 'vue';
import { useToast } from 'vue-toastification';
import { useRoute, useRouter } from 'vue-router';

// store, router, route, toast
const ordersStore = useOrdersStore();
const toast = useToast();
const router = useRouter();
const route = useRoute();

// 변수(orders)
const orders = ref({});

// onMounted
onMounted(async () => {
  await searchOrders();
});

// 주문 단일 조회
const searchOrders = async () => {
  if (route.query.storeIdx) {
    const res = await ordersStore.searchAsCompany(route.query.storeIdx, route.params.ordersIdx);
    if (res.success) {
      orders.value = ordersStore.orders;
    } else {
      router.push("/mypage/company/orders")
      toast.error(res.message);
    }
  } else {
    const res = await ordersStore.searchAsCustomer(route.params.ordersIdx);
    if (res.success) {
      orders.value = ordersStore.orders;
    } else {
      router.push("/mypage/customer/orders")
      toast.error(res.message);
    }
  }
}

// 뒤로 가기
const goBack = () => {
  router.go(-1)
}

// 주문 상태 포맷팅 함수
const formatOrdersStatus = (statusString) => {
  if (statusString === "STOCK_READY") return "재고 굿즈 결제 완료";
  else if (statusString === "STOCK_CANCEL") return "재고 굿즈 결제 취소";
  else if (statusString === "STOCK_COMPLETE") return "재고 굿즈 주문 확정";
  else if (statusString === "STOCK_DELIVERY") return "재고 굿즈 배달 중";
  else if (statusString === "RESERVE_READY") return "예약 굿즈 결제 완료";
  else if (statusString === "RESERVE_CANCEL") return "예약 굿즈 결제 취소";
  else if (statusString === "RESERVE_COMPLETE") return "예약 굿즈 주문 확정";
  else if (statusString === "RESERVE_DELIVERY") return "예약 굿즈 배달 중";
}

</script>

<style scoped>
.payment-page {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  width: 35rem;
  padding: 1rem;
}

hr {
  border: 1px solid #00c7aa;
  background-color: #00c7aa;
  margin: 0 5px;
}

.t1 {
  text-align: left;
  margin-bottom: 20px;
  color: #333;
}

.cart-table {
  width: 100%;
  table-layout: auto;
  border-radius: 8px;
  border: 1px solid #00c7ae;
  margin-bottom: 20px;
}

.cart-table th {
  font-size: 13px;
}

.cart_item_img {
  width: 70px;
  height: 70px;
}

.predict-price-container,
.userinfo-container {
  display: flex;
  flex-direction: column;
  padding: 10px;
  justify-content: space-between;
}

.predict-price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.userinfo-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
}

.predict-total-price {
  font-size: 20px;
  display: flex;
  justify-content: space-between;
  font-weight: bold;
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

.back-btn {
  width: auto;
  margin: 5px;
  text-align: center;
  margin-top: 10px;
  background-color: #00c7ae;
  color: #fff;
  padding: 10px;
  text-decoration: none;
  border-radius: 5px;
  border: none;
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
  color: red;
}
</style>