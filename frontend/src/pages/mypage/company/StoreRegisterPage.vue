<template>
  <div class="register-page">
    <form class="register-form" @submit.prevent="register">
      <div>
        <label>팝업 스토어 이름</label>
        <input class="register-input" v-model="storeName" type="text" placeholder="팝업 스토어 이름을 입력해주세요." />
      </div>
      <div>
        <label>팝업 스토어 설명</label>
        <textarea class="register-input" v-model="storeContent" placeholder="팝업 스토어에 대한 설명을 입력해주세요."></textarea>
      </div>
      <div>
        <label>팝업 스토어 카테고리</label>
        <input class="register-input" v-model="category" type="text" placeholder="팝업 스토어 카테고리를 입력해주세요." />
      </div>
      <div>
        <label>팝업 스토어 온라인 총 인원</label>
        <input class="register-input" v-model="totalPeople" type="number" placeholder="팝업 스토어 카테고리를 입력해주세요." />
      </div>
      <div>
        <label>팝업스토어 시작일/종료일</label>
        <div class="date-picker">
          <input class="register-input" v-model="storeStartDate" type="date" placeholder="시작일" />
          <input class="register-input" v-model="storeEndDate" type="date" placeholder="종료일" />
        </div>
      </div>
      <div>
        <label>주소/상세주소</label>
        <div class="address">
          <input class="register-input" v-model="address" type="text" placeholder="주소" @click="openAddressSearch" />
          <input class="register-input" v-model="addressDetail" type="text" placeholder="상세 주소" />
        </div>
      </div>
      <label for="file">
        <div class="file-upload-btn">팝업 스토어 이미지 파일 업로드</div>
      </label>
      <input @change="handleFileUpload" type="file" name="file" id="file" multiple />
      <div class="file-preview" v-if="fileUrls.length">
        <div v-for="(fileUrl, index) in fileUrls" :key="index" class="file-preview-item">
          <img :src="fileUrl" alt="file preview" />
        </div>
      </div>
      <div class="btn-container">
        <button type="submit" class="register-btn">등록</button>
        <router-link to="/mypage/company/store" class="register-btn">취소</router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useStoreStore } from "@/stores/useStoreStore";
import { useToast } from "vue-toastification";

// store, router, route, toast
const storeStore = useStoreStore();
const router = useRouter();
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

// onMounted 처리
onMounted(async () => {
  await loadMapjsApi();
});

// 주소 API 로드
const loadMapjsApi = async () => {
  const script = document.createElement("script");
  script.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
  document.head.appendChild(script);
}

// 주소 검색 처리
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

// 스토어 등록
const register = async () => {
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
  if (files.value.length === 0) {
    toast.error("이미지를 선택해주세요");
    return
  }
  Array.from(files.value).forEach((file) => { formData.append("files", file); });
  const res = await storeStore.registerStore(formData);
  if (res.success) {
    router.push("/mypage/company/store");
    toast.success(res.message);
  } else {
    toast.error(res.message);
  }
};

</script>

<style scoped>
.register-page {
  padding: 5px;
  border-radius: 8px;
}

.register-form {
  padding: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  row-gap: 1rem;
}

.register-input {
  border: 1px solid #e1e1e1;
  border-radius: 4px;
  display: block;
  padding: 1rem;
  font-size: 1rem;
  font-weight: 400;
  line-height: 1.5;
  width: 100%;
  box-sizing: border-box;
  color: #323232;
  background-color: #fff;
  resize: none;
}

#file {
  display: none;
}

.file-preview {
  display: flex;
  align-self: center;
  width: fit-content;
  height: fit-content;
}

.file-upload-btn {
  background-color: #00c7ae;
  color: white;
  padding: 0.75rem;
  text-align: center;
  cursor: pointer;
  border-radius: 5px;
  margin-top: 1rem;
}

.btn-container {
  display: flex;
  gap: 10px;
}

.register-btn {
  display: inline-block;
  text-align: center;
  vertical-align: middle;
  width: 100%;
  user-select: none;
  margin-top: 0.75rem;
  font-weight: 400;
  transition: opacity 0.2s ease-in-out;
  color: #fff;
  cursor: pointer;
  background-color: #00c7ae;
  border-color: #00c7ae;
  border: 0.0625rem solid transparent;
  padding: 0.6875rem 0.75rem;
  font-size: 1rem;
  line-height: 1.5;
  border-radius: 0.25rem;
  text-decoration: none;
}

.file-preview {
  display: flex;
  flex-wrap: wrap;
  margin-top: 1rem;
  gap: 10px;
}

.address {
  position: relative;
  display: flex;
  column-gap: 10px;
  flex-direction: row;
}

.file-preview-item img {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.date-picker {
  display: flex;
  gap: 10px;
}

.file-upload-btn:hover,
.register-btn:hover {
  opacity: 0.8;
}
</style>
