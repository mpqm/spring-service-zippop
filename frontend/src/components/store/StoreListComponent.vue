<template>
  <div class="default-list">
    <img class="list-item-img" v-if="store.searchStoreImageResList && store.searchStoreImageResList.length" :src="store.searchStoreImageResList[0].storeImageUrl" alt="store image" />
    <div class="list-item-info">
      <div class="list-item-info1">
        <p class="fs17fw800">{{ store.storeName }}</p>
        <p class="fs15fw400">{{ store.category }}</p>
      </div>
      <div class="list-item-info1">
        <p class="fs13fw400">{{ store.storeStartDate }} ~ {{ store.storeEndDate }}</p>
        <div class="list-item-info2">
          <Icon icon="iconoir:thumbs-up" width="20px" height="20px" style="color: #00c7ae" />
          <span class="fs13fw400"> &nbsp;{{ store.likeCount }} </span>
          &nbsp;
          <Icon icon="iconoir:user" width="20px" height="20px" style="color: #00c7ae" />
          <span class="fs13fw400">&nbsp;{{ store.totalPeople }} </span>
        </div>
      </div>
    </div>

    <!-- StoreManagePage용 -->
    <div v-if="showControl === 0" class="list-item-btn-container">
      <router-link class="list-item-btn" :to="store ? `/store/${store.storeIdx}` : '#'">
       <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
       팝업 보기
      </router-link>
      <router-link class="list-item-btn" :to="store ? `/mypage/company/store/update/${store.storeIdx}` : '#'">
        <Icon icon="iconoir:edit-pencil" width="20px" height="20px" style="color: #ffffff" />
        팝업 수정
      </router-link>
      <button class="list-item-btn" @click="deleteStore">
        <Icon icon="iconoir:trash" width="20px" height="20px" style="color: #ffffff" />
        팝업 삭제
      </button>
    </div>
    <!-- GoodsManage1Page용 -->
    <div v-if="showControl === 1" class="list-item-btn-container">
      <router-link class="list-item-btn" :to="store ? `/mypage/company/goods/${store.storeIdx}` : '#'">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
        굿즈 보기
      </router-link>
    </div>
    <!-- CompanyOrdersManage1Page1 -->
    <div v-if="showControl === 2" class="list-item-btn-container">
      <router-link class="list-item-btn" :to="store ? `/mypage/company/orders/${store.storeIdx}` : '#'">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
        거래 내역 보기</router-link>
    </div>
    <!-- CartManagement1Page용 -->
    <div v-if="showControl === 3" class="list-item-btn-container">
      <router-link class="list-item-btn" :to="store ? `/mypage/customer/cart/${store.storeIdx}` : '#'">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
        카트 보기
      </router-link>
    </div>
     <!-- ReserveManagementPage 용 -->
    <div v-if="showControl === 4" class="list-item-btn-container">
      <router-link class="list-item-btn" :to="store ? `/mypage/company/reserve/${store.storeIdx}` : '#'">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
        예약 보기
      </router-link>
    </div>
    <!-- SettlementManagementPage1 용 -->
    <div v-if="showControl === 5" class="list-item-btn-container">
      <router-link class="list-item-btn" :to="store ? `/mypage/company/settlement/${store.storeIdx}` : '#'">
        <Icon icon="iconoir:eye" width="20px" height="20px" style="color: #ffffff" />
        정산 내역 보기
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";
import { useStoreStore } from "@/stores/useStoreStore";
import { useToast } from "vue-toastification";
import { useRouter } from "vue-router";

// props 정의(store, showControl)
const props = defineProps({
  store: Object,
  showControl: Number,
});

// store, router, route, toast
const toast = useToast();
const router = useRouter();
const storeStore = useStoreStore();

// 스토어 삭제
const deleteStore = async () => {
  const res = await storeStore.deleteStore(props.store.storeIdx);
  if (res.success) {
    toast.success(res.message)
    router.go(0)
  } else {
    toast.error(res.message);
  }
}
</script>


