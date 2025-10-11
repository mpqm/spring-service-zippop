<template>
  <div class="lyt-child">
    <form class="ctn-rootform" @submit.prevent="update()">
      <div class="ctn-split">
        <h1 class="txt-def0">팝업 스토어 수정</h1>
        <div class="ctn-buttons">
          <button type="submit" class="btn-default">수정</button>
          <button type="button" @click="router.back()" class="btn-normal">취소</button>
        </div>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업 스토어 이름</label>
        <input class="ipt-default" v-model="storeName" type="text" placeholder="팝업 스토어 이름을 입력해주세요." />
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업 스토어 설명</label>
        <textarea class="ipt-default" v-model="storeContent" placeholder="팝업 스토어에 대한 설명을 입력해주세요."></textarea>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업 스토어 카테고리</label>
        <input class="ipt-default" v-model="category" type="text" placeholder="팝업 스토어 카테고리를 입력해주세요." />
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업 스토어 온라인 총 인원</label>
        <input class="ipt-default" v-model="totalPeople" type="number" placeholder="팝업 스토어 카테고리를 입력해주세요." />
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">팝업스토어 시작일/종료일</label>
        <div class="ctn-split">
          <input class="ipt-default" v-model="storeStartDate" type="date" placeholder="시작일" />
          <input class="ipt-default" v-model="storeEndDate" type="date" placeholder="종료일" />
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
import { useStoreStore } from "@/stores/useStoreStore";
import { useToast } from "vue-toastification";

// store, router, route, toast
const storeStore = useStoreStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();

// 변수(store)
const storeName = ref("");
const storeContent = ref("");
const category = ref("");
const totalPeople = ref(0);
const address = ref("");
const addressDetail = ref("");
const storeStartDate = ref("");
const storeEndDate = ref("");
const fileUrls = ref([]);
const files = ref([]);
const store = ref({});

// onMounted
onMounted(async () => {
  await search();
  await loadMapjsApi();
});

const search = async () => {
  const res = await storeStore.searchStore(route.params.storeIdx);
  if (res.success) {
    store.value = storeStore.store;
    await mapper();
  } else {
    router.push("/mypage/company/store")
    toast.error(res.message);
  }
}

// 매핑 함수
const mapper = async () => {
  storeName.value = storeStore.store.storeName;
  storeContent.value = storeStore.store.storeContent;
  category.value = storeStore.store.category;
  totalPeople.value = storeStore.store.totalPeople;
  address.value = storeStore.store.storeAddress.split(',')[0];
  addressDetail.value = storeStore.store.storeAddress.split(',')[1] || '';
  storeStartDate.value = storeStore.store.storeStartDate;
  storeEndDate.value = storeStore.store.storeEndDate;
  if (storeStore.store.searchStoreImageResList && storeStore.store.searchStoreImageResList.length) {
    fileUrls.value = storeStore.store.searchStoreImageResList.map(image => image.storeImageUrl);
  }
}

// 주소 API 로드
const loadMapjsApi = async () => {
  const script = document.createElement("script");
  script.src = "https://txt-def1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
  document.head.appendChild(script);
}

// 주소 API 로드
const openAddressSearch = () => {
  // eslint-disable-next-line no-undef
  new daum.Postcode({
    oncomplete: function (data) { address.value = data.address; },
  }).open();
};

// 파일 업로드
const handleFileUpload = (event) => {
  files.value = event.target.files;
  fileUrls.value = [];
  for (let i = 0; i < files.value.length; i++) {
    const file = files.value[i];
    fileUrls.value.push(URL.createObjectURL(file));
  }
};

// 스토어 수정
const update = async () => {
  const req = {
    storeName: storeName.value,
    storeContent: storeContent.value,
    category: category.value,
    totalPeople: totalPeople.value,
    storeAddress: address.value + "," + addressDetail.value,
    storeStartDate: storeStartDate.value,
    storeEndDate: storeEndDate.value,
  };
  const formData = new FormData();
  formData.append("dto", new Blob([JSON.stringify(req)], { type: "application/json" }));
  
  // 새로운 이미지를 선택한 경우에만 추가
  if (files.value.length > 0) {
    Array.from(files.value).forEach((file) => { formData.append("files", file);});
  }
  
  const res = await storeStore.updateStore(route.params.storeIdx, formData);
  if (res.success) {
    toast.success(res.message);
    router.push("/mypage/company/store");
  } else {
    toast.error(res.message);
  }
};

</script>

