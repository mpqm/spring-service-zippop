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
        async setPaymentData(paymentData) {
            this.paymentData = paymentData;
        },
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