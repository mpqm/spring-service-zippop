<template>
    <div>
        <HeaderComponent></HeaderComponent>
        <div class="detail-page">
            <div class="detail-container">
                <div class="left-panel">
                    <ImageSlider class="image-slider" :fileUrls="fileUrls"></ImageSlider>
                </div>
                <div class="right-panel">
                    <div class="title">
                        <h2>{{ goods.storeName }}</h2>
                        <p>{{ goods.goodsName }}</p>
                    </div>
                    <p class="t2">
                        <img class="people-img" src="../../assets/img/coin.png" alt="" />{{goods.goodsPrice }}원
                        <img class="like-img" src="../../assets/img/stock.png" alt="" />{{ goods.goodsAmount }}개
                    </p>
                    <p> {{ goods.goodsContent }}</p>
                    <div class="btn-container">
                        <button class="normal-btn" @click="registerCart"><img class="search-img" src="../../assets/img/cart-none.png">
                            &nbsp;<p>카트추가</p>
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <FooterComponent></FooterComponent>
    </div>
</template>

<script setup>
import HeaderComponent from "@/components/common/HeaderComponent.vue";
import FooterComponent from "@/components/common/FooterComponent.vue";
import ImageSlider from "@/components/common/ImageSlider.vue";
import { ref, onMounted } from "vue";
import { useGoodsStore } from "@/stores/useGoodsStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useAuthStore } from "@/stores/useAuthStore";
import { useCartStore } from "@/stores/useCartStore";

// store, router, route, toast
const goodsStore = useGoodsStore();
const authStore = useAuthStore();
const cartStore = useCartStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

// 변수
const goods = ref({});
const fileUrls = ref([]);

// onMounted 
onMounted(async () => {
    await search(route.params.goodsIdx);
});

// 굿즈 조회 
const search = async (goodsIdx) => {
    const res = await goodsStore.search(goodsIdx);
    if (res.success) {
        goods.value = goodsStore.goods;
        // 이미지 매핑
        mapper();
    } else {
        router.push("/")
        toast.error(res.message);
    }
}

// 매퍼(이미지)
const mapper = () => {
    if (goodsStore.goods.searchGoodsImageResList && goodsStore.goods.searchGoodsImageResList.length) {
        fileUrls.value = goodsStore.goods.searchGoodsImageResList.map(image => image.goodsImageUrl);
    }
}

// 카트 추가 
const registerCart = async () => {
    if (authStore.isLoggedIn) {
        const req = {
            goodsIdx: route.params.goodsIdx,
        }
        const res = await cartStore.register(req);
        if (res.success) {
            toast.success(res.message);
        } else {
            toast.error(res.message);
        }
    } else {
        toast.error("로그인이 필요합니다.");
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

.image-slider {
    width: 100%;
    height: 100%;
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

.left-panel,
.right-panel {
    width: 50%;
}

.right-panel {
    width: 50%;
    padding: 1rem;
    display: flex;
    flex-direction: column;
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

.btn-container {
    display: flex;
    flex-direction: column;
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
