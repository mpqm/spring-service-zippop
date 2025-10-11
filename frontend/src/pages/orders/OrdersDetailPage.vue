<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-rootpay">
      <div class="ctn-split">
        <h3 class="txt-def0">배송지 정보</h3>
        <button type="button" @click="goBack()" class="btn-small">
          <Icon icon="iconoir:nav-arrow-left" class="img-iconior"/>뒤로가기
        </button>
      </div>
      <div class="ctn-defaultinfo">
        <div class="ctn-split">
            <span class="txt-def2">구매자 이름</span> 
            <span class="txt-def2">{{ orders.name }}</span>
        </div>
        <div class="ctn-split">
            <span class="txt-def2">구매자 이메일</span> 
            <span class="txt-def2">{{ orders.email }}</span>
        </div>
        <div class="ctn-split">
            <span class="txt-def2">구매자 휴대전화번호</span> 
            <span class="txt-def2">{{ orders.phoneNumber }}</span>
        </div>
        <div class="ctn-split">
            <span class="txt-def2">배송지 주소</span> 
            <span class="txt-def2">{{ orders.address }}</span>
        </div>
      </div>

      <h3 class="txt-def0">상품 구매 정보</h3>
      <div class="ctn-defaultinfo">
        <div class="wrp-list">
          <div class="ctn-list1" v-for="item in orders.searchOrdersDetailResList" :key="item.ordersDetailIdx">
            <img v-if="item.searchGoodsRes.searchGoodsImageResList && item.searchGoodsRes.searchGoodsImageResList.length > 0" :src="item.searchGoodsRes.searchGoodsImageResList[0].goodsImageUrl" class="img-list" />
            <div class="ctn-listinfo1">
                  <p class="txt-def0"> {{ item.searchGoodsRes.goodsName }}</p>
              </div>
              <div class="ctn-listbuttons">
                  <span class="txt-def0">{{ item.eachPrice }}원 ({{ item.searchGoodsRes.goodsPrice }})</span>
                  <button class="btn-tagaction" type="text" readonly> {{ item.eachPrice / item.searchGoodsRes.goodsPrice }} </button>
              </div>
          </div>
        </div>
      </div>

      <h3 class="txt-def0">주문 정보</h3>
      <div class="ctn-defaultinfo">
        <div class="ctn-split">
          <span class="txt-def1">총 상품 가격</span> {{ orders.totalPrice - orders.deliveryCost + orders.usedPoint }}원
        </div>
        <div class="ctn-split">
          <span class="txt-def1">사용 포인트</span> {{ orders.usedPoint }} 원
        </div>
        <div class="ctn-split">
          <span class="txt-def1"> 총 배송비</span> {{ orders.deliveryCost }}원
        </div>
        <div class="ctn-split">
          <span class="txt-def1">주문 상태</span> {{ formatOrdersStatus(orders.orderStatus) }}
        </div>
        <div class="ctn-split">
          <span class="txt-def1">총 주문 금액</span> {{ orders.totalPrice }}원
        </div>
      </div>
      <div class="ctn-noticereward">
        <span>결제 요청 후 배송 확정 처리가 되면 환불이 불가능합니다.</span><br>
      </div>
    </div>
    <AppFooter></AppFooter>
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue';
import AppFooter from "@/components/AppFooter.vue";
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
