const LOGIN_REQUIRED_CODES = new Set([
  'AUTHENTICATION_REQUIRED',
  'TOKEN_EXPIRED',
  'INVALID_TOKEN',
  'UNSUPPORTED_TOKEN',
  'MALFORMED_TOKEN',
  'ACCESS_DENIED',
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

  // 프록시나 컨테이너가 본문 없이 반환한 인증 오류도 안전하게 처리한다.
  return !error?.response?.data?.errorCode && [401, 403].includes(error?.response?.status);
};

export const isSocketLoginRequiredError = (error) => {
  const text = [
    error?.headers?.message,
    error?.body,
    error?.message,
    typeof error === 'string' ? error : '',
  ].filter(Boolean).join(' ');

  return /AUTHENTICATION_REQUIRED|TOKEN_EXPIRED|INVALID_TOKEN|UNSUPPORTED_TOKEN|MALFORMED_TOKEN|ACCESS_DENIED|\b401\b|\b403\b/i.test(text);
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
