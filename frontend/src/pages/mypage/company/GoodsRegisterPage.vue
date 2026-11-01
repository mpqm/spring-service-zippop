<template>
  <div class="lyt-child">
    <form class="ctn-rootform ctn-reviewform ctn-standardform" @submit.prevent="createGoods">
      <div class="ctn-reviewheading">
        <div>
          <span class="txt-eyebrow">GOODS SETTINGS</span>
          <h2>팝업 굿즈 등록</h2>
          <p>판매할 굿즈의 기본 정보와 가격, 수량 및 대표 이미지를 등록할 수 있습니다.</p>
        </div>
        <div class="ctn-buttons">
          <button type="button" @click="router.back()" class="btn-normal btn-reviewsubmit">취소</button>
          <button type="submit" class="btn-default btn-reviewsubmit">등록</button>
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
      <div class="ctn-inputdefault ctn-mediauploadfield">
        <div class="ctn-mediauploadhead">
          <label class="ipt-default-label" for="goods-register-files">대표 이미지</label>
          <input id="goods-register-files" class="ipt-filehidden" @change="handleFileUpload" type="file" accept="image/*" name="files" multiple />
          <label class="btn-normal btn-fileupload" for="goods-register-files">이미지 선택</label>
        </div>
        <span class="txt-formhelp">굿즈에 표시할 대표 이미지를 선택해주세요.</span>
        <div v-if="fileUrls.length" class="wrp-popuppreview">
          <div v-for="(fileUrl, index) in fileUrls" :key="index" class="ctn-popuppreview">
            <img :src="fileUrl" :alt="`굿즈 이미지 ${index + 1}`" />
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from "vue";
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

const handleFileUpload = (event) => {
  files.value = event.target.files;
  fileUrls.value = [];
  for (let i = 0; i < files.value.length; i++) {
    fileUrls.value.push(URL.createObjectURL(files.value[i]));
  }
};

const createGoods = async () => {
  const req = {
    goodsName: goodsName.value,
    goodsAmount: goodsAmount.value,
    goodsPrice: goodsPrice.value,
    goodsContent: goodsContent.value,
    popupIdx: Number(route.params.popupIdx),
  };
  if (files.value.length === 0) {
    toast.error("이미지를 선택해주세요");
    return;
  }
  const formData = createMultipartRequest(req, files.value);
  const res = await goodsStore.createGoods(formData);
  if (res.success) {
    router.push(`/mypage/company/goods/${route.params.popupIdx}`);
    toast.success(res.message);
  } else {
    toast.error(res.message);
  }
};
</script>
