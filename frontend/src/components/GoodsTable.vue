<template>
  <div class="ctn-table">
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
        <img v-if="item.getGoodsImageResList && item.getGoodsImageResList.length > 0" :src="item.getGoodsImageResList[0].goodsImageUrl" alt="N/A" class="table-cell-image" />
      </template>

      <!-- 굿즈명 -->
      <template #item-goodsName="item">
        <span>{{ item.goodsName }}</span>
      </template>

      <!-- 굿즈 가격 -->
      <template #item-goodsPrice="item">
        <button class="btn-tagdefault">
          <Icon icon="iconoir:coin" width="16px" height="16px" class="ico-accent" />
          <span>{{ item.goodsPrice }}원</span>
        </button>
      </template>

      <!-- 굿즈 수량 -->
      <template #item-goodsAmount="item">
        <button class="btn-tagdefault">
          <Icon icon="iconoir:box-iso" width="16px" height="16px" class="ico-accent" />
          <span>{{ item.goodsAmount }}개</span>
        </button>
      </template>

      <!-- 굿즈 타입 -->
      <template #item-goodsStatus1="item">
        <span :class="getStatusClass1(item.goodsStatus)">
          {{ formatGoodsStatus1(item.goodsStatus) }}
        </span>
      </template>

      <!-- 굿즈 상태 -->
      <template #item-goodsStatus2="item">
        <span :class="getStatusClass2(item.goodsAmount)">
          {{ formatGoodsStatus2(item.goodsAmount) }}
        </span>
      </template>

      <!-- 액션 버튼들 -->
      <template #item-actions="item">
        <div class="ctn-tablebuttons">
          <router-link class="btn-tagaction" :to="item.goodsIdx ? `/goods/${route.params.popupIdx}/${item.goodsIdx}` : '#'">
            <Icon icon="iconoir:eye" width="16px" height="16px" />
          </router-link>
          <router-link class="btn-tagaction" :to="item.goodsIdx ? `/mypage/company/goods/${route.params.popupIdx}/update/${item.goodsIdx}` : '#'">
            <Icon icon="iconoir:edit-pencil" width="16px" height="16px" />
          </router-link>
          <button class="btn-tagaction" @click="deleteGoods(item.goodsIdx)">
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
import { useGoodsStore } from '@/stores/goodsStore'
import { useToast } from 'vue-toastification'
import { useRouter, useRoute } from 'vue-router'
import { Icon } from "@iconify/vue";

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

const formatGoodsStatus1 = (type) => {
  if (type === 'GOODS_RESERVED') return '예약 굿즈'
  if (type === 'GOODS_STOCK') return '재고 굿즈'
  return type
}

const formatGoodsStatus2 = (goodsAmount) => {
  if (goodsAmount <= 0) {
    return '품절 ' + goodsAmount
  } else if (goodsAmount <= 5) {
    return '재고 부족 ' + goodsAmount
  } else {
    return '판매 중 ' + goodsAmount
  }
}

const getStatusClass1 = (status) => {
  if (status === 'GOODS_RESERVED') return 'btn-active'
  if (status === 'GOODS_STOCK') return 'btn-complete'
  return ''
}

const getStatusClass2 = (goodsAmount) => {
  if (goodsAmount <= 0) {
    return 'btn-cancel'
  } else if (goodsAmount <= 5) {
    return 'btn-wating'
  } else {
    return 'btn-complete'
  }
}

const deleteGoods = async (goodsIdx) => {
  if (!confirm('정말로 이 굿즈를 삭제하시겠습니까?')) return;
  const res = await goodsStore.deleteGoods(goodsIdx)
  if (res.success) {
    toast.success(res.message)
    router.go(0)
  } else {
    toast.error(res.message)
  }
}
</script>
