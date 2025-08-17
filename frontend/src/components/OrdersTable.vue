<template>
  <div class="table-container">
    <EasyDataTable
      :headers="headers"
      :items="props.orders"
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
      <!-- 주문 번호 -->
      <template #item-impUid="item">
        <span>{{ item.impUid }}</span>
      </template>

      <!-- 주문자 -->
      <template #item-name="item">
        <span>{{ item.name }}</span>
      </template>

      <!-- 전화번호 -->
      <template #item-phoneNumber="item">
        <span>{{ item.phoneNumber }}</span>
      </template>

      <!-- 주문일 -->
      <template #item-createdAt="item">
        <span>{{ formatDate(item.createdAt) }}</span>
      </template>

      <!-- 사용 포인트 -->
      <template #item-usedPoint="item">
        <div class="table-cell-container">
          <Icon icon="iconoir:coins" width="16px" height="16px" style="color: #00c7ae" />
          <span>{{ item.usedPoint }}</span>
        </div>
      </template>

      <!-- 배송비 -->
      <template #item-deliveryCost="item">
        <span>{{ item.deliveryCost }}원</span>
      </template>

      <!-- 총 가격 -->
      <template #item-totalPrice="item">
        <span>{{ item.totalPrice }}원</span>
      </template>

      <!-- 주문 상태 -->
      <template #item-orderStatus="item">
        <span :class="getStatusClass(item.orderStatus)">{{ formatOrderStatus(item.orderStatus) }}</span>
      </template>

      <!-- 액션 버튼들 -->
      <template #item-actions="item">
        <div v-if="props.showControl === true" class="table-btn-container">
          <button class="table-btn orders-cancel-btn" :disabled="isCancelDisabled(item)" @click="cancelOrders(item.ordersIdx)">
            <Icon icon="iconoir:cancel" width="16px" height="16px" />
          </button>
          <button class="table-btn" @click="completeOrders(item.ordersIdx)">
            <Icon icon="iconoir:check" width="16px" height="16px" />
            주문확정
          </button>
          <router-link class="table-btn" :to="item.ordersIdx ? `/orders/${item.ordersIdx}` : '#'">
            <Icon icon="iconoir:eye" width="16px" height="16px" />
          </router-link>
        </div>

        <div v-if="props.showControl === false" class="table-btn-container">
          <button class="table-btn" @click="completeOrders(item.ordersIdx)">
            <Icon icon="iconoir:check" width="16px" height="16px" />
            배송확정
          </button>
          <router-link v-if="item.ordersIdx && route.params.storeIdx" class="table-btn" :to="`/orders/${item.ordersIdx}?storeIdx=${route.params.storeIdx}`">
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
import { useOrdersStore } from '@/stores/useOrdersStore'
import { useToast } from 'vue-toastification'
import { useRouter, useRoute } from 'vue-router'

// props 정의
const props = defineProps({
  orders: {
    type: Array,
    default: () => []
  },
  showControl: {
    type: Boolean,
    default: false
  }
})

// 반응형 데이터
const sortBy = ref('createdAt')
const sortType = ref('desc')

// store, router, toast
const toast = useToast()
const router = useRouter()
const route = useRoute()
const ordersStore = useOrdersStore()

// 테이블 헤더 정의
const headers = ref([
  { text: '주문번호', value: 'impUid', sortable: true },
  { text: '주문자', value: 'name', sortable: true },
  { text: '전화번호', value: 'phoneNumber', sortable: false },
  { text: '주문일', value: 'createdAt', sortable: true },
  { text: '사용포인트', value: 'usedPoint', sortable: true },
  { text: '배송비', value: 'deliveryCost', sortable: true },
  { text: '총가격', value: 'totalPrice', sortable: true },
  { text: '상태', value: 'orderStatus', sortable: true },
  { text: '', value: 'actions', sortable: false }
])

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
  if (!dateString) return ''
  
  try {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' }
    return new Date(dateString).toLocaleDateString('ko-KR', options)
  } catch (error) {
    return dateString
  }
}

// 주문 상태 포맷 함수
const formatOrderStatus = (statusString) => {
  if (statusString === "STOCK_READY") return "재고 굿즈 결제 완료"
  else if (statusString === "STOCK_CANCEL") return "재고 굿즈 결제 취소"
  else if (statusString === "STOCK_COMPLETE") return "재고 굿즈 주문 확정"
  else if (statusString === "STOCK_DELIVERY") return "재고 굿즈 배달 중"
  else if (statusString === "RESERVE_READY") return "예약 굿즈 결제 완료"
  else if (statusString === "RESERVE_CANCEL") return "예약 굿즈 결제 취소"
  else if (statusString === "RESERVE_COMPLETE") return "예약 굿즈 주문 확정"
  else if (statusString === "RESERVE_DELIVERY") return "예약 굿즈 배달 중"
  return statusString
}

// 상태별 클래스 반환 함수
const getStatusClass = (statusString) => {
  if (statusString === "STOCK_READY" || statusString === "RESERVE_READY") return "status-ready"
  else if (statusString === "STOCK_CANCEL" || statusString === "RESERVE_CANCEL") return "status-cancel"
  else if (statusString === "STOCK_COMPLETE" || statusString === "RESERVE_COMPLETE") return "status-complete"
  else if (statusString === "STOCK_DELIVERY" || statusString === "RESERVE_DELIVERY") return "status-delivery"
  return ""
}

// 주문 확정인 경우 취소 버튼 비활성화
const isCancelDisabled = (order) => {
  return order.orderStatus === "STOCK_COMPLETE" || order.orderStatus === "RESERVE_COMPLETE"
}

// 주문 취소
const cancelOrders = async (ordersIdx) => {
  if (!confirm('정말로 이 주문을 취소하시겠습니까?')) {
    return
  }

  try {
    const res = await ordersStore.cancel(ordersIdx)
    if (res.success) {
      toast.success(res.message)
      router.go(0)
    } else {
      toast.error(res.message)
    }
  } catch (error) {
    toast.error('취소 중 오류가 발생했습니다.')
  }
}

// 주문 확정(고객), 배송 확정(기업)
const completeOrders = async (ordersIdx) => {
  try {
    let res
    if (props.showControl) {
      res = await ordersStore.completeAsCustomer(ordersIdx)
    } else {
      res = await ordersStore.completeAsCompany(route.params.storeIdx, ordersIdx)
    }
    
    if (res.success) {
      toast.success(res.message)
      router.go(0)
    } else {
      toast.error(res.message)
    }
  } catch (error) {
    toast.error('처리 중 오류가 발생했습니다.')
  }
}
</script>

<style scoped>
.status-ready {
  color: #f39c12;
}

.status-cancel {
  color: #e74c3c;
}

.status-complete {
  color: #3498db;
}

.status-delivery {
  color: #27ae60;
}

.orders-cancel-btn {
  background-color: #e74c3c !important;
  border-color: #e74c3c !important;
}

.orders-cancel-btn:hover {
  background-color: #c0392b !important;
}

.orders-complete-btn {
  background-color: #27ae60 !important;
  border-color: #27ae60 !important;
}

.orders-complete-btn:hover {
  background-color: #229954 !important;
}

.table-btn:disabled {
  background-color: #95a5a6 !important;
  border-color: #95a5a6 !important;
  cursor: not-allowed;
  opacity: 0.6;
}
</style>