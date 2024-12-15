import { defineStore } from "pinia";
import axios from "axios";

import { backend } from "@/env";

// 전역 저장소 생성
export const useSettlementStore = defineStore("settlement", {
  state: () => ({
    settlementList: [],
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage, },
  actions: {

    // 정산 내역 조회
    async searchAllSettlement(storeIdx, page, size) {
      try {
        const res = await axios.get(
          `${backend}/settlement/search?storeIdx=${storeIdx}&page=${page}&size=${size}`,
          { withCredentials: true },
        );
        this.settlementList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
  },
});
