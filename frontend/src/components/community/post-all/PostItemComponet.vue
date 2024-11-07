<template>
    <li class="feed-item" data-v-0cdbcc7c data-v-fbeed1e4>
        <a href="/community/" data-testid="soomgo-life-feed-item" data-v-0cdbcc7c="" class="">
            <div class="feed-content" data-v-0cdbcc7c="">
                <div data-v-0cdbcc7c="">
                    <section class="item-wrapper" data-v-0cdbcc7c="">
                        <!-- 게시글 제목 -->
                        <h3 class="legacy-typography headline-subhead5 gray-900 text-align-left" data-v-746dd3c0="" data-v-0cdbcc7c="">
                            {{ post.title }}
                        </h3>
                        <!-- 게시글 내용 -->
                        <p class="content legacy-typography interface-body2 gray-500 text-align-left" data-v-746dd3c0="" data-v-0cdbcc7c="">
                            {{ post.content }}
                        </p>
                    </section>
                </div><!---->
            </div><!---->
            <div class="feed-footer" data-v-0cdbcc7c="">
                <div class="user-interaction" data-v-0cdbcc7c="">
                    <span class="like legacy-typography interface-description gray-300 text-align-left" data-v-746dd3c0="" data-v-0cdbcc7c="">
                        {{ post.likeCount }}
                    </span>
                    <span class="comment legacy-typography interface-description gray-300 text-align-left" data-v-746dd3c0="" data-v-0cdbcc7c="">
                        {{ post.commentCount }}
                    </span>
                </div>
                <span class="legacy-typography interface-description gray-300 text-align-left" data-v-746dd3c0="" data-v-0cdbcc7c="">
                    {{ relativeTime(post.createdAt) }}
                </span>
            </div>
        </a>
    </li>

</template>

<script>
export default {
    name: "PostItemComponent",
    data() {
        return {

        }
    },
    props: { post: { type: Object, required: true } },
    methods: {
    relativeTime(date) {
      const now = new Date();
      const postDate = new Date(date);
      const diff = now - postDate; // 밀리초 단위의 차이
      const minutes = Math.floor(diff / 60000);
      const hours = Math.floor(minutes / 60);
      const days = Math.floor(hours / 24);

      if (minutes < 1) return "방금 전";
      if (minutes < 60) return `${minutes}분 전`;
      if (hours < 24) return `${hours}시간 전`;
      return `${days}일 전`;
    }
  }
}

</script>

<style></style>