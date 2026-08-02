const MAIN_TABS = new Set(["popup", "goods", "reserve"]);

export const isPathWithin = (currentPath, targetPath) => (
  currentPath === targetPath || currentPath.startsWith(`${targetPath}/`)
);

export const resolveMainTab = (path, query = {}) => {
  if (MAIN_TABS.has(query.mainTab)) return query.mainTab;
  if (isPathWithin(path, "/goods") || isPathWithin(path, "/orders")) return "goods";
  if (isPathWithin(path, "/reserve")) return "reserve";
  if (path === "/" || isPathWithin(path, "/popup")) return "popup";
  return null;
};
