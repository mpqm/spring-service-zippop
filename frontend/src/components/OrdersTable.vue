<template>
  <div class="ctn-table">
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
        <button class="btn-tagdefault">
          <Icon icon="iconoir:coins" width="16px" height="16px" style="color: #00c7ae" />
          <span>{{ item.usedPoint }}</span>
        </button>
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
        <!-- 고객 주문 관리 -->
        <div v-if="props.showControl === true" class="ctn-tablebuttons">
          <button class="btn-tagdefault" :disabled="isCancelDisabled(item)" @click="cancelOrders(item)">
            <Icon icon="iconoir:cancel" width="16px" height="16px" />
          </button>
          <button class="btn-tagdefault" @click="completeOrders(item)">
            <Icon icon="iconoir:check" width="16px" height="16px" />
            주문확정
          </button>
          <router-link class="btn-tagdefault" :to="item.ordersIdx ? `/orders/${item.ordersIdx}` : '#'">
            <Icon icon="iconoir:eye" width="16px" height="16px" />
          </router-link>
        </div>

        <!-- 기업 주문 관리 -->
        <div v-if="props.showControl === false" class="ctn-tablebuttons">
          <button class="btn-tagdefault" @click="completeOrders(item)">
            <Icon icon="iconoir:check" width="16px" height="16px" />
            배송확정
          </button>
          <router-link v-if="item.ordersIdx && route.params.popupIdx" class="btn-tagdefault" :to="`/orders/${item.ordersIdx}?popupIdx=${route.params.popupIdx}`">
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
import { useOrdersStore } from '@/stores/ordersStore'
import { useToast } from 'vue-toastification'
import { useRouter, useRoute } from 'vue-router'
import { Icon } from "@iconify/vue";

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

const sortBy = ref('createdAt')
const sortType = ref('desc')

const toast = useToast()
const router = useRouter()
const route = useRoute()
const ordersStore = useOrdersStore()

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

const formatDate = (dateString) => {
  if (!dateString) return ''
  try {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric' }
    return new Date(dateString).toLocaleDateString('ko-KR', options)
  } catch (error) {
    return dateString
  }
}

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

const getStatusClass = (statusString) => {
  if (statusString === "STOCK_READY" || statusString === "RESERVE_READY") return "btn-waiting"
  else if (statusString === "STOCK_CANCEL" || statusString === "RESERVE_CANCEL") return "btn-cancel"
  else if (statusString === "STOCK_COMPLETE" || statusString === "RESERVE_COMPLETE") return "btn-complete"
  else if (statusString === "STOCK_DELIVERY" || statusString === "RESERVE_DELIVERY") return "btn-active"
  return ""
}

const isCancelDisabled = (orders) => {
  return orders.orderStatus === "STOCK_COMPLETE" || orders.orderStatus === "RESERVE_COMPLETE"
}

const cancelOrders = async (item) => {
  if (!confirm('정말로 이 주문을 취소하시겠습니까?')) return;
  const cancelStatus = item.orderStatus?.startsWith('STOCK') ? 'STOCK_CANCEL' : 'RESERVE_CANCEL';
  const res = await ordersStore.updateOrders(item.ordersIdx, { status: cancelStatus });
  if (res.success) {
    toast.success(res.message)
    router.go(0)
  } else {
    toast.error(res.message)
  }
}

const completeOrders = async (item) => {
  const completeStatus = item.orderStatus === 'STOCK_READY' ? 'STOCK_COMPLETE' : 'RESERVE_COMPLETE';
  let res;
  if (props.showControl) {
    // 고객: 주문 확정
    res = await ordersStore.updateOrders(item.ordersIdx, { status: completeStatus });
  } else {
    // 기업: 배송 확정
    res = await ordersStore.updateOrders(item.ordersIdx, { popupIdx: Number(route.params.popupIdx), status: completeStatus });
  }
  if (res.success) {
    toast.success(res.message)
    router.go(0)
  } else {
    toast.error(res.message)
  }
}
</script>
