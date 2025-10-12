import { createRouter, createWebHistory } from "vue-router";

import LoginPage from "@/pages/auth/LoginPage.vue";
import SignupPage from "@/pages/auth/SignupPage.vue";
import StoreMainPage from "@/pages/store/StoreMainPage.vue";
import StoreRegisterPage from "@/pages/mypage/company/StoreRegisterPage.vue";
import CompanyMyPage from "@/pages/mypage/company/CompanyMyPage.vue";
import StoreManagePage from "@/pages/mypage/company/StoreManagePage.vue";
import StoreUpdatePage from "@/pages/mypage/company/StoreUpdatePage.vue";
import StoreDetailPage from "@/pages/store/StoreDetailPage.vue";
import GoodsManagePage1 from "@/pages/mypage/company/GoodsManagePage1.vue";
import GoodsManagePage2 from "@/pages/mypage/company/GoodsManagePage2.vue";
import GoodsRegisterPage from "@/pages/mypage/company/GoodsRegisterPage.vue";
import GoodsUpdatePage from "@/pages/mypage/company/GoodsUpdatePage.vue";
import GoodsMainPage from "@/pages/goods/GoodsMainPage.vue";
import GoodsDetailPage1 from "@/pages/goods/GoodsDetailPage1.vue";
import GoodsDetailPage2 from "@/pages/goods/GoodsDetailPage2.vue";
import ErrorPage from "@/pages/error/ErrorPage.vue";
import EditProfilePage from "@/pages/mypage/common/EditProfilePage.vue";
import CustomerMyPage from "@/pages/mypage/customer/CustomerMyPage.vue";
import ReviewSearchPage from "@/pages/mypage/customer/ReviewSearchPage.vue";
import LikeManagePage from "@/pages/mypage/customer/LikeManagePage.vue";
import OrdersManagePage from "@/pages/mypage/customer/OrdersManagePage.vue";
import OrdersDetailPage from "@/pages/orders/OrdersDetailPage.vue";
import OrdersManagePage1 from "@/pages/mypage/company/OrdersManagePage1.vue";
import OrdersManagePage2 from "@/pages/mypage/company/OrdersManagePage2.vue";
import CartManagePage2 from "@/pages/mypage/customer/CartManagePage2.vue";
import CartManagePage1 from "@/pages/mypage/customer/CartManagePage1.vue";
import ReserveManagePage1 from "@/pages/mypage/company/ReserveManagePage1.vue";
import ReserveManagePage2 from "@/pages/mypage/company/ReserveManagePage2.vue";
import ReserveRegisterPage from "@/pages/mypage/company/ReserveRegisterPage.vue";
import ReserveMainPage from "@/pages/reserve/ReserveMainPage.vue";
import ReserveGoodsPage from "@/pages/reserve/ReserveSystemPage.vue";
import PayoutManagePage1 from "@/pages/mypage/company/PayoutManagePage1.vue";
import PayoutManagePage2 from "@/pages/mypage/company/PayoutManagePage2.vue";
import SupportPage from "@/pages/auth/SupportPage.vue";
import { useCartStore } from "@/stores/useCartStore";
import { useReserveStore } from "@/stores/useReserveStore";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 인증
    { path: "/login", component: LoginPage }, // 로그인 
    { path: "/signup", component: SignupPage }, // 회원가입
    { path: "/support", component: SupportPage }, // 계정 활성화

    // 팝업 스토어
    { path: "/", component: StoreMainPage }, // 팝업 스토어 메인 페이지
    { path: '/store/:storeIdx', component: StoreDetailPage }, // 팝업 스토어 상세 페이지

    // 굿즈 마켓
    { path: "/goods", component: GoodsMainPage }, // 굿즈 마켓 메인 페이지(스토어 목록)
    { path: '/goods/:storeIdx', component: GoodsDetailPage1 }, // 굿즈 마켓 상세 페이지
    { path: '/goods/:storeIdx/:goodsIdx', component: GoodsDetailPage2 }, // 굿즈 마켓 상세 페이지2(상점 인덱스 입력)

    // 주문 및 결제
    { path: "/orders/:ordersIdx", component: OrdersDetailPage }, // 주문 내역 상세 페이지

    // 팝업 예약
    { path: "/reserve", component: ReserveMainPage }, // 팝업 예약 메인 페이지
    { path: "/reserve/:storeIdx/:reserveIdx", component: ReserveGoodsPage }, // 팝업 스토어 예약 및 결제 페이지 (대기열 포함)

    // 마이페이지 기업
    {
      path: '/mypage/company',
      component: CompanyMyPage,
      children: [
        { path: 'store', component: StoreManagePage }, // 스토어 관리 메인 페이지
        { path: 'store/register', component: StoreRegisterPage }, // 스토어 관리 등록 페이지
        { path: 'store/update/:storeIdx', component: StoreUpdatePage }, // 스토어 관리 수정 페이지
        { path: 'store/:storeIdx', component: StoreDetailPage }, // 스토어 관리 상세 페이지

        { path: 'goods', component: GoodsManagePage1 }, // 굿즈 관리 메인 페이지(스토어 목록)
        { path: 'goods/:storeIdx', component: GoodsManagePage2 }, // 굿즈 관리 상세 페이지
        { path: 'goods/:storeIdx/register', component: GoodsRegisterPage }, // 굿즈 관리 등록 페이지
        { path: 'goods/:storeIdx/update/:goodsIdx', component: GoodsUpdatePage }, // 굿즈 관리 수정 페이지

        { path: 'orders', component: OrdersManagePage1, }, // 주문 관리 메인 페이지(스토어 목록)
        { path: 'orders/:storeIdx', component: OrdersManagePage2, }, // 주문 관리 상세 페이지

        { path: 'reserve', component: ReserveManagePage1, }, // 예약 관리 메인 페이지(스토어 목록)
        { path: 'reserve/:storeIdx', component: ReserveManagePage2, }, // 예약 관리 메인 페이지
        { path: 'reserve/register/:storeIdx', component: ReserveRegisterPage, }, // 예약 등록 페이지
        { path: 'payout', component: PayoutManagePage1, }, // 정산 관리 메인 페이지
        { path: 'payout/:storeIdx', component: PayoutManagePage2, }, // 정산 관리 상세 페이지
        // { path: 'payout/:storeIdx', component: SettlementDetailPage, }, // 정산 관리 상세 페이지
        { path: 'account-edit', component: EditProfilePage }, // 고객 정보 수정 페이지
      ]
    },

    // 마이페이지 고객
    {
      path: '/mypage/customer',
      component: CustomerMyPage,
      children: [
        { path: 'review', component: ReviewSearchPage }, // 리뷰 검색 메인 페이지
        { path: 'account-edit', component: EditProfilePage }, // 고객 정보 수정 페이지
        { path: 'like', component: LikeManagePage }, // 좋아요 관리 메인 페이지
        { path: 'cart', component: CartManagePage1, }, // 장바구니 관리 메인 페이지
        { path: 'cart/:storeIdx', component: CartManagePage2, }, // 장바구니 관리 메인 페이지
        { path: 'orders', component: OrdersManagePage, }, // 주문 관리 메인 페이지
      ]
    },
    
    
    // 에러
    { path: '/:catchAll(.*)', redirect: '/error', },
    { path: '/error', component: ErrorPage },
  ],
});

