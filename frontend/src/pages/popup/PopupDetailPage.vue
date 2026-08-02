<template>
  <div class="lyt-page">
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <div class="wrp-split">
        <div class="ctn-l50">
          <ImageSlider class="image-slider" :fileUrls="fileUrls" />
        </div>
        <div class="ctn-r50">
          <p class="txt-def1">{{ popup.popupName }}</p>
          <p class="txt-desc">{{ popup.popupContent }}</p>
          <div class="ctn-tagbutton">
            <button class="btn-tagdefault">{{ popup.category }}</button>
            <button class="btn-tagdefault">{{ popup.popupAddress }}</button>
            <button class="btn-tagdefault">{{ popup.popupStartDate }}<span class="divider">~</span>{{ popup.popupEndDate }}</button>
            <button class="btn-tagdefault" :class="{ active: isLiked }" @click="toggleLike">
              <Icon icon="iconoir:thumbs-up" width="20px" height="20px" />&nbsp;{{ currentLikeCount }}
            </button>
            <button class="btn-tagdefault"><Icon icon="iconoir:user" width="20px" height="20px"/>&nbsp;{{ popup.totalPeople }}</button>
            <CountDownTimer :targetTime="popup.popupEndDate" :flag="true"></CountDownTimer>
          </div>
        </div>
      </div>
      <div class="wrp-subheader">
        <div class="ctn-subheader">
          <a class="lnk-subheader" :class="{ active: activeMenu === 'goods' }" @click="setActiveMenu('goods')">굿즈 보기</a>
          <a class="lnk-subheader" :class="{ active: activeMenu === 'review' }" @click="setActiveMenu('review')">리뷰 보기</a>
          <a class="lnk-subheader" :class="{ active: activeMenu === 'reserve' }" @click="setActiveMenu('reserve')">예약 확인</a>
        </div>
      </div>

      <div class="lyt-child" v-if="activeMenu === 'goods'">
        <div class="ctn-inputsearch">
          <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="getGoodsList()" />
          <button class="btn-default" @click="getGoodsList()"><Icon icon="ic:search" width="20px" height="20px" /></button>
          <button class="btn-normal" @click="getGoodsList(true)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
        </div>
        <br/>
        <div class="wrp-list" v-if="goodsList && goodsList.length">
          <GoodsList v-for="goods in goodsList" :key="goods.goodsIdx" :goods="goods" :showControl="false" :popupIdx="popup.popupIdx" />
        </div>
        <AppEmptyState v-else title="등록된 굿즈가 없습니다" description="팝업 굿즈가 등록되면 이곳에서 확인할 수 있어요." />
        <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>

      <div v-if="activeMenu === 'review'" class="lyt-child">
        <form class="ctn-rootform ctn-reviewform" @submit.prevent="submitReview">
          <div class="ctn-reviewheading">
            <div>
              <span class="txt-eyebrow">SHARE YOUR EXPERIENCE</span>
              <h2>리뷰 등록</h2>
              <p>팝업에서 경험한 이야기를 다른 방문자에게 알려주세요.</p>
            </div>
            <button type="submit" class="btn-default btn-reviewsubmit">등록</button>
          </div>
          <div class="ctn-inputdefault">
            <label class="ipt-default-label" for="review-title">제목</label>
            <input id="review-title" class="ipt-default" v-model="reviewTitle" type="text" placeholder="후기의 제목을 남겨주세요" />
          </div>
          <div class="ctn-inputdefault">
            <label class="ipt-default-label" for="review-content">내용</label>
            <textarea id="review-content" class="ipt-default ipt-reviewcontent" v-model="reviewContent" rows="5" placeholder="팝업에서 좋았던 점과 방문 팁을 남겨주세요"></textarea>
          </div>
          <div class="ctn-inputdefault ctn-reviewrating">
            <label class="ipt-default-label" for="review-rating">평점</label>
            <input id="review-rating" class="ipt-default" v-model="reviewRating" type="number" min="1" max="5" step="0.5" placeholder="1점에서 5점 사이로 입력해주세요" />
            <span>1점부터 5점까지 입력할 수 있습니다.</span>
          </div>
        </form>
        <br>
        <div class="wrp-list" v-if="reviewList && reviewList.length">
          <ReviewList v-for="review in reviewList" :key="review.reviewIdx" :review="review" />
        </div>
        <AppEmptyState v-else title="등록된 리뷰가 없습니다" description="첫 번째 방문 후기를 남겨보세요." />
        <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>

      <div v-if="activeMenu === 'reserve'" class="lyt-child">
        <div class="wrp-list" v-if="reserveList && reserveList.length">
          <ReserveList v-for="reserve in reserveList" :key="reserve.reserveIdx" :reserve="reserve" :showControl="0" />
        </div>
        <AppEmptyState v-else title="등록된 예약 일정이 없습니다" description="새 예약 일정이 등록되면 이곳에서 확인할 수 있어요." />
        <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
      </div>
    </div>
    <AppFooter></AppFooter>
  </div>
</template>

<script setup>
import ImageSlider from "@/components/ImageSlider.vue";
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import CountDownTimer from "@/components/CountDownTimer.vue";
import GoodsList from "@/components/GoodsList.vue";
import ReviewList from "@/components/ReviewList.vue";
import ReserveList from "@/components/ReserveList.vue";
import AppPagination from "@/components/AppPagination.vue";
import { ref, onMounted, watch } from "vue";
import { usePopupStore } from "@/stores/popupStore";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/goodsStore";
import { useReserveStore } from "@/stores/reserveStore";
import { useAuthStore } from "@/stores/authStore";
import { Icon } from "@iconify/vue";

