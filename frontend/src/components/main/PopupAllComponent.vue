<template>
  <section id="community" class="community" data-v-4b40be3a="" data-v-0352d1b6="">
    <!-- 헤더 -->
    <div class="observer-container" data-v-71844fb9="" data-v-74db39ba="" data-v-4b40be3a="">
      <section data-v-74db39ba="" data-v-71844fb9="" data-observe="community-header" class="community-header"
        id="community-header">
        <h1 data-v-74db39ba="" data-v-71844fb9="" data-testid="community-main" class="community-title">팝업 스토어</h1>
      </section>
    </div>
    <PopupListComponent></PopupListComponent>
  </section>

</template>

<script>
import PopupListComponent from './PopupListComponent.vue';
export default {
  name: "PopupAllComponent",
  components: { PopupListComponent }
}
</script>

<style scoped>
.community[data-v-4b40be3a] {
  width: 80%;

}
</style>