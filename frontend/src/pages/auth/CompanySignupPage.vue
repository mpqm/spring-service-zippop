<template>
<div>
    <HeaderComponent></HeaderComponent>
    <div class="signup-page">
        <div class="signup-container">
            <form class="signup-form" @submit.prevent="signup">
                <img class="logo-img" src="../../assets/img/zippopbanner.png">
                <div>
                    <label>이메일</label>
                    <input class="signup-input" v-model="email" type="email" placeholder="ex) example@example.com"/>
                </div>
                <div>
                    <label >비밀번호</label>
                    <input class="signup-input" v-model="password" type="password" placeholder="비밀번호를 입력해 주세요."/>
                </div>
                <div>
                    <label >회원 이름</label>
                    <input class="signup-input" v-model="name" type="text" placeholder="이름(실명)을 입력해 주세요."/>
                </div>
                <div>
                    <label >휴대폰번호</label>
                    <input class="signup-input" v-model="phoneNumber" type="text" placeholder="ex) 01012341234"/>
                </div>
                <div>
                    <label >사업자등록번호</label>
                    <input class="signup-input" v-model="crn" type="password" placeholder="사업자등록번호"/>
                </div>
                <div>
                    <label >주소/상세주소</label>
                    <div class="address">
                    <input class="signup-input" v-model="address" type="text" placeholder="주소" @click="openAddressSearch">
                    <input class="signup-input" v-model="addressDetail" type="text" placeholder="상세 주소">
                </div>
                </div>

                <label for="file">
                    <div class="file-upload-btn">프로필 파일 업로드</div>
                </label>
                <input @change="handleFileUpload" type="file" name="file" id="file">
                <div class="file-preview" v-if="fileUrl">
                    <img :src="fileUrl" />
                </div>
                <div>
                    <p class="signup-notice">타인 명의로 가입 시 계정이 정지되고 재가입이 불가능합니다.<br>가입 후 이메일 인증까지 완료하여야 계정이 활성화됩니다. (유효시간3분)</p>
                    <button class="signup-btn" type="submit">기업 회원가입</button>
                </div>
            </form>
        </div>
    </div>
    <FooterComponent></FooterComponent>
</div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from '@/stores/useAuthStore';
import HeaderComponent from '@/components/HeaderComponent.vue';
import FooterComponent from "@/components/FooterComponent.vue";
import { useToast } from "vue-toastification";

const authStore = useAuthStore();
const router = useRouter();
const toast = useToast();

const email = ref("");
const password = ref("");
const name = ref("")
const phoneNumber = ref("")
const address = ref("")
const addressDetail = ref("")
const crn = ref(null);
const file = ref(null);
const fileUrl = ref(null);

onMounted(() => {
  const script = document.createElement('script');
  script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
  document.head.appendChild(script);
});

// 주소 검색
const openAddressSearch = () => {
    // eslint-disable-next-line no-undef
    new daum.Postcode({
    oncomplete: function (data) {
      address.value = data.address;
    }
  }).open();
};

const handleFileUpload = (event) => {
    file.value = event.target.files[0];
    if (file.value) { fileUrl.value = URL.createObjectURL(file.value); }
};

const signup = async () => {
    const formData = new FormData();
    const dto = {
        role: "ROLE_COMPANY",
        email: email.value,
        password: password.value,
        name: name.value,
        crn: crn.value,
        phone_number: phoneNumber.value,
        address: address.value + ',' + addressDetail.value
    }
    formData.append('dto', new Blob([JSON.stringify(dto)], { type: 'application/json' }));
    if (file.value) { formData.append('file', file.value); }

    const res = await authStore.signup(formData);
    if (res.success) {
        router.push("/");
        toast.success("가입 후 이메일 인증까지 완료하여야 계정이 활성화됩니다.");
    } else {
        toast.error("회원가입에 실패했습니다.")
    }
};
</script>

<style scoped>
.signup-page {
    padding: 3rem 0;
    background-color: #fafafa;
}

.signup-container {
    position: relative;
    display: flex;
    flex-direction: column;
    min-width: 0;
    word-wrap: break-word;
    background-color: #fff;
    background-clip: border-box;
    width: 40%;
    padding: 1rem;
    margin: 0 auto;
    border: 1px solid #f2f2f2;
    border-radius: 8px;
    box-shadow: none;
}

.signup-form {
    display: flex;
    margin-top: 0em;
    unicode-bidi: isolate;
    flex-wrap: wrap;
    flex-direction: column;
    justify-content: center;
    row-gap: 1rem;
}

.signup-btn {
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
}

.kakao-signup-btn {
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
    background-color: #fee500;
    border-color: #fee500;
    color: rgba(0, 0, 0, .85);
    border: 0.0625rem solid transparent;
    padding: 0.6875rem 0.75rem;
    font-size: 1rem;
    line-height: 1.5;
    border-radius: 0.25rem;
}

.file-upload-btn {
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
}

.signup-btn:hover, .file-upload-btn:hover {
    opacity: 0.8;
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

.signup-input {
    padding: 1rem;
    border: 1px solid #e1e1e1;
    border-radius: 4px;
    display: block;
    padding: 0.6875rem 1rem;
    font-size: 1rem;
    font-weight: 400;
    line-height: 1.5;
    width: 100%;
    color: #323232;
    background-color: #fff;
    background-clip: padding-box;
}

.logo-img {
    align-self: center;
    width: 300px;
    height: 100px;
}

.signup-notice {
    text-align: center;
    display: flex;
    flex-direction: column;
    padding: 8px;
    border-radius: 8px;
    background-color: #F2F2F2;
    margin: 0;
    font-size: 14px;
}

.address {
    position:relative;
    display: flex;
    column-gap: 10px;
    flex-direction: row;
}
</style>
