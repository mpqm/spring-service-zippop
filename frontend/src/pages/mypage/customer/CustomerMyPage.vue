<template>
  <div>
    <HeaderComponent></HeaderComponent>
    <div class="company-mypage">
      <div class="mypage-container">
        <MyPageHeaderComponent :userRole="'ROLE_CUSTOMER'"></MyPageHeaderComponent>
        <MyPageViewComponent></MyPageViewComponent>
      </div>
    </div>
    <FooterComponent></FooterComponent>
  </div>
</template>

<script setup>
import FooterComponent from "@/components/common/FooterComponent.vue";
import HeaderComponent from "@/components/common/HeaderComponent.vue";
import MyPageHeaderComponent from "@/components/mypage/MyPageHeaderComponent.vue";
import MyPageViewComponent from "@/components/mypage/MyPageViewComponent.vue";

</script>

<style scoped>
.company-mypage {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  width: 65rem;
  padding: 1rem;
}
</style>