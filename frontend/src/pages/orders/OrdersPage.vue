<template>
  <div class="lyt-child">
    <section v-if="goodsList.length" class="ctn-checkout">
      <div class="ctn-reviewheading">
        <div>
          <span class="txt-eyebrow">CHECKOUT</span>
          <h2>결제 정보 확인</h2>
          <p>배송지와 주문 상품을 확인한 뒤 결제를 진행해 주세요.</p>
        </div>
      </div>

      <div class="ctn-checkoutgrid">
        <div class="ctn-checkoutmain">
          <div class="ctn-checkoutsection">
            <h3 class="txt-def0">배송지 정보</h3>
            <dl class="lst-checkoutinfo">
              <div><dt>이름</dt><dd>{{ userInfo.name }}</dd></div>
              <div><dt>이메일</dt><dd>{{ userInfo.email }}</dd></div>
              <div><dt>연락처</dt><dd>{{ userInfo.phoneNumber }}</dd></div>
              <div><dt>주소</dt><dd>{{ userInfo.address }}</dd></div>
            </dl>
          </div>

          <div class="ctn-checkoutsection">
            <h3 class="txt-def0">주문 상품</h3>
            <div class="wrp-list">
              <div v-for="item in goodsList" :key="item.cartItemIdx" class="ctn-list1">
                <img v-if="item.getGoodsRes?.getGoodsImageResList?.length" :src="item.getGoodsRes.getGoodsImageResList[0].goodsImageUrl" class="img-list" alt="" />
                <div class="ctn-listinfo1">
                  <p class="txt-def0">{{ item.getGoodsRes?.goodsName }}</p>
                  <p class="txt-def1">{{ formatPrice(item.price) }}원 × {{ item.count }}</p>
                </div>
                <strong>{{ formatPrice(item.price * item.count) }}원</strong>
              </div>
            </div>
          </div>
        </div>

        <aside class="ctn-checkoutsummary">
          <h3 class="txt-def0">결제 금액</h3>
          <div><span>상품 금액</span><strong>{{ formatPrice(paymentData.totalPrice) }}원</strong></div>
          <div><span>포인트 할인</span><strong>-{{ formatPrice(paymentData.totalDiscount) }}원</strong></div>
          <div><span>배송비</span><strong>{{ formatPrice(paymentData.deliveryFee) }}원</strong></div>
          <div class="ctn-checkouttotal"><span>총 결제 금액</span><strong>{{ formatPrice(paymentData.finalOrderPrice) }}원</strong></div>
          <p class="ctn-noticereward">결제 완료 후 주문 상세 페이지에서 처리 상태를 확인할 수 있습니다.</p>
          <button class="btn-default" :disabled="paying" @click="payment">
            <Icon icon="iconoir:hand-card" width="20" />{{ paying ? '결제 처리 중' : '결제하기' }}
          </button>
          <button class="btn-normal" :disabled="paying" @click="router.back()">뒤로가기</button>
        </aside>
      </div>
    </section>

    <div v-else class="txt-null">
      <p>결제할 장바구니 정보가 없습니다.</p>
      <router-link class="btn-default" to="/mypage/customer/cart">장바구니로 이동</router-link>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import { Icon } from '@iconify/vue';
import { IAMPORT_NAME, IAMPORT_PG, IAMPORT_UID } from '@/config';
import { useAccountStore } from '@/stores/accountStore';
import { useCartStore } from '@/stores/cartStore';
import { useOrdersStore } from '@/stores/ordersStore';

const router = useRouter();
const toast = useToast();
const accountStore = useAccountStore();
const cartStore = useCartStore();
const ordersStore = useOrdersStore();
const paymentData = computed(() => ordersStore.paymentData || {});
const goodsList = computed(() => paymentData.value.goodsList || []);
const userInfo = computed(() => accountStore.userInfo || {});
const paying = ref(false);

onMounted(async () => {
  await accountStore.getAccount();
});

const formatPrice = value => Number(value || 0).toLocaleString('ko-KR');
const transformGoodsList = list => Object.assign({}, ...list);

const payment = () => {
  if (!paymentData.value.popupIdx) {
    toast.error('팝업 정보를 찾을 수 없습니다. 장바구니에서 다시 시도해 주세요.');
    return;
  }
  if (!window.IMP) {
    toast.error('결제 모듈을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.');
    return;
  }

  paying.value = true;
  window.IMP.init(IAMPORT_UID);
  window.IMP.request_pay({
    pg: IAMPORT_PG,
    merchant_uid: `zippop_${Date.now()}`,
    name: IAMPORT_NAME,
    amount: paymentData.value.finalOrderPrice,
    buyer_email: userInfo.value.email,
    buyer_name: userInfo.value.name,
    buyer_tel: userInfo.value.phoneNumber,
    custom_data: transformGoodsList(paymentData.value.customData || []),
  }, async rsp => {
    if (!rsp.success) {
      paying.value = false;
      toast.error(rsp.error_msg || '결제를 완료하지 못했습니다.');
      return;
    }

    const res = await ordersStore.createOrder({
      impUid: rsp.imp_uid,
      popupIdx: Number(paymentData.value.popupIdx),
    });
    if (!res.success) {
      paying.value = false;
      toast.error(res.message || '주문 검증에 실패했습니다.');
      return;
    }

    await cartStore.deleteCarts(paymentData.value.cartIdx);
    ordersStore.paymentData = {};
    toast.success('결제가 완료되었습니다.');
    router.replace(`/orders/${res.result.ordersIdx}`);
  });
};
</script>
