const { defineConfig } = require('@vue/cli-service')
const webpack = require('webpack')

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
  configureWebpack: {
    plugins: [
      new webpack.DefinePlugin({
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: false,
        __VUE_OPTIONS_API__: true,
        __VUE_PROD_DEVTOOLS__: false
      })
    ]
  }
});
