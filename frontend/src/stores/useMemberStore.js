import { defineStore } from "pinia";
import axios from "axios";
import { backend } from "@/const";

export const useMemberStore = defineStore("member", {
    state: () => ({ isLoggedIn: false }),
    persist: { storage: sessionStorage, },
    actions: {
        async signup(member) {
            try{
                console.log(member)
                const response = await axios.post(backend + "/member/signup", member);
                console.log(response.data)
                if (response.data.success) {
                    alert('이메일 인증 요청이 전송되었습니다. 이메일 인증 후 회원가입이 완료됩니다.');
                } else {
                    alert(response.data.message);
                }
            } catch (error) {
                alert('회원가입 중 오류가 발생했습니다.');
            }
        },
        async login(member) {
            const request_url = backend + "/member/login"
            console.log(request_url)
            let response = await axios.post("/api/api/v1" + "/member/login", member);
            if (response.status === 200) {
                this.isLoggedIn = true;
                return true;
            } else {
                return false;
            }
        },
        async logout() {
            let response = await axios.post(backend + "/member/logout");
            console.log(response);
            if (response.status === 200) {
                this.isLoggedIn = false;
                return true;
            } else {
                return false;
            }
        },
    },
});