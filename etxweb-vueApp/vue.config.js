const { defineConfig } = require('@vue/cli-service')
const application = require('./src/application.json')
const webpack = require('webpack')

process.env.VUE_APP_PAGE_SIZE = '10'
process.env.VUE_APP_PAGE_SIZES = '5 10 20 50 100 500'

module.exports = defineConfig({
  publicPath: application.build.contextPath, // process.env.BASE_URL will return 'publicPath' value
  assetsDir: 'public',
  lintOnSave: false,
  // outputDir: '../main/resources/public',
  devServer: {
    // open: process.platform === 'darwin',
    host: 'localhost',
    port: 4200,
    server: {
      type: 'https'
    },
    proxy: {
      '^/': {
        target: 'https://localhost:7003',
        ws: false,
        changeOrigin: false
      }
    }
  },
  pages: {
    index: {
      entry: 'src/main.ts',
      title: 'eTrustEx Web'
    }
  },
  configureWebpack: {
    devtool: 'source-map',
    resolve: {
      alias: {
        jszip: require.resolve('jszip/lib/index.js')
      },
      fallback: {
        crypto: require.resolve('crypto-browserify'),
        path: false,
        stream: require.resolve('stream-browserify'),
        util: require.resolve('util/'),
        buffer: require.resolve('buffer')
      }
    },
    plugins: [
      // fix "process is not defined" error:
      // (do "npm install process" before running the build)
      new webpack.ProvidePlugin({
        process: 'process/browser'
      }),
      new webpack.ProvidePlugin({
        Buffer: ['buffer', 'Buffer']
      })
    ]
  },
  pwa: {
    name: 'eTrustexWeb4',
    manifestOptions: {
      short_name: 'etxWeb4',
      icons: []
    },
    iconPaths: {
      faviconSVG: 'img/icons/logo-ec.svg',
      favicon32: null,
      favicon16: null,
      appleTouchIcon: null,
      maskIcon: null,
      msTileImage: null
    }
  },
  runtimeCompiler: true,
  transpileDependencies: true
})
