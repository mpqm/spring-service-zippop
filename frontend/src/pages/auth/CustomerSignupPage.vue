<template>
<div>
    <HeaderComponent></HeaderComponent>
    <div class="signup-page">
        <div class="signup-container">
            <img class="logo-img" src="../../assets/img/zippopbanner.png">
            <div class="set-user">
                <div class="customer"> 고객 가입</div>
                <a class="company" href="/signup/company">기업 가입</a>
            </div>
            <form class="signup-form" @submit.prevent="signup">
                <div>
                    <label>아이디</label>
                    <input class="signup-input" v-model="userId" type="id" placeholder="아이디를 입력해주세요"/>
                </div>
                <div>
                    <label >비밀번호</label>
                    <input class="signup-input" v-model="password" type="password" placeholder="비밀번호를 입력해 주세요."/>
                </div>
                <div>
                    <label>이메일</label>
                    <input class="signup-input" v-model="email" type="email" placeholder="ex) example@example.com"/>
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
                    <button class="signup-btn" type="submit">고객 회원가입</button>
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

const userId = ref("");
const email = ref("");
const password = ref("");
const name = ref("")
const phoneNumber = ref("")
const address = ref("")
const addressDetail = ref("")

const file = ref(null);
const fileUrl = ref(null);

onMounted(async() => {
    await loadMapjsApi()
});


const loadMapjsApi = async() => {
    const script = document.createElement("script");
    script.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    document.head.appendChild(script);
}

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
    const req = {
        role: "ROLE_CUSTOMER",
        userId: userId.value,
        email: email.value,
        password: password.value,
        name: name.value,
        phone_number: phoneNumber.value,
        address: address.value + ',' + addressDetail.value
    }
    
    formData.append('dto', new Blob([JSON.stringify(req)], { type: 'application/json' }));
    if (file.value) { formData.append('file', file.value); }

    const res = await authStore.signup(formData);
    if (res.success) {
        router.push("/");
        toast.success(res.message);
    } else {
        toast.error(res.message);
    }
};

</script>

<style scoped>
.signup-page {
    padding: 3rem 0;
    background-color: #fff;
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
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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
    box-sizing: border-box;
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

.set-user {
    margin-top: 10px;
    display: flex;
    flex-direction: row;
    justify-self: center;
    width: auto;
    margin-bottom: 12px;
    
    background-color: #fff;
    box-sizing: border-box;
    align-items: center;
    justify-content: space-between;
    height: 50px;
    column-gap: 10px;
}

.customer {
    cursor: pointer;
    align-items: center;
    justify-content: center;
    display: flex;
    flex: 1;
    width: 100%;
    height: 100%;
    background-color: #00c7ae;
    border-radius: 8px;
    color: white;
    font-weight: 600;
    line-height: 26px;
    font-size: 1rem;
    text-decoration: none;
}

.company {
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    border: 1px solid #e8e8e8;
    display: flex;
    flex: 1;
    width: 100%;
    height: 100%;
    font-weight: 600;
    line-height: 26px;
    font-size: 1rem;
    text-decoration: none;
    color: #000;
}

</style>
