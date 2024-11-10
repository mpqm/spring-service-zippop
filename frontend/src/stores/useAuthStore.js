import { defineStore } from "pinia";
import axios from "axios";
import { backend } from "@/const";

export const useAuthStore = defineStore("auth", {
    state: () => ({ isLoggedIn: false }),
    persist: { storage: sessionStorage, },
    actions: {
        async signup(req) {
            try{
                const res = await axios.post(
                    `${backend}/auth/signup`, req, 
                    { headers: { 'Content-Type': 'multipart/form-data' }, }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
        async login(req) {
            const request_url = backend + "/auth/login"
            console.log(request_url)
            let response = await axios.post("/api/api/v1" + "/auth/login", req);
            if (response.status === 200) {
                this.isLoggedIn = true;
                return true;
            } else {
                return false;
            }
        },
        async logout() {
            let response = await axios.post(backend + "/auth/logout");
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