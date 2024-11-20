import { createRouter, createWebHistory } from "vue-router";

import PaymentPage from "@/pages/orders/OrdersPage.vue";
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
    { path: "/login", component: LoginPage },
    { path: '/find-idpw', component: FindIdPwPage},
    { path: "/signup/customer", component: CustomerSignupPage },
    { path: "/signup/company", component: CompanySignupPage },
    
    { path: "/", component: StoreMainPage },
    { path: '/store/:storeIdx', component: StoreDetailPage },
    
    {path: "/goods", component: GoodsMainPage},
    {path: '/goods/:storeIdx', component: GoodsDetailPage},
    {path: '/goods-detail/:goodsIdx', component: GoodsDetailPage2},
    
    { path: "/orders", component: OrdersPage },
    { path: "/orders/:ordersIdx", component: OrdersDetailPage },
    
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
        { path: 'orders', component: CompanyOrdersManagePage1, },
        { path: 'orders/:storeIdx', component: CompanyOrdersManagePage2, },
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
        { path: 'orders', component: CustomerOrdersManagePage, },
      ]
    },

    {
      path: '/test', component: PaymentPage,
    },
    
    { path: '/chats', component: MainChatComponent },
    
    
    { path: '/error', component: ErrorPage },
    {
      path: '/:catchAll(.*)',
      redirect: '/error',
    },
  ],
});

export default router;
