<template>
    <div>
        <AppHeader></AppHeader>
        <div class="lyt-centertop">
            <div class="wrp-centertop">

                <form class="ctn-chidform ctn-formcard" @submit.prevent="findUserId">
                    <h1 class="txt-def0">아이디 찾기</h1>

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
                    <button class="btn-default" type="submit">아이디 찾기</button>
                </form>
                
                <form class="ctn-chidform ctn-formcard" @submit.prevent="findUserPassword">
                    <h1 class="txt-def0">비밀번호 찾기</h1>

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
                        <label class="ipt-default-label">아이디</label>
                        <input class="ipt-default" v-model="userId" type="userId" placeholder="아이디를 입력해 주세요." />
                    </div>
                    <button class="btn-default" type="submit">비밀번호 찾기</button>
                </form>
                
                <form class="ctn-chidform ctn-formcard" @submit.prevent="activateAccount">
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
import { useAccountStore } from '@/stores/accountStore';
import { ref } from "vue";
import { useToast } from "vue-toastification";
import { useRouter } from "vue-router";

const router = useRouter();
const accountStore = useAccountStore();
const toast = useToast();

const role = ref("");
const userEmail = ref("");
const userId = ref("");

// 아이디 찾기 
const findUserId = async () => {
    if (!role.value) {
        toast.error("회원 유형을 선택해주세요.");
        return;
    }
    const req = {
        role: role.value,
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
    if (!role.value) {
        toast.error("회원 유형을 선택해주세요.");
        return;
    }
    const req = {
        role: role.value,
        userId: userId.value
    }
    const res = await accountStore.findUserPassword(req)
    if (res.success) {
        toast.success(res.message)
    } else {
        toast.error(res.message)
    }
}

// 계정 활성화
const activateAccount = async () => {
    if (!role.value) {
        toast.error("회원 유형을 선택해주세요.");
        return;
    }
    const req = {
        role: role.value,
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
