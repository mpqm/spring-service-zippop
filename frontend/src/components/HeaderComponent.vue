<template>
<div class="header">
    <div class="header-container">
    <div class="left-section">
        <img class="logo-img" src="../assets/img/zippopbanner.png"/>
        <a :class="{'nav-link': true, 'active': isActive('/')}" href="/">팝업찾기</a>
        <a :class="{'nav-link': true, 'active': isActive('/market')}" href="/market">굿즈마켓</a>
        <a :class="{'nav-link': true, 'active': isActive('/community')}" href="/community">커뮤니티</a>
    </div>
    <div class="right-section" v-if="userStatus==true">
      <a @click="redirectToMypage">마이페이지</a>
      <a >장바구니</a>
      <button class="header-btn" @click="logout"> 로그아웃 </button>
    </div>
    <div class="right-section" v-if="userStatus==false">
      <router-link class="header-btn" to="/login">로그인</router-link>
      <router-link class="header-btn" to="/signup/customer">회원가입</router-link>
    </div>
</div>
</div>

</template>


<script setup>
import { useAuthStore } from '@/stores/useAuthStore';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';

const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();
const userStatus = ref(false)
const currentPath = ref(router.currentRoute.value.path);

onMounted( async() => {
  userStatus.value = authStore.isLoggedIn;
  if (userStatus.value == true) {
    await authStore.getInfo();
  }
})

const logout = async() => {
  const res = await authStore.logout()
  if(res.success){
    userStatus.value = false;
    router.push('/')
    toast.success('로그아웃 성공')
  } else {
    toast.error('로그아웃 실패')
  }
}
// 스타일 적용 함수
const isActive = (path) => {
  return currentPath.value === path;
};

router.afterEach((to) => {
  currentPath.value = to.path;
});

</script>


<style scoped>
.header {
  position: sticky;
  top: 0;
  background: #fff;
  border-bottom: 0.0625rem solid #f2f2f2;
  z-index: 99;
}
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 3.625rem;
  padding: 1rem;
  margin: 0 auto;
  text-align: center;
  width: 65rem;
  height: 100%;
}

.left-section {
  display: flex;
  column-gap: 10px;
  flex-direction: row;
  align-items: center;
}

.right-section {
  display: flex;
  column-gap: 10px;
  flex-direction: row;
  align-items: center;
}

.header-btn {
    display: block;
    text-align: center;
    width: auto;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.5rem;
    border-radius: 0.25rem;
    text-decoration: #000;
}
.header-btn:hover {
  opacity: 0.8;
}

.nav-link{
  font-weight: 600;
  font-size: 1rem;
  text-decoration: none;
  color: #000;
  padding: 8px;
}

.active {
  border-bottom: 1px solid #00c7ae;
  color: #00c7ae;
}

.logo-img {
    align-self: center;
    width: 150px;
    height: 45px;
    margin-right: 10px;
}

</style>