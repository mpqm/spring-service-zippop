<template>
  <svg
    :class="className"
    :width="width"
    :height="height"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.8"
    stroke-linecap="round"
    stroke-linejoin="round"
    aria-hidden="true"
    focusable="false"
  >
    <g v-if="icon === 'logos:google-icon'" stroke="none">
      <path fill="#4285F4" d="M23.5 12.27c0-.79-.07-1.55-.2-2.27H12v4.3h6.44a5.5 5.5 0 0 1-2.39 3.61v3h3.87c2.27-2.09 3.58-5.17 3.58-8.64Z" />
      <path fill="#34A853" d="M12 24c3.24 0 5.96-1.08 7.95-2.93l-3.87-3c-1.08.72-2.45 1.15-4.08 1.15-3.13 0-5.78-2.11-6.73-4.95H1.27v3.09A12 12 0 0 0 12 24Z" />
      <path fill="#FBBC05" d="M5.27 14.27a7.2 7.2 0 0 1 0-4.54V6.64H1.27a12 12 0 0 0 0 10.72l4-3.09Z" />
      <path fill="#EA4335" d="M12 4.78c1.77 0 3.36.61 4.61 1.82l3.45-3.45C17.95 1.17 15.24 0 12 0A12 12 0 0 0 1.27 6.64l4 3.09C6.22 6.89 8.87 4.78 12 4.78Z" />
    </g>
    <g v-else-if="icon === 'simple-icons:kakaotalk'" stroke="none" fill="currentColor">
      <path d="M12 3C6.48 3 2 6.58 2 11c0 2.85 1.9 5.35 4.76 6.77l-.98 3.59a.35.35 0 0 0 .54.38l4.26-2.85c.47.06.94.09 1.42.09 5.52 0 10-3.58 10-8s-4.48-7.98-10-7.98Z" />
      <path d="m7.2 8.4 1.05 3.28L9.3 8.4h1.25l-1.7 4.75v2.2H7.72v-2.2L6 8.4h1.2Zm5.1 0h1.13v5.87h2.32v1.08H12.3V8.4Zm4.1 0h1.13v6.95H16.4V8.4Z" fill="#fff" />
    </g>
    <g v-else-if="icon === 'simple-icons:naver'" stroke="none" fill="currentColor">
      <path d="M3 3h6.35l5.3 7.15V3H21v18h-6.32l-5.33-7.17V21H3V3Z" />
    </g>
    <g v-else v-html="iconPaths[icon] || iconPaths['iconoir:user']" />
  </svg>
</template>

<script setup>
// eslint-disable-next-line no-undef
defineProps({
  icon: { type: String, required: true },
  width: { type: [String, Number], default: 24 },
  height: { type: [String, Number], default: 24 },
  className: { type: [String, Array, Object], default: undefined },
});

