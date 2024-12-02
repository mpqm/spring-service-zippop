<template>
    <div>
        <div class="payment-page">

            <hr>
            <h3 class="t1">배송지 정보</h3>
            <div class="userinfo-container">
                <div class="userinfo-item"><span>구매자 이름</span> {{ userInfo.name }}</div>
                <div class="userinfo-item"><span>구매자 이메일</span> {{ userInfo.email }}</div>
                <div class="userinfo-item"><span>구매자 휴대전화번호</span> {{ userInfo.phoneNumber }}</div>
                <div class="userinfo-item"><span>배송지 주소</span> {{ userInfo.address }}</div>
            </div>
            <hr>
            <h3 class="t1">상품 구매 정보</h3>
            <table class="cart-table">
                <tbody>
                    <th>상품이미지</th>
                    <th>상품명</th>
                    <th>상품가격</th>
                    <th>상품수량</th>
                    <tr v-for="item in goodsList" :key="item.goodsIdx">
                        <td>
                            <img v-if="item.searchGoodsRes.searchGoodsImageResList && item.searchGoodsRes.searchGoodsImageResList.length > 0"
                                :src="item.searchGoodsRes.searchGoodsImageResList[0].goodsImageUrl" class="cart_item_img" />
                        </td>
                        <td> {{ item.searchGoodsRes.goodsName }}
                        </td>
                        <td> {{ item.price * item.count }}원 ({{ item.price }}) </td>
                        <td>{{ item.count }}</td>
                    </tr>
                </tbody>
            </table>
            <hr>
            <h3 class="t1">주문 정보</h3>
            <div class="predict-price-container">
                <div class="predict-price-item"><span>총 상품 가격</span> {{ paymentData.totalPrice }}원</div>
                <div class="predict-price-item"><span>총 할인</span> {{ paymentData.totalDiscount }}원</div>
                <div class="predict-price-item"><span>총 배송비</span> {{ paymentData.deliveryFee }}원</div>
                <h3 class="predict-total-price"><span>총 주문 금액</span> {{ paymentData.finalOrderPrice }}원</h3>
            </div>
            <div class="reward-area">
                <span>결제 요청 후 배송 확정 처리가 되면 환불이 불가능합니다.</span><br>
            </div>
            <button type="button" @click="payment" class="pay-btn">결제하기</button>
            <button type="button" @click="cancelPayment" class="pay-btn">뒤로가기</button>
        </div>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/useAuthStore';
import { useOrdersStore } from '@/stores/useOrdersStore';
import { onMounted, ref } from 'vue';
import { useToast } from 'vue-toastification';
import { IAMPORT_NAME, IAMPORT_PG, IAMPORT_UID } from '@/env';
import { useCartStore } from '@/stores/useCartStore';
import { useRoute, useRouter } from 'vue-router';

// store, router, route, toast
const toast = useToast();
const authStore = useAuthStore();
const cartStore = useCartStore();
const ordersStore = useOrdersStore();
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
                const res = await ordersStore.verify(rsp.imp_uid, false);
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

// 결제 취소
const cancelPayment = async () => {
    router.push(`/reserve/${route.params.storeIdx}/${route.params.reserveIdx}/cart`);
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

.pay-btn {
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

.detail-page {
    display: flex;
    padding: 1rem;
    width: 35rem;
    flex-direction: column;
    margin: 10px auto;
    width: 35rem;
    gap: 10px;
}

.detail-container {
    display: flex;
    align-items: center;
    column-gap: 10px;
    background-color: #fff;
    border-radius: 8px;
    border: 1px solid #00c7ae;
    margin: 0;
    width: 35rem;
}

.left-section {
    display: flex;
    margin-left: 10px;
    margin-right: auto;
    gap: 10px;
    align-items: center;
}

.right-section {
    display: flex;
    padding: 1rem;
    margin-left: auto;
    gap: 10px;
    align-items: center;
}

.cancel-btn {
    background-color: #ff4d4f;
    color: #fff;
    border: none;
    font-size: 0.8rem;
    padding: 0.8rem;
    font-weight: bold;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.orders-btn {
    background-color: #00c7ae;
    color: #fff;
    border: none;
    font-size: 0.8rem;
    padding: 0.8rem;
    font-weight: bold;
    border-radius: 8px;
    cursor: pointer;
    transition: background-color 0.3s ease;
}

.orders-btn,
.cancel-btn:hover {
    opacity: 0.8;
}
</style>