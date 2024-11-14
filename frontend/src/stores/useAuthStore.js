import { defineStore } from "pinia";
import axios from "axios";
import { backend } from "@/const";

export const useAuthStore = defineStore("auth", {
    state: () => ({ 
        userInfo: {
            userId: '',
            email: '',
            name: '',
            role: '',
            address: '',
            point: '',
            phoneNumber: '',
            crn: '',
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
                this.userInfo.userId = null;
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
                this.userInfo.userId = res.data.result.userId;
                this.userInfo.email = res.data.result.email;
                this.userInfo.name = res.data.result.name;
                this.userInfo.role = res.data.result.role;
                this.userInfo.profileImageUrl = res.data.result.profileImageUrl;
                this.userInfo.address = res.data.result.address;
                this.userInfo.point = res.data.result.point;
                this.userInfo.phoneNumber = res.data.result.phoneNumber;
                this.userInfo.crn = res.data.result.crn;
                return res.data;
            } catch (error) {
                if (error.response.status == 401) {
                    await this.logout();
                }
                return error.response.data
            }
        },
        async editInfo(req) {
            try{
                const res = await axios.patch(
                    `${backend}/auth/edit-info`, req, 
                    { headers: { 'Content-Type': 'multipart/form-data' }, withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
        async editPassword(req) {
            try{
                const res = await axios.patch(
                    `${backend}/auth/edit-password`, req, 
                    { headers: { 'Content-Type': 'application/json' }, withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
        async inActive() {
            try{
                const res = await axios.get(
                    `${backend}/auth/inactive`,
                    { withCredentials: true }
                );
                this.logout()
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
    },
});