<template>
    <div class="update-page">
      <form class="update-form" @submit.prevent="update()">
        <div>
          <label>팝업 스토어 이름</label>
          <input class="update-input" v-model="storeName" type="text" placeholder="팝업 스토어 이름을 입력해주세요."/>
        </div>
        <div>
          <label>팝업 스토어 설명</label>
          <textarea class="update-input" v-model="storeContent" placeholder="팝업 스토어에 대한 설명을 입력해주세요."></textarea>
        </div>
        <div>
          <label>팝업 스토어 카테고리</label>
          <input class="update-input" v-model="category" type="text" placeholder="팝업 스토어 카테고리를 입력해주세요." />
        </div>
        <div>
          <label>팝업 스토어 온라인 총 인원</label>
          <input class="update-input" v-model="totalPeople" type="number" placeholder="팝업 스토어 카테고리를 입력해주세요." />
        </div>
        <div>
          <label>팝업스토어 시작일/종료일</label>
          <div class="date-picker">
            <input class="update-input" v-model="storeStartDate" type="date" placeholder="시작일" />
            <input class="update-input" v-model="storeEndDate" type="date" placeholder="종료일" />
          </div>
        </div>
        <div>
          <label>주소/상세주소</label>
          <div class="address">
            <input class="update-input" v-model="address" type="text" placeholder="주소" @click="openAddressSearch" />
            <input class="update-input" v-model="addressDetail" type="text" placeholder="상세 주소" />
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
          <button type="submit" class="update-btn">수정</button>
          <router-link to="/mypage/company/store" class="update-btn">취소</router-link>
        </div>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from "vue";
  import { useRouter, useRoute } from "vue-router";
  import { useStoreStore } from "@/stores/useStoreStore";
  import { useToast } from "vue-toastification";
  
  const storeStore = useStoreStore();
  const router = useRouter();
  const route = useRoute();
  const toast = useToast();

  const storeIdx = ref(null);
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

  onMounted(async() => {
    await search(route.params.storeIdx);
    await loadMapjsApi();
  });
  
  const search = async(storeIdx) => {
    const res = await storeStore.search(storeIdx);
    if(res.success){
        store.value = storeStore.store;
        await autoSet();
    }else {
        router.push("/mypage/company/store")
        toast.error("해당 팝업스토어를 찾을 수 없습니다.")
    }
  }

  const autoSet = async() => {
    storeIdx.value = storeStore.store.storeIdx;
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

  const loadMapjsApi = async() => {
    const script = document.createElement("script");
    script.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    document.head.appendChild(script);
  }

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
      const file = files.value[i];
      fileUrls.value.push(URL.createObjectURL(file));
    }
  };
  
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
  
    Array.from(files.value).forEach((file) => {
      formData.append("files", file); 
    });
  
    const res = await storeStore.update(storeIdx.value, formData);
    if (res.success) {
        toast.success("팝업 스토어 수정에 성공했습니다.");
        router.push("/mypage/company/store");
    } else {
        toast.error("팝업 스토어 수정에 실패했습니다.");
    }
  };
  </script>
  
  <style scoped>
  .update-page {
    padding: 5px;
    border-radius: 8px;
  }
  
  .update-form {
    padding: 10px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    row-gap: 1rem;
  }
  
  .update-input {
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
  
  .update-btn {
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
      position:relative;
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
  </style>
  