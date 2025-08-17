<template>
  <div class="table-container">

    <EasyDataTable
      :headers="headers"
      :items="props.stores"
      :sort-by="sortBy"
      :sort-type="sortType"
      class="customize-table"
      table-class-name="customize-table"
      header-text-direction="center"
      body-text-direction="center"
      alternating
      border-cell
      hide-footer
    >
      <!-- 이미지 컬럼 -->
      <template #item-image="item">
        <img v-if="item.searchStoreImageResList && item.searchStoreImageResList.length > 0" :src="item.searchStoreImageResList[0].storeImageUrl" alt="N/A" class="table-cell-image" />
      </template>

      <template #item-storeStartDate="item"> 
          {{ formatDate(item.storeStartDate) }}
      </template>

      <template #item-storeEndDate="item"> 
          {{ formatDate(item.storeEndDate) }}
      </template>

      <template #item-likeCount="item">
        <div class="table-cell-container">
          <Icon icon="iconoir:thumbs-up" width="16px" height="16px" style="color: #00c7ae" />
          <span>{{ item.likeCount }}</span>
        </div>
      </template>

      <template #item-totalPeople="item">
        <div class="table-cell-container">
          <Icon icon="iconoir:user" width="16px" height="16px" style="color: #00c7ae" />
          <span>{{ item.totalPeople }}</span>
        </div>
      </template>

      <!-- 스토어 상태 -->
      <template #item-storeStatus="item">
        <span :class="getStatusClass(item.storeStatus)">
          {{ formatStoreStatus(item.storeStatus) }}
        </span>
      </template>

      <template #item-actions="item">
          <div v-if="props.showControl === 0" class="table-btn-container">
            <router-link class="table-btn" :to="item.storeIdx ? `/store/${item.storeIdx}` : '#'" >
              <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
            <router-link  class="table-btn" :to="item.storeIdx ? `/mypage/company/store/update/${item.storeIdx}` : '#'" >
              <Icon icon="iconoir:edit-pencil" width="16px" height="16px" />
            </router-link>
            <button class="table-btn" @click="deleteStore(item.storeIdx)">
              <Icon icon="iconoir:trash" width="16px" height="16px" />
            </button>
          </div>

          <!-- 다른 페이지용 액션들 -->
          <div v-if="props.showControl === 1" class="table-btn-container">
            <router-link class="table-btn" :to="item.storeIdx ? `/mypage/company/goods/${item.storeIdx}` : '#'">
            <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
          </div>

          <div v-if="props.showControl === 2" class="table-btn-container">
            <router-link class="table-btn" :to="item.storeIdx ? `/mypage/company/orders/${item.storeIdx}` : '#'">
              <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
          </div>

          <div v-if="props.showControl === 3" class="table-btn-container">
            <router-link class="table-btn" :to="item.storeIdx ? `/mypage/customer/cart/${item.storeIdx}` : '#'">
              <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
          </div>

          <div v-if="props.showControl === 4" class="table-btn-container">
            <router-link class="table-btn" :to="item.storeIdx ? `/mypage/company/reserve/${item.storeIdx}` : '#'">
              <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
          </div>

          <div v-if="props.showControl === 5" class="table-btn-container">
            <router-link class="table-btn" :to="item.storeIdx ? `/mypage/company/settlement/${item.storeIdx}` : '#'">
              <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
          </div>
      </template>
    </EasyDataTable>
  </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'
import EasyDataTable from 'vue3-easy-data-table'
import 'vue3-easy-data-table/dist/style.css'
import { useStoreStore } from '@/stores/useStoreStore'
import { useToast } from 'vue-toastification'
import { useRouter } from 'vue-router'

// props 정의
const props = defineProps({
  stores: {
    type: Array,
    default: () => []
  },
  showControl: {
    type: Number,
    default: 0
  }
})

// store, router, toast
const toast = useToast()
const router = useRouter()
const storeStore = useStoreStore()
const sortBy = ref('storeName')
const sortType = ref('asc')
const headers = ref([
  { text: '이미지', value: 'image', sortable: false },
  { text: '스토어명', value: 'storeName', sortable: true },
  { text: '카테고리', value: 'category', sortable: true },
  { text: '시작일', value: 'storeStartDate', sortable: true },
  { text: '종료일', value: 'storeEndDate', sortable: true },
  { text: '상태', value: 'storeStatus', sortable: true },
  { text: '좋아요', value: 'likeCount', sortable: true },
  { text: '총 인원', value: 'totalPeople', sortable: true },
  { text: '', value: 'actions', sortable: false }
])

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
  if (!dateString) return ''
  
  try {
    const date = new Date(dateString)
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    }).replace(/\./g, '.').replace(/\s/g, '')
  } catch (error) {
    return dateString
  }
}

// 스토어 상태 포맷팅 함수
const formatStoreStatus = (status) => {
  if (status === 'STORE_START') return '진행 중'
  if (status === 'STORE_END') return '종료됨'
  return status
}

// 스토어 상태별 클래스
const getStatusClass = (status) => {
  if (status === 'STORE_START') return 'status-completed'
  if (status === 'STORE_END') return 'status-cancel'
  return ''
}

// 스토어 삭제 함수
const deleteStore = async (storeIdx) => {
  const res = await storeStore.deleteStore(storeIdx)
  if (res.success) {
    toast.success(res.message)
    router.go(0)
  } else {
    toast.error(res.message)
  }
}
</script>
