<template>
  <div class="lyt-child">
    <div class="ctn-rootform ctn-accountforms">
      <form @submit.prevent="updateAccount" class="ctn-rootform ctn-reviewform">
        <div class="ctn-reviewheading">
          <div>
            <span class="txt-eyebrow">PROFILE SETTINGS</span>
            <h2>회원정보 수정</h2>
            <p>이름과 연락처, 주소 및 프로필 이미지를 관리할 수 있습니다.</p>
          </div>
          <button class="btn-default btn-reviewsubmit" type="submit">저장</button>
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
          <input class="ipt-default" type="tel" v-model="userInfo.crn" required />
        </div>
        <div class="ctn-inputdefault">
          <label class="ipt-default-label">주소/상세주소</label>
          <div class="ctn-addressfields">
            <input class="ipt-default" v-model="address" type="text" placeholder="주소" @click="openAddressSearch">
            <input class="ipt-default" v-model="addressDetail" type="text" placeholder="상세 주소">
          </div>
        </div>
        <div class="ctn-inputdefault">
          <label class="ipt-default-label">프로필 이미지</label>
          <div class="ctn-profileupload">
            <div class="ctn-filepreview" v-if="fileUrl"><img :src="fileUrl" alt="프로필 미리보기" /></div>
            <input class="ipt-filehidden" @change="handleFileUpload" type="file" accept="image/*" name="file" id="file">
            <label class="btn-normal btn-fileupload" for="file">이미지 선택</label>
          </div>
        </div>
      </form>

      <form @submit.prevent="editPassword" class="ctn-rootform ctn-reviewform">
        <div class="ctn-reviewheading">
          <div>
            <span class="txt-eyebrow">SECURITY</span>
            <h2>비밀번호 변경</h2>
            <p>현재 비밀번호를 확인한 후 새로운 비밀번호로 변경합니다.</p>
          </div>
          <button class="btn-default btn-reviewsubmit" type="submit">변경</button>
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

      <div class="ctn-reviewform ctn-accountdanger">
        <div class="ctn-reviewheading">
          <div>
            <span class="txt-eyebrow">ACCOUNT STATUS</span>
            <h2>계정 비활성화</h2>
            <p>계정을 비활성화하면 ZIPPOP 서비스를 더 이상 이용할 수 없습니다.</p>
          </div>
          <button class="btn-danger" type="button" @click="deactivateAccount">계정 비활성화</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAccountStore } from '@/stores/accountStore';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'vue-toastification';
import { createMultipartRequest } from '@/utils/multipart';

const accountStore = useAccountStore();
const toast = useToast();
const router = useRouter();

const userInfo = ref({});
const address = ref("");
const addressDetail = ref("");
const showCrn = ref(false);
const fileUrl = ref(null);
const file = ref(null);
const originPassword = ref("");
const newPassword = ref("");

onMounted(async () => {
  await loadMapjsApi();
  await getAccount();
});

const getAccount = async () => {
  const res = await accountStore.getAccount();
  if (res.success) {
    userInfo.value = accountStore.userInfo;
    const info = accountStore.userInfo;
    address.value = info.address?.split(',')[0] || '';
    addressDetail.value = info.address?.split(',')[1] || '';
    fileUrl.value = info.profileImageUrl || null;
    showCrn.value = info.role === "ROLE_COMPANY";
  } else {
    router.push("/");
    toast.error(res.message);
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
    oncomplete: function (data) { address.value = data.address; }
  }).open();
};

const handleFileUpload = (event) => {
  const selectedFile = event.target.files?.[0];
  if (!selectedFile) return;
  if (!selectedFile.type.startsWith('image/')) {
    toast.error('이미지 파일만 선택할 수 있습니다.');
    event.target.value = '';
    return;
  }
  if (selectedFile.size > 5 * 1024 * 1024) {
    toast.error('프로필 이미지는 5MB 이하만 등록할 수 있습니다.');
    event.target.value = '';
    return;
  }
  if (fileUrl.value?.startsWith('blob:')) URL.revokeObjectURL(fileUrl.value);
  file.value = selectedFile;
  fileUrl.value = URL.createObjectURL(selectedFile);
};

const updateAccount = async () => {
  const req = {
    name: userInfo.value.name,
    phoneNumber: userInfo.value.phoneNumber,
    address: address.value + ',' + addressDetail.value,
    profileImageUrl: file.value ? null : accountStore.userInfo.profileImageUrl,
    crn: userInfo.value.crn,
  };
  const formData = createMultipartRequest(req, file.value ? [file.value] : [], 'file');

  const res = await accountStore.updateAccount(formData);
  if (res.success) {
    await accountStore.getAccount();
    toast.success(res.message);
    router.push("/");
  } else {
    toast.error(res.message);
  }
};

const editPassword = async () => {
  const req = { newPassword: newPassword.value, originPassword: originPassword.value };
  const res = await accountStore.resetPassword(req);
  if (res.success) {
    toast.success(res.message);
    router.push("/");
  } else {
    toast.error(res.message);
  }
};

const deactivateAccount = async () => {
  const res = await accountStore.deactivateAccount();
  if (res.success) {
    toast.success(res.message);
    router.push("/");
  } else {
    toast.error(res.message);
  }
};
</script>
