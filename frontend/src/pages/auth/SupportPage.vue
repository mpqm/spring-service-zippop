<template>
    <div>
        <AppHeader></AppHeader>
        <div class="lyt-centertop">
            <div class="wrp-centertop">

                <form class="ctn-chidform" @submit.prevent="findId">
                    <h1 class="txt-def0">아이디 찾기</h1>
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">이메일</label>
                        <input class="ipt-default" v-model="userEmail" type="email" placeholder="이메일을 입력해 주세요." />
                    </div>
                    <button class="btn-default" type="submit">아이디 찾기</button>
                </form>
                
                <form class="ctn-chidform" @submit.prevent="findPw">
                    <h1 class="txt-def0">비밀번호 찾기</h1>
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">아이디</label>
                        <input class="ipt-default" v-model="userId" type="userId" placeholder="아이디를 입력해 주세요." />
                    </div>
                    <button class="btn-default" type="submit">비밀번호 찾기</button>
                </form>
                
                <form class="ctn-chidform" @submit.prevent="active">
                    <h1 class="txt-def0">계정 활성화</h1>
                    
                    <div class="ctn-checkbox">
                        <label class="lbl-checkbox">
                            <input type="radio" value="ROLE_COMPANY" v-model="role" />
                            기업회원
                        </label>
                        <label class="lbl-checkbox">
                            <input type="radio" value="ROLE_CUSTOMER" v-model="role" />
                            고객회원
                        </label>
                    </div>

                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">이메일</label>
                        <input class="ipt-default" v-model="userEmail" type="email" placeholder="이메일을 입력해 주세요." />
                    </div>
                    
                    <button class="btn-default" type="submit">계정 활성화</button>
                </form>
                
                <button class="btn-default" type="button" @click="router.back()">뒤로가기</button>

            </div>
        </div>
        <AppFooter></AppFooter>
    </div>
</template>

<script setup>
import AppFooter from "@/components/AppFooter.vue";
import AppHeader from '@/components/AppHeader.vue';
import { useAuthStore } from '@/stores/useAuthStore';
import { ref } from "vue";
import { useToast } from "vue-toastification";
import { useRouter } from "vue-router";

// store, router, route, toast
const router = useRouter();
const role = ref("");   // 단일 선택값(문자열)


// store, router, route, toast
const authStore = useAuthStore();
const toast = useToast();

// 변수(auth)
const userEmail = ref("");
const userId = ref("");

// 아이디 찾기 
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

// 비밀번호 찾기 
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