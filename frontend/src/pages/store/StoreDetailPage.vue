<template>
    <div>
        <HeaderComponent></HeaderComponent>
        <div class="detail-page">
            <div class="detail-container">
                <div class="title">
                <h2>{{ storeName }}</h2>
                <p>{{ category }}</p>
            </div>
            <p class="t2">
                <img class="like-img" src="../../assets/img/like-fill.png" alt="" />&nbsp;{{ likeCount }}
                <img class="people-img" src="../../assets/img/people.png" alt="" />&nbsp;{{ totalPeople }}
            </p>
            <p> {{ storeStartDate }} ~ {{ storeEndDate }}</p>
            <div v-if="fileUrls.length" class="file-preview">
                <div v-for="(fileUrl, index) in fileUrls" :key="index" class="file-preview-item">
                    <img :src="fileUrl" alt="file preview" />
                </div>
            </div>
            <p> {{ storeContent }}</p>
            <CountDownTimer>
                
            </CountDownTimer>
            진행중인 예약
            <div class="reserve-container">
                
            </div>
            리뷰 보기
            <div class="review-container">

            </div>
            굿즈 보기
            <div class="goods-container">

            </div>
        </div>
        </div>
        <FooterComponent></FooterComponent>
    </div>
</template>


<script setup>
import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import CountDownTimer from "@/components/CountDownTimer.vue";
import { ref, onMounted } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";

const storeStore = useStoreStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

const storeIdx = ref(null);
const storeName = ref("");
const storeContent = ref("");
const category = ref("");
const totalPeople = ref(0);
const likeCount = ref(0);
const address = ref("");
const addressDetail = ref("");
const storeStartDate = ref("");
const storeEndDate = ref("");
const fileUrls = ref([]);
const store = ref({});
onMounted(async () => {
    await search(route.params.storeIdx);
    await autoSet();
});

const search = async (storeIdx) => {
    const res = await storeStore.search(storeIdx);
    if (res.success) {
        store.value = storeStore.store;
        await autoSet();
    } else {
        router.push("/")
        toast.error("해당 팝업스토어를 찾을 수 없습니다.")
    }
}

const autoSet = async () => {
    storeIdx.value = storeStore.store.storeIdx;
    storeName.value = storeStore.store.storeName;
    storeContent.value = storeStore.store.storeContent;
    category.value = storeStore.store.category;
    totalPeople.value = storeStore.store.totalPeople;
    likeCount.value = storeStore.store.likeCount;
    address.value = storeStore.store.storeAddress.split(',')[0];
    addressDetail.value = storeStore.store.storeAddress.split(',')[1] || '';
    storeStartDate.value = storeStore.store.storeStartDate;
    storeEndDate.value = storeStore.store.storeEndDate;
    if (storeStore.store.searchStoreImageResList && storeStore.store.searchStoreImageResList.length) {
        fileUrls.value = storeStore.store.searchStoreImageResList.map(image => image.storeImageUrl);
    }
}

</script>

<style scoped>
.detail-page {
    margin: 0 auto;
    display: flex;
    flex-direction: column;
    width: 65rem;
    padding: 1rem;
}
.detail-container {
    margin: 10px auto;
    border-radius: 8px;
    border: 1px solid #00c7ae;
    width: 65rem;
    padding: 5px;
}
.title {
    display: flex;
    align-items: center;
    gap: 10px;
}
.file-preview {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 1rem;
}

.file-preview-item img {
    width: 100px;
    height: 100px;
    object-fit: cover;
}
.store-img {
  width: 100%;
  height: fit-content;
  border-radius: 8px;
  margin-top: 4px;
}

.like-img {
  object-fit: cover;
  width: auto;
  height: 24px;
  margin-right: 5px;
  vertical-align: middle;
}

.people-img {
  object-fit: cover;
  width: auto;
  height: 30px;
  vertical-align: middle;
}
</style>