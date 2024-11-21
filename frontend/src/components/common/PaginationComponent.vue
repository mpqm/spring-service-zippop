<template>
  <div class="pagination">
    <!-- 그룹 이동 버튼 -->
    <button v-if="!hideBtns && props.totalPages > pageGroupSize && currentGroup > 0" class="pagination-group-btn" @click="prevGroup"> 
      &lt;&lt; 
    </button>

    <!-- 이전 페이지 버튼 -->
    <button v-if="!hideBtns" class="pagination-move-btn" @click="changePage(currentPage - 1)" :disabled="currentPage === 0"> 
      &lt; 
    </button>

    <!-- 페이지 버튼 -->
    <span v-if="!hideBtns">
      <button v-for="page in groupPages" :key="page" :class="['pagination-btn', { active: page === currentPage + 1 }]" @click="changePage(page - 1)">
        {{ page }}
      </button>
    </span>

    <!-- 다음 페이지 버튼 -->
    <button v-if="!hideBtns" class="pagination-move-btn" @click="changePage(currentPage + 1)" :disabled="currentPage === props.totalPages - 1">
      &gt;
    </button>

    <!-- 그룹 이동 버튼 -->
    <button v-if="!hideBtns && props.totalPages > pageGroupSize && (currentGroup + 1) * pageGroupSize < props.totalPages" class="pagination-group-btn" @click="nextGroup">
      &gt;&gt;
    </button>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, ref, computed, watch } from "vue";

// Props 정의
const props = defineProps({
  currentPage: { type: Number, required: true },
  totalPages: { type: Number, required: true },
  hideBtns: { type: Boolean, default: false }
});

// 이벤트 정의
const emit = defineEmits(['page-changed']);

// 페이지 그룹 크기
const pageGroupSize = 10;

// 현재 페이지 그룹 계산
const currentGroup = ref(Math.floor(props.currentPage / pageGroupSize));

// props.currentPage 변경 감지하여 그룹 업데이트
watch(() => props.currentPage, (newPage) => {
  currentGroup.value = Math.floor(newPage / pageGroupSize);
});

// 페이지 변경 시 이벤트 발생
const changePage = (newPage) => {
  emit('page-changed', newPage);
};

// 이전 그룹 이동
const prevGroup = () => {
  if (currentGroup.value > 0) {
    currentGroup.value -= 1;
    const firstPageOfGroup = currentGroup.value * pageGroupSize;
    changePage(firstPageOfGroup);
  }
};

// 다음 그룹 이동
const nextGroup = () => {
  const maxGroup = Math.floor((props.totalPages - 1) / pageGroupSize);
  if (currentGroup.value < maxGroup) {
    currentGroup.value += 1;
    const firstPageOfGroup = currentGroup.value * pageGroupSize;
    changePage(firstPageOfGroup);
  }
};

// 현재 그룹의 페이지 목록 계산
const groupPages = computed(() => {
  const start = currentGroup.value * pageGroupSize + 1;
  const end = Math.min(start + pageGroupSize - 1, props.totalPages);
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

.pagination-move-btn,
.pagination-group-btn {
  color: #fff;
  font-weight: 900;
  background-color: #00c7ae;
  border: none;
}

.pagination-move-btn:hover,
.pagination-group-btn:hover {
  opacity: 0.8;
}

.pagination-btn.active {
  background-color: #00c7ae;
  color: #fff;
}

.pagination-btn:disabled,
.pagination-move-btn:disabled,
.pagination-group-btn:disabled {
  background-color: #f5f5f5;
  color: #b5b5b5;
  cursor: not-allowed;
}
</style>
