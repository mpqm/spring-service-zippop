import { ref, onMounted, onUnmounted } from 'vue';

const MAIN_TABS = new Set(['popup', 'goods', 'reserve']);

export const isPathWithin = (currentPath, targetPath) => (
  currentPath === targetPath || currentPath.startsWith(`${targetPath}/`)
);

export const resolveMainTab = (path, query = {}) => {
  if (MAIN_TABS.has(query.mainTab)) return query.mainTab;
  if (isPathWithin(path, '/goods') || isPathWithin(path, '/orders')) return 'goods';
  if (isPathWithin(path, '/reserve')) return 'reserve';
  if (path === '/' || isPathWithin(path, '/popup')) return 'popup';
  return null;
};

export const createMultipartRequest = (payload, files = [], fileField = 'files') => {
  const formData = new FormData();
  formData.append('req', new Blob([JSON.stringify(payload)], { type: 'application/json' }));
  Array.from(files || []).forEach((file) => formData.append(fileField, file));
  return formData;
};

const LOGIN_REQUIRED_CODES = new Set([
  'AUTHENTICATION_REQUIRED',
  'TOKEN_EXPIRED',
  'INVALID_TOKEN',
  'UNSUPPORTED_TOKEN',
  'MALFORMED_TOKEN',
  'ACCOUNT_DISABLED',
]);

let redirectPromise = null;

export const getErrorPayload = (error) => {
  const payload = error?.response?.data;
  if (payload && typeof payload === 'object') {
    return payload;
  }
  return {
    success: false,
    status: error?.response?.status,
    errorCode: 'NETWORK_ERROR',
    message: '서버에 연결할 수 없습니다.',
  };
};

export const isLoginRequiredError = (error) => {
  const payload = getErrorPayload(error);
  if (LOGIN_REQUIRED_CODES.has(payload.errorCode)) {
    return true;
  }

  return !error?.response?.data?.errorCode && [401, 403].includes(error?.response?.status);
};

export const isSocketLoginRequiredError = (error) => {
  const text = [
    error?.headers?.message,
    error?.body,
    error?.message,
    typeof error === 'string' ? error : '',
  ].filter(Boolean).join(' ');

  return /AUTHENTICATION_REQUIRED|TOKEN_EXPIRED|INVALID_TOKEN|UNSUPPORTED_TOKEN|MALFORMED_TOKEN|\b401\b/i.test(text);
};

export const redirectToLogin = (router, authStore, redirectPath) => {
  authStore.clearSession();
  if (router.currentRoute.value.path === '/login') {
    return Promise.resolve();
  }
  if (redirectPromise) {
    return redirectPromise;
  }

  const redirect = redirectPath || router.currentRoute.value.fullPath || '/';
  redirectPromise = router.replace({
    path: '/login',
    query: { reason: 'auth', redirect },
  }).finally(() => {
    redirectPromise = null;
  });
  return redirectPromise;
};

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
