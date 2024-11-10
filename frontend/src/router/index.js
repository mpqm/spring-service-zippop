import { createRouter, createWebHistory } from "vue-router";

import MainPage from "@/pages/MainPage.vue";

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
import CompanyMypageMainComponent from "@/components/companymypage/CompanyMypageMainComponent.vue";
import ChargeListComponent from "@/components/companymypage/ChargeListComponent.vue";
import ProductRegisterComponent from "@/components/companymypage/ProductRegisterComponent.vue";
import PopupRegisterComponent from "@/components/companymypage/PopupRegisterComponent.vue";
import MainChatComponent from "@/components/chat/MainChatComponent.vue";
import LoginPage from "@/pages/auth/LoginPage.vue";
import CustomerSignupPage from "@/pages/auth/CustomerSignupPage.vue";
import CompanySignupPage from "@/pages/auth/CompanySignupPage.vue";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: MainPage },
    { path: "/login", component: LoginPage },
    { path: "/signup/customer", component: CustomerSignupPage },
    { path: "/signup/company", component: CompanySignupPage },

    { path: '/cart', component: CartComponent },
    {
      path: '/mypage',
      component: MypageMainComponent,
      children: [
        { path: 'popup', component: PopupGoodsComponent },
        { path: 'account-edit', component: EditProfileComponent },
        { path: 'reviews', component: MyReviewsComponent }
      ]
    },
    {
      path: '/managermypage',
      component: CompanyMypageMainComponent,
      children: [
        { path: 'charge', component: ChargeListComponent },
        { path: 'goods', component: ProductRegisterComponent },
        { path: 'account-edit', component: EditProfileComponent },
        { path: 'popup-register', component: PopupRegisterComponent }
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
