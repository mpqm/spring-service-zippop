import { isPathWithin, resolveMainTab } from "@/utils/navigation";

describe("header navigation state", () => {
  test.each([
    ["/", {}, "popup"],
    ["/popup/118", {}, "popup"],
    ["/goods", {}, "goods"],
    ["/goods/120/240", {}, "goods"],
    ["/orders/31", {}, "goods"],
    ["/reserve", {}, "reserve"],
    ["/reserve/120/8", {}, "reserve"],
    ["/popup/120", { mainTab: "reserve" }, "reserve"],
    ["/orders/31", { mainTab: "reserve" }, "reserve"],
  ])("resolves %s to %s", (path, query, expected) => {
    expect(resolveMainTab(path, query)).toBe(expected);
  });

  it("keeps a dropdown active on descendant pages only", () => {
    expect(isPathWithin("/mypage/company/goods/3/update/5", "/mypage/company/goods")).toBe(true);
    expect(isPathWithin("/mypage/company/goods-old", "/mypage/company/goods")).toBe(false);
  });

  test.each([
    ["/mypage/company/popup/register", "/mypage/company/popup"],
    ["/mypage/company/popup/update/113", "/mypage/company/popup"],
    ["/mypage/company/goods/113/register", "/mypage/company/goods"],
    ["/mypage/company/goods/113/update/7", "/mypage/company/goods"],
    ["/mypage/company/reserve/register/113", "/mypage/company/reserve"],
    ["/mypage/company/orders/113", "/mypage/company/orders"],
    ["/mypage/company/payout/113", "/mypage/company/payout"],
    ["/mypage/customer/cart/9", "/mypage/customer/cart"],
  ])("keeps the parent sub-tab active for %s", (currentPath, parentPath) => {
    expect(isPathWithin(currentPath, parentPath)).toBe(true);
  });
});
