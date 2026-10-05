<template>
    <div>
        <AppHeader></AppHeader>
        <div class="lyt-centertop">
            <div class="wrp-centertop">
                <form class="ctn-rootform ctn-formcard" @submit.prevent="login">
                    <img class="img-mainlogo" src="../../assets/img/zippoicon-removebg.png" alt="ZIPPOP">
                    
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">아이디</label>
                        <input class="ipt-default" v-model="userId" type="id" placeholder="아이디를 입력해 주세요." />
                    </div>

                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">비밀번호</label>
                        <input class="ipt-default" v-model="password" type="password" placeholder="비밀번호를 입력해 주세요." />
                    </div>

                    <div class="ctn-split">
                        <a class="lnk-default" href="/support">계정지원</a>
                        <label><input type="checkbox" /> 자동 로그인 </label>
                    </div>

                    <button class="btn-default" type="submit" :disabled="isSubmitting">
                        {{ isSubmitting ? "로그인 중..." : "로그인" }}
                    </button>
                    <a class="btn-default" href="/signup">회원가입</a>
                    <button class="btn-default" type="button"><img src="../../assets/img/social-login-kakao.png" alt="카카오" />카카오로 시작하기 </button>

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
