<template>
  <div>
    <AppHeader></AppHeader>
    <div class="customer-mypage">
      <div class="mypage-container">
        <div class="sub-menu-header">
          <div class="sub-menu-list">
            <router-link class="sub-menu-link" to="/mypage/customer/cart">장바구니</router-link>
            <router-link class="sub-menu-link" to="/mypage/customer/like">좋아요</router-link>
            <router-link class="sub-menu-link" to="/mypage/customer/review">리뷰</router-link>
            <router-link class="sub-menu-link" to="/mypage/customer/orders">결제 내역</router-link>
            <!-- <router-link class="sub-menu-link" to="/chats">문의 채팅</router-link> -->
            <router-link class="sub-menu-link" to="/mypage/customer/account-edit">계정 관리</router-link>
          </div>
        </div>
        <div class="component-display">
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

<style scoped>
.customer-mypage {
  margin: 0 auto;
  min-height: 700px;
  display: flex;
  flex-direction: column;
  width: 65rem;
  padding: 1rem;
}
</style>