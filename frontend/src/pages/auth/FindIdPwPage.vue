<template>
    <div>
        <HeaderComponent></HeaderComponent>
        <div class="find-idpw-page">
            <div class="find-idpw-container">
                <h1>ID/PW 찾기</h1>
                <form class="find-idpw-form" @submit.prevent="findId">
                    <div>
                        <label>이메일</label>
                        <input class="find-idpw-input" v-model="userEmail" type="email" placeholder="이메일을 입력해 주세요." />
                    </div>
                    <button class="find-idpw-btn" type="submit">아이디 찾기</button>
                </form>
                <form class="find-idpw-form" @submit.prevent="findPw">
                    <div>
                        <label>아이디</label>
                        <input class="find-idpw-input" v-model="userId" type="userId" placeholder="아이디를 입력해 주세요." />
                    </div>
                    <button class="find-idpw-btn" type="submit">비밀번호 찾기</button>
                </form>
            </div>
        </div>
        <FooterComponent></FooterComponent>
    </div>
</template>

<script setup>
import FooterComponent from "@/components/FooterComponent.vue";
import HeaderComponent from '@/components/HeaderComponent.vue';
import { useAuthStore } from '@/stores/useAuthStore';
import { ref } from "vue";
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const toast = useToast();
const userEmail = ref("");
const userId = ref("");

const findId = async () => {
    const req = {
        email: userEmail.value
    }
    const res = await authStore.findId(req)
    if (res.success) {
        toast.success(res.message)
    } else {
        toast.error(res.message)
    }
}
const findPw = async () => {
    const req = {
        userId: userId.value
    }
    const res = await authStore.findPw(req)
    if (res.success) {
        toast.success(res.message)
    } else {
        toast.error(res.message)
    }
}

</script>

<style scoped>
.find-idpw-page {
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

.auto-find-idpw {
    cursor: pointer;
}

.find-id-pw:hover,
.auto-find-idpw:hover {
    cursor: pointer;
    color: #00c7ae;
}

.find-idpw-container {
    position: relative;
    display: flex;
    gap: 20px;
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

.find-idpw-form {
    display: flex;
    margin-top: 0em;
    unicode-bidi: isolate;
    flex-wrap: wrap;
    flex-direction: column;
    justify-content: center;
    row-gap: 1rem;
}

.find-idpw-btn {
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

.kakao-find-idpw-btn {
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

.find-idpw-btn:hover,
.kakao-find-idpw-btn:hover {
    opacity: 0.8;
}

.find-idpw-input {
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
    box-sizing: border-box;
}

.logo-img {
    align-self: center;
    width: 300px;
    height: 100px;
}
</style>