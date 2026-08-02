<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-rootpay">
      <div class="ctn-split">
        <h3 class="txt-def0">배송지 정보</h3>
        <button type="button" @click="router.go(-1)" class="btn-small">
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
          <div class="ctn-list1" v-for="item in orders.getOrdersDetailResList" :key="item.ordersDetailIdx">
            <img v-if="item.getGoodsRes.getGoodsImageResList && item.getGoodsRes.getGoodsImageResList.length > 0" :src="item.getGoodsRes.getGoodsImageResList[0].goodsImageUrl" class="img-list" />
            <div class="ctn-listinfo1">
              <p class="txt-def0">{{ item.getGoodsRes.goodsName }}</p>
            </div>
            <div class="ctn-listbuttons">
              <span class="txt-def0">{{ item.eachPrice }}원 ({{ item.getGoodsRes.goodsPrice }})</span>
              <button class="btn-tagaction" type="text" readonly>{{ item.eachPrice / item.getGoodsRes.goodsPrice }}</button>
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
          <span class="txt-def1">총 배송비</span> {{ orders.deliveryCost }}원
        </div>
        <div class="ctn-split">
          <span class="txt-def1">주문 상태</span> {{ formatOrderStatus(orders.orderStatus) }}
        </div>
        <div class="ctn-split">
          <span class="txt-def1">총 주문 금액</span> {{ orders.totalPrice }}원
        </div>
      </div>
      <div class="ctn-noticereward">
        <span>결제 요청 후 배송 확정 처리가 되면 환불이 불가능합니다.</span>
      </div>
    </div>
    <AppFooter></AppFooter>
  </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue';
import AppFooter from "@/components/AppFooter.vue";
import { Icon } from "@iconify/vue";
import { useOrdersStore } from '@/stores/ordersStore';
import { onMounted, ref } from 'vue';
import { useToast } from 'vue-toastification';
import { useRoute, useRouter } from 'vue-router';

const ordersStore = useOrdersStore();
const toast = useToast();
const router = useRouter();
const route = useRoute();

const orders = ref({});

onMounted(async () => {
  await getOrder();
});

const getOrder = async () => {
  if (route.query.popupIdx) {
    const res = await ordersStore.getPopupOrdersDetail(route.query.popupIdx, route.params.ordersIdx);
    if (res.success) {
      orders.value = ordersStore.orders;
    } else {
      router.push("/mypage/company/orders");
      toast.error(res.message);
    }
  } else {
    const res = await ordersStore.getOrder(route.params.ordersIdx);
    if (res.success) {
      orders.value = ordersStore.orders;
    } else {
      router.push("/mypage/customer/orders");
      toast.error(res.message);
    }
  }
};

const formatOrderStatus = (statusString) => {
  if (statusString === "STOCK_READY") return "재고 굿즈 결제 완료";
  else if (statusString === "STOCK_CANCEL") return "재고 굿즈 결제 취소";
  else if (statusString === "STOCK_COMPLETE") return "재고 굿즈 주문 확정";
  else if (statusString === "STOCK_DELIVERY") return "재고 굿즈 배달 중";
  else if (statusString === "RESERVE_READY") return "예약 굿즈 결제 완료";
  else if (statusString === "RESERVE_CANCEL") return "예약 굿즈 결제 취소";
  else if (statusString === "RESERVE_COMPLETE") return "예약 굿즈 주문 확정";
  else if (statusString === "RESERVE_DELIVERY") return "예약 굿즈 배달 중";
  return statusString;
};
</script>
