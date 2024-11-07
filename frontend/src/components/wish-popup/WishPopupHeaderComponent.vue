<template>
    <div data-v-1eaba17a="" class="top-mobile">
        <div data-v-71844fb9="" data-v-1eaba17a="" class="observer-container">
            <div data-v-a5bd2c08="" data-v-1eaba17a="" class="pro-bucket-banner-mobile" data-v-71844fb9="">
                <h2 data-v-a5bd2c08="" class="title"><span data-v-a5bd2c08="">관심있는 팝업들을</span> <span
                        data-v-a5bd2c08="">다 찜하셨나요?</span></h2><img data-v-a5bd2c08=""
                    src="https://assets.cdn.soomgo.com/dist/pro-bucket-note.svg?90cfac6e2e36c1b47157e234fe1ee25c"
                    alt="pro bucket banner image" class="banner-img">
            </div><!---->
        </div>
        <div data-v-47a56272="" data-v-1eaba17a="" class="actions pro-bucket-actions fixed">
            <div data-v-47a56272="" class="delete">항목</div><!---->
        </div>
    </div>

    <div data-v-1eaba17a="" class="top-desktop">
        <div data-v-47a56272="" data-v-1eaba17a="" class="actions">

            <div data-v-47a56272="" class="delete">항목</div><!---->
        </div>
    </div>


    <div data-v-1eaba17a="" class="divider"></div>


</template>

<script>
import { useFavoriteStore } from '@/stores/useFavoriteStore';

export default {
    name: "WishPopupHeaderComponent",
    setup() {
        const favoriteStore = useFavoriteStore();

        // 스토어가 로드된 후 fetchFavoriteStores를 호출
        favoriteStore.fetchFavoriteStores().then(() => {
            console.log("Favorite stores fetched successfully");
        }).catch(error => {
            console.error("Error fetching favorite stores:", error);
        });

        return { favoriteStores: favoriteStore.favoriteStores };
    },
};
</script>

<style></style>