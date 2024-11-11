import { defineStore } from "pinia";
import axios from "axios";

import { backend } from "@/const";

// 전역 저장소 생성
export const useStoreStore = defineStore("store", {
  state: () => ({
    storeList: [],
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage, },
  actions: {
    async register(formData) {
      try {
        let response = await axios.post(
          backend + "/popup-store/register",
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          },
          { withCredentials: true }
        );
        console.log(response);
      } catch (error) {
        console.error("Error", error);
        return false;
      }
    },
    async searchAll(page, size) {
      try {
        const res = await axios.get(
          `${backend}/store/search-all/as-guest?page=${page}&size=${size}`
        );
        this.storeList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async searchAllByKeyword(keyword, page, size) {
      try {
        const res = await axios.get(
          `${backend}/store/search-all/as-guest?keyword=${keyword}&page=${page}&size=${size}`
        );
        this.storeList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
  },
});
