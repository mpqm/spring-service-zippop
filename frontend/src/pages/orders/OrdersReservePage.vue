<template>
    <div>
        <div class="lyt-rootpay">
            <div class="ctn-split">
                <h3 class="txt-def0">배송지 정보</h3>
                <button type="button" @click="router.back()" class="btn-small">
                    <Icon icon="iconoir:nav-arrow-left" class="img-iconior"/>뒤로가기
                </button>
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
            <div class="ctn-defaultinfo">
                <div class="ctn-list1" v-for="item in goodsList" :key="item.goodsIdx">
                    <img v-if="item.searchGoodsRes.searchGoodsImageResList && item.searchGoodsRes.searchGoodsImageResList.length > 0" :src="item.searchGoodsRes.searchGoodsImageResList[0].goodsImageUrl" class="img-list" />
                    <div class="ctn-listinfo1">
                        <p class="txt-def1"> {{ item.searchGoodsRes.goodsName }}</p>
                    </div>
                    <div class="ctn-listbuttons">
                        <p class="txt-def0"> {{ item.price }}원</p>
                        <span class="txt-def0">x</span>
                        <button :value="item.count" class="btn-tagaction" type="text" readonly> {{ item.count }} </button>
                    </div>
                </div>
            </div>

            <h3 class="txt-def0">주문 정보</h3>
            <div class="predict-price-container">
                <div class="predict-price-item"><span>총 상품 가격</span> {{ paymentData.totalPrice }}원</div>
                <h3 class="predict-total-price"><span>총 주문 금액</span> {{ paymentData.finalOrderPrice }}원</h3>
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

            <div class="ctn-split">


            </div>
        </div>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/useAuthStore';
import { useOrdersStore } from '@/stores/useOrdersStore';
import { onMounted, ref } from 'vue';
import { useToast } from 'vue-toastification';
import { IAMPORT_NAME, IAMPORT_PG, IAMPORT_UID } from '@/config';
import { useCartStore } from '@/stores/useCartStore';
import { useReserveStore } from "@/stores/useReserveStore";
import { useRoute, useRouter } from 'vue-router';
import { Icon } from '@iconify/vue';

// store, router, route, toast
const toast = useToast();
const authStore = useAuthStore();
const cartStore = useCartStore();
const ordersStore = useOrdersStore();
const reserveStore = useReserveStore();
const router = useRouter();
const route = useRoute();

// 변수(orders)
const paymentData = ref({})
const goodsList = ref([]);
const userInfo = ref({});
// onMounted
onMounted(async () => {
    await mapper();
});

// 매핑 함수
const mapper = async () => {
    paymentData.value = ordersStore.paymentData
    goodsList.value = paymentData.value.goodsList;
    userInfo.value = authStore.userInfo
}

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
    IMP.request_pay(
        {
            pg: IAMPORT_PG,
            merchant_uid: "order_no_" + new Date().getMilliseconds(),
            name: IAMPORT_NAME,
            amount: paymentData.value.finalOrderPrice, // 전달받은 총 주문 금액
            buyer_email: userInfo.value.email,
            buyer_name: userInfo.value.name,
            custom_data: transformGoodsList(paymentData.value.customData), // 전달받은 데이터 포함
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
