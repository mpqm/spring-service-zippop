import { defineStore } from "pinia";
import axios from "axios";
import { BACKEND_URL } from "@/config";

export const useGoodsStore = defineStore("goods", {
  state: () => ({
    goodsList: [],
    goods: {},
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage },
  actions: {

    // 굿즈 등록 - POST /api/v1/goods (req 안에 popupIdx 포함)
    async createGoods(req) {
      try {
        const res = await axios.post(`${BACKEND_URL}/goods`, req, { headers: { "Content-Type": "multipart/form-data" }, withCredentials: true } );
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 굿즈 단일 조회
    async getGoods(goodsIdx) {
      try {
        const res = await axios.get(`${BACKEND_URL}/goods/${goodsIdx}`);
        this.goods = res.data.result;
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 굿즈 목록 조회 - GET /api/v1/goods (전체/검색)
    async getGoodsList(popupIdx, keyword, page, size) {
      try {
        const res = await axios.get(`${BACKEND_URL}/goods?popupIdx=${popupIdx ?? ""}&keyword=${keyword ?? ""}&page=${page}&size=${size}`,);
        this.goodsList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 굿즈 수정
    async updateGoods(goodsIdx, req) {
      try {
        const res = await axios.patch(`${BACKEND_URL}/goods/${goodsIdx}`, req, { headers: { "Content-Type": "multipart/form-data", }, withCredentials: true },);
        return res.data
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 굿즈 삭제
    async deleteGoods(goodsIdx) {
      try {
        const res = await axios.delete(`${BACKEND_URL}/goods/${goodsIdx}`, { withCredentials: true },);
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    }
  },
});
