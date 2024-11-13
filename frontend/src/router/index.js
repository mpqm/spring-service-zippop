import { createRouter, createWebHistory } from "vue-router";


import CommunityPage from "@/pages/CommunityPage.vue";
import PaymentPage from "@/pages/PaymentPage.vue";

import PostAllComponent from "@/components/community/post-all/PostAllComponent.vue";
import PostCreateComponent from "@/components/community/post-edit/PostCreateComponent.vue";
import CartComponent from "@/components/cart/CartComponent.vue";
import WishPopupPage from "@/pages/WishPopupPage.vue";
import MypageMainComponent from "@/components/customermypage/MypageMainComponent.vue";
import PopupGoodsComponent from "@/components/customermypage/PopupGoodsComponent.vue";
import EditProfileComponent from "@/components/customermypage/EditProfileComponent.vue";
import MyReviewsComponent from "@/components/customermypage/MyReviewsComponent.vue";
import ChargeListComponent from "@/components/companymypage/ChargeListComponent.vue";
import StoreRegisterPage from "@/pages/company/StoreRegisterPage.vue";
import MainChatComponent from "@/components/chat/MainChatComponent.vue";
import LoginPage from "@/pages/auth/LoginPage.vue";
import CustomerSignupPage from "@/pages/auth/CustomerSignupPage.vue";
import CompanySignupPage from "@/pages/auth/CompanySignupPage.vue";
import StoreMainPage from "@/pages/store/StoreMainPage.vue";
import CompanyMyPage from "@/pages/company/CompanyMyPage.vue";
import StoreManagementPage from "@/pages/company/StoreManagementPage.vue";
import ProductRegisterPage from "@/pages/ProductRegisterPage.vue";
import StoreUpdatePage from "@/pages/company/StoreUpdatePage.vue";
import StoreDetailPage from "@/pages/store/StoreDetailPage.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: StoreMainPage },
    { path: "/login", component: LoginPage },
    { path: "/signup/customer", component: CustomerSignupPage },
    { path: "/signup/company", component: CompanySignupPage },
    { path: '/store/:storeIdx', component: StoreDetailPage },
    {
      path: '/mypage/company',
      component: CompanyMyPage ,
      children: [
        { path: 'store', component: StoreManagementPage },
        { path: 'store/register', component: StoreRegisterPage },
        { path: 'store/update/:storeIdx', component: StoreUpdatePage },
        { path: '/store/:storeIdx', component: StoreDetailPage },
        { path: 'goods', component: ProductRegisterPage },
        { path: 'charge', component: ChargeListComponent },
        { path: 'account-edit', component: EditProfileComponent },
      ]
    },
    { path: '/cart', component: CartComponent },
    {
      path: '/mypage/customer',
      component: MypageMainComponent,
      children: [
        { path: 'popup', component: PopupGoodsComponent },
        { path: 'account-edit', component: EditProfileComponent },
        { path: 'reviews', component: MyReviewsComponent }
      ]
    },

    { path: '/chats', component: MainChatComponent },
   
    { path: "/payment", component: PaymentPage },
    {
      path: "/community",
      children: [
        { path: "post-all", component: PostAllComponent },
        {
          path: "post-edit",
          children: [
            { path: "create", component: PostCreateComponent },
          ],
        }
      ],
      component: CommunityPage
    },
    {
      path: "/wish_popup",
      component: WishPopupPage
    }],
});

export default router;
