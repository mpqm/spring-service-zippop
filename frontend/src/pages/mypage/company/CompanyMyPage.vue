<template>
  <div>
    <AppHeader></AppHeader>
    <div class="lyt-root">
      <div>
        <section class="ctn-mypageintro">
          <h1>기업 마이페이지</h1>
          <p>팝업 운영, 예약, 주문과 정산 현황을 한곳에서 관리하세요.</p>
        </section>
        <nav class="wrp-subheader" aria-label="기업 마이페이지 메뉴">
          <div class="ctn-subheader">
            <router-link :class="{ active: isActive('/mypage/company/popup') }" class="lnk-subheader" to="/mypage/company/popup">팝업 관리</router-link>
            <router-link :class="{ active: isActive('/mypage/company/goods') }" class="lnk-subheader" to="/mypage/company/goods">굿즈 관리</router-link>
            <router-link :class="{ active: isActive('/mypage/company/reserve') }" class="lnk-subheader" to="/mypage/company/reserve">예약 관리</router-link>
            <router-link :class="{ active: isActive('/mypage/company/orders') }" class="lnk-subheader" to="/mypage/company/orders">주문 내역</router-link>
            <router-link :class="{ active: isActive('/mypage/company/payout') }" class="lnk-subheader" to="/mypage/company/payout">정산 내역</router-link>
            <router-link :class="{ active: isActive('/mypage/company/account') }" class="lnk-subheader" to="/mypage/company/account">계정 관리</router-link>
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
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import { useRoute } from "vue-router";
import { isPathWithin } from "@/utils/navigation";

const route = useRoute();
const isActive = path => isPathWithin(route.path, path);
</script>
