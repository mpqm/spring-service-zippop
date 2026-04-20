import { defineStore } from "pinia";
import axios from "axios";

import { BACKEND_URL } from "@/config";

// 전역 저장소 생성
export const usePopupStore = defineStore("popup", {
  state: () => ({
    popupList: [],
    popup: {},
    likeList: [],
    reviewList: [],
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage },

  actions: {

    // 팝업 등록
    async createPopup(req) {
      try {
        const res = await axios.post(`${BACKEND_URL}/popups`, req, { headers: { "Content-Type": "multipart/form-data", }, withCredentials: true },);
        return res.data
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 팝업 단일 조회(팝업 인덱스)
    async getPopup(popupIdx) {
      try {
        const res = await axios.get(`${BACKEND_URL}/popups/${popupIdx}`);
        this.popup = res.data.result;
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 팝업 목록 조회(flag = 팝업 마감 여부, 키워드 검색)
    async getPopups(status, keyword, page, size) {
      try {
        const res = await axios.get(`${BACKEND_URL}/popups?status=${status}&keyword=${keyword ?? ""}&page=${page}&size=${size}`);
        this.popupList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 팝업 목록 조회(기업 등록, 키워드 검색) - GET /api/v1/company/popups
    async getCompanyPopups(keyword, page, size) {
      try {
        const res = await axios.get(`${BACKEND_URL}/company/popups?keyword=${keyword ?? ""}&page=${page}&size=${size}`, { withCredentials: true },);
        this.popupList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 팝업 수정
    async updatePopup(popupIdx, req) {
      try {
        const res = await axios.patch(`${BACKEND_URL}/popups/${popupIdx}`, req, { headers: { "Content-Type": "multipart/form-data", }, withCredentials: true },);
        return res.data
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 팝업 삭제
    async deletePopup(popupIdx) {
      try {
        const res = await axios.delete(`${BACKEND_URL}/popups/${popupIdx}`, { withCredentials: true },);
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 팝업 좋아요 등록/취소
    async togglePopupLike(popupIdx) {
      try {
        const res = await axios.post(`${BACKEND_URL}/popups/${popupIdx}/likes`, {}, {withCredentials: true,});
        const index = this.likeList.findIndex(popup => popup.popupIdx === popupIdx);
        if (index > -1) {
          this.likeList.splice(index, 1);
        } else {
          const currentPopup = this.popupList.find(popup => popup.popupIdx === popupIdx) || this.popup;
          if (currentPopup && currentPopup.popupIdx === popupIdx) {
            this.likeList.push(currentPopup);
          }
        }
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },
    
    // 팝업 좋아요 목록 조회 (전체 목록)
    async getMyLikedPopups(page, size) {
      try {
        const res = await axios.get(`${BACKEND_URL}/popups/likes/me?page=${page}&size=${size}`, {withCredentials: true,} );
        this.likeList = res.data.result.content || [];
        return res.data;
      } catch (error) {
        this.likeList = [];
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 팝업 리뷰 등록
    async createPopupReview(popupIdx, req){
      try {
        const res = await axios.post(`${BACKEND_URL}/popups/${popupIdx}/reviews`, req, { withCredentials: true },);
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 팝업 리뷰 목록 조회(전체)
    async getPopupReviews(popupIdx, page, size){
      try {
        const res = await axios.get(`${BACKEND_URL}/popups/${popupIdx}/reviews?page=${page}&size=${size}`,);
        this.reviewList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }      
    },

    // 팝업 리뷰 목록 조회(고객)
    async getMyReviews(page, size){
      try {
        const res = await axios.get(`${BACKEND_URL}/popups/reviews/me?page=${page}&size=${size}`, { withCredentials: true },);
        this.reviewList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages;
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }      
    }
  },
});
