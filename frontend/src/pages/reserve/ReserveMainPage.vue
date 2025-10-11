<template>
    <div>
        <AppHeader></AppHeader>
        <div class="lyt-root">
            <h2 class="txt-maintitle">팝업 스토어 예약 일정을 확인하세요</h2>
            <div class="ctn-inputsearch">
                <input class="ipt-default" v-model="searchQuery" type="text" placeholder="검색어를 입력하세요" @keyup.enter="searchAllByKeyword" />
                <button class="btn-default" @click="searchAllByKeyword"><Icon icon="ic:search" width="20px" height="20px" /></button>
                <button class="btn-normal" @click="searchAll(0)"><Icon icon="ic:baseline-refresh" width="20px" height="20px" /></button>
            </div>
            <div class="lyt-cardgrid" v-if="reserveList && reserveList.length">
                <ReserveCard v-for="reserve in reserveList" :key="reserve.reserveIdx" :reserve="reserve" />
            </div>
            <div v-else>
                <p>검색 결과에 해당하는 팝업 스토어 목록이 없습니다.</p>
            </div>
            <AppPagination :currentPage="currentPage" :totalPages="totalPages" :hideBtns="hideBtns" @page-changed="changePage" />
        </div>
        <AppFooter></AppFooter>
    </div>
</template>
<script setup>
import AppHeader from "@/components/AppHeader.vue";
import AppFooter from "@/components/AppFooter.vue";
import ReserveCard from "@/components/ReserveCard.vue";
import AppPagination from "@/components/AppPagination.vue";
import { useReserveStore } from "@/stores/useReserveStore";
import { onMounted, ref } from "vue";

// reserve, router, route, toast
const reserveStore = useReserveStore();

// 변수(reserve)
const reserveList = ref([]);
const currentPage = ref(0);
const pageSize = ref(12);
const totalElements = ref(0);
const totalPages = ref(0);
const hideBtns = ref(false);
const isKeywordSearch = ref(false);
const searchQuery = ref("");

// onMounted
onMounted(async () => {
    await searchAll(currentPage.value, pageSize.value);
});

// 스토어 목록 조회
const searchAll = async (flag) => {
    if (flag === 0) {
        currentPage.value = 0;
        searchQuery.value = "";
        isKeywordSearch.value = false; // 일반 검색 상태로 전환
    }
    const res = await reserveStore.searchAllReserve(currentPage.value, pageSize.value);
    if (res.success) {
        totalElements.value = reserveStore.totalElements;
        totalPages.value = reserveStore.totalPages;
        reserveList.value = reserveStore.reserveList;
        hideBtns.value = false;
    } else {
        reserveList.value = [];
        totalElements.value = 0;
        totalPages.value = 0;
        hideBtns.value = true;
    }
};

// 스토어 목록 조회(키워드 검색)
const searchAllByKeyword = async () => {
    if (!isKeywordSearch.value) {
        currentPage.value = 0; // 키워드 검색 상태로 진입 시 페이지를 초기화
        isKeywordSearch.value = true; // 키워드 검색 상태 활성화
    }
    const res = await reserveStore.searchAllReserveByKeyword(searchQuery.value, currentPage.value, pageSize.value);
    if (res.success) {
        totalElements.value = reserveStore.totalElements;
        totalPages.value = reserveStore.totalPages;
        reserveList.value = reserveStore.storeList;
        hideBtns.value = false;
    } else {
        reserveList.value = [];
        totalElements.value = 0;
        totalPages.value = 0;
        hideBtns.value = true;
    }
};

// 페이지 네이션
const changePage = async (newPage) => {
    if (newPage < 0 || newPage >= totalPages.value) return; // 유효한 페이지 번호인지 확인
    currentPage.value = newPage;
    if (isKeywordSearch.value) { // 키워드 검색 상태일 경우
        await searchAllByKeyword();
    } else { // 일반 검색 상태일 경우
        await searchAll();
    }
};

</script>