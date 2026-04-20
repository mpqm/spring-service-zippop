<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <div>
        <div class="wrp-subheader">
          <div class="ctn-subheader">
            <router-link active-class="active" class="lnk-subheader" to="/mypage/customer/cart">장바구니</router-link>
            <router-link active-class="active" class="lnk-subheader" to="/mypage/customer/like">좋아요</router-link>
            <router-link active-class="active" class="lnk-subheader" to="/mypage/customer/review">리뷰</router-link>
            <router-link active-class="active" class="lnk-subheader" to="/mypage/customer/orders">결제 내역</router-link>
            <router-link active-class="active" class="lnk-subheader" to="/mypage/customer/account">계정 관리</router-link>
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
</script>