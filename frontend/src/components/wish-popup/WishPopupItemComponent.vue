<template>
    <div data-v-11ae1556="" data-v-1eaba17a="" class="pro-bucket-card pro-bucket-list-item">
        <div data-v-11ae1556="" class="d-flex">
            <div data-v-11ae1556="" class="pro-info text-truncate">
                <div data-v-11ae1556="" class="pro-name-wrapper">
                    <h5 data-v-11ae1556="" class="pro-name text-truncate">{{ store.storeName }}</h5>
                </div>
                <p data-v-11ae1556="" class="pro-introduction text-truncate">{{ store.storeContent }}</p>
                <div data-v-959ba008="" data-v-11ae1556="" class="profile-badges">
                    <div data-v-959ba008="" class="review-rate">
                        <img data-v-959ba008="" />
                        <span data-v-959ba008="" class="rate">좋아요 {{ store.likeCount }}</span>
                        <span data-v-959ba008="" class="count">{{ store.category }}</span>
                    </div>
                    <span data-v-959ba008="" class="badge-item"> {{ store.storeAddress }}</span>
                </div>
            </div>
            <div data-v-11ae1556="">
                <div data-v-29d5164e="" data-v-11ae1556="" class="user-profile-picture pro-profile-picture">
                    <div data-v-29d5164e="" data-name="image" class="is-square"
                        :style="{ backgroundImage: `url(${store.getPopupStoreImageResList[0]?.imageUrl})` }">
                    </div>
                </div>
            </div>
        </div>
        <div class="pro-extra-info">
            <span>기간 : {{ store.storeStartDate }} ~ </span>
            <span class="avg-response-time">{{ store.storeEndDate }}</span>
        </div>

        <button @click="deleteFavorite(store.storeIdx)" class="btn-delete">찜 취소</button>

    </div>
</template>


<script>
export default {
    name: "WishPopupItemComponent",
    props: {
        store: {
            type: Object,
            required: true
        }
    },
    methods: {
        deleteFavorite(storeIdx) {
            // 부모 컴포넌트로 storeIdx 전달
            this.$emit('delete-favorite', storeIdx);
        }
    }
}
</script>

<style>
.btn-delete {
    margin-top: 7px;
    padding: 1px 10px;
    background-color: #00c7ae;
    color: white;
    border: none;
    cursor: pointer;
    border-radius: 4px;
}
</style>