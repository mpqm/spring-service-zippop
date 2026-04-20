import { defineStore } from "pinia";
import axios from "axios";
import { BACKEND_URL } from "@/config";

export const useReserveStore = defineStore("reserve", {
    state: () => ({
        reserveList: [],
        reserve: {},
        totalElements: null,
        totalPages: null,
        access : false,
    }),
    persist: { storage: sessionStorage },
    actions: {

        // 예약 등록 - POST /api/v1/reserves
        async createReserve(req) {
            try {
                const res = await axios.post(`${BACKEND_URL}/reserves`, req, { withCredentials: true });
                return res.data
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },
        // 예약 삭제 - DELETE /api/v1/reserves/{reserveIdx}
        async deleteReserve(reserveIdx) {
            try {
                const res = await axios.delete(`${BACKEND_URL}/reserves/${reserveIdx}`, { withCredentials: true } );
                return res.data
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },
        // 예약 대기열 접속 - get /api/v1/reserves/{reserveIdx}/enrollment
        async enrollReserve(reserveIdx) {
            try {
                const res = await axios.get(`${BACKEND_URL}/reserves/${reserveIdx}/enrollment`, { withCredentials: true });
                return res.data
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },
        // 예약 취소 - DELETE /api/v1/reserves/{reserveIdx}/enrollment
        async cancelReserve(reserveIdx) {
            try {
                const res = await axios.delete(`${BACKEND_URL}/reserves/${reserveIdx}/enrollment`, { withCredentials: true });
                return res.data
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },
        // 팝업 예약 목록 조회(고객) - GET /api/v1/popups/{popupIdx}/reserves
        async getPopupReserves(popupIdx, page, size, keyword) {
            try {
                const res = await axios.get(
                    `${BACKEND_URL}/popups/${popupIdx}/reserves?keyword=${keyword ?? ""}&page=${page}&size=${size}`,
                );
                this.reserveList = res.data.result.content;
                this.totalElements = res.data.result.totalElements;
                this.totalPages = res.data.result.totalPages;
                return res.data
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },
        // 팝업 예약 목록 조회(기업) - GET /api/v1/company/popups/{popupIdx}/reserves
        async getCompanyPopupReserves(popupIdx, page, size) {
            try {
                const res = await axios.get(
                    `${BACKEND_URL}/company/popups/${popupIdx}/reserves?page=${page}&size=${size}`,
                    { withCredentials: true }
                );
                this.reserveList = res.data.result.content;
                this.totalElements = res.data.result.totalElements;
                this.totalPages = res.data.result.totalPages;
                return res.data
            } catch (error) {
                return error.response?.data ?? { success: false, message: '서버에 연결할 수 없습니다.' };
            }
        },
        // async status(reserveIdx) {
        //     try {
        //         const res = await axios.get(
        //             `${BACKEND_URL}/reserve/status?reserveIdx=${reserveIdx}`,
        //             { withCredentials: true }
        //         );
        //         return res.data
        //     } catch (error) {
        //         return error.response.data
        //     }
        // }
    }
});