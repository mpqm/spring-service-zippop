import { defineStore } from "pinia";
import axios from "axios";

import { backend } from "@/const";

// 전역 저장소 생성
export const useGoodsStore = defineStore("goods", {
  state: () => ({ 
    goodsList: [],
    goods: {},
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage, },
  actions: {
    async register(storeIdx, req) {
      try {
        const res = await axios.post(
          `${backend}/goods/register?storeIdx=${storeIdx}`, req,
          { headers: { "Content-Type": "multipart/form-data", }, withCredentials: true }
        );
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async search(goodsIdx) {
      try {
        const res = await axios.get(
          `${backend}/goods/search?goodsIdx=${goodsIdx}`
        );
        this.goods = res.data.result;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async searchAll(page, size) {
      try {
        const res = await axios.get(
          `${backend}/goods/search-all?page=${page}&size=${size}`
        );
        this.goodsList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async searchAllByStoreIdx(storeIdx, page, size) {
      try {
        const res = await axios.get(
          `${backend}/goods/search-all?storeIdx=${storeIdx}&page=${page}&size=${size}`
        );
        this.goodsList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async searchAllByKeywordAndStoreIdx(keyword, storeIdx, page, size) {
      try {
        const res = await axios.get(
          `${backend}/goods/search-all?keyword=${keyword}&storeIdx=${storeIdx}&page=${page}&size=${size}`
        );
        this.goodsList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },
    async update(goodsIdx, req) {
      try {
        const res = await axios.patch(
          `${backend}/goods/update?goodsIdx=${goodsIdx}`, req,
          { headers: { "Content-Type": "multipart/form-data", }, withCredentials: true },
        );
        return res.data
      } catch (error) {
        return error.response.data;
      }
    },
    async delete(goodsIdx) {
      try {
        const res = await axios.delete(
          `${backend}/goods/delete?goodsIdx=${goodsIdx}`,
          { withCredentials: true },
        );
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    }
  },

});
