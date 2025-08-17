<template>
  <div>
    <AppHeader></AppHeader>
    <div class="root-page">
      <div>
        <div class="sub-menu-header">
          <div class="sub-menu-list">
            <router-link class="sub-menu-link" to="/mypage/company/store">팝업 관리</router-link>
            <router-link class="sub-menu-link" to="/mypage/company/goods">굿즈 관리</router-link>
            <router-link class="sub-menu-link" to="/mypage/company/reserve">예약 관리</router-link>
            <router-link class="sub-menu-link" to="/mypage/company/orders">주문 내역</router-link>
            <router-link class="sub-menu-link" to="/mypage/company/settlement">정산 내역</router-link>
            <!-- <router-link class="sub-menu-link" to="/chats">문의 채팅</router-link> -->
            <router-link class="sub-menu-link" to="/mypage/company/account-edit">계정 관리</router-link>
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
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
</script>