<template>
  <div id="layout">
    <PopupItemComponent v-for="(popup, index) in popupstoreStore.dataList.content" :key="index" :popup="popup" />
  </div>
</template>

<script>
import PopupItemComponent from './PopupItemComponent.vue';
import { usePopupStore } from '@/stores/useStoreStore';
import { mapStores } from 'pinia';
export default {
  name: "PostListComponent",
  components: { PopupItemComponent },
  computed: { ...mapStores(usePopupStore) },
  mounted() { this.popupstoreStore.searchAll(); },
}
</script>

<style scoped>
#layout {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}
</style>
