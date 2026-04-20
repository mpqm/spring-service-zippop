import { defineStore } from "pinia";
import axios from "axios";
import { BACKEND_URL } from "@/config";

// 전역 저장소 생성
export const usePayoutStore = defineStore("payout", {
  state: () => ({
    payout: [],
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage },
  actions: {
    // 정산 내역 조회
    async getCompanyPopupPayouts(popupIdx, page, size) {
      try {
        const res = await axios.get(`${BACKEND_URL}/company/popups/${popupIdx}/payouts?page=${page}&size=${size}`, { withCredentials: true },);
        this.payout = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },
  },
});
