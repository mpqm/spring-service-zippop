import { defineStore } from 'pinia';
import axios from 'axios';
import { backend } from '@/env';

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartList: [],
    cartItemList: [],
    totalElements: null,
    totalPages: null,
  }),
  persist: { storage: sessionStorage },
  actions: {

    // 장바구니 등록
    async register(req) {
      try {
        const res = await axios.post(
          `${backend}/cart/register`, req,
          { withCredentials: true }
        );
        return res.data;
      } catch (error) {
        return error.response.data;
      }
    },

    // 장바구니 목록 조회
    async searchAll(page, size) {
      try {
        const res = await axios.get(
          `${backend}/cart/search-all?page=${page}&size=${size}`,
          { withCredentials: true }
        );
        this.cartList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages; 
        return res.data;
      } catch (error) {
        return error.response.data
      }
    },

    // 장바구니 아이템 목록 조회
    async itemSearchAll(storeIdx) {
      try {
        const res = await axios.get(
          `${backend}/cart/item/search-all?storeIdx=${storeIdx}`,
          { withCredentials: true }
        );
        this.cartItemList = res.data.result;
        return res.data;
      } catch (error) {
        return error.response.data
      }
    },

    // 장바구니 아에팀 수량 조절
    async itemCount(cartItemIdx, operation) {
      try {
        const res = await axios.get(
          `${backend}/cart/item/count?cartItemIdx=${cartItemIdx}&operation=${operation}`,
          { withCredentials: true }
        );
        return res.data;
      } catch (error) {
        return error.response.data
      }
    },

    // 장바구니 아이템 삭제
    async deleteCartItem(cartItemIdx) {
      try {
        const res = await axios.delete(
          `${backend}/cart/item/delete?cartItemIdx=${cartItemIdx}`,
          { withCredentials: true }
        );
        return res.data
      } catch (error) {
        return error.response.data
      }
    },

    // 장바구니 아이템 전체 삭제
    async deleteAllCartItems(storeIdx) {
      try {
        const res = await axios.delete(
          `${backend}/cart/item/delete-all?storeIdx=${storeIdx}`,
          { withCredentials: true }
        );
        this.cartItemList = [];
        return res.data
      } catch (error) {
        return error.response.data
      }
    },
  }
});
