import { defineStore } from 'pinia';
import axios from 'axios';
import { BACKEND_URL } from "@/config";

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
    async createCart(req) {
      try {
        const res = await axios.post(`${BACKEND_URL}/carts`, req, { withCredentials: true });
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 장바구니 목록 조회
    async getCarts(page, size) {
      try {
        const res = await axios.get(`${BACKEND_URL}/carts?page=${page}&size=${size}`, { withCredentials: true } );
        this.cartList = res.data.result.content;
        this.totalElements = res.data.result.totalElements;
        this.totalPages = res.data.result.totalPages; 
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 장바구니 아이템 목록 조회
    async getCartItems(cartIdx) {
      try {
        const res = await axios.get(`${BACKEND_URL}/carts/${cartIdx}/items`, { withCredentials: true });
        this.cartItemList = res.data.result;
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 장바구니 아이템 수량 조절 - PATCH /api/v1/carts/{cartIdx}/items/{cartItemIdx}/quantity?operation=INCREASE|DECREASE
    async updateCartItemQuantity(cartIdx, cartItemIdx, operation) {
      try {
        const res = await axios.patch(
          `${BACKEND_URL}/carts/${cartIdx}/items/${cartItemIdx}/quantity?operation=${operation}`,
          null,
          { withCredentials: true }
        );
        return res.data;
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 장바구니 아이템 삭제
    async deleteCartItem(cartIdx, cartItemIdx) {
      try {
        const res = await axios.delete(`${BACKEND_URL}/carts/${cartIdx}/items/${cartItemIdx}`, { withCredentials: true });
        return res.data
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },

    // 장바구니 아이템 전체 삭제
    async deleteCarts(cartIdx) {
      try {
        const res = await axios.delete(`${BACKEND_URL}/carts/${cartIdx}`, { withCredentials: true } );
        this.cartItemList = [];
        return res.data
      } catch (error) {
        return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
      }
    },
  }
});
