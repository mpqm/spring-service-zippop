import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from '@/stores/authStore';
import { useAccountStore } from '@/stores/accountStore';
import { useCartStore } from '@/stores/cartStore';
import { useOrdersStore } from '@/stores/ordersStore';
import { useReserveStore } from '@/stores/reserveStore';
import { isLoginRequiredError } from '@/utils/errorHandling';

jest.mock('axios', () => ({
  get: jest.fn(), post: jest.fn(), patch: jest.fn(), delete: jest.fn(),
}));

test('clearing authentication removes previous account cart, checkout and queue access', () => {
  setActivePinia(createPinia());
  useAuthStore().isLoggedIn = true;
  useAccountStore().userInfo = { name: 'previous customer' };
  useCartStore().cartItemList = [{ cartItemIdx: 1 }];
  useOrdersStore().paymentData = { cartIdx: 1 };
  useReserveStore().access = true;

  useAuthStore().clearSession();

  expect(useAccountStore().userInfo).toEqual({});
  expect(useCartStore().cartItemList).toEqual([]);
  expect(useOrdersStore().paymentData).toEqual({});
  expect(useReserveStore().access).toBe(false);
});

test('a disabled account response requires a fresh login', () => {
  expect(isLoginRequiredError({ response: { status: 401, data: { errorCode: 'ACCOUNT_DISABLED' } } })).toBe(true);
});
