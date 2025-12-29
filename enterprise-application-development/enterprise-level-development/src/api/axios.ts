import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

instance.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  console.log('【request 拦截器】config', config)
  return config
})

instance.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端统一返回格式: { code, message, data }
    if (res.code === 0 || res.code === 200) {
      // 成功：返回 data 字段
      return res.data
    } else {
      // 业务错误：抛出错误信息
      const error = new Error(res.message || '请求失败')
      return Promise.reject(error)
    }
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      const auth = useAuthStore()
      auth.logout(true)
    }
    // 如果后端返回了错误信息，使用后端信息
    let message = error.message || '网络错误'
    if (error.response) {
      // 服务器返回了响应
      const res = error.response.data
      if (res && typeof res === 'object') {
        message = res.message || res.error || `服务器错误 (${status})`
      } else {
        message = `服务器错误 (${status})`
      }
      // 500 错误时，记录详细信息用于调试
      if (status === 500) {
        console.error('服务器 500 错误:', {
          url: error.config?.url,
          method: error.config?.method,
          data: error.config?.data,
          response: res,
        })
      }
    } else if (error.request) {
      // 请求已发送但没有收到响应（可能是网络问题或代理问题）
      message = '无法连接到服务器，请检查网络或代理配置'
      console.error('请求失败，未收到响应:', error.config?.url)
    }
    return Promise.reject(new Error(message))
  }
)

export default instance


instance.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  console.log('【request 拦截器】config.data', config.data)
  // ✅ 新增：把最终要放到 HTTP body 的字符串也打印
  console.log('【request 拦截器】即将发送的原始 body', JSON.stringify(config.data))
  return config
})