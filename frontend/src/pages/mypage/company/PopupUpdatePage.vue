<template>
  <div class="lyt-child">
    <form class="ctn-rootform" @submit.prevent="updatePopup">
      <div class="ctn-split">
        <h1 class="txt-def0">팝업 스토어 수정</h1>
        <div class="ctn-buttons">
          <button type="submit" class="btn-default">수정</button>
          <button type="button" @click="router.back()" class="btn-normal">취소</button>
        </div>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업 스토어 이름</label>
        <input class="ipt-default" v-model="popupName" type="text" placeholder="팝업 스토어 이름을 입력해주세요." />
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업 스토어 설명</label>
        <textarea class="ipt-default" v-model="popupContent" placeholder="팝업 스토어에 대한 설명을 입력해주세요."></textarea>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업 스토어 카테고리</label>
        <input class="ipt-default" v-model="category" type="text" placeholder="팝업 스토어 카테고리를 입력해주세요." />
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업 스토어 온라인 총 인원</label>
        <input class="ipt-default" v-model="totalPeople" type="number" placeholder="팝업 스토어 총 인원을 입력해주세요." />
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업스토어 시작일/종료일</label>
        <div class="ctn-split">
          <input class="ipt-default" v-model="popupStartDate" type="date" placeholder="시작일" />
          <input class="ipt-default" v-model="popupEndDate" type="date" placeholder="종료일" />
        </div>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">주소/상세주소</label>
        <div class="ctn-split">
          <input class="ipt-default" v-model="address" type="text" placeholder="주소" @click="openAddressSearch" />
          <input class="ipt-default" v-model="addressDetail" type="text" placeholder="상세 주소" />
        </div>
      </div>
      <label for="file">
        <div class="btn-default">팝업 스토어 이미지 파일 업로드</div>
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
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { usePopupStore } from "@/stores/popupStore";
import { useToast } from "vue-toastification";

const popupStore = usePopupStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

const popupName = ref("");
const popupContent = ref("");
const category = ref("");
const totalPeople = ref(0);
const address = ref("");
const addressDetail = ref("");
const popupStartDate = ref("");
const popupEndDate = ref("");
const fileUrls = ref([]);
const files = ref([]);

onMounted(async () => {
  await getPopup();
  await loadMapjsApi();
});

const getPopup = async () => {
  const res = await popupStore.getPopup(route.params.popupIdx);
  if (res.success) {
    mapper();
  } else {
    router.push("/mypage/company/popup");
    toast.error(res.message);
  }
};

const mapper = () => {
  const p = popupStore.popup;
  popupName.value = p.popupName;
  popupContent.value = p.popupContent;
  category.value = p.category;
  totalPeople.value = p.totalPeople;
  address.value = p.popupAddress?.split(',')[0] || '';
  addressDetail.value = p.popupAddress?.split(',')[1] || '';
  popupStartDate.value = p.popupStartDate;
  popupEndDate.value = p.popupEndDate;
  if (p.getPopupImageResList && p.getPopupImageResList.length) {
    fileUrls.value = p.getPopupImageResList.map(image => image.popupImageUrl);
  }
};

const loadMapjsApi = async () => {
  const script = document.createElement("script");
  script.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
  document.head.appendChild(script);
};

const openAddressSearch = () => {
  // eslint-disable-next-line no-undef
  new daum.Postcode({
    oncomplete: function (data) { address.value = data.address; },
  }).open();
};

const handleFileUpload = (event) => {
  files.value = event.target.files;
  fileUrls.value = [];
  for (let i = 0; i < files.value.length; i++) {
    fileUrls.value.push(URL.createObjectURL(files.value[i]));
  }
};

const updatePopup = async () => {
  const req = {
    popupName: popupName.value,
    popupContent: popupContent.value,
    category: category.value,
    totalPeople: totalPeople.value,
    popupAddress: address.value + "," + addressDetail.value,
    popupStartDate: popupStartDate.value,
    popupEndDate: popupEndDate.value,
  };
  const formData = new FormData();
  formData.append("dto", new Blob([JSON.stringify(req)], { type: "application/json" }));
  if (files.value.length > 0) {
    Array.from(files.value).forEach((file) => { formData.append("files", file); });
  }
  const res = await popupStore.updatePopup(route.params.popupIdx, formData);
  if (res.success) {
    toast.success(res.message);
    router.push("/mypage/company/popup");
  } else {
    toast.error(res.message);
  }
};
</script>
