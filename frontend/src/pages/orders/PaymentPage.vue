<template>
  <section data-v-a178adf6="" class="pay-button">
        <span> 상품 결제 굿즈 IDX 1, 2번 2개씩 삼</span>
        <button data-v-a178adf6="" type="button" @click="payment"
        class="btn btn-primary btn-block">결제하기</button>
  </section><!---->
</template>

<script setup>
import { useAuthStore } from '@/stores/useAuthStore';
import { useOrdersStore } from '@/stores/useOrdersStore';
import { onMounted, ref } from 'vue';
import { useToast } from 'vue-toastification';
import { IAMPORT_NAME, IAMPORT_PG, IAMPORT_UID } from '@/env';
import { useCartStore } from '@/stores/useCartStore';
import { useRouter } from 'vue-router';
const toast = useToast();
const paymentData = ref({})
const authStore = useAuthStore();
const cartStore = useCartStore();
const ordersStore = useOrdersStore();
const userEmail = ref('');
const userName = ref('');
const router = useRouter();
onMounted(async() => {
  paymentData.value = ordersStore.paymentData
  userEmail.value = authStore.userInfo.email;
  userName.value = authStore.userInfo.name;
  console.log(paymentData.value);
});

const transformGoodsList = (goodsList) => {
  return goodsList.reduce((acc, item) => {
    const [goodsIdx, count] = Object.entries(item)[0];
    acc[goodsIdx] = count;
    return acc;
  }, {});
};


const payment = () => {
  if (typeof window.IMP === 'undefined') {
    console.error("Iamport script not loaded");
    return;
  }
  const IMP = window.IMP;
  IMP.init(IAMPORT_UID);
  IMP.request_pay(
    {
      pg: IAMPORT_PG,
      merchant_uid: "order_no_" + new Date().getMilliseconds(),
      name: IAMPORT_NAME,
      amount: paymentData.value.finalOrderPrice, // 전달받은 총 주문 금액
      buyer_email: userEmail.value,
      buyer_name: userName.value,
      custom_data: transformGoodsList(paymentData.value.goodsList), // 전달받은 데이터 포함
    },
    async (rsp) => {
      if (rsp.success) {
        const res = await ordersStore.verify(rsp.imp_uid, 0);
        if (res.success) {
          await cartStore.deleteAllCartItems();
          toast.success("결제를 처리했습니다.");
          router.push("/mypage/customer/cart")
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


<style scoped>
</style>
