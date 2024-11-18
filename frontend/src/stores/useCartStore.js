import { defineStore } from 'pinia';
import axios from 'axios';
import { backend } from '@/const';

export const useCartStore = defineStore('cart', {
  state: () => ({
    cartItems: [],
    totalPrice: 0,
    totalDiscount: 0,
    deliveryFee: 2500,
    finalOrderPrice: 2500,
    userPoints: 0,
    usePoints: false,
  }),
  persist: { storage: sessionStorage },
  actions: {
    // 장바구니 데이터 가져오기
    async fetchCart() {
      try {
        const response = await axios.get(`${backend}/search-all`, { withCredentials: true });
        if (response.data.success) {
          this.cartItems = response.data.result;
          this.cartItems.forEach(item => {
            item.itemTotalPrice = item.itemPrice * item.itemCount;
          });
          this.totalPrice = this.cartItems.reduce((acc, item) => acc + item.itemTotalPrice, 0);
          this.updateFinalOrderPrice();
        } else {
          console.error('Failed to fetch cart data:', response.data.message);
        }
      } catch (error) {
        console.error('Error fetching cart:', error);
      }
    },

    // 사용자 포인트 가져오기
    async fetchUserPoints() {
      try {
        const response = await axios.get(`${backend}/points`, { withCredentials: true });
        if (response.data.success) {
          this.userPoints = response.data.result.point;
        } else {
          console.error('Failed to fetch user points:', response.data.message);
        }
      } catch (error) {
        console.error('Error fetching user points:', error);
      }
    },

    // 장바구니 수량 조정하기
    async adjustCartCount(item, operation) {
      const operationValue = operation === -1 ? 1 : 0; // `1` for decrement, `0` for increment
      try {
        const response = await axios.get(`${backend}/count`, {
          params: { cartIdx: item.cartIdx, operation: operationValue },
          withCredentials: true,
        });
        if (response.data.success) {
          item.itemCount = operation === -1 ? item.itemCount - 1 : item.itemCount + 1;
          item.itemTotalPrice = item.itemPrice * item.itemCount;
          this.totalPrice = this.cartItems.reduce((acc, item) => acc + item.itemTotalPrice, 0);
          this.updateFinalOrderPrice();
        } else {
          console.error('Failed to adjust cart count:', response.data.message);
        }
      } catch (error) {
        console.error('Error adjusting cart count:', error);
      }
    },

    // 장바구니 항목 삭제하기
    async deleteCartItem(cartIdx) {
      try {
        const response = await axios.delete(`${backend}/delete`, {
          params: { cartIdx },
          withCredentials: true,
        });
        if (response.data.success) {
          this.cartItems = this.cartItems.filter(item => item.cartIdx !== cartIdx);
          this.totalPrice = this.cartItems.reduce((acc, item) => acc + item.itemTotalPrice, 0);
          this.updateFinalOrderPrice();
        } else {
          console.error('Failed to delete cart item:', response.data.message);
        }
      } catch (error) {
        console.error('Error deleting cart item:', error);
      }
    },

    // 모든 장바구니 항목 삭제하기
    async deleteAllCartItems() {
      try {
        const response = await axios.delete(`${backend}/delete-all`, { withCredentials: true });
        if (response.data.success) {
          this.cartItems = [];
          this.totalPrice = 0;
          this.updateFinalOrderPrice();
        } else {
          console.error('Failed to delete all cart items:', response.data.message);
        }
      } catch (error) {
        console.error('Error deleting all cart items:', error);
      }
    },

    // 총 할인액 업데이트
    updateTotalDiscount() {
      this.totalDiscount = this.usePoints ? this.userPoints : 0;
    },

    // 최종 주문 금액 업데이트
    updateFinalOrderPrice() {
      const totalProductPrice = this.totalPrice;
      const totalDiscountPrice = this.usePoints ? this.userPoints : 0;
      const totalDeliveryPrice = this.deliveryFee;
      this.finalOrderPrice = totalProductPrice - totalDiscountPrice + totalDeliveryPrice;
    }
  }
});