const iconPaths = {
  'ic:search': '<circle cx="10.8" cy="10.8" r="6.8"/><path d="m16 16 5 5"/>',
  'iconoir:add-square': '<rect x="3" y="3" width="18" height="18" rx="3"/><path d="M12 8v8M8 12h8"/>',
  'iconoir:bell': '<path d="M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4"/>',
  'iconoir:box-iso': '<path d="m12 2 9 5v10l-9 5-9-5V7l9-5Z"/><path d="m3.5 7.5 8.5 5 8.5-5M12 12.5V22M7.5 4.5l9 5"/>',
  'iconoir:calendar-plus': '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18M12 13v5M9.5 15.5h5"/>',
  'iconoir:camera': '<path d="M3 8h4l2-3h6l2 3h4v12H3z"/><circle cx="12" cy="14" r="4"/>',
  'iconoir:cancel': '<circle cx="12" cy="12" r="9"/><path d="m9 9 6 6m0-6-6 6"/>',
  'iconoir:cart': '<path d="M3 4h2l2.5 11h10L21 8H6M9 19h.01M17 19h.01"/><circle cx="9" cy="19" r="1.5"/><circle cx="17" cy="19" r="1.5"/>',
  'iconoir:check': '<path d="m4 12 5 5L20 6"/>',
  'iconoir:clock': '<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
  'iconoir:coin': '<circle cx="12" cy="12" r="9"/><path d="M15 8.5c-.7-.6-1.6-.9-2.7-.9-1.5 0-2.5.7-2.5 1.8 0 2.8 5.1 1.1 5.1 4 0 1.2-1.1 2-2.8 2-1.1 0-2.2-.4-3-1.1M12 6v12"/>',
  'iconoir:coins': '<circle cx="9" cy="10" r="6"/><path d="M15 7a6 6 0 0 1 0 12M9 7v6m-2-4h4m-4 2h4M4 18a6 6 0 0 0 10 1"/>',
  'iconoir:edit-pencil': '<path d="m4 16-.8 4.8L8 20l11-11-4-4L4 16Z"/><path d="m13.5 6.5 4 4"/>',
  'iconoir:eye': '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7S2 12 2 12Z"/><circle cx="12" cy="12" r="3"/>',
  'iconoir:fire-flame': '<path d="M12 22a7 7 0 0 0 7-7c0-4-3-6-3-10-2 2-3 3-3 6-2-2-3-4-3-7-3 3-6 7-6 11a8 8 0 0 0 8 7Z"/><path d="M12 22a4 4 0 0 0 4-4c0-2-1-3-2-4-1 2-2 2-2 4-1-1-2-2-2-3-1 1-2 2-2 4a4 4 0 0 0 4 3Z"/>',
  'iconoir:hand-card': '<rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 10h18M7 15h4"/>',
  'iconoir:log-in': '<path d="M10 17l5-5-5-5M15 12H3"/><path d="M12 3h6a3 3 0 0 1 3 3v12a3 3 0 0 1-3 3h-6"/>',
  'iconoir:log-out': '<path d="m14 7 5 5-5 5M19 12H8"/><path d="M12 3H6a3 3 0 0 0-3 3v12a3 3 0 0 0 3 3h6"/>',
  'iconoir:minus': '<path d="M5 12h14"/>',
  'iconoir:nav-arrow-down': '<path d="m6 9 6 6 6-6"/>',
  'iconoir:nav-arrow-left': '<path d="m15 18-6-6 6-6"/>',
  'iconoir:nav-arrow-right': '<path d="m9 18 6-6-6-6"/>',
  'iconoir:plus': '<path d="M12 5v14M5 12h14"/>',
  'iconoir:refresh-double': '<path d="M20 7v5h-5M4 17v-5h5"/><path d="M5.5 9A7 7 0 0 1 18 6l2 2M4 16l2 2a7 7 0 0 0 12.5-3"/>',
  'iconoir:search-window': '<rect x="3" y="4" width="18" height="14" rx="2"/><path d="M7 8h.01M10 8h.01M13 8h.01M8 22h8M12 18v4m3-8 2 2"/><circle cx="14" cy="14" r="2"/>',
  'iconoir:star': '<path d="m12 3 2.8 5.7 6.2.9-4.5 4.4 1.1 6.2-5.6-3-5.6 3 1.1-6.2L3 9.6l6.2-.9L12 3Z"/>',
  'iconoir:thumbs-up': '<path d="M7 10v11H4a2 2 0 0 1-2-2v-7a2 2 0 0 1 2-2h3Zm0 0 5-8c2 0 3 2 2 4l-1 4h6a2 2 0 0 1 2 2l-2 7a2 2 0 0 1-2 2H7"/>',
  'iconoir:trash': '<path d="M3 6h18M8 6V4h8v2m3 0-1 15H6L5 6m4 4v7m6-7v7"/>',
  'iconoir:user': '<circle cx="12" cy="8" r="4"/><path d="M4 21a8 8 0 0 1 16 0"/>',
  'iconoir:user-badge-check': '<circle cx="10" cy="8" r="4"/><path d="M2 21a8 8 0 0 1 14-5m1-1 2 2 4-4"/>',
  'iconoir:user-cart': '<circle cx="10" cy="8" r="4"/><path d="M2 21a8 8 0 0 1 14-5m2-3h2l1 5h-5l-1-4m2 7h.01m3 0h.01"/>',
  'iconoir:user-plus': '<circle cx="9" cy="8" r="4"/><path d="M1 21a8 8 0 0 1 14-5m5-5v6m-3-3h6"/>',
  'ic:outline-first-page': '<path d="M18 6 12 12l6 6M10 6l-6 6 6 6"/>',
  'ic:outline-last-page': '<path d="m6 6 6 6-6 6m8-12 6 6-6 6"/>',
  'ic:round-navigate-before': '<path d="m15 18-6-6 6-6"/>',
  'ic:round-navigate-next': '<path d="m9 18 6-6-6-6"/>',
};
</script>
