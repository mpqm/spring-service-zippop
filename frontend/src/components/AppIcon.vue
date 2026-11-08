<template>
  <span
    :class="['app-icon', iconClass, className]"
    :style="{ width: iconWidth, height: iconHeight }"
    aria-hidden="true"
    focusable="false"
  />
</template>

<script setup>
import { computed } from 'vue';

// eslint-disable-next-line no-undef
const props = defineProps({
  icon: { type: String, required: true },
  width: { type: [String, Number], default: 24 },
  height: { type: [String, Number], default: 24 },
  className: { type: [String, Array, Object], default: undefined },
});

const iconClass = computed(() => `app-icon--${props.icon.replace(':', '-')}`);
const withUnit = (value) => /^\d+(?:\.\d+)?$/.test(String(value)) ? `${value}px` : value;
const iconWidth = computed(() => withUnit(props.width));
const iconHeight = computed(() => withUnit(props.height));
</script>