const goodsStore = useGoodsStore();
const popupStore = usePopupStore();
const reserveStore = useReserveStore();
const authStore = useAuthStore();
const route = useRoute();
const router = useRouter();
const toast = useToast();

const fileUrls = ref([]);
const popup = ref({});
const activeMenu = ref('goods');

const isLiked = ref(false);
const currentLikeCount = ref(0);

const searchQuery = ref("");
const goodsList = ref([]);
const currentPage = ref(0);
const goodsPageSize = ref(8);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);

const reviewList = ref([]);
const reviewPageSize = ref(8);
const reviewContent = ref('');
const reviewTitle = ref('');
const reviewRating = ref(0);

const reserveList = ref([]);

onMounted(async () => {
  await getPopup();
  await getGoodsList();
});

const getPopup = async () => {
  const res = await popupStore.getPopup(route.params.popupIdx);
  if (res.success) {
    popup.value = popupStore.popup;
    if (popupStore.popup.getPopupImageResList && popupStore.popup.getPopupImageResList.length) {
      fileUrls.value = popupStore.popup.getPopupImageResList.map(image => image.popupImageUrl);
    }
    isLiked.value = popupStore.likeList.some(s => s.popupIdx === popupStore.popup.popupIdx);
    currentLikeCount.value = popupStore.popup.likeCount || 0;
  } else {
    router.push("/");
    toast.error(res.message);
  }
};

const setActiveMenu = (menu) => {
  activeMenu.value = menu;
  currentPage.value = 0;
  searchQuery.value = "";
  if (menu === 'goods') {
    getGoodsList();
  } else if (menu === 'review') {
    getPopupReviews();
  } else if (menu === 'reserve') {
    getPopupReserves();
  }
};

const changePage = (newPage) => {
  if (newPage < 0 || newPage >= totalPages.value) return;
  currentPage.value = newPage;
  if (activeMenu.value === 'goods') {
    getGoodsList();
  } else if (activeMenu.value === 'review') {
    getPopupReviews();
  } else if (activeMenu.value === 'reserve') {
    getPopupReserves();
  }
};

const getGoodsList = async (reset = false) => {
  if (reset) {
    currentPage.value = 0;
    searchQuery.value = "";
  }
  const res = await goodsStore.getGoodsList(route.params.popupIdx, searchQuery.value || null, currentPage.value, goodsPageSize.value);
  if (res.success) {
    totalElements.value = goodsStore.totalElements;
    totalPages.value = goodsStore.totalPages;
    goodsList.value = goodsStore.goodsList;
    hideBtns.value = false;
  } else {
    goodsList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
    hideBtns.value = true;
  }
};

const submitReview = async () => {
  const req = {
    reviewTitle: reviewTitle.value,
    reviewContent: reviewContent.value,
    reviewRating: reviewRating.value,
  };
  const res = await popupStore.createPopupReview(route.params.popupIdx, req);
  if (res.success) {
    toast.success(res.message);
    reviewTitle.value = '';
    reviewContent.value = '';
    reviewRating.value = 0;
    await getPopupReviews();
  } else {
    toast.error(res.message);
  }
};

const getPopupReviews = async () => {
  const res = await popupStore.getPopupReviews(route.params.popupIdx, currentPage.value, reviewPageSize.value);
  if (res.success) {
    reviewList.value = popupStore.reviewList;
    totalElements.value = popupStore.totalElements;
    totalPages.value = popupStore.totalPages;
  } else {
    reviewList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
  }
};

const getPopupReserves = async () => {
  const res = await reserveStore.getPopupReserves(route.params.popupIdx, currentPage.value, reviewPageSize.value, null);
  if (res.success) {
    reserveList.value = reserveStore.reserveList;
    totalElements.value = reserveStore.totalElements;
    totalPages.value = reserveStore.totalPages;
  } else {
    reserveList.value = [];
    totalElements.value = 0;
    totalPages.value = 0;
  }
};

watch(() => popupStore.likeList, () => {
  if (popup.value.popupIdx) {
    isLiked.value = popupStore.likeList.some(s => s.popupIdx === popup.value.popupIdx);
  }
}, { deep: true });

watch(() => popup.value, (newPopup) => {
  if (newPopup && newPopup.popupIdx) {
    isLiked.value = popupStore.likeList.some(s => s.popupIdx === newPopup.popupIdx);
    currentLikeCount.value = newPopup.likeCount || 0;
  }
}, { immediate: true, deep: true });

const toggleLike = async () => {
  if (!authStore.isLoggedIn) {
    router.push("/");
    toast.error("로그인이 필요합니다.");
    return;
  }
  if (authStore.userInfo?.role === "ROLE_COMPANY") {
    toast.error("고객 회원만 좋아요를 누를 수 있습니다.");
    return;
  }

  const wasLiked = isLiked.value;
  isLiked.value = !isLiked.value;
  currentLikeCount.value += isLiked.value ? 1 : -1;

  const res = await popupStore.togglePopupLike(popup.value.popupIdx);
  if (!res.success) {
    isLiked.value = wasLiked;
    currentLikeCount.value += wasLiked ? 1 : -1;
    toast.error(res.message);
  }
};
</script>
