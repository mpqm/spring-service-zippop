<!-- src/components/PaginationComponent.vue -->
<template>
  <div class="pagination">
    <button 
      v-if="props.totalPages > pageGroupSize && currentGroup > 0" 
      class="pagination-group-btn" 
      @click="prevGroup">
      &lt;&lt;
    </button>
    <button 
      v-if="!hideBtns" 
      class="pagination-move-btn" 
      @click="changePage(currentPage - 1)" 
      :disabled="currentPage === 0">
      &lt;
    </button>

    <span v-for="page in groupPages" :key="page">
      <button 
        :class="['pagination-btn', { active: page === currentPage + 1 }]" 
        @click="changePage(page - 1)">
        {{ page }}
      </button>
    </span>

    <button 
      v-if="!hideBtns" 
      class="pagination-move-btn" 
      @click="changePage(currentPage + 1)" 
      :disabled="currentPage === totalPages - 1">
      &gt;
    </button>
    <button 
      v-if="props.totalPages > pageGroupSize && (currentGroup + 1) * pageGroupSize < props.totalPages" 
      class="pagination-group-btn" 
      @click="nextGroup">
      &gt;&gt;
    </button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, computed } from "vue";

const props = defineProps({
  currentPage: { type: Number, required: true },
  totalPages: { type: Number, required: true },
  hideBtns: { type: Boolean, default: false }
});

const emit = defineEmits(['page-changed']);
const pageGroupSize = 10; // 페이지 그룹 크기 (10개씩)

const currentGroup = ref(Math.floor(props.currentPage / pageGroupSize));

const changePage = (newPage) => {
  emit('page-changed', newPage);
};

const prevGroup = () => {
  currentGroup.value = Math.max(0, currentGroup.value - 1);
  const firstPageOfGroup = currentGroup.value * pageGroupSize;
  changePage(firstPageOfGroup);
};

const nextGroup = () => {
  currentGroup.value = Math.min(Math.floor(props.totalPages / pageGroupSize), currentGroup.value + 1);
  const firstPageOfGroup = currentGroup.value * pageGroupSize;
  changePage(firstPageOfGroup);
};

const groupPages = computed(() => {
  const start = currentGroup.value * pageGroupSize + 1;
  const end = Math.min(start + pageGroupSize - 1, props.totalPages);
  if (props.totalPages <= pageGroupSize) {
    return Array.from({ length: props.totalPages }, (_, i) => i + 1);
  }
  return Array.from({ length: end - start + 1 }, (_, i) => start + i);
});
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 2rem;
}

.pagination-btn,
.pagination-move-btn,
.pagination-group-btn {
  padding: 0.5rem 1rem;
  color: black;
  border: 1px solid #ddd;
  background-color: white;
  cursor: pointer;
  border-radius: 5px;
}

.pagination-move-btn, .pagination-group-btn {
  color: #fff;
  font-weight: 900;
  background-color: #00c7ae;
  border: none;
}

.pagination-btn.active {
  background-color: #00c7ae;
  color: #fff;
}
</style>
