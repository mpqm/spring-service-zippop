import { defineStore } from "pinia";
import axios from "axios";

import { backend } from "@/const";

// 전역 저장소 생성
export const useStoreStore = defineStore("store", {
  state: () => ({
    storeList: [],
    store: {},
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage, },
  actions: {
    async register(req) {
      try {
        const res = await axios.post(
          `${backend}/store/register`, req,
          {
            headers: { "Content-Type": "multipart/form-data", },
            withCredentials: true
          },
        );
        return res.data
      } catch (error) {
        return error.response.data;
      }
    },
    async search(storeIdx) {
      try {
        const res = await axios.get(
          `${backend}/store/search?storeIdx=${storeIdx}`
        );
        this.store = res.data.result;
        return res.data;
      } catch (error) {
        return error.response.data;
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
    async searchAllAsCompany(page, size) {
      try {
        const res = await axios.get(
          `${backend}/store/search-all/as-company?page=${page}&size=${size}`,
          { withCredentials: true },
        );
        this.storeList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async searchAllByKeywordAsCompany(keyword, page, size) {
      try {
        const res = await axios.get(
          `${backend}/store/search-all/as-company?keyword=${keyword}&page=${page}&size=${size}`,
          { withCredentials: true },
        );
        this.storeList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async update(storeIdx, req) {
      try {
        const res = await axios.patch(
          `${backend}/store/update?storeIdx=${storeIdx}`, req,
          {
            headers: { "Content-Type": "multipart/form-data", },
            withCredentials: true
          },
        );
        return res.data
      } catch (error) {
        return error.response.data;
      }
    },
    async delete(storeIdx) {
      try {
        const res = await axios.delete(
          `${backend}/store/delete?storeIdx=${storeIdx}`,
          { withCredentials: true },
        );
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    }
  },
});
