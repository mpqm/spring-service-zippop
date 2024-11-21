import { defineStore } from 'pinia';
import axios from 'axios';
import { backend } from '@/env';

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

        // 결제 정보 검증 요청
        async verify(imp_uid, flag) {
            try {
                const res = await axios.get(
                    `${backend}/orders/verify?impUid=${imp_uid}&flag=${flag}`,
                    { withCredentials: true }
                );
                return res.data;
            } catch (error) {
                return error.response.data;
            }
        },

        // 결제 취소
        async cancel(ordersIdx) {
            try {
                const res = await axios.get(
                    `${backend}/orders/cancel?ordersIdx=${ordersIdx}`,
                    { withCredentials: true }
                );
                return res.data;
            } catch (error) {
                return error.response.data;
            }
        },

        // 결제 완료 (고객)
        async completeAsCustomer(ordersIdx) {
            try {
                const res = await axios.get(
                    `${backend}/orders/complete?ordersIdx=${ordersIdx}`,
                    { withCredentials: true }
                );
                return res.data;
            } catch (error) {
                return error.response.data;
            }
        },

        // 배송 완료 (기업)
        async completeAsCompany(storeIdx, ordersIdx) {
            try {
                const res = await axios.get(
                    `${backend}/orders/complete?storeIdx=${storeIdx}&ordersIdx=${ordersIdx}`,
                    { withCredentials: true }
                );
                return res.data;
            } catch (error) {
                return error.response.data;
            }
        },

        // 고객 주문 단일 조회
        async searchAsCustomer(ordersIdx) {
            try {
                const res = await axios.get(
                    `${backend}/orders/search/as-customer?ordersIdx=${ordersIdx}`,
                    { withCredentials: true }
                );
                this.orders = res.data.result;
                return res.data;
            } catch (error) {
                return error.response.data;
            }
        },

        // 고객 주문 목록 조회
        async searchAllAsCustomer(page, size) {
            try {
                const res = await axios.get(
                    `${backend}/orders/search-all/as-customer?page=${page}&size=${size}`,
                    { withCredentials: true }
                );
                this.ordersList = res.data.result.content;
                this.totalElements = res.data.result.totalElements;
                this.totalPages = res.data.result.totalPages;
                return res.data;
            } catch (error) {
                return error.response.data;
            }
        },

        // 기업 주문 단일 조회
        async searchAsCompany(storeIdx, ordersIdx) {
            try {
                const res = await axios.get(
                    `${backend}/orders/search/as-company?ordersIdx=${ordersIdx}&storeIdx=${storeIdx}`,
                    { withCredentials: true }
                );
                this.orders = res.data.result;
                return res.data;
            } catch (error) {
                return error.response.data;
            }
        },

        // 기업 주문 목록 조회
        async searchAllAsCompany(storeIdx, page, size) {
            try {
                const res = await axios.get(
                    `${backend}/orders/search-all/as-company?storeIdx=${storeIdx}&page=${page}&size=${size}`,
                    { withCredentials: true }
                );
                this.ordersList = res.data.result.content;
                this.totalElements = res.data.result.totalElements;
                this.totalPages = res.data.result.totalPages;
                return res.data;
            } catch (error) {
                return error.response.data;
            }
        },
    },
},);