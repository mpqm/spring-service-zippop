<template>
    <div>
        <HeaderComponent></HeaderComponent>
        <div class="login-page">
            <div class="login-container">
                <form class="login-form" @submit.prevent="login">
                    <img class="logo-img" src="../../assets/img/zippopbanner.png">
                    <div>
                        <label>아이디</label>
                        <input class="login-input" v-model="userId" type="id" placeholder="아이디를 입력해 주세요." />
                    </div>
                    <div>
                        <label>비밀번호</label>
                        <input class="login-input" v-model="password" type="password" placeholder="비밀번호를 입력해 주세요." />
                    </div>
                    <div class="relate-container">
                        <a class="find-id-pw" href="/find-idpw">ID/PW 찾기</a>
                        <label class="auto-login"><input type="checkbox" /> 자동 로그인 </label>
                    </div>
                    <div>
                        <button class="login-btn" type="submit">로그인</button>
                        <a class="login-btn" href="/signup/customer">회원가입</a>
                        <button class="kakao-login-btn"><img src="../../assets/img/social-login-kakao.png" alt="카카오" />&nbsp;카카오 로그인 </button>
                    </div>
                </form>
            </div>
        </div>
        <FooterComponent></FooterComponent>
    </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from '@/stores/useAuthStore';
import HeaderComponent from '@/components/common/HeaderComponent.vue';
import FooterComponent from "@/components/common/FooterComponent.vue";
import { useToast } from "vue-toastification";

// store, router, route, toast
const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();

// 변수(auth)
const userId = ref("");
const password = ref("");

// onMounted 
onMounted(async () => { 
    await emailVerify(); 
})

// 이메일 인증 링크 리다이렉션시 
const emailVerify = async () => {
    const query = router.currentRoute.value.query;
    if (query.success) {
        toast.success("이메일 인증에 성공했습니다.");
    }
    if (query.error) {
        toast.error("이메일 인증에 실패했습니다. 다시 시도해주세요.");
    }
}

// 로그인 
const login = async () => {
    const req = {
        userId: userId.value,
        password: password.value,
    }
    const res = await authStore.login(req);
    if (res.success) {
        router.push("/");
        toast.success(res.message)
    } else {
        toast.error(res.message)
    }
};

</script>

<style scoped>
.login-page {
    padding: 3rem 0;
    background-color: #fff;
}

.relate-container {
    margin: 0 5px;
    text-align: center;
    display: flex;
    justify-content: space-between;
}

.find-id-pw {
    text-decoration: none;
    color: #000;
    cursor: pointer;
}

.auto-login {
    cursor: pointer;
}

.find-id-pw:hover,
.auto-login:hover {
    cursor: pointer;
    color: #00c7ae;
}

.login-container {
    position: relative;
    display: flex;
    flex-direction: column;
    min-width: 0;
    word-wrap: break-word;
    background-color: #fff;
    background-clip: border-box;
    width: 40%;
    padding: 1rem;
    margin: 0 auto;
    border: 1px solid #f2f2f2;
    border-radius: 8px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.login-form {
    display: flex;
    margin-top: 0em;
    unicode-bidi: isolate;
    flex-wrap: wrap;
    flex-direction: column;
    justify-content: center;
    row-gap: 1rem;
}

.login-btn {
    display: inline-block;
    text-align: center;
    vertical-align: middle;
    width: 100%;
    user-select: none;
    margin-top: 0.75rem;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #00c7ae;
    border-color: #00c7ae;
    border: 0.0625rem solid transparent;
    padding: 0.6875rem 0.75rem;
    font-size: 1rem;
    line-height: 1.5;
    border-radius: 0.25rem;
    text-decoration: none;
    box-sizing: border-box;
}

.kakao-login-btn {
    display: inline-block;
    text-align: center;
    vertical-align: middle;
    width: 100%;
    user-select: none;
    margin-top: 0.75rem;
    font-weight: 400;
    transition: opacity 0.2s ease-in-out;
    color: #fff;
    cursor: pointer;
    background-color: #fee500;
    border-color: #fee500;
    color: rgba(0, 0, 0, .85);
    border: 0.0625rem solid transparent;
    padding: 0.6875rem 0.75rem;
    font-size: 1rem;
    line-height: 1.5;
    border-radius: 0.25rem;
    text-decoration: none;
}

.login-input {
    padding: 1rem;
    border: 1px solid #e2e2e2;
    border-radius: 4px;
    display: block;
    padding: 0.6875rem 1rem;
    font-size: 1rem;
    font-weight: 400;
    line-height: 1.5;
    width: 100%;
    color: #323232;
    background-color: #fff;
    background-clip: padding-box;
    box-sizing: border-box;
}

.logo-img {
    align-self: center;
    width: 300px;
    height: 100px;
}

.login-btn:hover,
.kakao-login-btn:hover {
    opacity: 0.8;
}
</style>
