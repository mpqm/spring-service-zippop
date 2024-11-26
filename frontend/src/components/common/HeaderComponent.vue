<template>
  <div class="header">
    <!-- 오른쪽 기본 헤더 -->
    <div class="header-container">
      <div class="left-section">
        <img class="logo-img" src="../../assets/img/zippopbanner.png" />
        <a :class="{ 'nav-link': true, active: isActive('/') }" href="/">팝업찾기</a>
        <a :class="{ 'nav-link': true, active: isActive('/goods') }" href="/goods">굿즈마켓</a>
      </div>
      <!-- 고객용 -->
      <div class="right-section" v-if="userStatus && userInfo.role === 'ROLE_CUSTOMER'">
        <a :class="{ 'nav-link': true, active: isActive('/mypage/customer/like') }" href="/mypage/customer/like">좋아요</a>
        <a :class="{ 'nav-link': true, active: isActive('/mypage/customer/cart') }"
          href="/mypage/customer/cart">장바구니</a>
        <div class="user-info" @click="toggleDropdown">
          <img src="../../assets/img/customer.png" alt="Customer Icon" />
          <span>{{ userInfo.name }}</span>
          <img class="profile-img" :src="userInfo.profileImageUrl" />
          <img src="../../assets/img/drop-down.png" />
          <div class="dropdown" v-if="isDropdownVisible">
            <a class="dropdown-link" href="/mypage/customer/cart">마이페이지</a>
            <a class="dropdown-link" @click="logout">로그아웃</a>
          </div>
        </div>
      </div>
      <!-- 기업용 -->
      <div class="right-section" v-if="userStatus && userInfo.role === 'ROLE_COMPANY'">
        <div class="user-info" @click="toggleDropdown">
          <img class="company-img" src="../../assets/img/company.png" alt="Company Icon" />
          <span>{{ userInfo.name }}</span>
          <img class="profile-img" :src="userInfo.profileImageUrl" />
          <img src="../../assets/img/drop-down.png" />
          <div class="dropdown" v-if="isDropdownVisible">
            <a class="dropdown-link" href="/mypage/company/store">관리페이지</a>
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
  height: 3.625rem;
  padding: 1rem;
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
  border-bottom: 1px solid #00c7ae;
  color: #00c7ae;
}

.logo-img {
  width: 150px;
  height: 45px;
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
  position: relative;
}

.dropdown {
  position: absolute;
  right: 0;
  top: 100%;
  margin-top: 0.5rem;
  background-color: #fff;
  border: 1px solid #f2f2f2;
  border-radius: 0.25rem;
  padding: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 8rem;
  align-content: center;
}

.dropdown-link {
  display: flex;
  justify-content: center;
  font-size: 1rem;
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
</style>
