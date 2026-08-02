<template>
  <section class="txt-null ctn-emptystate" role="status">
    <div class="ctn-emptycopy">
      <p>{{ title }}</p>
      <span v-if="description">{{ description }}</span>
    </div>
    <router-link v-if="to && actionLabel" class="btn-tagaction" :to="to">
      {{ actionLabel }}
    </router-link>
    <button v-else-if="actionLabel" class="btn-tagaction" type="button" @click="$emit('action')">
      {{ actionLabel }}
    </button>
  </section>
</template>

<script setup>
import { defineEmits, defineProps } from 'vue';

defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
  to: { type: String, default: '' },
  actionLabel: { type: String, default: '' },
});

defineEmits(['action']);
</script>
