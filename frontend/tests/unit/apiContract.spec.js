import { createPinia, setActivePinia } from "pinia";
import axios from "axios";
import { BACKEND_URL } from "@/config";
import { createMultipartRequest } from "@/utils/multipart";
import { useAccountStore } from "@/stores/accountStore";
import { useCartStore } from "@/stores/cartStore";
import { useGoodsStore } from "@/stores/goodsStore";
import { useOrdersStore } from "@/stores/ordersStore";
import { usePopupStore } from "@/stores/popupStore";
import { useReserveStore } from "@/stores/reserveStore";

jest.mock("axios", () => ({
  get: jest.fn(),
  post: jest.fn(),
  patch: jest.fn(),
  delete: jest.fn(),
}));

const success = (result = null) => ({ data: { success: true, result } });

beforeEach(() => {
  setActivePinia(createPinia());
  jest.clearAllMocks();
});

describe("multipart API contract", () => {
  it("uses the backend request-part names req and files", () => {
    const file = new File(["image"], "goods.png", { type: "image/png" });
    const formData = createMultipartRequest({ goodsName: "굿즈" }, [file]);

    expect(formData.has("req")).toBe(true);
    expect(formData.has("dto")).toBe(false);
    expect(formData.getAll("files")).toEqual([file]);
  });

  it("uses the account file request-part name", () => {
    const file = new File(["profile"], "profile.png", { type: "image/png" });
    const formData = createMultipartRequest({ name: "사용자" }, [file], "file");

    expect(formData.get("file")).toBe(file);
    expect(formData.has("files")).toBe(false);
  });

  it("lets the runtime generate multipart boundaries", async () => {
    const formData = createMultipartRequest({ popupName: "팝업" });
    axios.post.mockResolvedValue(success());
    axios.patch.mockResolvedValue(success());

    await useAccountStore().createAccount(formData);
    await useGoodsStore().createGoods(formData);
    await usePopupStore().updatePopup(7, formData);

    expect(axios.post).toHaveBeenNthCalledWith(1, `${BACKEND_URL}/accounts`, formData);
    expect(axios.post).toHaveBeenNthCalledWith(2, `${BACKEND_URL}/goods`, formData, { withCredentials: true });
    expect(axios.patch).toHaveBeenCalledWith(`${BACKEND_URL}/popups/7`, formData, { withCredentials: true });
  });
});

describe("backend response DTO contract", () => {
  it("keeps cart items on the backend getGoodsRes field", async () => {
    const item = { cartItemIdx: 3, count: 2, getGoodsRes: { goodsIdx: 11, goodsName: "키링" } };
    axios.get.mockResolvedValue(success([item]));
    const store = useCartStore();

    await store.getCartItems(9);

    expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/carts/9/items`, { withCredentials: true });
    expect(store.cartItemList).toEqual([item]);
    expect(store.cartItemList[0].searchGoodsRes).toBeUndefined();
  });

  it("keeps order details on getOrdersDetailResList/getGoodsRes", async () => {
    const order = {
      ordersIdx: 5,
      getOrdersDetailResList: [{ ordersDetailIdx: 6, getGoodsRes: { goodsIdx: 11 } }],
    };
    axios.get.mockResolvedValue(success(order));
    const store = useOrdersStore();

    await store.getOrder(5);

    expect(store.orders).toEqual(order);
    expect(store.orders.searchOrdersDetailResList).toBeUndefined();
    expect(store.orders.getOrdersDetailResList[0].searchGoodsRes).toBeUndefined();
  });
});

describe("request endpoint contract", () => {
  it("uses the cart quantity endpoint and operation query", async () => {
    axios.patch.mockResolvedValue(success());
    await useCartStore().updateCartItemQuantity(2, 4, "increase");
    expect(axios.patch).toHaveBeenCalledWith(
      `${BACKEND_URL}/carts/2/items/4/quantity?operation=INCREMENT`,
      null,
      { withCredentials: true },
    );
  });

  it("uses the reserve enrollment endpoint", async () => {
    axios.get.mockResolvedValue(success());
    await useReserveStore().enrollReserve(13);
    expect(axios.get).toHaveBeenCalledWith(`${BACKEND_URL}/reserves/13/enrollment`, { withCredentials: true });
  });
});
