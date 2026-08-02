<template>
  <div class="lyt-child">
    <form class="ctn-rootform ctn-reviewform ctn-popupform" @submit.prevent="updatePopup">
      <div class="ctn-reviewheading">
        <div>
          <span class="txt-eyebrow">POPUP SETTINGS</span>
          <h2>팝업 스토어 수정</h2>
          <p>팝업의 기본 정보와 운영 일정, 대표 이미지를 관리할 수 있습니다.</p>
        </div>
        <div class="ctn-buttons">
          <button type="button" class="btn-normal btn-reviewsubmit" @click="router.back()">취소</button>
          <button type="submit" class="btn-default btn-reviewsubmit">저장</button>
        </div>
      </div>

      <div class="ctn-inputdefault">
        <label class="ipt-default-label" for="popup-name">팝업 스토어 이름</label>
        <input id="popup-name" class="ipt-default" v-model="popupName" type="text" placeholder="팝업 스토어 이름을 입력해주세요." required />
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label" for="popup-content">팝업 스토어 설명</label>
        <textarea id="popup-content" class="ipt-default ipt-reviewcontent" v-model="popupContent" rows="5" placeholder="팝업 스토어에 대한 설명을 입력해주세요." required></textarea>
      </div>
      <div class="ctn-popupformgrid">
        <div class="ctn-inputdefault">
          <label class="ipt-default-label" for="popup-category">카테고리</label>
          <input id="popup-category" class="ipt-default" v-model="category" type="text" placeholder="카테고리를 입력해주세요." required />
        </div>
        <div class="ctn-inputdefault">
          <label class="ipt-default-label" for="popup-people">일일 총 인원</label>
          <input id="popup-people" class="ipt-default" v-model="totalPeople" type="number" min="1" placeholder="총 인원을 입력해주세요." required />
        </div>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">운영 일정</label>
        <div class="ctn-addressfields">
          <input class="ipt-default" v-model="popupStartDate" type="date" aria-label="팝업 시작일" required />
          <input class="ipt-default" v-model="popupEndDate" type="date" aria-label="팝업 종료일" required />
        </div>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">주소</label>
        <div class="ctn-addressfields">
          <input class="ipt-default" v-model="address" type="text" placeholder="주소 검색" @click="openAddressSearch" readonly required />
          <input class="ipt-default" v-model="addressDetail" type="text" placeholder="상세 주소" />
        </div>
      </div>
      <div class="ctn-inputdefault">
        <label class="ipt-default-label">대표 이미지</label>
        <div class="ctn-popupupload">
          <input id="popup-files" class="ipt-filehidden" @change="handleFileUpload" type="file" accept="image/*" name="files" multiple />
          <label class="btn-normal btn-fileupload" for="popup-files">이미지 선택</label>
          <span>새 이미지를 선택하지 않으면 기존 이미지가 유지됩니다.</span>
        </div>
        <div class="wrp-popuppreview" v-if="fileUrls.length">
          <div v-for="(fileUrl, index) in fileUrls" :key="`${fileUrl}-${index}`" class="ctn-popuppreview">
            <img :src="fileUrl" :alt="`팝업 이미지 ${index + 1}`" />
          </div>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useToast } from "vue-toastification";
import { usePopupStore } from "@/stores/popupStore";
import { createMultipartRequest } from "@/utils/multipart";

const popupStore = usePopupStore();
const router = useRouter();
const route = useRoute();
const toast = useToast();
const popupName = ref("");
const popupContent = ref("");
const category = ref("");
const totalPeople = ref(0);
const address = ref("");
const addressDetail = ref("");
const popupStartDate = ref("");
const popupEndDate = ref("");
const fileUrls = ref([]);
const files = ref([]);

onMounted(async () => {
  await getPopup();
  await loadMapjsApi();
});

onBeforeUnmount(() => {
  fileUrls.value.filter(url => url.startsWith("blob:")).forEach(url => URL.revokeObjectURL(url));
});

const getPopup = async () => {
  const res = await popupStore.getPopup(route.params.popupIdx);
  if (!res.success) {
    toast.error(res.message);
    router.push("/mypage/company/popup");
    return;
  }
  const popup = popupStore.popup;
  popupName.value = popup.popupName;
  popupContent.value = popup.popupContent;
  category.value = popup.category;
  totalPeople.value = popup.totalPeople;
  const addressParts = popup.popupAddress?.split(",") || [];
  address.value = addressParts[0] || "";
  addressDetail.value = addressParts.slice(1).join(",") || "";
  popupStartDate.value = popup.popupStartDate;
  popupEndDate.value = popup.popupEndDate;
  fileUrls.value = (popup.getPopupImageResList || []).map(image => image.popupImageUrl);
};

const loadMapjsApi = async () => {
  if (window.daum?.Postcode) return;
  await new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.src = "https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    script.onload = resolve;
    script.onerror = reject;
    document.head.appendChild(script);
  }).catch(() => toast.error("주소 검색 서비스를 불러오지 못했습니다."));
};

const openAddressSearch = async () => {
  if (!window.daum?.Postcode) await loadMapjsApi();
  if (!window.daum?.Postcode) return;
  new window.daum.Postcode({ oncomplete: data => { address.value = data.address; } }).open();
};

const handleFileUpload = event => {
  fileUrls.value.filter(url => url.startsWith("blob:")).forEach(url => URL.revokeObjectURL(url));
  files.value = Array.from(event.target.files || []);
  fileUrls.value = files.value.map(file => URL.createObjectURL(file));
};

const updatePopup = async () => {
  if (popupEndDate.value < popupStartDate.value) {
    toast.error("종료일은 시작일보다 빠를 수 없습니다.");
    return;
  }
  const req = {
    popupName: popupName.value,
    popupContent: popupContent.value,
    category: category.value,
    totalPeople: Number(totalPeople.value),
    popupAddress: [address.value, addressDetail.value].filter(Boolean).join(","),
    popupStartDate: popupStartDate.value,
    popupEndDate: popupEndDate.value,
  };
  const res = await popupStore.updatePopup(route.params.popupIdx, createMultipartRequest(req, files.value));
  if (res.success) {
    toast.success(res.message);
    router.push("/mypage/company/popup");
  } else {
    toast.error(res.message);
  }
};
</script>
