<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <div>
        <div class="wrp-subheader">
          <div class="ctn-subheader">
            <router-link :class="{ active: isActive('/mypage/customer/cart') }" class="lnk-subheader" to="/mypage/customer/cart">장바구니</router-link>
            <router-link :class="{ active: isActive('/mypage/customer/like') }" class="lnk-subheader" to="/mypage/customer/like">좋아요</router-link>
            <router-link :class="{ active: isActive('/mypage/customer/review') }" class="lnk-subheader" to="/mypage/customer/review">리뷰</router-link>
            <router-link :class="{ active: isActive('/mypage/customer/orders') }" class="lnk-subheader" to="/mypage/customer/orders">결제 내역</router-link>
            <router-link :class="{ active: isActive('/mypage/customer/account') }" class="lnk-subheader" to="/mypage/customer/account">계정 관리</router-link>
          </div>
        </div>
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
