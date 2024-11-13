import { defineStore } from "pinia";
import axios from "axios";
import { backend } from "@/const";

export const useAuthStore = defineStore("auth", {
    state: () => ({ 
        userInfo: {
            email: '',
            name: '',
            role: '',
            profileImageUrl: '',
        },
        isLoggedIn: false 
    }),
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
            try{
                const res = await axios.post(
                    `${backend}/auth/login`, req,
                    { headers: { 'Content-Type': 'application/json' }, }
                );
                this.isLoggedIn = true;
                await this.getInfo()
                return res.data;
            } catch (error) {
                this.isLoggedIn = false;
                return error.response.data
            }
        },
        async logout() {
            try {
                const res = await axios.post(backend + "/auth/logout");
                this.userInfo.email = null;
                this.userInfo.name = null;
                this.userInfo.role = null;
                this.userInfo.profileImageUrl = null;
                this.isLoggedIn = false;
                return res.data;
            } catch (error) {
                return error.response.data   
            }
        },
        async getInfo() {
            try{
                const res = await axios.get(
                    `${backend}/auth/get-info`, 
                    { headers: { 'Content-Type': 'application/json', }, withCredentials: true}
                );
                this.userInfo.email = res.data.result.email;
                this.userInfo.name = res.data.result.name;
                this.userInfo.role = res.data.result.role;
                this.userInfo.profileImageUrl = res.data.result.profileImageUrl;
                return res.data
            } catch (error) {
                if (error.response.status == 401) {
                    await this.logout();
                }
                return error.response.data
            }
        }
    },
});