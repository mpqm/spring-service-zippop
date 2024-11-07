<template>
    <div data-v-05cd076e="" data-v-4b40be3a="">
        <article topic-id="all" class="community-feed-list" data-v-fbeed1e4 data-v-05cd076e>
            <ul class="feed-list" data-v-fbeed1e4="">
                <PostItemComponet
                    v-for="(post, index) in postStore.dataList.content" :key="index" :post="post"
                >
                </PostItemComponet>
            </ul>
        </article>
    </div>
</template>

<script>
import PostItemComponet from './PostItemComponet.vue';
import { usePostStore } from '@/stores/usePostStore';
import { mapStores } from 'pinia';
export default {
    name: "PostListComponent",
    components: { PostItemComponet },
    computed: {...mapStores(usePostStore)},
    mounted(){  this.postStore.searchAll(); }
}
</script>

<style></style>