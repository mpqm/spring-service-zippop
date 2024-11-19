import { createRouter, createWebHistory } from "vue-router";

import PaymentPage from "@/pages/orders/PaymentPage.vue";
import MainChatComponent from "@/components/chat/MainChatComponent.vue";
import LoginPage from "@/pages/auth/LoginPage.vue";
import CustomerSignupPage from "@/pages/auth/CustomerSignupPage.vue";
import CompanySignupPage from "@/pages/auth/CompanySignupPage.vue";
import StoreMainPage from "@/pages/store/StoreMainPage.vue";
import StoreRegisterPage from "@/pages/mypage/StoreRegisterPage.vue";
import CompanyMyPage from "@/pages/mypage/CompanyMyPage.vue";
import StoreManagePage from "@/pages/mypage/StoreManagePage.vue";
import StoreUpdatePage from "@/pages/mypage/StoreUpdatePage.vue";
import StoreDetailPage from "@/pages/store/StoreDetailPage.vue";
import GoodsManage1Page from "@/pages/mypage/GoodsManage1Page.vue";
import GoodsManage2Page from "@/pages/mypage/GoodsManage2Page.vue";
import GoodsRegisterPage from "@/pages/mypage/GoodsRegisterPage.vue";
import GoodsUpdatePage from "@/pages/mypage/GoodsUpdatePage.vue";
import GoodsMainPage from "@/pages/goods/GoodsMainPage.vue";
import GoodsDetailPage from "@/pages/goods/GoodsDetailPage.vue";
import ErrorPage from "@/pages/error/ErrorPage.vue";
import EditProfilePage from "@/pages/mypage/EditProfilePage.vue";
import FindIdPwPage from "@/pages/auth/FindIdPwPage.vue";
import CustomerMyPage from "@/pages/mypage/CustomerMyPage.vue";
import ReviewSearchPage from "@/pages/mypage/ReviewSearchPage.vue";
import LikeManagePage from "@/pages/mypage/LikeManagePage.vue";
import CartPage from "@/pages/orders/CartPage.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/login", component: LoginPage },
    { path: '/find-idpw', component: FindIdPwPage},
    { path: "/signup/customer", component: CustomerSignupPage },
    { path: "/signup/company", component: CompanySignupPage },
    
    { path: "/", component: StoreMainPage },
    { path: '/store/:storeIdx', component: StoreDetailPage },

    {path: "/goods", component: GoodsMainPage},
    {path: '/goods/:goodsIdx', component: GoodsDetailPage},

    {
      path: '/mypage/company',
      component: CompanyMyPage ,
      children: [
        { path: 'store', component: StoreManagePage },
        { path: 'store/register', component: StoreRegisterPage },
        { path: 'store/update/:storeIdx', component: StoreUpdatePage },
        { path: 'store/:storeIdx', component: StoreDetailPage },
        { path: 'goods', component: GoodsManage1Page },
        { path: 'goods/:storeIdx', component: GoodsManage2Page },
        { path: 'goods/:storeIdx/register', component: GoodsRegisterPage },
        { path: 'goods/:storeIdx/update/:goodsIdx', component: GoodsUpdatePage },
        { path: 'account-edit', component: EditProfilePage },
      ]
    },

    {
      path: '/mypage/customer',
      component: CustomerMyPage ,
      children: [
        { path: 'review', component: ReviewSearchPage },
        { path: 'account-edit', component: EditProfilePage },
        { path: 'like', component: LikeManagePage },
        { path: 'cart', component: CartPage, },
      ]
    },
    {
      path: '/test', component: PaymentPage,
    },
    
    { path: '/chats', component: MainChatComponent },
    
    { path: "/payment", component: PaymentPage },
    
    { path: '/error', component: ErrorPage },
    {
      path: '/:catchAll(.*)',
      redirect: '/error',
    },
  ],
});

export default router;
