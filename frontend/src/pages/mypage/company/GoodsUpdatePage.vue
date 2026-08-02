<template>
  <div class="lyt-child">
    <form class="ctn-rootform ctn-reviewform ctn-standardform" @submit.prevent="updateGoods">
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
import { useGoodsStore } from "@/stores/goodsStore";
import { createMultipartRequest } from "@/utils/multipart";

const goodsStore = useGoodsStore();
const router = useRouter();
const toast = useToast();
const route = useRoute();

const goodsName = ref("");
const goodsAmount = ref(0);
const goodsPrice = ref(0);
const goodsContent = ref("");
const fileUrls = ref([]);
const files = ref([]);

onMounted(async () => {
  await getGoods(route.params.goodsIdx);
});

const getGoods = async (goodsIdx) => {
  const res = await goodsStore.getGoods(goodsIdx);
  if (res.success) {
      const g = goodsStore.goods;
    goodsName.value = g.goodsName;
    goodsAmount.value = g.goodsAmount;
    goodsPrice.value = g.goodsPrice;
    goodsContent.value = g.goodsContent;
    if (g.getGoodsImageResList && g.getGoodsImageResList.length) {
      fileUrls.value = g.getGoodsImageResList.map(image => image.goodsImageUrl);
    }
  } else {
    router.push(`/mypage/company/goods/${route.params.popupIdx}`);
    toast.error(res.message);
  }
};

const handleFileUpload = (event) => {
  files.value = event.target.files;
  fileUrls.value = [];
  for (let i = 0; i < files.value.length; i++) {
    fileUrls.value.push(URL.createObjectURL(files.value[i]));
  }
};

const updateGoods = async () => {
  const req = {
    goodsName: goodsName.value,
    goodsAmount: goodsAmount.value,
    goodsPrice: goodsPrice.value,
    goodsContent: goodsContent.value,
  };
  if (files.value.length === 0) {
    toast.error("수정할 상품 이미지를 선택해주세요.");
    return;
  }
  const formData = createMultipartRequest(req, files.value);
  const res = await goodsStore.updateGoods(route.params.goodsIdx, formData);
  if (res.success) {
    toast.success(res.message);
    router.push(`/mypage/company/goods/${route.params.popupIdx}`);
  } else {
    toast.error(res.message);
  }
};
</script>
