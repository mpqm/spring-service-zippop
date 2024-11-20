<template>
  <div>
    <HeaderComponent></HeaderComponent>
    <div class="company-mypage">
        <div class="mypage-container">
          <MyPageHeaderComponent :userRole="userRole"></MyPageHeaderComponent>
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
import { onMounted, ref } from "vue";
import { useAuthStore } from "@/stores/useAuthStore";
const authStore = useAuthStore();

const userRole = ref();
onMounted(async() => {
  userRole.value = authStore.userInfo.role;
})
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