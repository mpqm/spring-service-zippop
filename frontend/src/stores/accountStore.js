import { defineStore } from "pinia";
import axios from "axios";
import { BACKEND_URL } from "@/config";
import { useAuthStore } from "@/stores/authStore";

export const useAccountStore = defineStore("account", {
  state: () => ({
    userInfo: {
      userId: "",
      email: "",
      name: "",
      role: "",
      address: "",
      point: "",
      phoneNumber: "",
      crn: "",
      profileImageUrl: "",
    },
  }),
  persist: { storage: sessionStorage },
  actions: {
    // 회원가입
    async createAccount(req) {
      try {
        const res = await axios.post(`${BACKEND_URL}/accounts`, req);
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 내 정보 조회
    async getAccount() {
      try {
        const res = await axios.get(`${BACKEND_URL}/accounts/me`, { withCredentials: true,});
        this.userInfo = res.data.result || {};
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 유저정보 수정
    async updateAccount(req) {
      try {
        const res = await axios.patch(`${BACKEND_URL}/accounts/me`, req, { withCredentials: true });
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 비밀번호 수정
    async resetPassword(req) {
      try {
        const res = await axios.patch(`${BACKEND_URL}/accounts/password/reset`, req, { withCredentials: true });
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 아이디 찾기
    async findUserId(req) {
      try {
        const res = await axios.post(`${BACKEND_URL}/accounts/id/find`, req);
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 비밀번호 찾기
    async findUserPassword(req) {
      try {
        const res = await axios.post(`${BACKEND_URL}/accounts/password/find`, req );
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 계정 비활성화
    async deactivateAccount() {
      try {
        const res = await axios.delete(`${BACKEND_URL}/accounts/me`, { withCredentials: true, });
        const authStore = useAuthStore();
        await authStore.logout();
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 계정 활성화
    async activateAccount(req) {
      try {
        const res = await axios.post( `${BACKEND_URL}/accounts/me/activation`, req, { withCredentials: true });
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },
  },
});