router.beforeEach(async (to, from, next) => {
  const cartStore = useCartStore();
  const reserveStore = useReserveStore();
  const storeIdx = from.params.storeIdx;
  const reserveIdx = from.params.reserveIdx;
  const access = reserveStore.access;

  const isGoodsPage = (path) => path && path.includes('/reserve/') && path.includes('/goods');
  const isOrdersPage = (path) => path && path.includes('/reserve/') && path.includes('/orders');
  const isQueuePage = (path) => path && path.includes('/reserve/') && path.split('/').length === 4;

  if(access){
    // `/goods`에서 대기열로 이동 시 예약 취소하고 스토어 페이지로 리디렉션
    if (isGoodsPage(from.path) && isQueuePage(to.path)) {
      cartStore.deleteCart(storeIdx);
      reserveStore.cancel(reserveIdx);
      next(`/store/${storeIdx}`);
      return;
    }

    // goods <-> orders 같은 예약 내에서 양방향 이동 허용
    const fromIsReserve = isGoodsPage(from.path) || isOrdersPage(from.path);
    const toIsReserve = isGoodsPage(to.path) || isOrdersPage(to.path);
    
    if (fromIsReserve && toIsReserve) {
      // 같은 예약 내에서의 이동이면 그냥 허용
      if (String(from.params.storeIdx) === String(to.params.storeIdx) &&
          String(from.params.reserveIdx) === String(to.params.reserveIdx)) {
        next();
        return;
      }
    }

    // `/goods`, `/orders`에서 완전히 벗어나려 할 때만 알림창 표시
    if (fromIsReserve && !toIsReserve) {
      const confirmLeave = confirm("페이지를 떠나시겠습니까? 예약이 취소됩니다.");
      if (confirmLeave) {
        cartStore.deleteCart(storeIdx);
        reserveStore.cancel(reserveIdx);
        next(); // 이동 허용
      } else {
        next(false); // 이동 차단
      }
      return;
    }

    next(); // 다른 모든 경우 이동 허용
  } else {
    next()
  }
});

export default router;
