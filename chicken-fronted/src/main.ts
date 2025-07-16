import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

/**
 * 在 Vue 生态中，interface 通常用于描述"是什么"（数据结构），而 type 更适合描述"怎么做"（类型关系和操作）
 */
const app = createApp(App)
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)

app.use(ElementPlus)
// 注册使用 ElementPlusIconsVue 的所有 icon 图标组件
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
app.use(router)
app.use(pinia)

app.mount('#app')
