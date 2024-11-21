import { defineStore } from "pinia";
import axios from "axios";
import { backend } from "@/env";

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

        // 회원가입
        async signup(req) {
            try {
                const res = await axios.post(
                    `${backend}/auth/signup`, req,
                    { headers: { 'Content-Type': 'multipart/form-data' }, }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },

        // 로그인
        async login(req) {
            try {
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

        // 로그아웃
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

        // 유저정보 로드
        async getInfo() {
            try {
                const res = await axios.get(
                    `${backend}/auth/get-info`,
                    { headers: { 'Content-Type': 'application/json', }, withCredentials: true }
                );
                this.userInfo = res.data.result;
                return res.data;
            } catch (error) {
                if (error.response.status == 401) {
                    await this.logout();
                }
                return error.response.data
            }
        },

        // 유저정보 수정
        async editInfo(req) {
            try {
                const res = await axios.patch(
                    `${backend}/auth/edit-info`, req,
                    { headers: { 'Content-Type': 'multipart/form-data' }, withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },

        // 패스워드 수정
        async editPassword(req) {
            try {
                const res = await axios.patch(
                    `${backend}/auth/edit-password`, req,
                    { headers: { 'Content-Type': 'application/json' }, withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },

        // 아이디 찾기
        async findId(req) {
            try {
                const res = await axios.post(
                    `${backend}/auth/find-id`, req,
                    { withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },

        // 비밀번호 찾기
        async findPw(req) {
            try {
                const res = await axios.post(
                    `${backend}/auth/find-password`, req,
                    { withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
    },
});