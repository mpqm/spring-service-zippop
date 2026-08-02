<template>
  <div class="ctn-table">

    <EasyDataTable
      :headers="headers"
      :items="props.popups"
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
        <img v-if="item.getPopupImageResList && item.getPopupImageResList.length > 0" :src="item.getPopupImageResList[0].popupImageUrl" alt="N/A" class="table-cell-image" />
      </template>

      <template #item-popupStartDate="item"> 
          {{ formatDate(item.popupStartDate) }}
      </template>

      <template #item-popupEndDate="item"> 
          {{ formatDate(item.popupEndDate) }}
      </template>

      <template #item-likeCount="item">
        <button class="btn-tagdefault">
          <Icon icon="iconoir:thumbs-up" width="16px" height="16px" class="ico-accent" />
          <span>{{ item.likeCount }}</span>
        </button>
      </template>

      <template #item-totalPeople="item">
        <button class="btn-tagdefault">
          <Icon icon="iconoir:user" width="16px" height="16px" class="ico-accent" />
          <span>{{ item.totalPeople }}</span>
        </button>
      </template>

      <!-- 팝업 상태 -->
      <template #item-popupStatus="item">
        <span :class="getStatusClass(item.popupStatus)">
          {{ formatPopupStatus(item.popupStatus) }}
        </span>
      </template>

      <template #item-actions="item">
          <!-- 팝업 관리 페이지 -->
          <div v-if="props.showControl === 0" class="ctn-tablebuttons">
            <router-link class="btn-tagaction" :to="item.popupIdx ? `/popup/${item.popupIdx}` : '#'">
              <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
            <router-link class="btn-tagaction" :to="item.popupIdx ? `/mypage/company/popup/update/${item.popupIdx}` : '#'">
              <Icon icon="iconoir:edit-pencil" width="16px" height="16px" />
            </router-link>
            <button class="btn-tagaction" @click="deletePopup(item.popupIdx)">
              <Icon icon="iconoir:trash" width="16px" height="16px" />
            </button>
          </div>

          <!-- 굿즈 관리 페이지 -->
          <div v-if="props.showControl === 1" class="ctn-tablebuttons">
            <router-link class="btn-tagaction" :to="item.popupIdx ? `/mypage/company/goods/${item.popupIdx}` : '#'">
              <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
          </div>

          <!-- 주문 관리 페이지 -->
          <div v-if="props.showControl === 2" class="ctn-tablebuttons">
            <router-link class="btn-tagaction" :to="item.popupIdx ? `/mypage/company/orders/${item.popupIdx}` : '#'">
              <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
          </div>

          <!-- 예약 관리 페이지 -->
          <div v-if="props.showControl === 4" class="ctn-tablebuttons">
            <router-link class="btn-tagaction" :to="item.popupIdx ? `/mypage/company/reserve/${item.popupIdx}` : '#'">
              <Icon icon="iconoir:eye" width="16px" height="16px" />
            </router-link>
          </div>

          <!-- 정산 관리 페이지 -->
          <div v-if="props.showControl === 5" class="ctn-tablebuttons">
            <router-link class="btn-tagaction" :to="item.popupIdx ? `/mypage/company/payout/${item.popupIdx}` : '#'">
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
import { usePopupStore } from '@/stores/popupStore'
import { useToast } from 'vue-toastification'
import { useRouter } from 'vue-router'
import { Icon } from "@iconify/vue";

const props = defineProps({
  popups: {
    type: Array,
    default: () => []
  },
  showControl: {
    type: Number,
    default: 0
  }
})

const toast = useToast()
const router = useRouter()
const popupStore = usePopupStore()
const sortBy = ref('popupName')
const sortType = ref('asc')
const headers = ref([
  { text: '이미지', value: 'image', sortable: false },
  { text: '팝업명', value: 'popupName', sortable: true },
  { text: '카테고리', value: 'category', sortable: true },
  { text: '시작일', value: 'popupStartDate', sortable: true },
  { text: '종료일', value: 'popupEndDate', sortable: true },
  { text: '상태', value: 'popupStatus', sortable: true },
  { text: '좋아요', value: 'likeCount', sortable: true },
  { text: '총 인원', value: 'totalPeople', sortable: true },
  { text: '', value: 'actions', sortable: false }
])

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

const formatPopupStatus = (status) => {
  if (status === 'POPUP_START') return '진행 중'
  if (status === 'POPUP_END') return '종료됨'
  return status
}

const getStatusClass = (status) => {
  if (status === 'POPUP_START') return 'btn-complete'
  if (status === 'POPUP_END') return 'btn-cancel'
  return ''
}

const deletePopup = async (popupIdx) => {
  if (!confirm('정말로 이 팝업을 삭제하시겠습니까?')) return;
  const res = await popupStore.deletePopup(popupIdx)
  if (res.success) {
    toast.success(res.message)
    router.go(0)
  } else {
    toast.error(res.message)
  }
}
</script>
