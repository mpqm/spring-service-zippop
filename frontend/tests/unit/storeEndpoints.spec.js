import { createPinia, setActivePinia } from "pinia";
import axios from "axios";
import { BACKEND_URL } from "@/config";
import { useAccountStore } from "@/stores/accountStore";
import { useAuthStore } from "@/stores/authStore";
import { useCartStore } from "@/stores/cartStore";
import { useGoodsStore } from "@/stores/goodsStore";
import { useOrdersStore } from "@/stores/ordersStore";
import { usePayoutStore } from "@/stores/payoutStore";
import { usePopupStore } from "@/stores/popupStore";
import { useReserveStore } from "@/stores/reserveStore";

jest.mock("axios", () => ({
  get: jest.fn(),
  post: jest.fn(),
  patch: jest.fn(),
  delete: jest.fn(),
}));

const page = { content: [], totalElements: 0, totalPages: 0 };
const ok = (result = null) => ({ data: { success: true, code: 200, message: "ok", result } });

beforeEach(() => {
  setActivePinia(createPinia());
  jest.clearAllMocks();
  axios.get.mockResolvedValue(ok(page));
  axios.post.mockResolvedValue(ok());
  axios.patch.mockResolvedValue(ok());
  axios.delete.mockResolvedValue(ok());
});

test("account store matches every account controller endpoint", async () => {
  const store = useAccountStore();
  const request = { value: true };
  await store.createAccount(request);
  await store.getAccount();
  await store.updateAccount(request);
  await store.resetPassword(request);
  await store.findUserId(request);
  await store.findUserPassword(request);
  await store.activateAccount(request);

  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/accounts`, request);
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/accounts/me`, { withCredentials: true });
  expect(axios.patch).toHaveBeenCalledWith(`${BACKEND_URL}/accounts/me`, request, { withCredentials: true });
  expect(axios.patch).toHaveBeenCalledWith(`${BACKEND_URL}/accounts/password/reset`, request, { withCredentials: true });
  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/accounts/id/find`, request);
  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/accounts/password/find`, request);
  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/accounts/me/activation`, request, { withCredentials: true });
});

test("goods store matches goods CRUD endpoints", async () => {
  const store = useGoodsStore();
  const request = new FormData();
  axios.get.mockResolvedValueOnce(ok({ goodsIdx: 5 })).mockResolvedValueOnce(ok(page));
  await store.createGoods(request);
  await store.getGoods(5);
  await store.getGoodsList(7, "키링", 0, 8);
  await store.updateGoods(5, request);
  await store.deleteGoods(5);

  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/goods`, request, { withCredentials: true });
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/goods/5`);
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/goods?popupIdx=7&keyword=키링&page=0&size=8`);
  expect(axios.patch).toHaveBeenCalledWith(`${BACKEND_URL}/goods/5`, request, { withCredentials: true });
  expect(axios.delete).toHaveBeenCalledWith(`${BACKEND_URL}/goods/5`, { withCredentials: true });
});

