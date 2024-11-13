<template>
  <div class="mypage-header">
    <div v-if="userRole === 'ROLE_COMPANY'" class="menu-list">
      <router-link class="menu-link" to="/mypage/company/store">팝업 관리</router-link>
      <router-link class="menu-link" to="/mypage/company/goods">굿즈 관리</router-link>
      <router-link class="menu-link" to="/managermypage/account-edit">회원정보 수정</router-link>
      <router-link class="menu-link" to="/managermypage/charge">수수료 결제 내역</router-link>
      <router-link class="menu-link" to="/chats">문의 채팅</router-link>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue';

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
  background-color:#00c7ae;
}
</style>
