<template>
  <div class="lyt-child">

    <form class="ctn-rootform" @submit.prevent="update">
      <div class="ctn-split">
        <h1 class="txt-def0">팝업 굿즈 수정</h1>
        <div class="ctn-buttons">
          <button type="submit" class="btn-default">수정</button>
          <button type="button" @click="router.back()" class="btn-normal">취소</button>
        </div>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">굿즈 이름</label>
        <input class="ipt-default" v-model="goodsName" type="text" placeholder="팝업 굿즈 이름을 입력해주세요." />
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">굿즈 설명</label>
        <textarea class="ipt-default" v-model="goodsContent" placeholder="팝업 굿즈에 대한 설명을 입력해주세요."></textarea>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">굿즈 가격</label>
        <input class="ipt-default" v-model="goodsPrice" type="number" placeholder="팝업 굿즈 가격을 입력해주세요." />
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">굿즈 수량</label>
        <input class="ipt-default" v-model="goodsAmount" type="number" placeholder="팝업 굿즈 수량을 입력해주세요." />
      </div>
      <label for="file">
        <div class="btn-default">팝업 굿즈 이미지 파일 업로드</div>
      </label>

      <input @change="handleFileUpload" type="file" name="file" id="file" multiple />
      <div class="wrp-filepreview" v-if="fileUrls.length">
        <div v-for="(fileUrl, index) in fileUrls" :key="index" class="ctn-filepreview">
          <img :src="fileUrl" alt="file preview" />
        </div>
      </div>
    </form>
    
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { useGoodsStore } from "@/stores/useGoodsStore";

// store, router, route, toast
const goodsStore = useGoodsStore();
const router = useRouter();
const toast = useToast();
const route = useRoute();

// 변수(goods)
const goodsName = ref("");
const goodsAmount = ref(0);
const goodsPrice = ref(0);
const goodsContent = ref("");
const goods = ref({});
const fileUrls = ref([]);
const files = ref([]);

// onMounted 
onMounted(async () => {
  await search(route.params.goodsIdx);
});

// 굿즈 단일 조회
const search = async (goodsIdx) => {
  const res = await goodsStore.search(goodsIdx);
  if (res.success) {
    goods.value = goodsStore.goods;
    await mapper();
  } else {
    router.push(`/mypage/company/goods/${route.params.storeIdx}`);
    toast.error(res.message);
  }
}

// 매핑 함수
const mapper = async () => {
  goodsName.value = goodsStore.goods.goodsName;
  goodsAmount.value = goodsStore.goods.goodsAmount;
  goodsPrice.value = goodsStore.goods.goodsPrice;
  goodsContent.value = goodsStore.goods.goodsContent;
  if (goodsStore.goods.searchGoodsImageResList && goodsStore.goods.searchGoodsImageResList.length) {
    fileUrls.value = goodsStore.goods.searchGoodsImageResList.map(image => image.goodsImageUrl);
  }
}

// 파일 업로드 
const handleFileUpload = (event) => {
  files.value = event.target.files;
  fileUrls.value = [];
  for (let i = 0; i < files.value.length; i++) {
    const file = files.value[i];
    fileUrls.value.push(URL.createObjectURL(file));
  }

};

// 굿즈 수정
const update = async () => {
  const req = {
    goodsName: goodsName.value,
    goodsAmount: goodsAmount.value,
    goodsPrice: goodsPrice.value,
    goodsContent: goodsContent.value,
  };
  const formData = new FormData();
  formData.append("dto", new Blob([JSON.stringify(req)], { type: "application/json" }));
  
  // 새로운 이미지를 선택한 경우에만 추가
  if (files.value.length > 0) {
    Array.from(files.value).forEach((file) => {
      formData.append("files", file);
    });
  }
  
  const res = await goodsStore.update(route.params.goodsIdx, formData);
  if (res.success) {
    toast.success(res.message);
    router.push(`/mypage/company/goods/${route.params.storeIdx}`);
  } else {
    toast.error(res.message);
  }
};

</script>