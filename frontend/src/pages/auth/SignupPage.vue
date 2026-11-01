<template>
    <div>
        <AppHeader></AppHeader>
        <div class="lyt-centertop">
            <div class="wrp-centertop">
                <img class="img-mainlogo" src="../../assets/img/zippoicon-removebg.png" alt="ZIPPOP">
                
                <!-- 회원 유형 선택 탭 -->
                <div class="ctn-buttons ctn-accounttypes">
                    <button type="button" class="btn-big" :class="{ active: userType === 'customer' }" :aria-pressed="userType === 'customer'" @click="userType = 'customer'"><Icon icon="iconoir:user-cart" width="22" height="22" />고객 회원</button>
                    <button type="button" class="btn-big" :class="{ active: userType === 'company' }" :aria-pressed="userType === 'company'" @click="userType = 'company'"><Icon icon="iconoir:user-badge-check" width="22" height="22" />기업 회원</button>
                </div>
                
                <form class="ctn-rootform ctn-formcard" @submit.prevent="createAccount">
                    <!-- 아이디 -->
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">아이디</label>
                        <input class="ipt-default" v-model="userId" type="text" placeholder="아이디를 입력해주세요" />    
                    </div>

                    <!-- 비밀번호 -->
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">비밀번호</label>
                        <input class="ipt-default" v-model="password" type="password" placeholder="비밀번호를 입력해 주세요." />
                    </div>

                    <!-- 이름 (고객) / 회사명 (기업) -->
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">{{ userType === 'customer' ? '회원 이름' : '회사명' }} </label>
                        <input class="ipt-default" v-model="name" type="text" :placeholder="userType === 'customer' ? '이름(실명)을 입력해 주세요.' : '회사명을 입력해 주세요.'" />
                    </div>
                    
                    <!-- 이메일 -->
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label"> {{ userType === 'customer' ? '이메일' : '회사 대표 이메일' }}</label>
                        <input class="ipt-default" v-model="email" type="email" placeholder="ex) example@example.com" />
                    </div>

                    <!-- 전화번호 -->
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label"> {{ userType === 'customer' ? '휴대폰번호' : '회사 대표 번호' }}
                        </label>
                        <input class="ipt-default" v-model="phoneNumber" type="text" placeholder="ex) 01012341234" />
                    </div>

                    <!-- 사업자등록번호 (기업만) -->
                    <div class="ctn-inputdefault" v-if="userType === 'company'">
                        <label class="ipt-default-label">사업자등록번호</label>
                        <input class="ipt-default" v-model="crn" type="text" placeholder="사업자등록번호" />
                    </div>

                    <!-- 주소 -->
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">{{ userType === 'customer' ? '주소' : '회사 주소' }} </label>
                        <input class="ipt-default" v-model="address" type="text" placeholder="주소" @click="openAddressSearch"/>
                    </div>

                    <!-- 상세 주소 -->
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label">{{ userType === 'customer' ? '상세주소' : '회사 상세 주소' }}</label>
                        <input class="ipt-default" v-model="addressDetail" type="text" placeholder="상세 주소" />
                    </div>

                    <!-- 프로필 파일 업로드 -->
                    <div class="ctn-inputdefault">
                        <label class="ipt-default-label" for="profile-file">프로필</label>
                        <div class="ctn-profileupload">
                            <div class="ctn-profileavatar">
                                <img v-if="fileUrl" :src="fileUrl" alt="선택한 프로필 이미지 미리보기" />
                                <Icon v-else icon="iconoir:user" width="44" height="44" aria-hidden="true" />
                            </div>
                            <label class="btn-profileupload" for="profile-file" aria-label="프로필 이미지 선택" title="프로필 이미지 선택">
                                <Icon icon="iconoir:camera" width="18" height="18" />
                            </label>
                            <input id="profile-file" class="ipt-filehidden" @change="handleFileUpload" type="file" name="file" accept="image/*">
                        </div>
                    </div>
                    
                    <!-- 제출 버튼 -->
                    <button class="btn-default" type="submit"> {{ userType === 'customer' ? '고객 회원가입' : '기업 회원가입' }} </button>

                    <!-- 안내 -->
                    <p class="ctn-noticedefault">
                        타인 명의로 가입 시 계정이 정지되고 재가입이 불가능합니다.<br>
                        가입 후 이메일 인증까지 완료하여야 계정이 활성화됩니다.<br>
                        (유효시간 3분)
                    </p>
                </form>
            </div>
        </div>
        <AppFooter></AppFooter>
    </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useAccountStore } from '@/stores/accountStore';
import AppHeader from '@/components/AppHeader.vue';
import AppFooter from "@/components/AppFooter.vue";
import { useToast } from "vue-toastification";
import { createMultipartRequest } from "@/utils/multipart";
import { Icon } from "@iconify/vue";

const accountStore = useAccountStore();
const router = useRouter();
const toast = useToast();

const userType = ref('customer');
const userId = ref("");
const email = ref("");
const password = ref("");
const name = ref("");
const phoneNumber = ref("");
const address = ref("");
const addressDetail = ref("");
const crn = ref("");
const file = ref(null);
const fileUrl = ref(null);

// 파일 업로드 
const handleFileUpload = (event) => {
    file.value = event.target.files[0];
    if (file.value) { 
        fileUrl.value = URL.createObjectURL(file.value); 
    }
};

// 주소 검색 처리
const openAddressSearch = async () => {
    try {
        // Daum 우편번호 스크립트가 없다면 로드
        if (!window.daum || !window.daum.Postcode) {

        await new Promise((resolve, reject) => {
            const script = document.createElement("script");
            script.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
            script.onload = () => resolve();
            script.onerror = () => reject(toast.error("스크립트 로딩 실패"));
            document.head.appendChild(script);
        });
        }

        // Daum 객체 확인
        if (!window.daum || !window.daum.Postcode) toast.error("Daum 우편번호 서비스를 사용할 수 없습니다.");

        // eslint-disable-next-line no-undef
        new daum.Postcode({oncomplete: (data) => address.value = data.address}).open();

    } catch (error) {
        toast.error("주소 검색 서비스를 불러올 수 없습니다.");
    }
};

// 회원 가입 
const createAccount = async () => {
    const req = {
        role: userType.value === 'customer' ? "ROLE_CUSTOMER" : "ROLE_COMPANY",
        userId: userId.value,
        email: email.value,
        password: password.value,
        name: name.value,
        phoneNumber: phoneNumber.value,
        address: address.value + ',' + addressDetail.value,
        ...(userType.value === 'company' && { crn: crn.value })
    };

    const formData = createMultipartRequest(req, file.value ? [file.value] : [], 'file');
    
    const res = await accountStore.createAccount(formData);
    if (res.success) {
        await router.push("/login");
        toast.success(res.message);
    } else {
        toast.error(res.message);
    }
};

</script>

