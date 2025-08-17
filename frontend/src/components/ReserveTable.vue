<template>
  <div class="table-container">
    <EasyDataTable
      :headers="headers"
      :items="props.reserves"
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
      <!-- 예약 인원수 -->
      <template #item-reservePeople="item">
        <div class="table-cell-container">
          <Icon icon="iconoir:user" width="16px" height="16px" style="color: #00c7ae" />
          <span>{{ item.reservePeople }}명</span>
        </div>
      </template>

      <!-- 예약 시작 날짜 -->
      <template #item-reserveStartDate="item">
        <span>{{ formatDate(item.reserveStartDate) }}</span>
      </template>

      <!-- 예약 시간 -->
      <template #item-reserveTime="item">
        <span>{{ formatTime(item.reserveStartTime) }} ~ {{ formatTime(item.reserveEndTime) }}</span>
      </template>

      <!-- 카운트다운 타이머 -->
      <template #item-countdown="item">
        <CountDownTimer :targetTime="item.reserveStartTime" :flag="false" />
      </template>

      <!-- 예약 상태 -->
      <template #item-status="item">
        <span :class="getStatusClass(item)">
          {{ getStatusText(item) }}
        </span>
      </template>

      <!-- 액션 버튼들 -->
      <template #item-actions="item">
        <div v-if="props.showControl === true" class="table-btn-container">
          <button class="table-btn" @click="deleteReserve(item)">
            <Icon icon="iconoir:trash" width="16px" height="16px" />
          </button>
        </div>

        <div v-if="props.showControl === false" class="table-btn-container">
          <router-link class="table-btn" :to="`/reserve/${item.storeIdx}/${item.reserveIdx}`">
            <Icon icon="iconoir:calendar-plus" width="16px" height="16px" />
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
import CountDownTimer from '@/components/CountDownTimer.vue'
import { useReserveStore } from '@/stores/useReserveStore'
import { useToast } from 'vue-toastification'
import { useRouter } from 'vue-router'

// props 정의
const props = defineProps({
  reserves: {
    type: Array,
    default: () => []
  },
  showControl: {
    type: Boolean,
    default: false
  }
})

// 반응형 데이터
const sortBy = ref('reserveStartDate')
const sortType = ref('asc')

// store, router, toast
const toast = useToast()
const router = useRouter()
const reserveStore = useReserveStore()

// 테이블 헤더 정의
const headers = ref([
  { text: '예약인원', value: 'reservePeople', sortable: true },
  { text: '예약날짜', value: 'reserveStartDate', sortable: true },
  { text: '예약시간', value: 'reserveTime', sortable: false },
  { text: '남은시간', value: 'countdown', sortable: false },
  { text: '상태', value: 'status', sortable: false },
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

// 시간 포맷팅 함수
const formatTime = (dateTimeString) => {
  if (!dateTimeString) return ""
  const timePart = dateTimeString.split("T")[1]
  return timePart ? timePart.slice(0, 5) : ""
}

// 예약 상태 텍스트
const getStatusText = (reserve) => {
  const now = new Date()
  const startTime = new Date(reserve.reserveStartTime)
  const endTime = new Date(reserve.reserveEndTime)

  if (now < startTime) {
    return '예약 대기'
  } else if (now >= startTime && now <= endTime) {
    return '진행 중'
  } else {
    return '완료'
  }
}

// 예약 상태별 클래스
const getStatusClass = (reserve) => {
  const now = new Date()
  const startTime = new Date(reserve.reserveStartTime)
  const endTime = new Date(reserve.reserveEndTime)

  if (now < startTime) {
    return 'table-cell-status-waiting'
  } else if (now >= startTime && now <= endTime) {
    return 'table-cell-status-active'
  } else {
    return 'table-cell-status-completed'
  }
}

// 예약 삭제
const deleteReserve = async (reserve) => {
  if (!confirm('정말로 이 예약을 삭제하시겠습니까?')) {
    return
  }

  try {
    const res = await reserveStore.delete(reserve.storeIdx, reserve.reserveIdx)
    if (res.success) {
      toast.success(res.message)
      router.go(0)
    } else {
      toast.error(res.message)
    }
  } catch (error) {
    toast.error('삭제 중 오류가 발생했습니다.')
  }
}
</script>