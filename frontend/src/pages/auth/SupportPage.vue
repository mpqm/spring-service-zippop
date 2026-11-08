<template>
    <div>
        <AppHeader></AppHeader>
        <div class="lyt-centertop">
            <div class="wrp-centertop">
                <button class="btn-authback" type="button" aria-label="뒤로가기" title="뒤로가기" @click="router.back()">
                    <Icon icon="iconoir:nav-arrow-left" width="20" height="20" />
                    <span>뒤로가기</span>
                </button>

                <form class="ctn-chidform ctn-formcard" @submit.prevent="findUserId">
                    <h1 class="txt-def0">아이디 찾기</h1>

                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">이메일</label>
                        <input class="ipt-default" v-model="userEmail" type="email" placeholder="이메일을 입력해 주세요." />
                    </div>
                    <button class="btn-default" type="submit">아이디 찾기</button>
                </form>
                
                <form class="ctn-chidform ctn-formcard" @submit.prevent="findUserPassword">
                    <h1 class="txt-def0">비밀번호 찾기</h1>

                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">아이디</label>
                        <input class="ipt-default" v-model="userId" type="userId" placeholder="아이디를 입력해 주세요." />
                    </div>
                    <button class="btn-default" type="submit">비밀번호 찾기</button>
                </form>
                
                <form class="ctn-chidform ctn-formcard" @submit.prevent="activateAccount">
                    <h1 class="txt-def0">계정 활성화</h1>

                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">이메일</label>
                        <input class="ipt-default" v-model="userEmail" type="email" placeholder="이메일을 입력해 주세요." />
                    </div>
                    
                    <button class="btn-default" type="submit">계정 활성화</button>
                </form>
                
            </div>
        </div>
        <AppFooter></AppFooter>
    </div>
</template>

<script setup>
import AppFooter from "@/components/AppFooter.vue";
import AppHeader from '@/components/AppHeader.vue';
import { useAccountStore } from '@/stores/accountStore';
import { ref } from "vue";
import { useToast } from "vue-toastification";
import { useRouter } from "vue-router";

const router = useRouter();
const accountStore = useAccountStore();
const toast = useToast();

const userEmail = ref("");
const userId = ref("");

// 아이디 찾기 
const findUserId = async () => {
    const req = {
        email: userEmail.value
    }
    const res = await accountStore.findUserId(req)
    if (res.success) {
        toast.success(res.message)
    } else {
        toast.error(res.message)
    }
}

// 비밀번호 찾기 
const findUserPassword = async () => {
    const req = {
        userId: userId.value
    }
    const res = await accountStore.findUserPassword(req)
    if (res.success) {
        toast.success("가입 이메일로 비밀번호 재설정 링크를 보냈습니다.")
    } else {
        toast.error(res.message)
    }
}

// 계정 활성화
const activateAccount = async () => {
    const req = {
        email: userEmail.value
    }
    const res = await accountStore.activateAccount(req)
    if (res.success) {
        toast.success(res.message)
    } else {
        toast.error(res.message)
    }
}
</script>
