<template>
    <div>
        <div class="like-management-page">
            <div class="like-list" v-if="likeList && likeList.length">
                <LikeListComponent v-for="store in likeList" :key="store.storeIdx" :store="store" />
            </div>
            <div class="notice" v-else>
                <p>좋아요한 팝업 스토어가 없습니다.</p>
            </div>
            <PaginationComponent
                :currentPage="currentPage"
                :totalPages="totalPages"
                :hideBtns="hideBtns"
                @page-changed="changePage"
            />
        </div>
    </div>
      </template>
      
      <script setup>
import LikeListComponent from "@/components/store/LikeListComponent.vue";
import PaginationComponent from "@/components/common/PaginationComponent.vue";
     import { useStoreStore } from "@/stores/useStoreStore";
      import { onMounted, ref } from "vue";
      
      const storeStore = useStoreStore();
      const likeList = ref([]);
      const currentPage = ref(0);
      const pageSize = ref(8);
      const totalElements = ref(0);
      const totalPages = ref(0);
      const hideBtns = ref(false);
      
      onMounted(async () => {
        await searchAll(currentPage.value, pageSize.value);
      });
      
      const changePage = (newPage) => {
        if (newPage >= 0) {
          currentPage.value = newPage;
          searchAll(currentPage.value, pageSize.value);
        }
      };
      
      const searchAll = async (page) => {
        const res = await storeStore.searchAllLike(page, pageSize.value);
        if(res.success){
        totalElements.value = storeStore.totalElements;
        totalPages.value = storeStore.totalPages;
        likeList.value = storeStore.likeList;
        hideBtns.value = false;
      } else {
        likeList.value = null;
        totalElements.value = 0;
        totalPages.value = 0;
        hideBtns.value = true;
      }
      };

    
      </script>
      
      <style scoped>
      .like-management-page {
        flex-direction: row;
        width: 65rem;
      }
    
      .like-control {
        padding: 5px;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }
    .notice{
        text-align: center;
      }
      
      .like-list {
        width: auto;
        padding: 5px;
        display: flex;
        flex-direction: column;
        gap: 10px;
      }
      
      .pagination {
        display: flex;
        justify-content: center;
        gap: 10px;
        margin: 10px auto;
      }
      
      .pagination-btn {
        padding: 0.5rem 1rem;
        color: black;
        border: 1px solid #ddd;
        cursor: pointer;
        border-radius: 5px;
      }
      
      .pagination-move-btn {
        padding: 0.5rem 1rem;
        color: #fff;
        background-color: #00c7ae;
        border: none;
        cursor: pointer;
        border-radius: 5px;
      }
      
      .pagination-btn.active {
        background-color: #00c7ae;
        color: #fff;
      }
    
      .like-register-btn{
        display: block;
        text-align: center;
        width: auto;
        font-weight: 400;
        transition: opacity 0.2s ease-in-out;
        color: #fff;
        cursor: pointer;
        background-color: #00c7ae;
        border-color: #00c7ae;
        border: 0.0625rem solid transparent;
        padding: 0.5rem;
        border-radius: 0.25rem;
        text-decoration: #000;
      }
    
    .search-container {
        display: flex;
        gap: 10px;
        justify-content: center;
    }
    
    .search-input {
      border: 1px solid #e1e1e1;
        border-radius: 4px;
        display: flex;
        font-size: 1rem;
        font-weight: 400;
        line-height: 1.5; 
        padding: 0.5rem;
        width: 30rem;
        box-sizing: border-box;
        color: #323232;
        background-color: #fff;
    }
    
    .search-btn {
      display: block;
        text-align: center;
        width: auto;
        font-weight: 400;
        transition: opacity 0.2s ease-in-out;
        color: #fff;
        cursor: pointer;
        background-color: #00c7ae;
        border-color: #00c7ae;
        border: 0.0625rem solid transparent;
        padding: 0.5rem;
        border-radius: 0.25rem;
        text-decoration: #000;
    }
    
    .search-btn:hover, .pagination-btn:hover, .pagination-move-btn:hover, .like-register-btn:hover{
      opacity: 0.8;
    }
    
    .search-img {
      padding: 0 1.25rem;
    }
      </style>
      