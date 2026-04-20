import { createRouter, createWebHistory } from "vue-router";

import LoginPage from "@/pages/auth/LoginPage.vue";
import SignupPage from "@/pages/auth/SignupPage.vue";
import PopupMainPage from "@/pages/popup/PopupMainPage.vue";
import PopupDetailPage from "@/pages/popup/PopupDetailPage.vue";
import CompanyMyPage from "@/pages/mypage/company/CompanyMyPage.vue";
import PopupManagePage from "@/pages/mypage/company/PopupManagePage.vue";
import PopupRegisterPage from "@/pages/mypage/company/PopupRegisterPage.vue";
import PopupUpdatePage from "@/pages/mypage/company/PopupUpdatePage.vue";
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
import LikeManagePage from "@/pages/mypage/customer/LikeManagePage.vue";
import ReviewManagePage from "@/pages/mypage/customer/ReviewManagePage.vue";
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
import ReserveSystemPage from "@/pages/reserve/ReserveSystemPage.vue";
import PayoutManagePage1 from "@/pages/mypage/company/PayoutManagePage1.vue";
import PayoutManagePage2 from "@/pages/mypage/company/PayoutManagePage2.vue";
import SupportPage from "@/pages/auth/SupportPage.vue";
import { useReserveStore } from "@/stores/reserveStore";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/login", component: LoginPage },
    { path: "/signup", component: SignupPage },
    { path: "/support", component: SupportPage },
    { path: "/", component: PopupMainPage },
    { path: '/popup/:popupIdx', component: PopupDetailPage },
    { path: "/goods", component: GoodsMainPage },
    { path: '/goods/:popupIdx', component: GoodsDetailPage1 },
    { path: '/goods/:popupIdx/:goodsIdx', component: GoodsDetailPage2 },
    { path: "/orders/:ordersIdx", component: OrdersDetailPage },
    { path: "/reserve", component: ReserveMainPage },
    { path: "/reserve/:popupIdx/:reserveIdx", component: ReserveSystemPage },

    {
      path: '/mypage/company',
      component: CompanyMyPage,
      children: [
        { path: 'popup', component: PopupManagePage },
        { path: 'popup/register', component: PopupRegisterPage },
        { path: 'popup/update/:popupIdx', component: PopupUpdatePage },
        { path: 'popup/:popupIdx', component: PopupDetailPage },
        { path: 'goods', component: GoodsManagePage1 },
        { path: 'goods/:popupIdx', component: GoodsManagePage2 },
        { path: 'goods/:popupIdx/register', component: GoodsRegisterPage },
        { path: 'goods/:popupIdx/update/:goodsIdx', component: GoodsUpdatePage },
        { path: 'orders', component: OrdersManagePage1 },
        { path: 'orders/:popupIdx', component: OrdersManagePage2 },
        { path: 'reserve', component: ReserveManagePage1 },
        { path: 'reserve/:popupIdx', component: ReserveManagePage2 },
        { path: 'reserve/register/:popupIdx', component: ReserveRegisterPage },
        { path: 'payout', component: PayoutManagePage1 },
        { path: 'payout/:popupIdx', component: PayoutManagePage2 },
        { path: 'account', component: EditProfilePage },
      ]
    },

    {
      path: '/mypage/customer',
      component: CustomerMyPage,
      children: [
        { path: 'review', component: ReviewManagePage },
        { path: 'account', component: EditProfilePage },
        { path: 'like', component: LikeManagePage },
        { path: 'cart', component: CartManagePage1 },
        { path: 'cart/:cartIdx', component: CartManagePage2 },
        { path: 'orders', component: OrdersManagePage },
      ]
    },

    { path: '/:catchAll(.*)', redirect: '/error' },
    { path: '/error', component: ErrorPage },
  ],
});

router.beforeEach(async (to, from, next) => {
  const reserveStore = useReserveStore();
  const popupIdx = from.params.popupIdx;
  const reserveIdx = from.params.reserveIdx;
  const access = reserveStore.access;

  const isGoodsPage = (path) => path && path.includes('/reserve/') && path.includes('/goods');
  const isOrdersPage = (path) => path && path.includes('/reserve/') && path.includes('/orders');
  const isQueuePage = (path) => path && path.includes('/reserve/') && path.split('/').length === 4;

  if (access) {
    if (isGoodsPage(from.path) && isQueuePage(to.path)) {
      reserveStore.cancelReserve(reserveIdx);
      next(`/popup/${popupIdx}`);
      return;
    }

    const fromIsReserve = isGoodsPage(from.path) || isOrdersPage(from.path);
    const toIsReserve = isGoodsPage(to.path) || isOrdersPage(to.path);

    if (fromIsReserve && toIsReserve) {
      if (String(from.params.popupIdx) === String(to.params.popupIdx) &&
          String(from.params.reserveIdx) === String(to.params.reserveIdx)) {
        next();
        return;
      }
    }

    if (fromIsReserve && !toIsReserve) {
      const confirmLeave = confirm("페이지를 떠나시겠습니까? 예약이 취소됩니다.");
      if (confirmLeave) {
        reserveStore.cancelReserve(reserveIdx);
        next();
      } else {
        next(false);
      }
      return;
    }

    next();
  } else {
    next();
  }
});

export default router;
