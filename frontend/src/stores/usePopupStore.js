import { defineStore } from "pinia";
import axios from "axios";

import { backend } from "@/const";

// 전역 저장소 생성
export const usePopupStore = defineStore("popupstore", {
  state: () => ({ isLoggedIn: false, dataList: [] }),
  persist: {
    storage: sessionStorage,
  },
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
    async searchAll() {
      try {
        let response = await axios.get(backend + "/popup-store/search-all?page=0&size=10");
        if (response.status === 200) {
          this.dataList = response.data.result;
          console.log(this.dataList);
        } else {
          return false;
        }
      } catch (error) {
        console.error("Error", error);
        return false;
      }
    },
  },
});
