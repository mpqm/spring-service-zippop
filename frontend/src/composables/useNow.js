import { ref, onMounted, onUnmounted } from 'vue';

const now = ref(Date.now());
let timer;
let users = 0;

export function useNow() {
  onMounted(() => {
    now.value = Date.now();
    if (users++ === 0) timer = setInterval(() => { now.value = Date.now(); }, 1000);
  });
  onUnmounted(() => {
    if (--users === 0) { clearInterval(timer); timer = undefined; }
  });
  return now;
}
