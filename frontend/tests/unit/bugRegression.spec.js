import { createPinia, setActivePinia } from 'pinia';
import { shallowMount, mount, flushPromises } from '@vue/test-utils';
import { defineComponent, h } from 'vue';
import axios from 'axios';
import { usePopupStore } from '@/stores/popupStore';
import { useGoodsStore } from '@/stores/goodsStore';
import { useReserveStore } from '@/stores/reserveStore';
import { useAccountStore } from '@/stores/accountStore';
import { useNow } from '@/composables/useNow';
import PopupMainPage from '@/pages/popup/PopupMainPage.vue';
import AppPagination from '@/components/AppPagination.vue';
import EditProfilePage from '@/pages/mypage/common/EditProfilePage.vue';

jest.mock('axios', () => ({ get: jest.fn(), post: jest.fn(), patch: jest.fn(), delete: jest.fn() }));
jest.mock('vue-router', () => ({ useRouter: () => ({ push: jest.fn() }) }));
jest.mock('vue-toastification', () => ({ useToast: () => ({ error: jest.fn(), success: jest.fn() }) }));

const ok = result => ({ data: { success: true, result } });
const empty = { content: [], totalPages: 3, totalElements: 20 };
let wrapper;

beforeEach(() => {
  setActivePinia(createPinia());
  jest.clearAllMocks();
  axios.get.mockResolvedValue(ok(empty));
});
afterEach(() => { wrapper?.unmount(); wrapper = null; jest.useRealTimers(); });

test('special characters survive every search endpoint unchanged', async () => {
  const keyword = 'A&B + C# 한글';
  await usePopupStore().getPopups('POPUP_START', keyword, 0, 8);
  await usePopupStore().getCompanyPopups(keyword, 0, 8);
  await useGoodsStore().getGoodsList(7, keyword, 0, 8);
  await useReserveStore().getPopupReserves(7, 0, 8, keyword);
  expect(axios.get.mock.calls).toHaveLength(4);
  axios.get.mock.calls.forEach(([url]) => {
    expect(new URL(url, 'http://localhost').searchParams.get('keyword')).toBe(keyword);
  });
});

test('liked pagination metadata is separate and cached likes survive another page', async () => {
  const store = usePopupStore();
  store.totalPages = 9;
  axios.get.mockResolvedValueOnce(ok({ content: [{ popupIdx: 1 }], totalPages: 2, totalElements: 11 }));
  await store.getMyLikedPopups(0, 10);
  axios.get.mockResolvedValueOnce(ok({ content: [{ popupIdx: 2 }], totalPages: 2, totalElements: 11 }));
  await store.getMyLikedPopups(1, 10);
  expect(store.totalPages).toBe(9);
  expect(store.likeTotalPages).toBe(2);
  expect(store.likeTotalElements).toBe(11);
  expect(store.likePageList.map(p => p.popupIdx)).toEqual([2]);
  expect(store.likeList.map(p => p.popupIdx)).toEqual([1, 2]);
});

test('submitting a new search after paging always requests the first page', async () => {
  wrapper = shallowMount(PopupMainPage);
  await flushPromises();
  wrapper.findComponent(AppPagination).vm.$emit('page-changed', 2);
  await flushPromises();
  expect(new URL(axios.get.mock.calls.at(-1)[0], 'http://localhost').searchParams.get('page')).toBe('2');
  await wrapper.find('input').setValue('new keyword');
  await wrapper.find('input').trigger('keyup.enter');
  await flushPromises();
  expect(new URL(axios.get.mock.calls.at(-1)[0], 'http://localhost').searchParams.get('page')).toBe('0');
});

test('editing profile fields does not mutate the global account before saving', async () => {
  axios.get.mockResolvedValue(ok({ name: 'Original', role: 'ROLE_CUSTOMER', address: 'Seoul,101' }));
  wrapper = shallowMount(EditProfilePage);
  await flushPromises();
  await wrapper.find('#name').setValue('Unsaved');
  expect(useAccountStore().userInfo.name).toBe('Original');
});

test('the shared clock updates while mounted and releases its timer on unmount', async () => {
  jest.useFakeTimers();
  jest.setSystemTime(new Date('2026-11-01T00:00:00Z'));
  wrapper = mount(defineComponent({ setup() { const now = useNow(); return () => h('span', String(now.value)); } }));
  await wrapper.vm.$nextTick();
  const before = Number(wrapper.text());
  jest.advanceTimersByTime(1000);
  await wrapper.vm.$nextTick();
  expect(Number(wrapper.text())).toBe(before + 1000);
  wrapper.unmount();
  wrapper = null;
  expect(jest.getTimerCount()).toBe(0);
});
