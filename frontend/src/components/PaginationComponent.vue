<!-- src/components/PaginationComponent.vue -->
<template>
<div class="pagination">
    <button v-if="!hideBtns" class="pagination-move-btn" @click="changePage(currentPage - 1)" :disabled="currentPage === 0">이전</button>
    <span v-for="page in totalPages" :key="page">
        <button :class="['pagination-btn', { active: page === currentPage + 1 }]" @click="changePage(page - 1)"> {{ page }}</button>
    </span>
    <button v-if="!hideBtns" class="pagination-move-btn" @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages - 1">다음</button>
</div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";

defineProps({
    currentPage: {
        type: Number,
        required: true,
      },
      totalPages: {
        type: Number,
        required: true,
      },
      hideBtns: {
        type: Boolean,
        default: false,
      }
});
const emit = defineEmits(['page-changed'])
const changePage = (newPage) => {
    emit('page-changed', newPage);
};
</script>
  
  <style scoped>
  .pagination {
    display: flex;
    justify-content: center;
    gap: 10px;
    margin-top: 2rem;
  }
  
  .pagination-btn {
    padding: 0.5rem 1rem;
    color: black;
    border: 1px solid #fff;
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
  </style>
  