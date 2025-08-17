<template>
  <div class="header-component">
    <!-- 오른쪽 기본 헤더 -->
    <div class="header-container">
      <div class="header-section">
        <img class="header-logo" src="../assets/img/zippopbanner.png" />
        <a :class="{ 'header-nav-link': true, active: isActive('/') }" href="/">팝업찾기</a>
        <a :class="{ 'header-nav-link': true, active: isActive('/goods') }" href="/goods">굿즈마켓</a>
        <a :class="{ 'header-nav-link': true, active: isActive('/reserve') }" href="/reserve">팝업예약</a>
      </div>
      <!-- 고객용 -->
      <div class="header-section" v-if="userStatus && userInfo.role === 'ROLE_CUSTOMER'">
        <div class="header-user-info" @click="toggleDropdown">
          <span class="header-user-name">
            {{ userInfo.name }}
            <Icon icon="iconoir:user-cart" width="30px" height="30px"  style="color: #00c7ae" />
          </span>
          <img class="header-profile-img" :src="userInfo.profileImageUrl" />
          <div class="header-dropdown" v-if="isDropdownVisible">
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/customer/cart') }" href="/mypage/customer/cart">장바구니</a>
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/customer/like') }" href="/mypage/customer/like">좋아요</a>
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/customer/review') }" href="/mypage/customer/review">리뷰</a>
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/customer/orders') }" href="/mypage/customer/orders">결재 내역</a>
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/customer/account-edit') }" href="/mypage/customer/account-edit">계정 관리</a>
            <a class="header-dropdown-link" @click="logout">로그아웃</a>
          </div>
        </div>
      </div>
      <!-- 기업용 -->
      <div class="header-section" v-if="userStatus && userInfo.role === 'ROLE_COMPANY'">
        <div class="header-user-info" @click="toggleDropdown">
          <span class="header-user-name"> 
            {{ userInfo.name }}
            <Icon icon="iconoir:user-badge-check" width="30px" height="30px"  style="color: #00c7ae" />
          </span>
          <img class="header-profile-img" :src="userInfo.profileImageUrl" /> 
          <div class="header-dropdown" v-if="isDropdownVisible">
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/company/store') }" href="/mypage/company/store">팝업 관리</a>
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/company/goods') }" href="/mypage/company/goods">굿즈 관리</a>
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/company/reserve') }" href="/mypage/company/reserve">예약 관리</a>
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/company/orders') }" href="/mypage/company/orders">거래 내역</a>
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/company/settlement') }" href="/mypage/company/settlement">정산 내역</a>
            <a :class="{ 'header-dropdown-link': true, active: isActive('/mypage/company/account-edit') }" href="/mypage/company/account-edit">계정 관리</a>
            <a class="header-dropdown-link" @click="logout">로그아웃</a>
          </div>
        </div>
      </div>
      <!-- 로그인/회원가입 -->
      <div class="header-section" v-if="!userStatus">
        <router-link class="default-btn" to="/login">로그인</router-link>
        <router-link class="default-btn" to="/signup/customer">회원가입</router-link>
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
const userInfo = ref({})
const userStatus = ref(false);
const currentPath = ref(router.currentRoute.value.path);
const isDropdownVisible = ref(false);

// onMounted
onMounted(async () => {
  userStatus.value = authStore.isLoggedIn
  if (userStatus.value) {
    await authStore.getInfo();
    userInfo.value = authStore.userInfo
  }
});

// 로그아웃
const logout = async () => {
  const res = await authStore.logout();
  if (res.success) {
    userStatus.value = false;
    isDropdownVisible.value = false;
    router.push('/');
    toast.success(res.message);
  } else {
    toast.error(res.message);
  }
};

// 드롭다운 버튼 클릭시
const toggleDropdown = () => {
  isDropdownVisible.value = !isDropdownVisible.value;
};

// 헤더 활성화 함수
const isActive = (path) => {
  return currentPath.value === path;
};

// 라우팅
router.afterEach((to) => {
  currentPath.value = to.path;
});

</script>
