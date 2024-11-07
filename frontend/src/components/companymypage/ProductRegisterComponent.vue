<template>
  <div class="container">

    <form @submit.prevent="register">
      <fieldset data-v-74c08a55="" data-v-ab788270="" class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">스토어 번호
        </legend>
        <div><!---->
          <div data-v-74c08a55="" class="input-wrap"><input v-model="popupGoods.storeIdx" name="storeIdx"
              data-v-74c08a55="" type="number" placeholder="ex) 춘식이" class="form-control is-invalid" maxlength="50"
              aria-invalid="true" id="__BVID__1434"></div>
          <div data-v-74c08a55="" class="info-wrap">
            <div data-v-74c08a55="" class="d-block invalid-feedback">스토어 번호를 입력하세요.</div>
          </div><!----><!----><!---->
        </div>
      </fieldset>
      <fieldset data-v-74c08a55="" data-v-ab788270="" class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">굿즈 이름
        </legend>
        <div><!---->
          <div data-v-74c08a55="" class="input-wrap"><input v-model="popupGoods.productName" name="productName"
              data-v-74c08a55="" type="text" placeholder="ex) 춘식이" class="form-control is-invalid" maxlength="50"
              aria-invalid="true" id="__BVID__1434"></div>
          <div data-v-74c08a55="" class="info-wrap">
            <div data-v-74c08a55="" class="d-block invalid-feedback">상품 이름을 입력해주세요.</div>
          </div><!----><!----><!---->
        </div>
      </fieldset>
      <fieldset data-v-74c08a55="" data-v-ab788270="" class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">굿즈 가격
        </legend>
        <div><!---->
          <div data-v-74c08a55="" class="input-wrap"><input v-model="popupGoods.productPrice" name="productPrice"
              data-v-74c08a55="" type="number" placeholder="1000원 단위 입력" class="form-control is-invalid" maxlength="50"
              aria-invalid="true" id="__BVID__1434"></div>
          <div data-v-74c08a55="" class="info-wrap">
            <div data-v-74c08a55="" class="d-block invalid-feedback">팝업 굿즈 가격을 입력해 주세요.</div>
          </div><!----><!----><!---->
        </div>
      </fieldset>
      <fieldset data-v-74c08a55="" data-v-ab788270="" class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">팝업 굿즈 수량
        </legend>
        <div><!---->
          <div data-v-74c08a55="" class="input-wrap"><input v-model="popupGoods.productAmount" data-v-74c08a55=""
              name="productAmount" type="number" placeholder="ex) 1개 이상 입력" class="form-control is-invalid"
              maxlength="50" aria-invalid="true" id="__BVID__1434"></div>
          <div data-v-74c08a55="" class="info-wrap">
            <div data-v-74c08a55="" class="d-block invalid-feedback">팝업 굿즈 수량을 입력해주세요.</div>
          </div><!----><!----><!---->
        </div>
      </fieldset>
      <fieldset class="form-group" id="__BVID__1432">
        <legend tabindex="-1" class="bv-no-focus-ring col-form-label pt-0" id="__BVID__1432__BV_label_">팝업 굿즈 사진
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
          <span data-v-b7b1ce04="">팝업 굿즈 등록</span>
          <span data-v-b7b1ce04="" style="display: none">
            <div data-v-fc3fcce8="" data-v-b7b1ce04="" class="">
              <div data-v-fc3fcce8="" class="indicator-body" style="
                        width: 24px;
                        height: 24px;
                        border-width: 0.25rem;
                        border-style: solid;
                        border-color: rgb(255, 255, 255) rgba(255, 255, 255, 0.2)
                        rgba(255, 255, 255, 0.2);
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
import { useProductStore } from '@/stores/useProductStore';
export default {
  name: "ProductRegisterComponent",
  data() {
    return {
      popupGoods: {
        storeIdx: 0,
        productName: '',
        productPrice: '',
        productAmount: '',
        totalPeople: '',
      },
      imageFile: [],
    };
  },
  computed: {
    ...mapStores(useProductStore)
  },
  methods: {
    async register() {
      const formData = new FormData();
      const jsonBlob = new Blob([JSON.stringify(this.popupGoods)], { type: 'application/json' })

      Array.from(this.imageFile).forEach((file) => {
        formData.append("files", file);
      })

      formData.append("dto", jsonBlob);

      // console.log(this.popupGoods.storeIdx);

      try {
        console.log("1");
        const result = await this.productStore.register(this.popupGoods.storeIdx, formData);
        console.log(result);
        if (result === 200) {
          this.$router.push("/");
        }
      } catch (error) {
        console.error("등록 실패:", error);
      }
    },
    onFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          this.imageData = e.target.result;
        };
        reader.readAsDataURL(file);
      }
      this.imageFile.push(event.target.files[0]);
    },

  }
}
</script>

<style></style>