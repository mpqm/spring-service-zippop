<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <div>
        <section class="ctn-mypageintro">
          <h1>고객 마이페이지</h1>
          <p>장바구니, 관심 팝업, 주문과 리뷰를 한곳에서 관리하세요.</p>
        </section>
        <nav class="wrp-subheader" aria-label="고객 마이페이지 메뉴">
          <div class="ctn-subheader">
            <router-link :class="{ active: isActive('/mypage/customer/cart') }" class="lnk-subheader" to="/mypage/customer/cart">장바구니</router-link>
            <router-link :class="{ active: isActive('/mypage/customer/like') }" class="lnk-subheader" to="/mypage/customer/like">좋아요</router-link>
            <router-link :class="{ active: isActive('/mypage/customer/review') }" class="lnk-subheader" to="/mypage/customer/review">리뷰</router-link>
            <router-link :class="{ active: isActive('/mypage/customer/orders') }" class="lnk-subheader" to="/mypage/customer/orders">결제 내역</router-link>
            <router-link :class="{ active: isActive('/mypage/customer/account') }" class="lnk-subheader" to="/mypage/customer/account">계정 관리</router-link>
          </div>
        </nav>
        <div class="lyt-router">
          <router-view></router-view>
        </div>
      </div>
    </div>
    <AppFooter></AppFooter>
  </div>
</template>

<script setup>
import AppFooter from "@/components/AppFooter.vue";
import AppHeader from "@/components/AppHeader.vue";
import { useRoute } from "vue-router";
import { isPathWithin } from "@/utils/navigation";

const route = useRoute();
const isActive = path => isPathWithin(route.path, path);
</script>
