import { defineStore } from "pinia";
import axios from "axios";

import { backend } from "@/env";

// 전역 저장소 생성
export const useStoreStore = defineStore("store", {
  state: () => ({
    storeList: [],
    store: {},
    likeList: {},
    reviewList: [],
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage, },
  actions: {
    async registerStore(req) {
      try {
        const res = await axios.post(
          `${backend}/store/register`, req,
          { headers: { "Content-Type": "multipart/form-data", }, withCredentials: true },
        );
        return res.data
      } catch (error) {
        return error.response.data;
      }
    },
    async searchStore(storeIdx) {
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
    async searchAllStore(page, size) {
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
    async searchAllStoreByKeyword(keyword, page, size) {
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
    async searchAllStoreAsCompany(page, size) {
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
    async searchAllStoreByKeywordAsCompany(keyword, page, size) {
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
    async updateStore(storeIdx, req) {
      try {
        const res = await axios.patch(
          `${backend}/store/update?storeIdx=${storeIdx}`, req,
          { headers: { "Content-Type": "multipart/form-data", }, withCredentials: true },
        );
        return res.data
      } catch (error) {
        return error.response.data;
      }
    },
    async deleteStore(storeIdx) {
      try {
        const res = await axios.delete(
          `${backend}/store/delete?storeIdx=${storeIdx}`,
          { withCredentials: true },
        );
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async registerLike(storeIdx) {
      try {
        const res = await axios.get(
          `${backend}/store/like?storeIdx=${storeIdx}`, 
          {withCredentials: true,}
        );
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async searchAllLike(page, size) {
      try {
        const res = await axios.get(
          `${backend}/store/like/search-all?page=${page}&size=${size}`,
          {withCredentials: true,}
        );
        this.likeList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async registerReview(storeIdx, req){
      try {
        const res = await axios.post(
          `${backend}/store/review/registe?storeIdx=${storeIdx}`, req,
          { withCredentials: true },
        );
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async searchAllReview(storeIdx, page, size){
      try {
        const res = await axios.get(
          `${backend}/store/review/search-all/as-guest?storeIdx=${storeIdx}&page=${page}&size=${size}`,
        );
        this.reviewList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }      
    },
    async searchAllReviewAsCustomer(page, size){
      try {
        const res = await axios.get(
          `${backend}/store/review/search-all/as-customer?&page=${page}&size=${size}`,
          { withCredentials: true },
        );
        this.reviewList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }      
    }
  },
});
