import { defineStore } from "pinia";
import axios from "axios";
import { backend } from "@/env";

export const useReserveStore = defineStore("reserve", {
    state: () => ({
        reserveList: [],
        reserve: {},
        totalElements: null,
        totalPages: null,
    }),
    persist: { storage: sessionStorage, },
    actions: {

        // 예약 등록
        async register(req) {
            try {
                const res = await axios.post(
                    `${backend}/reserve/register`, req,
                    { withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
        async enroll(reserveIdx) {
            try {
                const res = await axios.get(
                    `${backend}/reserve/enroll?reserveIdx=${reserveIdx}`,
                    { withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
        async cancel(reserveIdx) {
            try {
                const res = await axios.get(
                    `${backend}/reserve/cancel?reserveIdx=${reserveIdx}`,
                    { withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
        async searchAllReserve(page, size) {
            try {
                const res = await axios.get(
                    `${backend}/reserve/search-all?page=${page}&size=${size}`,
                );
                this.reserveList = res.data.result.content;
                this.totalElements = res.data.result.totalElements;
                this.totalPages = res.data.result.totalPages;
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
        async searchAllReserveByKeyword(keyword, page, size) {
            try {
                const res = await axios.get(
                    `${backend}/reserve/search-all?keyword=${keyword}&page=${page}&size=${size}`,
                );
                this.reserveList = res.data.result.content;
                this.totalElements = res.data.result.totalElements;
                this.totalPages = res.data.result.totalPages;
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
        async searchAllReserveAsCompany(storeIdx, page, size) {
            try {
                const res = await axios.get(
                    `${backend}/reserve/search-all/as-company?storeIdx=${storeIdx}&page=${page}&size=${size}`,
                    { withCredentials: true }
                );
                this.reserveList = res.data.result.content;
                this.totalElements = res.data.result.totalElements;
                this.totalPages = res.data.result.totalPages;
                return res.data
            } catch (error) {
                return error.response.data
            }
        },
        async status(reserveIdx) {
            try {
                const res = await axios.get(
                    `${backend}/reserve/status?reserveIdx=${reserveIdx}`,
                    { withCredentials: true }
                );
                return res.data
            } catch (error) {
                return error.response.data
            }
        }
    }
});