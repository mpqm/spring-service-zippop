<template>
  <div class="list-container2">
    <div>
      <p v-if="hideStoreName" class="t1">{{ review.storeName }}</p>
      <p class="t1">{{ review.reviewTitle }}</p>
      <p class="t3">{{ review.reviewContent }}</p>
    </div>
    <div class="list-info3">
      <div class="list-info2">
        <p class="t3">{{ review.customerName }}/</p>
        <p class="t3">{{ review.customerEmail }}</p>
      </div>
      <div class="list-info2">
        <Icon icon="iconoir:star" width="20px" height="20px" style="color: #00c7ae" />
        &nbsp;{{ review.reviewRating }}
      </div>
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