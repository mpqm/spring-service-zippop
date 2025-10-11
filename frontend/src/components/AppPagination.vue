<template>
  <div v-if="props.totalPages > 0" class="wrp-pagination">
    <!-- 그룹 이동 버튼 -->
    <button v-if="!hideBtns && props.totalPages > pageGroupSize && currentGroup > 0" class="btn-paginationgroup" @click="prevGroup"> 
      <Icon icon="ic:outline-first-page" width="16px" height="16px" />
    </button>

    <!-- 이전 페이지 버튼 -->
    <button v-if="!hideBtns" class="btn-paginationmove" @click="changePage(currentPage - 1)" :disabled="currentPage === 0"> 
      <Icon icon="ic:round-navigate-before" width="16px" height="16px" />
    </button>

    <!-- 페이지 버튼 -->
  <!-- 페이지 버튼 -->
  <div v-if="!hideBtns" class="ctn-paginationpages">
    <button 
      v-for="page in groupPages" 
      :key="page" 
      :class="['btn-paginationpage', { active: page === currentPage + 1 }]" 
      @click="changePage(page - 1)"
    >
      {{ page }}
    </button>
  </div>

    <!-- 다음 페이지 버튼 -->
    <button v-if="!hideBtns" class="btn-paginationmove" @click="changePage(currentPage + 1)" :disabled="currentPage === props.totalPages - 1">
      <Icon icon="ic:round-navigate-next" width="16px" height="16px" />
    </button>

    <!-- 그룹 이동 버튼 -->
    <button v-if="!hideBtns && props.totalPages > pageGroupSize && (currentGroup + 1) * pageGroupSize < props.totalPages" class="btn-paginationgroup" @click="nextGroup">
      <Icon icon="ic:outline-last-page" width="16px" height="16px" />
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
.wrp-pagination {
    display: flex;
    flex-direction: row;
    justify-content: center;
    gap: 10px;
    margin-top: 2rem;
}

.ctn-paginationpages {
    display: flex;
    flex-direction: row;
    gap: 10px;
}

.btn-pagination, .btn-paginationpage, .btn-paginationmove, .btn-paginationgroup {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 42px;
    height: 42px;
    min-width: 42px;
    min-height: 42px;
    padding: 0.5rem;
    font-size: 0.95rem;
    font-weight: 600;
    text-align: center;
    border-radius: 8px;
    border: 1px solid #e0e0e0;
    background-color: #fff;
    color: #333;
    cursor: pointer;
    transition: 
        background-color 0.25s ease,
        box-shadow 0.25s ease,
        color 0.25s ease,
        transform 0.15s ease;
}

.btn-paginationpage:hover {
    background-color: #f4fffd;
    border-color: #00c7ae;
    box-shadow: 0 2px 5px rgba(0, 199, 174, 0.2);
    transform: translateY(-2px);
}

.btn-paginationpage.active {
    background-color: #00c7ae;
    color: #fff;
    border-color: #00c7ae;
    box-shadow: 0 3px 6px rgba(0, 199, 174, 0.3);
}

.btn-paginationmove {
    background-color: #00c7ae;
    color: #fff;
    border: none;
    font-weight: 700;
}

.btn-paginationmove:hover {
    background-color: #00b39d;
    box-shadow: 0 3px 6px rgba(0, 199, 174, 0.3);
    transform: translateY(-2px);
}

.btn-paginationgroup {
    background-color: #00c7ae;
    color: #fff;
    border: none;
    font-weight: 700;
}

.btn-paginationgroup:hover {
    background-color: #00b39d;
    box-shadow: 0 3px 6px rgba(0, 199, 174, 0.3);
    transform: translateY(-2px);
}

.btn-pagination:disabled, .btn-paginationpage:disabled, .btn-paginationmove:disabled, .btn-paginationgroup:disabled {
    background-color: #f0fffc;
    color: #00c7ae;
    cursor: not-allowed;
    box-shadow: none;
    transform: none;
}
</style>