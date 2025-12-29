import './assets/main.css'
import 'ant-design-vue/dist/reset.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'

// 开发态启动 MSW mock，正式环境可移除
if (import.meta.env.DEV) {
  import('./mocks/browser').then(({ startMock }) => startMock())
}

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(Antd)

// 恢复持久化 token，避免刷新后丢失登录态
const auth = useAuthStore(pinia)
auth.restore()

app.mount('#app')
