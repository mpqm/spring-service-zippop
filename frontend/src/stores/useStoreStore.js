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

    // 스토어 등록
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

    // 스토어 단일 조회(스토어 인덱스)
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

    // 스토어 목록 조회(flag = 스토어 마감 여부)
    async searchAllStore(status, page, size) {
      try {
        const res = await axios.get(
          `${backend}/store/search-all?status=${status}&page=${page}&size=${size}`
        );
        this.storeList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },

    // 스토어 목록 조회(flag = 스토어 마감 여부, 키워드 검색)
    async searchAllStoreByKeyword(status, keyword, page, size) {
      try {
        const res = await axios.get(
          `${backend}/store/search-all?status=${status}&keyword=${keyword}&page=${page}&size=${size}`
        );
        this.storeList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },

    // 스토어 목록 조회(기업 등록)
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

    // 스토어 목록 조회(기업 등록, 키워드 검색)
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

    // 스토어 수정
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

    // 스토어 삭제
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

    // 스토어 좋아요 등록
    async registerLike(storeIdx) {
      try {
        const res = await axios.get(
          `${backend}/store/like/register?storeIdx=${storeIdx}`, 
          {withCredentials: true,}
        );
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    
    // 스토어 좋아요 목록 조회
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

    // 스토어 리뷰 등록
    async registerReview(storeIdx, req){
      try {
        const res = await axios.post(
          `${backend}/store/review/register?storeIdx=${storeIdx}`, req,
          { withCredentials: true },
        );
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },

    // 스토어 리뷰 목록 조회(전체)
    async searchAllReview(storeIdx, page, size){
      try {
        const res = await axios.get(
          `${backend}/store/review/search-all?storeIdx=${storeIdx}&page=${page}&size=${size}`,
        );
        this.reviewList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }      
    },

    // 스토어 리뷰 목록 조회(고객)
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
