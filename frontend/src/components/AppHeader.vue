<template>
  <header class="lyt-header">
    <!-- 오른쪽 기본 헤더 -->
    <div class="wrp-header">
      <div class="ctn-header">
        <router-link class="lnk-brand" to="/" aria-label="ZIPPOP 홈">
          <img class="img-headerlogo" src="../assets/img/zippoicon-removebg.png" alt="ZIPPOP" />
          <span class="txt-brandmark">ZIP<span>POP</span></span>
        </router-link>
        <router-link :class="{ 'lnk-header': true, active: activeMainTab === 'popup' }" to="/" >팝업찾기</router-link>
        <router-link :class="{ 'lnk-header': true, active: activeMainTab === 'goods' }" to="/goods" >재고 마켓</router-link>
        <router-link :class="{ 'lnk-header': true, active: activeMainTab === 'reserve' }" to="/reserve" >사전 예약</router-link>
      </div>
      <!-- 고객용 -->
      <div class="ctn-header" v-if="userStatus && userInfo.role === 'ROLE_CUSTOMER'">
        <div class="ctn-userinfo" @click="toggleDropdown">
          <span class="txt-headerusername">
            {{ userInfo.name }}
            <Icon icon="iconoir:user-cart" width="30px" height="30px"  class="ico-accent" />
          </span>
          <img class="img-headerprofile" :src="userInfo.profileImageUrl" />
          <div class="ctn-dropdown" v-if="isDropdownVisible">
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/customer/cart') }" to="/mypage/customer/cart">장바구니</router-link>
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/customer/like') }" to="/mypage/customer/like">좋아요</router-link>
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/customer/review') }" to="/mypage/customer/review">리뷰</router-link>
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/customer/orders') }" to="/mypage/customer/orders">결재 내역</router-link>
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/customer/account') }" to="/mypage/customer/account">계정 관리</router-link>
            <a class="lnk-dropdown" @click="logout">로그아웃</a>
          </div>
        </div>
      </div>
      <!-- 기업용 -->
      <div class="ctn-header" v-if="userStatus && userInfo.role === 'ROLE_COMPANY'">
        <div class="ctn-userinfo" @click="toggleDropdown">
          <span class="txt-headerusername"> 
            {{ userInfo.name }}
            <Icon icon="iconoir:user-badge-check" width="30px" height="30px"  class="ico-accent" />
          </span>
          <img class="img-headerprofile" :src="userInfo.profileImageUrl" /> 
          <div class="ctn-dropdown" v-if="isDropdownVisible">
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/company/popup') }" to="/mypage/company/popup">팝업 관리</router-link>
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/company/goods') }" to="/mypage/company/goods">굿즈 관리</router-link>
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/company/reserve') }" to="/mypage/company/reserve">예약 관리</router-link>
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/company/orders') }" to="/mypage/company/orders">거래 내역</router-link>
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/company/payout') }" to="/mypage/company/payout">정산 내역</router-link>
            <router-link :class="{'lnk-dropdown': true, active: isActive('/mypage/company/account') }" to="/mypage/company/account">계정 관리</router-link>
            <a class="lnk-dropdown" @click="logout">로그아웃</a>
          </div>
        </div>
      </div>
      <!-- 로그인/회원가입 -->
      <div class="ctn-header" v-if="!userStatus">
        <router-link class="btn-default" to="/login">로그인</router-link>
        <router-link class="btn-default" to="/signup">회원가입</router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { useAccountStore } from '@/stores/accountStore';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { isPathWithin, resolveMainTab } from '@/utils/navigation';
const route = useRoute();

const authStore = useAuthStore();
const accountStore = useAccountStore();
const router = useRouter();
const toast = useToast();
const userInfo = computed(() => accountStore.userInfo);
const userStatus = computed(() => authStore.isLoggedIn);
const isDropdownVisible = ref(false);

const activeMainTab = computed(() => resolveMainTab(route.path, route.query));
const isActive = (path) => isPathWithin(route.path, path);

// 로그아웃
const logout = async () => {
  const res = await authStore.logout();
  if (res.success) {

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

</script>
