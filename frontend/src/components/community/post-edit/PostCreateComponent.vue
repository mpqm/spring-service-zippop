<template>
    <section data-v-517e3499="" data-v-0352d1b6="" class="write-post-container">
        <!-- 헤더 버튼 -->
        <div data-v-d87b96c8="" data-v-517e3499="" class="select-subject-header">
            <div data-v-d87b96c8="" class="subject-header-wrapper">
                <h1 data-v-746dd3c0="" data-v-d87b96c8="" class="legacy-typography headline-subhead3 gray-900 text-align-left" value="provider-news" id="__BVID__385">커뮤니티 글쓰기</h1>
                <div>
                    <button data-v-d87b96c8="" type="button" class="btn btn-secondary write-post-submit active">
                        <router-link to="/community/post-all">취소</router-link>
                    </button>
                    <button @click="register" data-v-d87b96c8="" type="button" class="btn btn-secondary write-post-submit active">
                        등록
                    </button>
                </div>
            </div>
        </div>
        <!-- 이미지 -->
        <!-- <div data-v-517e3499="" class="attach-file-wrapper"> -->

        <!-- </div> -->
        <!-- 에디터 -->
        <div data-v-3437dfdd="" data-v-517e3499="" class="editor-body-container is-bottom-margin">
            <div data-v-3437dfdd="" class="editor-body-wrapper">
                <!-- 제목 -->
                <div data-v-067f75d6="" data-v-3437dfdd="" class="editor-title-wrapper">
                    <div data-v-067f75d6="" class="editor-title-container">
                        <label data-v-067f75d6="" for="post-title-input" class="editor-title-label">
                            <input v-model="post.title" name="title" data-v-746dd3c0="" data-v-067f75d6="" id="post-title-input" type="text" placeholder="제목을 입력해주세요." class="post-title-input legacy-typography headline-subhead5 gray-900 text-align-left">
                        </label>
                    </div>
                </div>
                <!-- 내용 -->
                <div data-v-3437dfdd="" class="divider-wrapper">
                    <hr data-v-3437dfdd="" class="hr-divider">
                </div>
                
                <div data-v-c7163a48="" data-v-3437dfdd="" class="editor-contents">
                    <span data-v-746dd3c0="" data-v-c7163a48="" class="legacy-typography interface-body2 gray-900 text-align-left">
                        <textarea v-model="post.content" name="content" placeholder="내용을 입력해주세요" data-v-c7163a48="" data-v-746dd3c0="" class="editor-contents-textarea"></textarea>
                    </span>
                </div>
                <!-- image -->
                <div data-v-c7163a48="" data-v-3437dfdd="" class="editor-contents">
                    <input @change="onFileChange" type="file" accept="image/jpg,image/jpeg,image/png,image/gif,image/bmp,image/heic" multiple="multiple" class="custom-file-input" id="__BVID__387" style="z-index: -5;">
                    <label data-browse="업로드" class="custom-file-label" for="__BVID__387">
                        <span class="d-block form-file-text" style="pointer-events: none;">파일이 선택되지 않았습니다.</span>
                    </label>
                </div>
            </div>
        </div>
    </section>
</template>     

<script>
import { usePostStore } from '@/stores/usePostStore';
import { mapStores } from 'pinia';

export default {
    name: "PostCreateComponent",
    data() {
        return {
            post: {
                title: "",
                content: "",
            },
            imageFiles: [],
        }
    },
    computed: {...mapStores(usePostStore)},
    methods: {
        async register() {
      const formData = new FormData();
      const jsonBlob = new Blob([JSON.stringify(this.post)], { type: 'application/json' })
      formData.append("dto", jsonBlob);
      formData.append("files", this.imageFiles)

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
    onFileChange(event) { this.imageFiles = event.target.files; },
    }
}
</script>

<style></style>