test("popup store matches popup, like, and review endpoints", async () => {
  const store = usePopupStore();
  const request = new FormData();
  axios.get.mockResolvedValue(ok(page));
  await store.createPopup(request);
  await store.getPopup(7);
  await store.getPopups("POPUP_START", "서울", 0, 8);
  await store.getCompanyPopups("서울", 0, 8);
  await store.updatePopup(7, request);
  await store.deletePopup(7);
  await store.togglePopupLike(7);
  await store.getMyLikedPopups(0, 10);
  await store.createPopupReview(7, { reviewRating: 5 });
  await store.getPopupReviews(7, 0, 10);
  await store.getMyReviews(0, 10);

  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/popups`, request, { withCredentials: true });
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/popups/7`);
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/popups?status=POPUP_START&keyword=서울&page=0&size=8`);
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/company/popups?keyword=서울&page=0&size=8`, { withCredentials: true });
  expect(axios.patch).toHaveBeenCalledWith(`${BACKEND_URL}/popups/7`, request, { withCredentials: true });
  expect(axios.delete).toHaveBeenCalledWith(`${BACKEND_URL}/popups/7`, { withCredentials: true });
  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/popups/7/likes`, {}, { withCredentials: true });
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/popups/likes/me?page=0&size=10`, { withCredentials: true });
  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/popups/7/reviews`, { reviewRating: 5 }, { withCredentials: true });
});

test("cart store matches cart CRUD and quantity endpoints", async () => {
  const store = useCartStore();
  axios.get.mockResolvedValueOnce(ok(page)).mockResolvedValueOnce(ok([]));
  await store.createCart({ goodsIdx: 5, popupIdx: 7 });
  await store.getCarts(0, 10);
  await store.getCartItems(3);
  await store.updateCartItemQuantity(3, 4, "increase");
  await store.deleteCartItem(3, 4);
  await store.deleteCarts(3);

  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/carts`, { goodsIdx: 5, popupIdx: 7 }, { withCredentials: true });
  expect(axios.patch).toHaveBeenCalledWith(`${BACKEND_URL}/carts/3/items/4/quantity?operation=INCREMENT`, null, { withCredentials: true });
  expect(axios.delete).toHaveBeenCalledWith(`${BACKEND_URL}/carts/3/items/4`, { withCredentials: true });
  expect(axios.delete).toHaveBeenCalledWith(`${BACKEND_URL}/carts/3`, { withCredentials: true });
});

test("orders, reserve, and payout stores match customer and company endpoints", async () => {
  const orders = useOrdersStore();
  const reserve = useReserveStore();
  const payout = usePayoutStore();
  axios.get.mockResolvedValue(ok(page));
  await orders.createOrder({ impUid: "imp", popupIdx: 7 });
  await orders.updateOrders(9, { status: "STOCK_CANCEL" });
  await orders.getOrder(9);
  await orders.getOrders(0, 10);
  await orders.getPopupOrdersDetail(7, 9);
  await orders.getCompanyPopupOrdersList(7, 0, 10);
  await reserve.createReserve({ popupIdx: 7 });
  await reserve.deleteReserve(8);
  await reserve.enrollReserve(8);
  await reserve.cancelReserve(8);
  await reserve.getPopupReserves(7, 0, 10, "오후");
  await reserve.getCompanyPopupReserves(7, 0, 10);
  await payout.getCompanyPopupPayouts(7, 0, 10);

  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/orders`, { impUid: "imp", popupIdx: 7 }, { withCredentials: true });
  expect(axios.patch).toHaveBeenCalledWith(`${BACKEND_URL}/orders/9`, { status: "STOCK_CANCEL" }, { withCredentials: true });
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/company/popups/7/orders/9`, { withCredentials: true });
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/company/popups/7/orders?page=0&size=10`, { withCredentials: true });
  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/reserves`, { popupIdx: 7 }, { withCredentials: true });
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/reserves/8/enrollment`, { withCredentials: true });
  expect(axios.delete).toHaveBeenCalledWith(`${BACKEND_URL}/reserves/8/enrollment`, { withCredentials: true });
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/company/popups/7/payouts?page=0&size=10`, { withCredentials: true });
});

test("authentication store confirms the account before marking login successful", async () => {
  const auth = useAuthStore();
  axios.post.mockResolvedValue(ok());
  axios.get.mockResolvedValue(ok({ userId: "customer01", role: "ROLE_CUSTOMER" }));

  const result = await auth.login({ userId: "customer01", password: "pass" });

  expect(result.success).toBe(true);
  expect(auth.isLoggedIn).toBe(true);
  expect(axios.post).toHaveBeenCalledWith(`${BACKEND_URL}/auth/login`, expect.any(Object), { withCredentials: true });
  expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/accounts/me`, { withCredentials: true });
});
