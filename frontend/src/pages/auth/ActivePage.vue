<template>
    <div>
        <HeaderComponent></HeaderComponent>
        <div class="active-page">
            <div class="active-container">
                <h1>계정활성화</h1>
                <form class="active-form" @submit.prevent="active">
                    
                    <div class="checkbox-group">
                        <label>
                            <input type="radio" value="ROLE_COMPANY" v-model="role" />
                            기업회원
                        </label>
                        <label>
                            <input type="radio" value="ROLE_CUSTOMER" v-model="role" />
                            고객회원
                        </label>
                    </div>

                    <div>
                        <label>이메일</label>
                        <input class="active-input" v-model="userEmail" type="email" placeholder="이메일을 입력해 주세요." />
                    </div>
                    
                    <button class="active-btn" type="submit">아이디 찾기</button>
                </form>
            </div>
        </div>
        <FooterComponent></FooterComponent>
    </div>
</template>

<script setup>
import FooterComponent from "@/components/common/FooterComponent.vue";
import HeaderComponent from '@/components/common/HeaderComponent.vue';
import { useAuthStore } from '@/stores/useAuthStore';
import { ref } from "vue";
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const toast = useToast();

const userEmail = ref("");
const role = ref("");   // 단일 선택값(문자열)

// 계정 활성화
const active = async () => {
    if (!role.value) {
        toast.error("회원 유형을 선택해주세요.");
        return;
    }
    const req = {
        role: role.value,
        email: userEmail.value
    }
    const res = await authStore.active(req)
    if (res.success) {
        toast.success(res.message)
    } else {
        toast.error(res.message)
    }
}
</script>

<style scoped>
.active-page {
    padding: 3rem 0;
    background-color: #fff;
}

.active-container {
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

.active-form {
    display: flex;
    margin-top: 0em;
    unicode-bidi: isolate;
    flex-wrap: wrap;
    flex-direction: column;
    justify-content: center;
    row-gap: 1rem;
}

.active-btn {
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

.active-btn:hover {
    opacity: 0.8;
}

.active-input {
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
</style>