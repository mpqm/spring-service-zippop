<template>
  <header class="lyt-header">
    <!-- 오른쪽 기본 헤더 -->
    <div class="wrp-header">
      <div class="ctn-header">
        <router-link class="lnk-brand" to="/" aria-label="ZIPPOP 홈">
          <img class="img-headerlogo" src="../assets/img/zippoicon-removebg.png" alt="ZIPPOP" />
          <span class="txt-brandmark">ZIP<span>POP</span></span>
        </router-link>
        <router-link :class="{ 'lnk-header': true, active: activeMainTab === 'popup' }" to="/" >팝업 찾기</router-link>
        <router-link :class="{ 'lnk-header': true, active: activeMainTab === 'goods' }" to="/goods" >재고 마켓</router-link>
        <router-link v-if="userStatus" :class="{ 'lnk-header': true, active: route.path.startsWith('/mypage/') }" :to="myPagePath">마이페이지</router-link>
      </div>
      <!-- 로그인 사용자 -->
      <div class="ctn-header" v-if="userStatus">
        <div class="ctn-userinfo">
          <span class="txt-headerusername">
            {{ userInfo.name }}
            <Icon :icon="userInfo.role === 'ROLE_COMPANY' ? 'iconoir:user-badge-check' : 'iconoir:user-cart'" width="30px" height="30px" class="ico-accent" />
          </span>
          <img v-if="userInfo.profileImageUrl" class="img-headerprofile" :src="userInfo.profileImageUrl" alt="프로필" />
        </div>
        <button class="btn-normal btn-headerlogout" type="button" @click="logout">
          <Icon icon="iconoir:log-out" width="20px" height="20px" />로그아웃
        </button>
      </div>
      <!-- 로그인/회원가입 -->
      <div class="ctn-header" v-if="!userStatus">
        <router-link class="btn-default btn-headericon" to="/login" aria-label="로그인" title="로그인">
          <Icon icon="iconoir:log-in" width="20px" height="20px" />로그인
        </router-link>
        <router-link class="btn-default btn-headericon" to="/signup" aria-label="회원가입" title="회원가입">
          <Icon icon="iconoir:user-plus" width="20px" height="20px" />회원가입
        </router-link>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useAuthStore } from '@/stores/authStore';
import { useAccountStore } from '@/stores/accountStore';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import { resolveMainTab } from '@/utils/navigation';
const route = useRoute();

const authStore = useAuthStore();
const accountStore = useAccountStore();
const router = useRouter();
const toast = useToast();
const userInfo = computed(() => accountStore.userInfo);
const userStatus = computed(() => authStore.isLoggedIn);

const activeMainTab = computed(() => resolveMainTab(route.path, route.query));
const myPagePath = computed(() => userInfo.value.role === 'ROLE_COMPANY'
  ? '/mypage/company/popup'
  : '/mypage/customer/cart');

// 로그아웃
const logout = async () => {
  const res = await authStore.logout();
  if (res.success) {

    router.push('/');
    toast.success(res.message);
  } else {
    toast.error(res.message);
  }
};

</script>
