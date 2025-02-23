const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  devServer: {
    proxy: {
      "/api": {
        target: process.env.VUE_APP_BACKEND_URL, // `/api/v1`로 프록시
        changeOrigin: true,
        pathRewrite: { "^/api": "/v1" }, // `/api` → `/v1`로 변경
      },
    },
  },
  transpileDependencies: true,
});
