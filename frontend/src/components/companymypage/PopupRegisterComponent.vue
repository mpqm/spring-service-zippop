<template>
  <div class="container">
    <form @submit.prevent="register">
      <fieldset data-v-74c08a55="" data-v-ab788270="" class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">팝업 스토어 이름
        </legend>
        <div><!---->
          <div data-v-74c08a55="" class="input-wrap"><input v-model="popupStore.storeName" name="storeName"
              data-v-74c08a55="" type="text" placeholder="ex) 카카오 프렌즈 팝업" class="form-control is-invalid" maxlength="50"
              aria-invalid="true" id="__BVID__1434"></div>
          <div data-v-74c08a55="" class="info-wrap">
            <div data-v-74c08a55="" class="d-block invalid-feedback">팝업 스토어 이름을 입력해 주세요.</div>
          </div><!----><!----><!---->
        </div>
      </fieldset>
      <fieldset data-v-74c08a55="" data-v-ab788270="" class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">팝업 스토어 주소
        </legend>
        <div><!---->
          <div data-v-74c08a55="" class="input-wrap"><input v-model="popupStore.storeAddress" name="storeAddress"
              data-v-74c08a55="" type="text" placeholder="ex) 서울특별시 동작구 보라매로 87" class="form-control is-invalid"
              minlength="10" maxlength="50" aria-invalid="true" id="__BVID__1434"></div>
          <div data-v-74c08a55="" class="info-wrap">
            <div data-v-74c08a55="" class="d-block invalid-feedback">팝업 스토어 주소를 입력해 주세요.</div>
          </div><!----><!----><!---->
        </div>
      </fieldset>
      <fieldset data-v-74c08a55="" data-v-ab788270="" class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">팝업 스토어 시작일
        </legend>
        <div><!---->
          <div data-v-74c08a55="" class="input-wrap"><input v-model="popupStore.storeEndDate" data-v-74c08a55=""
              name="storeEndDate" type="date" placeholder="ex) 2024-08-07" class="form-control is-invalid"
              minlength="10" maxlength="50" aria-invalid="true" id="__BVID__1434"></div>
          <div data-v-74c08a55="" class="info-wrap">
            <div data-v-74c08a55="" class="d-block invalid-feedback">팝업 스토어 시작일을 입력해주세요.</div>
          </div><!----><!----><!---->
        </div>
      </fieldset>
      <fieldset data-v-74c08a55="" data-v-ab788270="" class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">팝업 스토어 카테고리
        </legend>
        <div><!---->
          <div data-v-74c08a55="" class="input-wrap"><input v-model="popupStore.category" data-v-74c08a55=""
              name="category" type="text" placeholder="ex) 뷰티" class="form-control is-invalid" maxlength="50"
              aria-invalid="true" id="__BVID__1434"></div>
          <div data-v-74c08a55="" class="info-wrap">
            <div data-v-74c08a55="" class="d-block invalid-feedback">팝업 스토어 카테고리를 입력해주세요.</div>
          </div><!----><!----><!---->
        </div>
      </fieldset>
      <fieldset data-v-74c08a55="" data-v-ab788270="" class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">팝업 스토어 설명
        </legend>
        <div><!---->
          <div data-v-74c08a55="" class="input-wrap"><input v-model="popupStore.storeContent" data-v-74c08a55=""
              name="storeContent" type="text" placeholder="팝업 스토어 상세 설명" class="form-control is-invalid" minlength="10"
              maxlength="50" aria-invalid="true" id="__BVID__1434"></div>
          <div data-v-74c08a55="" class="info-wrap">
            <div data-v-74c08a55="" class="d-block invalid-feedback">팝업 스토어에 대한 설명을 입력해주세요.</div>
          </div><!----><!----><!---->
        </div>
      </fieldset>
      <fieldset class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">팝업 스토어 사진
        </legend>
        <div>
          <div class="input-wrap">
            <input @change="onFileChange" type="file" accept="image/*" class="form-control-file">
          </div>
          <div v-if="files" class="image-preview">
            <img :src="files" alt="Preview" class="img-thumbnail mt-2">
          </div>
        </div>
      </fieldset>
      <div data-v-b7b1ce04="" class="col-12">
        <button data-v-b7b1ce04="" data-testid="btn-login" type="submit" class="btn btn-login btn-primary">
          <span data-v-b7b1ce04="">팝업 스토어 등록</span>
          <span data-v-b7b1ce04="" style="display: none">
            <div data-v-fc3fcce8="" data-v-b7b1ce04="" class="">
              <div data-v-fc3fcce8="" class="indicator-body" style="
                        width: 24px;
                        height: 24px;
                        border-width: 0.25rem;
                        border-style: solid;
                        border-image: initial;
                    "></div>
              <p data-v-fc3fcce8="" style="display: none"></p>
            </div>
          </span>
        </button>
      </div>
    </form>
  </div>
</template>


<script>
import { mapStores } from 'pinia'
import { usePopupStore } from '@/stores/usePopupStore';
export default {
  name: "PopupRegisterComponent",
  data() {
    return {
      popupStore: {
        storeName: '',
        storeAddress: '',
        storeEndtDate: '',
        category: '',
        storeContent: '',
      },
      imageFile: [],
    };
  },
  computed: {
    ...mapStores(usePopupStore)
  },
  methods: {
    async register() {
      const formData = new FormData();
      const jsonBlob = new Blob([JSON.stringify(this.popupStore)], { type: 'application/json' })

      Array.from(this.imageFile).forEach((file) => {
        formData.append("files", file);
      })

      formData.append("dto", jsonBlob);

      console.log(formData);

      try {
        const result = await this.popupstoreStore.register(formData);
        if (result) {
          console.log("등록 성공");
        }
      } catch (error) {
        console.error("등록 실패:", error);
      }
    },
    onFileChange(event) {
      this.imageFile.push(event.target.files[0]);
    },

  }
}
</script>

<style></style>