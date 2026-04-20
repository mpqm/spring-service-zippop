<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <div>
        <div class="wrp-subheader">
          <div class="ctn-subheader">
          <router-link  active-class="active" class="lnk-subheader" to="/mypage/company/popup">팝업 관리</router-link>
            <router-link active-class="active" class="lnk-subheader" to="/mypage/company/goods">굿즈 관리</router-link>
            <router-link active-class="active" class="lnk-subheader" to="/mypage/company/reserve">예약 관리</router-link>
            <router-link active-class="active" class="lnk-subheader" to="/mypage/company/orders">주문 내역</router-link>
            <router-link active-class="active" class="lnk-subheader" to="/mypage/company/payout">정산 내역</router-link>
            <router-link active-class="active" class="lnk-subheader" to="/mypage/company/account">계정 관리</router-link>
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
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
</script>