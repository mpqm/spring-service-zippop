<template>
    <div>
        <AppHeader></AppHeader>
        <div class="lyt-centertop">
            <div class="wrp-centertop">
                <form class="ctn-rootform ctn-formcard" @submit.prevent="login">
                    <img class="img-mainlogo" src="../../assets/img/zippoicon-removebg.png" alt="ZIPPOP">
                    
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">아이디</label>
                        <input class="ipt-default" v-model="userId" type="text" name="username" autocomplete="username" required placeholder="아이디를 입력해 주세요." />
                    </div>

                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">비밀번호</label>
                        <input class="ipt-default" v-model="password" type="password" name="password" autocomplete="current-password" required placeholder="비밀번호를 입력해 주세요." />
                    </div>

                    <div class="ctn-loginactions">
                        <button class="btn-default btn-loginicon" type="submit" :disabled="isSubmitting" aria-label="로그인" title="로그인">
                            <Icon :icon="isSubmitting ? 'iconoir:refresh-double' : 'iconoir:log-in'" width="24" height="24" />{{ isSubmitting ? '로그인 중...' : '로그인' }}
                        </button>
                        <router-link class="btn-default btn-loginicon" to="/signup" aria-label="회원가입" title="회원가입">
                            <Icon icon="iconoir:user-plus" width="24" height="24" />회원가입
                        </router-link>
                        <router-link class="btn-default btn-loginicon" to="/support" aria-label="계정지원" title="계정지원">
                            <Icon icon="iconoir:help-circle" width="24" height="24" />계정지원
                        </router-link>
                    </div>

                    <div class="ctn-socialactions" aria-label="소셜 로그인">
                        <button class="btn-sociallogin btn-kakao" type="button" aria-label="카카오 로그인" title="카카오 로그인" disabled aria-disabled="true">
                            <span class="ico-socialbrand"><Icon icon="simple-icons:kakaotalk" width="21" height="21" /></span>
                            <span class="txt-socialbrand">카카오</span>
                        </button>
                        <button class="btn-sociallogin btn-google" type="button" aria-label="구글 로그인" title="구글 로그인" disabled aria-disabled="true">
                            <span class="ico-socialbrand"><Icon icon="logos:google-icon" width="21" height="21" /></span>
                            <span class="txt-socialbrand">구글</span>
                        </button>
                        <button class="btn-sociallogin btn-naver" type="button" aria-label="네이버 로그인" title="네이버 로그인" disabled aria-disabled="true">
                            <span class="ico-socialbrand"><Icon icon="simple-icons:naver" width="17" height="17" /></span>
                            <span class="txt-socialbrand">네이버</span>
                        </button>
                    </div>

                </form>
            </div>
        </div>
        <AppFooter></AppFooter>
    </div>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue';
import AppFooter from "@/components/AppFooter.vue";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from '@/stores/authStore';
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();

const userId = ref("");
const password = ref("");
const isSubmitting = ref(false);

// onMounted 
onMounted(async () => { 
    await emailVerify(); 
})

// 이메일 인증 링크 리다이렉션시 
const emailVerify = async () => {
    const query = router.currentRoute.value.query;
    if (query.success === "true") {
        toast.success("이메일 인증에 성공했습니다.");
    } else if (query.error === "true") {
        toast.error("이메일 인증에 실패했습니다. 다시 시도해주세요.");
    } else if (query.reason === 'auth') {
        toast.error('로그인이 필요합니다.');
    }
}

// 로그인 
const login = async () => {
    if (isSubmitting.value) {
        return;
    }

    isSubmitting.value = true;
    try {
        const req = {
            userId: userId.value,
            password: password.value,
        }
        const res = await authStore.login(req);
        if (res?.success === true) {
            const redirect = router.currentRoute.value.query.redirect;
            await router.push(typeof redirect === 'string' && redirect.startsWith('/') ? redirect : '/');
            toast.success(res.message);
        } else {
            toast.error(res?.message || '로그인에 실패했습니다.');
        }
    } finally {
        isSubmitting.value = false;
    }
};

</script>
