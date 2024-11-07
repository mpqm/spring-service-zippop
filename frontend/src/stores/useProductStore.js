import { defineStore } from "pinia";
import axios from "axios";

import { backend } from "@/const";

// 전역 저장소 생성
export const useProductStore = defineStore("product", {
  state: () => ({ isLoggedIn: false }),
  persist: {
    storage: sessionStorage,
  },
  actions: {
    async register(storeIdx, formData) {
      try {
        console.log(backend + "/popup-goods/register?storeIdx=" + storeIdx);
        let response = await axios.post(
          backend + "/register?storeIdx=" + storeIdx,
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          },
          { withCredentials: true }
        );
        return response.status;
      } catch (error) {
        console.error("Error", error);
        return false;
      }
    },
  },
});
