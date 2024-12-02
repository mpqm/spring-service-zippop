<template>
  <div class="register-page">
    <form class="register-form" @submit.prevent="register">
      <div>
        <label>굿즈 이름</label>
        <input class="register-input" v-model="goodsName" type="text" placeholder="팝업 굿즈 이름을 입력해주세요." />
      </div>
      <div>
        <label>굿즈 설명</label>
        <textarea class="register-input" v-model="goodsContent" placeholder="팝업 굿즈에 대한 설명을 입력해주세요."></textarea>
      </div>
      <div>
        <label>굿즈 가격</label>
        <input class="register-input" v-model="goodsPrice" type="number" placeholder="팝업 굿즈 가격을 입력해주세요." />
      </div>
      <div>
        <label>굿즈 수량</label>
        <input class="register-input" v-model="goodsAmount" type="number" placeholder="팝업 굿즈 수량을 입력해주세요." />
      </div>
      <label for="file">
        <div class="file-upload-btn">팝업 굿즈 이미지 파일 업로드</div>
      </label>
      <input @change="handleFileUpload" type="file" name="file" id="file" multiple />
      <div class="file-preview" v-if="fileUrls.length">
        <div v-for="(fileUrl, index) in fileUrls" :key="index" class="file-preview-item">
          <img :src="fileUrl" alt="file preview" />
        </div>
      </div>
      <div class="btn-container">
        <button type="submit" class="register-btn">등록</button>
        <router-link to="/mypage/company/goods/" class="register-btn">취소</router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref } from "vue";
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
const fileUrls = ref([]);
const files = ref([]);

// 파일 업로드 
const handleFileUpload = (event) => {
  files.value = event.target.files;
  fileUrls.value = [];
  for (let i = 0; i < files.value.length; i++) {
    const file = files.value[i];
    fileUrls.value.push(URL.createObjectURL(file));
  }
};

// 굿즈 등록
const register = async () => {
  const req = {
    goodsName: goodsName.value,
    goodsAmount: goodsAmount.value,
    goodsPrice: goodsPrice.value,
    goodsContent: goodsContent.value,
  };
  const formData = new FormData();
  formData.append("dto", new Blob([JSON.stringify(req)], { type: "application/json" }));
  if (files.value.length === 0) {
    toast.error("이미지를 선택해주세요");
    return
  }
  Array.from(files.value).forEach((file) => { formData.append("files", file); });
  const res = await goodsStore.register(route.params.storeIdx, formData);
  if (res.success) {
    router.push(`/mypage/company/goods/${route.params.storeIdx}`);
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

.file-preview-item img {
  width: 100px;
  height: 100px;
  object-fit: cover;
}
</style>
