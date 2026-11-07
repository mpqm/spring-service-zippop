import { defineStore } from "pinia";
import axios from "axios";
import { BACKEND_URL } from "@/config";
import { useAccountStore } from "@/stores/accountStore";
import { usePopupStore } from "@/stores/popupStore";
import { useCartStore } from "@/stores/cartStore";
import { useOrdersStore } from "@/stores/ordersStore";
import { useReserveStore } from "@/stores/reserveStore";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    isLoggedIn: false,
  }),
  persist: { storage: sessionStorage },
  actions: {
    clearSession() {
      this.isLoggedIn = false;
      const accountStore = useAccountStore();
      accountStore.userInfo = {};
      const popupStore = usePopupStore();
      popupStore.likeList = [];
      popupStore.likePageList = [];
      popupStore.likeTotalPages = 0;
      popupStore.likeTotalElements = 0;
      popupStore.likesLoadedCompletely = false;
      useCartStore().$reset();
      useOrdersStore().$reset();
      useReserveStore().$reset();
    },
    // 로그인
    async login(req) {
      try {
        const res = await axios.post(`${BACKEND_URL}/auth/login`, req, {withCredentials: true,});

        // 동일 브라우저에서 계정을 전환할 때 이전 사용자의 상태를 먼저 제거한다.
        this.clearSession();

        // 로그인 후 계정 정보 불러오기
        const accountStore = useAccountStore();
        const accountResponse = await accountStore.getAccount();
        if (!accountResponse.success) {
          this.clearSession();
          return accountResponse;
        }

        this.isLoggedIn = true;
        if (accountStore.userInfo.role === "ROLE_CUSTOMER") {
          await usePopupStore().refreshAllLikedPopups();
        }

        return res.data;
      } catch (error) {
        this.isLoggedIn = false;
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 로그아웃
    async logout() {
      try {
        const res = await axios.post(`${BACKEND_URL}/auth/logout`, {}, { withCredentials: true,});

        this.clearSession();

        return res.data;
      } catch (error) {
        this.clearSession();
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },
  },
});
