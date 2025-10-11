import { defineStore } from "pinia";
import axios from "axios";

import { backend } from "@/config";

// 전역 저장소 생성
export const usePayoutStore = defineStore("payout", {
  state: () => ({
    payout: [],
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage, },
  actions: {

    // 정산 내역 조회
    async searchAllPayout(storeIdx, page, size) {
      try {
        const res = await axios.get(
          `${backend}/payout/search?storeIdx=${storeIdx}&page=${page}&size=${size}`,
          { withCredentials: true },
        );
        this.payout = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
  },
});
