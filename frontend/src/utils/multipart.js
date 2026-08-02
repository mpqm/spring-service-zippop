export const createMultipartRequest = (payload, files = [], fileField = "files") => {
  const formData = new FormData();
  formData.append("req", new Blob([JSON.stringify(payload)], { type: "application/json" }));
  Array.from(files || []).forEach((file) => formData.append(fileField, file));
  return formData;
};
