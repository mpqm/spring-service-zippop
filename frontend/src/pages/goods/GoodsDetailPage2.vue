<template>
    <div>
        <AppHeader></AppHeader>
        <div class="lyt-root">
            <div class="wrp-split">
                <div class="ctn-l50">
                    <ImageSlider class="image-slider" :fileUrls="fileUrls"></ImageSlider>
                </div>
                <div class="ctn-r50">
                    <p class="txt-def1">{{ goods.goodsName }}</p>
                    <p class="txt-desc"> {{ goods.goodsContent }}</p>
                    <div class="ctn-tagbutton">
                        <button class="btn-tagdefault">{{ goods.storeName }}</button>
                        <button class="btn-tagdefault"><Icon icon="iconoir:coin" width="20px" height="20px" style="color: #00c7ae" />{{goods.goodsPrice }}원</button>
                        <button class="btn-tagdefault"><Icon icon="iconoir:box-iso" width="20px" height="20px" style="color: #00c7ae" />{{ goods.goodsAmount }}개</button>
                        <button class="btn-tagaction" @click="registerCart"><Icon icon="iconoir:cart" width="20px" height="20px" />장바구니</button>
                        <button class="btn-tagaction" @click="router.back()"><Icon icon="iconoir:nav-arrow-left" width="20px" height="20px" />뒤로가기</button>
                    </div>
                </div>
            </div>
        </div>
        <AppFooter></AppFooter>
    </div>
</template>

<script setup>
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import ImageSlider from "@/components/ImageSlider.vue";
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
            storeIdx: route.params.storeIdx,
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