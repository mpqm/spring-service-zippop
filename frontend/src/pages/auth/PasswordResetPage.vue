<template>
  <div>
    <AppHeader />
    <div class="lyt-centertop">
      <div class="wrp-centertop">
        <form class="ctn-rootform ctn-formcard" @submit.prevent="submit">
          <h1 class="txt-def0">비밀번호 재설정</h1>
          <div class="ctn-inputdefault">
            <label class="ipt-default-label">새 비밀번호</label>
            <input class="ipt-default" v-model="newPassword" type="password" minlength="8" maxlength="72" required />
          </div>
          <div class="ctn-inputdefault">
            <label class="ipt-default-label">새 비밀번호 확인</label>
            <input class="ipt-default" v-model="confirmation" type="password" minlength="8" maxlength="72" required />
          </div>
          <button class="btn-default" type="submit" :disabled="submitting">비밀번호 변경</button>
        </form>
      </div>
    </div>
    <AppFooter />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import AppHeader from '@/components/AppHeader.vue';
import AppFooter from '@/components/AppFooter.vue';
import { useAccountStore } from '@/stores/accountStore';

const route = useRoute();
const router = useRouter();
const toast = useToast();
const accountStore = useAccountStore();
const newPassword = ref('');
const confirmation = ref('');
const submitting = ref(false);

const submit = async () => {
  const token = typeof route.query.token === 'string' ? route.query.token : '';
  if (!token) { toast.error('유효하지 않은 비밀번호 재설정 링크입니다.'); return; }
  if (newPassword.value !== confirmation.value) { toast.error('비밀번호 확인이 일치하지 않습니다.'); return; }
  submitting.value = true;
  try {
    const result = await accountStore.completePasswordReset({ token, newPassword: newPassword.value });
    if (!result.success) { toast.error(result.message || '비밀번호를 변경하지 못했습니다.'); return; }
    toast.success('비밀번호를 변경했습니다. 다시 로그인해주세요.');
    await router.replace('/login');
  } finally {
    submitting.value = false;
  }
};
</script>
