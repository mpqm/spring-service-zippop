<template>

    <div class="lyt-child">
        <div class="ctn-rootform">
            <form @submit.prevent="editInfo" class="ctn-chidform">
                <div class="ctn-split">
                    <h1 class="txt-def0">회원정보 수정</h1>
                    <button class="btn-default" type="submit">수정하기</button>
                </div>
                <div class="ctn-inputdefault">
                    <label class="ipt-default-label">이름</label>
                    <input class="ipt-default" type="text" id="name" v-model="userInfo.name" required />
                </div>
                <div class="ctn-inputdefault">
                    <label class="ipt-default-label">전화번호</label>
                    <input class="ipt-default" type="tel" id="phone" v-model="userInfo.phoneNumber" required />
                </div>
                <div class="ctn-inputdefault" v-if="showCrn">
                    <label class="ipt-default-label">사업자 등록번호</label>
                    <input class="ipt-default" type="tel" id="phone" v-model="userInfo.crn" required />
                </div>
                <div class="ctn-inputdefault">
                    <label class="ipt-default-label">주소/상세주소</label>
                    <div class="ctn-split">
                        <input class="ipt-default" v-model="address" type="text" placeholder="주소" @click="openAddressSearch">
                        <input class="ipt-default" v-model="addressDetail" type="text" placeholder="상세 주소">
                    </div>
                </div>
                
                <!-- 프로필 파일 업로드 -->
                <input @change="handleFileUpload" type="file" name="file" id="file">
                <div class="ctn-filepreview" v-if="fileUrl"><img :src="fileUrl" /></div>
                <label for="file"><div class="btn-default">프로필 파일 업로드</div></label>

            </form>
            <form @submit.prevent="editPassword" class="ctn-chidform">
                <div class="ctn-split">
                    <h1 class="txt-def0">비밀번호 변경</h1>
                    <button class="btn-default" type="submit">비밀번호 변경</button>
                </div>
                <div class="ctn-inputdefault">
                    <label class="ipt-default-label">기존 비밀번호</label>
                    <input class="ipt-default" type="password" v-model="originPassword" required />
                </div>
                <div class="ctn-inputdefault">
                    <label class="ipt-default-label">새 비밀번호</label>
                    <input class="ipt-default" type="password" v-model="newPassword" required />
                </div>
            </form>

            <div class="ctn-chidform">
                <div class="ctn-split">
                    <h1 class="txt-def0">계정 비활성화</h1>
                    <button class="btn-default" @click="inActive">계정 비활성화</button>
                </div>
            </div>  
        </div>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/useAuthStore';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';

// store, router, route, toast
const authStore = useAuthStore();
const toast = useToast();
const router = useRouter();

// 변수(auth)
const userInfo = ref({})
const address = ref("");
const addressDetail = ref("");
const showCrn = ref(false);
const fileUrl = ref(null);
const file = ref(null);
const originPassword = ref("");
const newPassword = ref("");

// onMounted 
onMounted(async () => {
    await loadMapjsApi();
    await getInfo();
})

// 유저 정보 불러오기
const getInfo = async () => {
    const res = await authStore.getInfo();
    if (res.success) {
        userInfo.value = authStore.userInfo;
        mapper();
    } else {
        router.push("/")
        toast.error(res.message);
    }
}

// 주소 API 로드
const loadMapjsApi = async () => {
    const script = document.createElement("script");
    script.src = "https://txt-def1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
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

// 매핑 함수
const mapper = async () => {
    address.value = authStore.userInfo.address.split(',')[0];
    addressDetail.value = authStore.userInfo.address.split(',')[1] || '';
    fileUrl.value = authStore.userInfo.profileImageUrl || null;
    if(authStore.userInfo.role == "ROLE_COMPANY") {
        showCrn.value = true;
    } else {
        showCrn.value = false;
    }
}

// 파일 업로드
const handleFileUpload = (event) => {
    file.value = event.target.files[0];
    if (file.value) { fileUrl.value = URL.createObjectURL(file.value); }
};

// 유저 정보 수정
const editInfo = async () => {
    const formData = new FormData();
    const req = {
        name: userInfo.value.name,
        phoneNumber: userInfo.value.phoneNumber,
        address: address.value + ',' + addressDetail.value,
        profileImageUrl: file.value ? null : authStore.userInfo.profileImageUrl, // 새 파일이 있으면 null, 없으면 기존 URL 유지
        crn: userInfo.value.crn,
    }
    formData.append('dto', new Blob([JSON.stringify(req)], { type: 'application/json' }));
    if (file.value) { formData.append('file', file.value); }

    const res = await authStore.editInfo(formData)
    if (res.success) {
        toast.success(res.message);
        router.push("/");
    } else {
        toast.error(res.message);
    }
}

// 비밀번호 수정
const editPassword = async () => {
    const req = {
        newPassword: newPassword.value,
        originPassword: originPassword.value
    }
    const res = await authStore.editPassword(req)
    if (res.success) {
        toast.success(res.message);
        router.push("/");
    } else {
        toast.error(res.message);
    }
}

// 계정 비활성화
const inActive = async () => {
    const res = await authStore.inActive()
    if (res.success) {
        toast.success(res.message);
        router.push("/");
    } else {
        toast.error(res.message);
    }
}

</script>