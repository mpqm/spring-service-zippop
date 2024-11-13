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
import HeaderComponent from "@/components/HeaderComponent.vue";
import FooterComponent from "@/components/FooterComponent.vue";
import MyPageHeaderComponent from "@/components/MyPageHeaderComponent.vue";
import MyPageViewComponent from "@/components/MyPageViewComponent.vue";
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