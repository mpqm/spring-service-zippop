<template>
    <div>
        <ul class="feed-list">
            <WishPopupItemComponent v-for="(store, index) in favoriteStore.favorites" :key="index" :store="store"
                @delete-favorite="handleDeleteFavorite">
            </WishPopupItemComponent>
        </ul>
    </div>
</template>

<script>
import { mapStores } from 'pinia';
import { useFavoriteStore } from '@/stores/useFavoriteStore';
import WishPopupItemComponent from './WishPopupItemComponent';

export default {
    name: "WishPopupListComponent",
    components: { WishPopupItemComponent },
    computed: { ...mapStores(useFavoriteStore) },
    mounted() {
        this.favoriteStore.fetchFavoriteStores();
        console.log(this.favoriteStore.favorites)
    },
    methods: {
        async handleDeleteFavorite(storeIdx) {
            try {
                // 백엔드에 즐겨찾기 상태를 토글하는 요청
                await this.favoriteStore.toggleFavorite(storeIdx);

                // 로컬 상태에서 해당 항목 제거
                this.favoriteStore.favorites = this.favoriteStore.favorites.filter(store => store.storeIdx !== storeIdx);
                console.log(`Deleted favorite store with index: ${storeIdx}`);
            } catch (error) {
                console.error(`Error deleting favorite store with index: ${storeIdx}`, error);
            }
        }
    }
};
</script>

<style></style>