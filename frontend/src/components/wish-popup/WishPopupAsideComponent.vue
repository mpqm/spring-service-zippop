<template>
    <aside class="aside" data-v-1eaba17a="">
        <div class="request-guide" data-v-b3ee3c58 data-v-1eaba17a="">
            <span class="desc" data-v-b3ee3c58="">
                더 다양한 팝업을 찾아보세요!
            </span>
            <button type="button" @click="goToMain" class="btn request-btn btn-primary btn-block" data-v-b3ee3c58>
                팝업 찾아보기
            </button>
        </div>
    </aside>
</template>

<script>

export default {
    name: "WishPopupAsideComponent",
    methods: {
        goToMain() {
            this.$router.push("/"); // 메인 페이지로 이동
        },
    },
}
</script>
<style></style>