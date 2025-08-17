<template>
  <div>
    <HeaderComponent></HeaderComponent>
    <div class="root-page">
      <div>
        <MyPageHeaderComponent :userRole="'ROLE_COMPANY'"></MyPageHeaderComponent>
        <MyPageViewComponent></MyPageViewComponent>
      </div>
    </div>
    <FooterComponent></FooterComponent>
  </div>
</template>

<script setup>
import HeaderComponent from "@/components/common/HeaderComponent.vue";
import FooterComponent from "@/components/common/FooterComponent.vue";
import MyPageHeaderComponent from "@/components/mypage/MyPageHeaderComponent.vue";
import MyPageViewComponent from "@/components/mypage/MyPageViewComponent.vue";

</script>