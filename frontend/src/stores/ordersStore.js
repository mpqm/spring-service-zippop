import { defineStore } from 'pinia';
import axios from 'axios';
import { BACKEND_URL } from "@/config";

export const useOrdersStore = defineStore('orders', {
    state: () => ({
        paymentData: {},
        ordersList: [],
        orders: {},
        totalElements: null,
        totalPages: null,
    }),
    persist: { storage: sessionStorage },
    actions: {

        // 결제 정보 설정
        async setPaymentData(paymentData) {
            this.paymentData = paymentData;
        },

        // 주문 생성 (결제 검증 포함) - POST /api/v1/orders
        // payload: { impUid, popupIdx, reserveIdx? } (예약 주문 시 reserveIdx 포함)
        async createOrder(payload) {
            try {
                const res = await axios.post(`${BACKEND_URL}/orders`, payload, { withCredentials: true });
                return res.data;
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },

        // 주문 상태 변경 - PATCH /api/v1/orders/{orderIdx}, body: { status: 'STOCK_CANCEL' | 'RESERVE_CANCEL' }
        // 구매 확정 (고객) - PATCH /api/v1/orders/{orderIdx}, body: { status: 'STOCK_COMPLETE' | 'RESERVE_COMPLETE' 
        // 배송 완료 (기업) - PATCH /api/v1/orders/{orderIdx}, body: { popupIdx, status: 'STOCK_COMPLETE' | 'RESERVE_COMPLETE' }
        async updateOrders(ordersIdx, req) {
            try {
                const res = await axios.patch(`${BACKEND_URL}/orders/${ordersIdx}`, req, { withCredentials: true });
                return res.data;
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },

        // 고객 주문 단일 조회 - GET /api/v1/orders/{ordersIdx}
        async getOrder(ordersIdx) {
            try {
                const res = await axios.get(`${BACKEND_URL}/orders/${ordersIdx}`, { withCredentials: true });
                this.orders = res.data.result;
                return res.data;
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },

        // 고객 주문 목록 조회 - GET /api/v1/orders
        async getOrders(page, size) {
            try {
                const res = await axios.get( `${BACKEND_URL}/orders?page=${page}&size=${size}`, { withCredentials: true });
                this.ordersList = res.data.result.content;
                this.totalElements = res.data.result.totalElements;
                this.totalPages = res.data.result.totalPages;
                return res.data;
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },

        // 기업 주문 단일 조회 - GET /api/v1/company/popups/{popupIdx}/orders/{ordersIdx}
        async getPopupOrdersDetail(popupIdx, ordersIdx) {
            try {
                const res = await axios.get(`${BACKEND_URL}/company/popups/${popupIdx}/orders/${ordersIdx}`, { withCredentials: true });
                this.orders = res.data.result;
                return res.data;
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },

        // 기업 주문 목록 조회 - GET /api/v1/company/popups/{popupIdx}/orders
        async getCompanyPopupOrdersList(popupIdx, page, size) {
            try {
                const res = await axios.get( `${BACKEND_URL}/company/popups/${popupIdx}/orders?page=${page}&size=${size}`, { withCredentials: true });
                this.ordersList = res.data.result.content;
                this.totalElements = res.data.result.totalElements;
                this.totalPages = res.data.result.totalPages;
                return res.data;
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },
    },
},);