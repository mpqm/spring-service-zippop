import { defineStore } from 'pinia';
import axios from 'axios';
import { backend } from '@/env';

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartList: [],
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
    async searchAll() {
      try {
        const res = await axios.get(
          `${backend}/cart/search-all`,
          { withCredentials: true }
        );
        this.cartList = res.data.result;
        return res.data;
      } catch (error) {
        return error.response.data
      }
    },

    // 장바구니 수량 증가
    async count(cartItemIdx, operation) {
      try {
        const res = await axios.get(
          `${backend}/cart/count?cartItemIdx=${cartItemIdx}&operation=${operation}`,
          { withCredentials: true }
        );
        return res.data;
      } catch (error) {
        return error.response.data
      }
    },

    // 장바구니 항목 삭제
    async deleteCartItem(cartItemIdx) {
      try {
        const res = await axios.delete(
          `${backend}/cart/delete?cartItemIdx=${cartItemIdx}`,
          { withCredentials: true }
        );
        return res.data
      } catch (error) {
        return error.response.data
      }
    },

    // 모든 장바구니 항목 삭제
    async deleteAllCartItems() {
      try {
        const res = await axios.delete(
          `${backend}/cart/delete-all`,
          { withCredentials: true }
        );
        this.cartList = [];
        return res.data
      } catch (error) {
        return error.response.data
      }
    },
  }
});
