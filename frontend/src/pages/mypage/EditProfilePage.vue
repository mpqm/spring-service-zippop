<template>
    <div>
        <hr>
        <div class="edit-profile-container">
            <h3 class="t1">회원정보 수정</h3>
            <form @submit.prevent="editInfo" class="edit-profile-form">
                <div class="form-group">
                    <label for="name">이름</label>
                    <input type="text" id="name" v-model="name" required />
                </div>
                <div class="form-group">
                    <label for="phone">전화번호</label>
                    <input type="tel" id="phone" v-model="phoneNumber" required />
                </div>
                <div class="form-group" v-if="showCrn">
                    <label for="phone">사업자 등록번호</label>
                    <input type="tel" id="phone" v-model="crn" required />
                </div>
                <div>
                    <label>주소/상세주소</label>
                    <div class="address">
                        <input class="edit-input" v-model="address" type="text" placeholder="주소" @click="openAddressSearch">
                        <input class="edit-input" v-model="addressDetail" type="text" placeholder="상세 주소">
                    </div>
                </div>
                <label for="file">
                    <div class="file-upload-btn">프로필 파일 업로드</div>
                </label>
                <input @change="handleFileUpload" type="file" name="file" id="file">
                <div class="file-preview" v-if="fileUrl"><img :src="fileUrl" /></div>
                <button type="submit" class="submit-btn">수정하기</button>
            </form>
        </div>
        <hr>
        <div class="edit-password-container">
            <h3 class="t1">비밀번호 변경</h3>
            <form @submit.prevent="editPassword" class="edit-profile-form">
                <div class="form-group">
                    <label for="name">기존 비밀번호</label>
                    <input type="password" v-model="originPassword" required />
                </div>
                <div class="form-group">
                    <label for="phone">새 비밀번호</label>
                    <input type="password" v-model="newPassword" required />
                </div>
                <button type="submit" class="submit-btn">비밀번호 변경하기</button>
            </form>
        </div>
        <hr>
        <div class="inActive-container">
            <h3 class="t1">계정 비활성화</h3>
            <button class="inActive-btn" @click="inActive">계정 비활성화</button>
        </div>
        <hr>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/useAuthStore';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
const authStore = useAuthStore();
const toast = useToast();
const router = useRouter();

const name = ref("");
const phoneNumber = ref("");
const address = ref("");
const addressDetail = ref("");
const crn = ref("");
const showCrn = ref(false);
const fileUrl = ref(null);
const file = ref(null);

const originPassword = ref("");
const newPassword = ref("");

onMounted(async () => {
    await loadMapjsApi();
    await authStore.getInfo();
    await autoSet();
})

const loadMapjsApi = async () => {
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

const autoSet = async () => {
    name.value = authStore.userInfo.name;
    phoneNumber.value = authStore.userInfo.phoneNumber;
    address.value = authStore.userInfo.address;
    crn.value = authStore.userInfo.crn;
    address.value = authStore.userInfo.address.split(',')[0]; 
    addressDetail.value = authStore.userInfo.address.split(',')[1] || '';
    if (authStore.userInfo.role == "ROLE_COMPANY") {
        showCrn.value = true;
    } else {
        showCrn.value = false;
    }
    fileUrl.value = authStore.userInfo.profileImageUrl || null;
}

const handleFileUpload = (event) => {
    file.value = event.target.files[0];
    if (file.value) { fileUrl.value = URL.createObjectURL(file.value); }
};

  const editInfo = async( ) => {
    const req = {
        name: name.value,
        phoneNumber: phoneNumber.value,
        address: address.value + ',' + addressDetail.value,
        crn: crn.value,
    }
    const res = await authStore.editInfo(req)
    if (res.success) {
        toast.success(res.message);
        router.push("/");
    } else {
        toast.error(res.message);
    }
}
const editPassword = async( ) => {
    const req = {
        newPassword: newPassword.value,
        originPassword:originPassword.value
    }
    const res = await authStore.editPassword(req)
    if (res.success) {
        toast.success(res.message);
        router.push("/");
    } else {
        toast.error(res.message);
    }
}

const inActive = async( ) => {
    const res = await authStore.inActive()
    if (res.success) {
        toast.success(res.message);
        router.push("/");
    } else {
        toast.error(res.message);
    }
}
</script>

<style scoped>
.edit-profile-container, .inActive-container, .edit-password-container {
    width: auto;
    padding: 10px;
}
hr {
    border : 1px solid #00c7aa;
    background-color:#00c7aa;
    margin: 0 5px;
}
.t1 {
    text-align: left;
    margin-bottom: 20px;
    color: #333;
}

.edit-profile-form {
    display: flex;
    flex-direction: column;
}

.form-group {
    margin-bottom: 15px;
}

label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
    color: #333;
}

input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    box-sizing: border-box;
    transition: border-color 0.3s;
}

input:focus {
    border-color: #00c7aa;
}

.submit-btn {
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

.submit-btn:hover, .inActive-btn:hover, .file-upload-btn:hover {
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
.address {
    position:relative;
    display: flex;
    column-gap: 10px;
    flex-direction: row;
}

.edit-input {
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
    box-sizing: border-box;
}

.inActive-btn{
    padding: 10px 15px;
    background-color: red;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
}
</style>