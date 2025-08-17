<template>
  <div class="table-container">
    <EasyDataTable
      :headers="headers"
      :items="props.goods"
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
        <img v-if="item.searchGoodsImageResList && item.searchGoodsImageResList.length > 0" :src="item.searchGoodsImageResList[0].goodsImageUrl" alt="N/A" class="table-cell-image" />
      </template>

      <!-- 굿즈명 -->
      <template #item-goodsName="item">
        <span>{{ item.goodsName }}</span>
      </template>

      <!-- 굿즈 가격 -->
      <template #item-goodsPrice="item">
        <div class="table-cell-container">
          <Icon icon="iconoir:coin" width="16px" height="16px" style="color: #00c7ae" />
          <span>{{ item.goodsPrice }}원</span>
        </div>
      </template>

      <!-- 굿즈 수량 -->
      <template #item-goodsAmount="item">
        <div class="table-cell-container">
          <Icon icon="iconoir:box-iso" width="16px" height="16px" style="color: #00c7ae" />
          <span>{{ item.goodsAmount }}개</span>
        </div>
      </template>

      <!-- 굿즈 타입 -->
      <template #item-goodsStatus1="item">
        <span>{{ formatGoodsStatus1(item.goodsStatus) }}</span>
      </template>
      <!-- 굿즈 상태 -->
      <template #item-goodsStatus2="item">
        <span>{{ formatGoodsStatus2(item.goodsAmount) }}</span>
      </template>

      <!-- 액션 버튼들 -->
      <template #item-actions="item">
        <div class="table-btn-container">
          <router-link class="table-btn" :to="item.goodsIdx ? `/goods/${route.params.storeIdx}/${item.goodsIdx}` : '#'">
            <Icon icon="iconoir:eye" width="16px" height="16px" />
          </router-link>
          <router-link class="table-btn" :to="item.goodsIdx ? `/mypage/company/goods/${route.params.storeIdx}/update/${item.goodsIdx}` : '#'">
            <Icon icon="iconoir:edit-pencil" width="16px" height="16px" />
          </router-link>
          <button class="table-btn" @click="deleteGoods(item.goodsIdx)">
            <Icon icon="iconoir:trash" width="16px" height="16px" />
          </button>
        </div>
      </template>
    </EasyDataTable>
  </div>
</template>

<script setup>
import { ref, defineProps } from 'vue'
import EasyDataTable from 'vue3-easy-data-table'
import 'vue3-easy-data-table/dist/style.css'
import { useGoodsStore } from '@/stores/useGoodsStore'
import { useToast } from 'vue-toastification'
import { useRouter, useRoute } from 'vue-router'

const props = defineProps({
  goods: {
    type: Array,
    default: () => []
  }
})
const goodsStore = useGoodsStore()
const toast = useToast()
const router = useRouter()
const route = useRoute()
const sortBy = ref('goodsName')
const sortType = ref('asc')
const headers = ref([
  { text: '이미지', value: 'image', sortable: false },
  { text: '굿즈명', value: 'goodsName', sortable: true },
  { text: '가격', value: 'goodsPrice', sortable: true },
  { text: '수량', value: 'goodsAmount', sortable: true },
  { text: '타입', value: 'goodsStatus1', sortable: false },
  { text: '상태', value: 'goodsStatus2', sortable: false },
  { text: '', value: 'actions', sortable: false }
])

// 굿즈 타입 포맷팅
const formatGoodsStatus1 = (type) => {
  if (type === 'GOODS_RESERVED') return '예약 굿즈'
  if (type === 'GOODS_STOCK') return '재고 굿즈'
  return type
}

// 굿즈 상태 텍스트
const formatGoodsStatus2 = (goodsAmount) => {
  if (goodsAmount <= 0) {
    return '품절'
  } else if (goodsAmount <= 5) {
    return '재고 부족'
  } else {
    return '판매 중'
  }
}

// 굿즈 삭제
const deleteGoods = async (goodsIdx) => {
  const res = await goodsStore.delete(goodsIdx)
  if (res.success) {
    toast.success(res.message)
    router.go(0)
  } else {
    toast.error(res.message)
  }
}
</script>