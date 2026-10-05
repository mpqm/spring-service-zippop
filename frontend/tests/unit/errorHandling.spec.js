import {
  getErrorPayload,
  isLoginRequiredError,
  isSocketLoginRequiredError,
  redirectToLogin,
} from '@/utils/errorHandling';

describe('common error handling', () => {
  it.each([
    'AUTHENTICATION_REQUIRED',
    'TOKEN_EXPIRED',
    'INVALID_TOKEN',
    'UNSUPPORTED_TOKEN',
    'MALFORMED_TOKEN',
  ])('treats %s as a login redirect error', (errorCode) => {
    const error = { response: { status: 401, data: { errorCode } } };
    expect(isLoginRequiredError(error)).toBe(true);
  });

  it('keeps an authenticated session for an access denied response', () => {
    const error = {
      response: {
        status: 403,
        data: { errorCode: 'ACCESS_DENIED', message: '접근 권한이 없습니다.' },
      },
    };
    expect(isLoginRequiredError(error)).toBe(false);
  });

  it('does not redirect a bad login credential response', () => {
    const error = {
      response: {
        status: 401,
        data: { errorCode: 'BAD_CREDENTIALS', message: '로그인 실패' },
      },
    };
    expect(isLoginRequiredError(error)).toBe(false);
  });

  it('normalizes a network error for stores and pages', () => {
    expect(getErrorPayload(new Error('offline'))).toEqual(expect.objectContaining({
      success: false,
      errorCode: 'NETWORK_ERROR',
    }));
  });

  it('recognizes the shared error code in a STOMP ERROR frame', () => {
    expect(isSocketLoginRequiredError({
      headers: { message: 'Failed to send: AUTHENTICATION_REQUIRED' },
    })).toBe(true);
    expect(isSocketLoginRequiredError('Whoops! Lost connection')).toBe(false);
    expect(isSocketLoginRequiredError({
      headers: { message: 'ACCESS_DENIED' },
      body: '403',
    })).toBe(false);
  });

  it('clears the session and preserves the requested path', async () => {
    const router = {
      currentRoute: { value: { path: '/mypage/company/popup', fullPath: '/mypage/company/popup?page=2' } },
      replace: jest.fn().mockResolvedValue(undefined),
    };
    const authStore = { clearSession: jest.fn() };

    await redirectToLogin(router, authStore);

    expect(authStore.clearSession).toHaveBeenCalledTimes(1);
    expect(router.replace).toHaveBeenCalledWith({
      path: '/login',
      query: { reason: 'auth', redirect: '/mypage/company/popup?page=2' },
    });
  });
});
