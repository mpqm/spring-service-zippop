import { createRouter, createWebHistory } from "vue-router";

import MainChatComponent from "@/components/chat/MainChatComponent.vue";
import LoginPage from "@/pages/auth/LoginPage.vue";
import CustomerSignupPage from "@/pages/auth/CustomerSignupPage.vue";
import CompanySignupPage from "@/pages/auth/CompanySignupPage.vue";
import StoreMainPage from "@/pages/store/StoreMainPage.vue";
import StoreRegisterPage from "@/pages/mypage/company/StoreRegisterPage.vue";
import CompanyMyPage from "@/pages/mypage/company/CompanyMyPage.vue";
import StoreManagePage from "@/pages/mypage/company/StoreManagePage.vue";
import StoreUpdatePage from "@/pages/mypage/company/StoreUpdatePage.vue";
import StoreDetailPage from "@/pages/store/StoreDetailPage.vue";
import GoodsManage1Page from "@/pages/mypage/company/GoodsManage1Page.vue";
import GoodsManage2Page from "@/pages/mypage/company/GoodsManage2Page.vue";
import GoodsRegisterPage from "@/pages/mypage/company/GoodsRegisterPage.vue";
import GoodsUpdatePage from "@/pages/mypage/company/GoodsUpdatePage.vue";
import GoodsMainPage from "@/pages/goods/GoodsMainPage.vue";
import GoodsDetailPage from "@/pages/goods/GoodsDetailPage.vue";
import ErrorPage from "@/pages/error/ErrorPage.vue";
import EditProfilePage from "@/pages/mypage/common/EditProfilePage.vue";
import FindIdPwPage from "@/pages/auth/FindIdPwPage.vue";
import CustomerMyPage from "@/pages/mypage/customer/CustomerMyPage.vue";
import ReviewSearchPage from "@/pages/mypage/customer/ReviewSearchPage.vue";
import LikeManagePage from "@/pages/mypage/customer/LikeManagePage.vue";
import CartPage from "@/pages/mypage/customer/CartPage.vue";
import GoodsDetailPage2 from "@/pages/goods/GoodsDetail2Page.vue";
import CustomerOrdersManagePage from "@/pages/mypage/customer/CustomerOrdersManagePage.vue";
import OrdersPage from "@/pages/orders/OrdersPage.vue";
import OrdersDetailPage from "@/pages/orders/OrdersDetailPage.vue";
import CompanyOrdersManagePage1 from "@/pages/mypage/company/CompanyOrdersManagePage1.vue";
import CompanyOrdersManagePage2 from "@/pages/mypage/company/CompanyOrdersManagePage2.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 인증
    { path: "/login", component: LoginPage }, // 로그인 
    { path: '/find-idpw', component: FindIdPwPage }, // 아이디/비밀번호 찾기
    { path: "/signup/customer", component: CustomerSignupPage }, // 고객 회원가입
    { path: "/signup/company", component: CompanySignupPage }, // 기업 회원가입

    // 팝업 스토어
    { path: "/", component: StoreMainPage }, // 팝업 스토어 메인 페이지
    { path: '/store/:storeIdx', component: StoreDetailPage }, // 팝업 스토어 상세 페이지

    // 굿즈 마켓
    { path: "/goods", component: GoodsMainPage }, // 굿즈 마켓 메인 페이지(스토어 목록)
    { path: '/goods/:storeIdx', component: GoodsDetailPage }, // 굿즈 마켓 상세 페이지
    { path: '/goods-detail/:goodsIdx', component: GoodsDetailPage2 }, // 굿즈 마켓 상세 페이지2(상점 인덱스 입력)

    // 주문 및 결제
    { path: "/orders", component: OrdersPage }, // 주문 및 결제 메인 페이지
    { path: "/orders/:ordersIdx", component: OrdersDetailPage }, // 주문 내역 상세 페이지

    // 마이페이지 기업
    {
      path: '/mypage/company',
      component: CompanyMyPage,
      children: [
        { path: 'store', component: StoreManagePage }, // 스토어 관리 메인 페이지
        { path: 'store/register', component: StoreRegisterPage }, // 스토어 관리 등록 페이지
        { path: 'store/update/:storeIdx', component: StoreUpdatePage }, // 스토어 관리 수정 페이지
        { path: 'store/:storeIdx', component: StoreDetailPage }, // 스토어 관리 상세 페이지

        { path: 'goods', component: GoodsManage1Page }, // 굿즈 관리 메인 페이지(스토어 목록)
        { path: 'goods/:storeIdx', component: GoodsManage2Page }, // 굿즈 관리 상세 페이지
        { path: 'goods/:storeIdx/register', component: GoodsRegisterPage }, // 굿즈 관리 등록 페이지
        { path: 'goods/:storeIdx/update/:goodsIdx', component: GoodsUpdatePage }, // 굿즈 관리 수정 페이지


        { path: 'orders', component: CompanyOrdersManagePage1, }, // 주문 관리 메인 페이지(스토어 목록)
        { path: 'orders/:storeIdx', component: CompanyOrdersManagePage2, }, // 주문 관리 상세 페이지
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
        { path: 'cart', component: CartPage, }, // 장바구니 관리 메인 페이지
        { path: 'orders', component: CustomerOrdersManagePage, }, // 주문 관리 메인 페이지
      ]
    },

    // 채팅
    { path: '/chats', component: MainChatComponent },

    // 에러
    { path: '/:catchAll(.*)', redirect: '/error', },
    { path: '/error', component: ErrorPage },
  ],
});

export default router;
