<template>
<div>
    <HeaderComponent></HeaderComponent>
    <div class="login-page">
        <div class="login-container">
            <form class="login-form" @submit.prevent="login">
                <img class="logo-img" src="../../assets/img/zippopbanner.png">
                <div>
                    <label>이메일</label>
                    <input class="login-input" v-model="email" type="email" placeholder="ex) example@example.com"/>
                </div>
                <div>
                    <label>비밀번호</label>
                    <input class="login-input" v-model="password" type="password" placeholder="비밀번호를 입력해 주세요."/>
                </div>
                <div>
                    <button class="login-btn" type="submit">로그인</button>
                    <a class="login-btn" href="/signup/customer">회원가입</a>
                    <button class="kakao-login-btn">
                        <img src="../../assets/img/social-login-kakao.png" alt="카카오" />&nbsp;카카오 로그인
                    </button>
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
import HeaderComponent from '@/components/HeaderComponent.vue';
import FooterComponent from "@/components/FooterComponent.vue";
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();

const email = ref("");
const password = ref("");

onMounted(async() => { await emailVerify(); })

const emailVerify = async() => {
    const query = router.currentRoute.value.query;
    if (query.success) { 
        toast.success("이메일 인증에 성공했습니다.");
    }
    if (query.error) {
        toast.error("이메일 인증에 실패했습니다. 다시 시도해주세요.");
    }
}

const login = async () => {
    const req = {
        email: email.value,
        password: password.value,
    }
    const res = await authStore.login(req.value);
    if (res.success) {
        router.push("/");
        toast.success("로그인에 성공했습니다.")
    }else{
        toast.error("로그인에 실패했습니다.")
    }
};
</script>

<style scoped>
.login-page {
    padding: 3rem 0;
    background-color: #fafafa;
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
    box-shadow: none;
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
}

.login-btn:hover, .kakao-login-btn:hover {
    opacity: 0.8;
}

.login-input {
    padding: 1rem;
    border: 1px solid #e1e1e1;
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
}

.logo-img {
    align-self: center;
    width: 300px;
    height: 100px;
}

</style>
