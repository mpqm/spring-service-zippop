<template>
  <div class="header">
    <!-- 오른쪽 기본 헤더 -->
    <div class="header-container">
      <div class="left-section">
        <img class="logo-img" src="../../assets/img/zippopbanner.png" />
        <a :class="{ 'nav-link': true, active: isActive('/') }" href="/">팝업찾기</a>
        <a :class="{ 'nav-link': true, active: isActive('/goods') }" href="/goods">굿즈마켓</a>
        <a :class="{ 'nav-link': true, active: isActive('/reserve') }" href="/reserve">팝업예약</a>
      </div>
      <!-- 고객용 -->
      <div class="right-section" v-if="userStatus && userInfo.role === 'ROLE_CUSTOMER'">
        <div class="user-info" @click="toggleDropdown">
          <span class="user-name">
            {{ userInfo.name }}
            <Icon icon="iconoir:user-cart" width="30px" height="30px"  style="color: #00c7ae" />
          </span>
          <img class="profile-img" :src="userInfo.profileImageUrl" />
          <div class="dropdown" v-if="isDropdownVisible">
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/customer/cart') }" href="/mypage/customer/cart">장바구니</a>
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/customer/like') }" href="/mypage/customer/like">좋아요</a>
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/customer/review') }" href="/mypage/customer/review">리뷰</a>
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/customer/orders') }" href="/mypage/customer/orders">결재 내역</a>
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/customer/account-edit') }" href="/mypage/customer/account-edit">계정 관리</a>
            <a class="dropdown-link" @click="logout">로그아웃</a>
          </div>
        </div>
      </div>
      <!-- 기업용 -->
      <div class="right-section" v-if="userStatus && userInfo.role === 'ROLE_COMPANY'">
        <div class="user-info" @click="toggleDropdown">
          <span class="user-name"> 
            {{ userInfo.name }}
            <Icon icon="iconoir:user-badge-check" width="30px" height="30px"  style="color: #00c7ae" />
          </span>
          <img class="profile-img" :src="userInfo.profileImageUrl" />
          <div class="dropdown" v-if="isDropdownVisible">
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/company/store') }" href="/mypage/company/store">팝업 관리</a>
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/company/goods') }" href="/mypage/company/goods">굿즈 관리</a>
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/company/reserve') }" href="/mypage/company/reserve">예약 관리</a>
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/company/orders') }" href="/mypage/company/orders">거래 내역</a>
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/company/settlement') }" href="/mypage/company/settlement">정산 내역</a>
            <a :class="{ 'dropdown-link': true, active: isActive('/mypage/company/account-edit') }" href="/mypage/company/account-edit">계정 관리</a>
            <a class="dropdown-link" @click="logout">로그아웃</a>
          </div>
        </div>
      </div>
      <!-- 로그인/회원가입 -->
      <div class="right-section" v-if="!userStatus">
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

// store, router, route, toast
const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();

// 변수(auth)
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

<style scoped>
.header {
  position: sticky;
  top: 0;
  background: #fff;
  border-bottom: 1px solid #f2f2f2;
  z-index: 99;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 2.5rem;
  padding: 0.5rem;
  width: 65rem;
  margin: 0 auto;
}

.left-section,
.right-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-btn {
  font-weight: 400;
  color: #fff;
  background-color: #00c7ae;
  border: none;
  padding: 0.5rem;
  border-radius: 0.25rem;
  cursor: pointer;
  transition: opacity 0.2s ease-in-out;
  text-decoration: none;
}

.header-btn:hover {
  opacity: 0.8;
}

.nav-link {
  font-weight: 600;
  font-size: 1rem;
  color: #000;
  padding: 8px;
  text-decoration: none;
}

.active {
  border-bottom: 3px solid #00c7ae;
  color: #00c7ae;
}

.logo-img {
  width: 120px;
  height: 40px;
}

.profile-img {
  border-radius: 50%;
  width: 30px;
  height: 30px;
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  vertical-align: middle;
  position: relative;
}

.dropdown {
  position: absolute;
  right: 1;
  top: 100%;
  margin-top: 0.5rem;
  background-color: #ffffff;
  border: 1px solid #00c7ae;
  border-radius: 0.25rem;
  padding: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 9rem;
  align-content: center;
}

.dropdown-link {
  display: flex;
  justify-content: center;
  font-size: 0.9rem;
  font-weight: bold;
  color: #000;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  text-decoration: none;
}

.dropdown-btn {
  display: flex;
  font-size: 1rem;
  color: #000;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  text-decoration: none;
}

.dropdown-link:hover,
.dropdown-btn:hover {
  background-color: #00c7ae;
  color: #fff;
  border-radius: 0.25rem;
}

.user-name {
  position: relative;
  font-weight: 600;
  display: flex;          
  align-items: center;   
  gap: 5px;             
}

.role-icon {
  position: absolute;
  top: -6px;   
  right: -16px;
}
</style>
