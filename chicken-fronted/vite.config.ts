import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 开启代理
  server: {
    host: '0.0.0.0',
    // public: '0.0.0.0:5173', // 本地的ip:端口号
    port: 5173,
    open: true,
    proxy: {
      '/api': {
        // 前缀替换成代理地址： 5173 -> 8080 后端tomcat服务器端口号
        target: 'http://localhost:8081/admin',
        ws: false,
        secure: false,
        changeOrigin: true,
        // /api去掉，变成空串，因为它只是一个标识而已，并不是路径
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      //拦截所有以 /upload 开头的请求,将匹配的请求转发到后端服务的 8081 端口。
      '/upload': {
        target: 'http://localhost:8081', // 后端服务地址
        changeOrigin: true,
        //下面这句可能并不需要, 主要是为了安全
        rewrite: (path) => path.replace(/^\/upload/, '/upload')
      }
    }
  }
})
