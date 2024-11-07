import { defineStore } from 'pinia';
import axios from 'axios';

axios.defaults.withCredentials = true;
import { backend } from "@/const";


export const useFavoriteStore = defineStore('favorite', {
    state: () => ({
        favorites: [], // 찜한 팝업 목록을 저장하는 상태
    }),
    persist: { storage: sessionStorage, },
    actions: {
        async fetchFavoriteStores() {
            try {
                const response = await axios.get(`${backend}/favorite/search`);
                if (response.status === 200) {
                    this.favorites = response.data.result.map(item => item.getPopupStoreRes);
                    console.log(this.favorites);
                } else {
                    console.error("Failed to fetch favorite stores.");
                }
            } catch (error) {
                console.error("찜한 팝업 목록 조회 중 오류가 발생했습니다.", error);
            }
        },
        async toggleFavorite(storeIdx) {
            try {
                const response = await axios.get(`${backend}/favorite/active`, {
                    params: { storeIdx }
                });
                if (response.status === 200) {
                    console.log("Favorite status toggled successfully.");
                } else {
                    console.error("Failed to toggle favorite status.");
                }
            } catch (error) {
                console.error("즐겨찾기 상태 토글 중 오류가 발생했습니다.", error);
            }
        }
    },
});