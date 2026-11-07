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
      </div>
      <!-- 로그인 사용자 -->
      <div class="ctn-header" v-if="userStatus">
        <div class="ctn-accountmenu" ref="accountMenuRef">
          <button
            class="btn-accountmenu"
            type="button"
            aria-haspopup="menu"
            :aria-expanded="isAccountMenuOpen"
            @click="isAccountMenuOpen = !isAccountMenuOpen"
          >
            <img v-if="userInfo.profileImageUrl" class="img-headerprofile" :src="userInfo.profileImageUrl" alt="" />
            <span class="txt-headerusername">{{ userInfo.name }}</span>
            <Icon :icon="userInfo.role === 'ROLE_COMPANY' ? 'iconoir:user-badge-check' : 'iconoir:user-cart'" width="25px" height="25px" class="ico-accent" />
            <Icon icon="iconoir:nav-arrow-down" width="16px" height="16px" class="ico-accountchevron" />
          </button>
          <div v-if="isAccountMenuOpen" class="ctn-accountdropdown" role="menu">
            <router-link class="lnk-accountdropdown" role="menuitem" :to="myPagePath" @click="isAccountMenuOpen = false">
              <Icon icon="iconoir:user" width="18px" height="18px" />마이페이지
            </router-link>
            <button class="btn-accountdropdownlogout" type="button" role="menuitem" @click="logout">
              <Icon icon="iconoir:log-out" width="18px" height="18px" />로그아웃
            </button>
          </div>
        </div>
      </div>
      <!-- 로그인/회원가입 -->
      <div class="ctn-header" v-if="!userStatus">
        <router-link class="btn-default btn-headericon" to="/login" aria-label="로그인" title="로그인">
          <Icon icon="iconoir:log-in" width="20px" height="20px" />로그인
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
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { resolveMainTab } from '@/utils/navigation';
const route = useRoute();

const authStore = useAuthStore();
const accountStore = useAccountStore();
const router = useRouter();
const toast = useToast();
const isAccountMenuOpen = ref(false);
const accountMenuRef = ref(null);
const userInfo = computed(() => accountStore.userInfo);
const userStatus = computed(() => authStore.isLoggedIn);

const activeMainTab = computed(() => resolveMainTab(route.path, route.query));
const myPagePath = computed(() => userInfo.value.role === 'ROLE_COMPANY'
  ? '/mypage/company/popup'
  : '/mypage/customer/cart');

const closeAccountMenuOnOutsideClick = event => {
  if (accountMenuRef.value && !accountMenuRef.value.contains(event.target)) {
    isAccountMenuOpen.value = false;
  }
};
const closeAccountMenuOnEscape = event => {
  if (event.key === 'Escape') isAccountMenuOpen.value = false;
};

onMounted(() => {
  document.addEventListener('click', closeAccountMenuOnOutsideClick);
  document.addEventListener('keydown', closeAccountMenuOnEscape);
});
onBeforeUnmount(() => {
  document.removeEventListener('click', closeAccountMenuOnOutsideClick);
  document.removeEventListener('keydown', closeAccountMenuOnEscape);
});
watch(() => route.fullPath, () => { isAccountMenuOpen.value = false; });

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
