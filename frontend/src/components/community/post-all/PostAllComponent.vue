<template>

        <section id="community" class="community" data-v-4b40be3a="" data-v-0352d1b6="">
          <!-- 헤더 -->
          <div class="observer-container" data-v-71844fb9="" data-v-74db39ba="" data-v-4b40be3a="">
            <section data-v-74db39ba="" data-v-71844fb9="" data-observe="community-header" class="community-header" id="community-header">
                <h1 data-v-74db39ba="" data-v-71844fb9="" data-testid="community-main" class="community-title">커뮤니티</h1>
                <div data-v-74db39ba="" data-v-71844fb9="">
                    <div data-v-32970c25="" class="write-button-desktop">
                        <button aria-haspopup="true" aria-expanded="false" type="button" class="btn btn-primary">
                         <router-link to="/community/post-edit/create">글쓰기</router-link>
                        </button>
                    </div>
                </div>
            </section>
          </div>
          <!-- 바디 -->
          <PostListComponent></PostListComponent>
        </section>
</template>

<script>
import PostListComponent from './PostListComponent.vue';
export default {
  name: "PostAllComponent",
  components: {PostListComponent }
}
</script>

<style></style>