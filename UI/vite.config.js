import {fileURLToPath,URL} from 'node:url'
// eslint-disable-next-line no-unused-vars
//import path from 'node:path'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
//module.exports = defineConfig({
export default defineConfig({
  plugins: [vue()],
  transpileDependencies: true ,
  resolve:{
    alias:{
      '@':fileURLToPath(new URL('./src', import.meta.url)),
    }
  },
  //代理配置
  server:{
    port:4726,
    proxy:{
      '/api':{//获取路径中包含api的请求源
        target:'http://localhost:4728',//后台服务器所在的源
        changeOrigin:true,
        rewrite:(path)=>path.replace(/^\/api/,'')///api替换为''
      }
    }
  }
})
