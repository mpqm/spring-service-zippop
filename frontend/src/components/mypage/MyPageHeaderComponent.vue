<template>
  <div class="mypage-header">
    <div v-if="userRole === 'ROLE_COMPANY'" class="menu-list">
      <router-link class="menu-link" to="/mypage/company/store">팝업 관리</router-link>
      <router-link class="menu-link" to="/mypage/company/goods">굿즈 관리</router-link>
      <router-link class="menu-link" to="/mypage/company/reserve">예약 관리</router-link>
      <router-link class="menu-link" to="/mypage/company/orders">결제 내역</router-link>
      <router-link class="menu-link" to="/mypage/company/settlement">정산 내역</router-link>
      <!-- <router-link class="menu-link" to="/chats">문의 채팅</router-link> -->
      <router-link class="menu-link" to="/mypage/company/account-edit">회원정보 관리</router-link>
    </div>
    <div v-if="userRole === 'ROLE_CUSTOMER'" class="menu-list">
      <router-link class="menu-link" to="/mypage/customer/cart">장바구니</router-link>
      <router-link class="menu-link" to="/mypage/customer/like">좋아요</router-link>
      <router-link class="menu-link" to="/mypage/customer/review">리뷰</router-link>
      <router-link class="menu-link" to="/mypage/customer/orders">결제 내역</router-link>
      <!-- <router-link class="menu-link" to="/chats">문의 채팅</router-link> -->
      <router-link class="menu-link" to="/mypage/customer/account-edit">회원정보 관리</router-link>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

// props 정의(유저 역할)
defineProps({
  userRole: String,
});

</script>

<style scoped>
.mypage-header {
  display: flex;
  column-gap: 10px;
  background-color: #fff;
  border-radius: 8px;
  border: 1px solid #00c7ae;
  margin: 0;
  width: 65rem;
  flex-direction: column;
}

.menu-list {
  display: flex;
  width: fit-content;
}

.menu-link {
  position: relative;
  padding: 1rem;
  text-decoration: none;
  color: #000;
  font-weight: bold;
  border-right: 1px solid #00c7ae;
}

.router-link-active {
  color: #fff;
  font-weight: bold;
  background-color: #00c7ae;
}
</style>
