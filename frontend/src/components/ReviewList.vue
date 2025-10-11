<template>
  <div class="ctn-list2">
    <div>
      <p v-if="hideStoreName" class="txt-def0">{{ review.storeName }}</p>
      <p class="txt-def1">{{ review.reviewTitle }}</p>
      <p class="txt-def2">{{ review.reviewContent }}</p>
    </div>
    <div class="ctn-listinfo1">
      <button class="btn-tagdefault">{{ review.customerName }}/</button>
      <button class="btn-tagdefault">{{ review.customerEmail }}</button>
      <button class="btn-tagdefault">
        <Icon icon="iconoir:star" width="16px" height="16px"/>
        {{ review.reviewRating }}
      </button>    
    </div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";

// props 정의 (review, hideStoreName)
defineProps({
  review: Object,
  hideStoreName: Boolean,
});

</script>