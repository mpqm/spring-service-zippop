<template>
  <MainComponent></MainComponent>
  
  <PopupAllComponent></PopupAllComponent>
</template>
<script>
import MainComponent from '@/components/main/MainComponent.vue';
import PopupAllComponent from '@/components/main/PopupAllComponent.vue';

export default {
  name: "MainPage",
  data() {
    return {
    }
  },
  methods: {
  },
  components: {
    MainComponent,
    PopupAllComponent
  },
}
</script>

<style></style>
