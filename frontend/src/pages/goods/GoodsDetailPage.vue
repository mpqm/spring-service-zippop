<template>
    <div>
        <HeaderComponent></HeaderComponent>
        <div class="detail-page">
            <div class="detail-container">
                <div class="left-panel">
                <div v-if="fileUrls.length" class="file-preview">
                    <div v-for="(fileUrl, index) in fileUrls" :key="index" class="file-preview-item">
                        <img :src="fileUrl" alt="file preview" />
                    </div>
                </div>
            </div>
            <div class="right-panel">
                    <div class="title">
                        <h2>{{ storeName }}</h2>
                        <p>{{ goodsName }}</p>
                    </div>
                    <p class="t2">
                        <img class="people-img" src="../../assets/img/coin.png" alt="" />{{ goodsPrice }}원
                        <img class="like-img" src="../../assets/img/stock.png" alt="" />{{ goodsAmount }}개
                    </p>
                    <p> {{ goodsContent }}</p>
                    <div class="btn-container">
                        <button class="normal-btn" @click="goStoreDetail"><img class="search-img" src="../../assets/img/cart-none.png" alt="">&nbsp;<p>카트추가</p></button>
                        <button class="normal-btn" @click="like"><img class="search-img" src="../../assets/img/payment-none.png" alt="">&nbsp;<p>결제하기</p></button>
                    </div>
            </div>
            </div>
        </div>
        <FooterComponent></FooterComponent>
    </div>
</template>

<script setup>
import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import { ref, onMounted } from "vue";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";

const goodsStore = useGoodsStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

const goodsIdx = ref(null);
const storeName = ref("");
const goodsName = ref("");
const goodsPrice = ref("");
const goodsContent = ref("");
const goodsAmount = ref(0);
const fileUrls = ref([]);
const goods = ref({});

onMounted(async () => {
    await search(route.params.goodsIdx);
    await autoSet();
});

const search = async (goodsIdx) => {
    const res = await goodsStore.search(goodsIdx);
    if (res.success) {
        goods.value = goodsStore.goods;
        await autoSet();
    } else {
        router.push("/")
        toast.error(res.message);
    }
}

const autoSet = async () => {
    storeName.value = goodsStore.goods.storeName;
    goodsIdx.value = goodsStore.goods.goodsIdx;
    goodsName.value = goodsStore.goods.goodsName;
    goodsContent.value = goodsStore.goods.goodsContent;
    storeName.value = goodsStore.goods.storeName;
    goodsPrice.value = goodsStore.goods.goodsPrice;
    goodsAmount.value = goodsStore.goods.goodsAmount;
    if (goodsStore.goods.searchGoodsImageResList && goodsStore.goods.searchGoodsImageResList.length) {
        fileUrls.value = goodsStore.goods.searchGoodsImageResList.map(image => image.goodsImageUrl);
    }
}
</script>

<style scoped>
.detail-page {
    display: flex;
    margin: 0 auto;
    padding: 1rem;
    width: 65rem;
    gap: 2rem;
}
.detail-container {
    display: flex;
    flex-direction: row;
    margin: 10px auto;
    border-radius: 8px;
    border: 1px solid #00c7ae;
    width: 65rem;
    padding: 5px;
}

.left-panel, .right-panel {
    width: 50%;
}
.right-panel {
    width: 50%;
    padding: 1rem;
    border: 1px solid #00c7ae;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}
.file-preview {
    display: flex;
    overflow-x: auto;
    gap: 10px;
    padding: 1rem 0;
}
.file-preview-item img {
    width: 100%;
    max-height: 300px;
    object-fit: cover;
    border-radius: 8px;
}

.title {
    display: flex;
    align-items: center;
    gap: 10px;
}
.t2 {
    display: flex;
    align-items: center;
    gap: 10px;
    margin: 1rem 0;
}
.btn-container{
  display: flex;
  width: auto;
  gap: 10px;
}
.normal-btn {
  display: flex;
    text-align: center;
    width: 100%;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.5rem;
    border-radius: 0.25rem;
    text-decoration: #000;
    align-items: center;
    justify-content: center;
}
</style>
