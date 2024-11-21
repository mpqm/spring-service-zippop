<template>
  <div>
    <HeaderComponent></HeaderComponent>
    <div class="company-mypage">
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

<style scoped>
.company-mypage {
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  width: 65rem;
  padding: 1rem;
}
</style>