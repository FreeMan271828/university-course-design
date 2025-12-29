import { defineStore } from 'pinia'
import { loginApi, registerApi } from '@/api/auth'

//git测试

type User = {
  id: string
  name: string
  role: string
  email?: string
}

type Credentials = {
  username: string
  password: string
  email?: string
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: '' as string,
    user: null as User | null,
    loading: false,
  }),
  actions: {
    async login(payload: Credentials) {
      this.loading = true
      try {
        const data: any = await loginApi(payload)
        // 后端返回格式: { code: 200, data: { token, role: ['ADMIN'], name, id }, message: 'success' }
        // axios 拦截器已提取 data 字段，所以这里 data 是 data.data 部分
        if (typeof data === 'string') {
          // data 是 token 字符串
          this.token = data
          // 注意：登录后不会自动获取用户信息，user 为 null
        } else if (data && typeof data === 'object') {
          // 如果 data 是对象，提取 token 和用户信息
          this.token = data.token || data.accessToken || ''
          const roleValue = Array.isArray(data.role) ? data.role[0] : data.role
          this.user = {
            id: data.id ?? '',
            name: data.name ?? '',
            role: roleValue ?? '',
          }
        } else {
          throw new Error('登录失败：响应数据格式错误')
        }
        if (!this.token) {
          throw new Error('登录失败：未返回凭证')
        }
        localStorage.setItem('token', this.token)
        if (this.user) {
          localStorage.setItem('user', JSON.stringify(this.user))
        }
      } catch (error: any) {
        // 重新抛出错误，让调用方处理
        throw error
      } finally {
        this.loading = false
      }
    },
    async register(payload: Credentials & { realName?: string; roleCode?: string }) {
      this.loading = true
      try {
        // 注册时，只发送 username, password, realName, roleCode
        // realName 可为空字符串，roleCode 默认 USER
        const registerPayload = {
          username: payload.username,
          password: payload.password,
          email: payload.email ?? '',
          realName: payload.realName ?? '',
          roleCode: payload.roleCode ?? 'USER',
        }
        const data: any = await registerApi(registerPayload)
        // 返回原始响应以便调用方可以直接使用（例如用于调试或后续逻辑）
        console.log('【auth.register 接口返回 raw】', data)
        // 后端返回 { code, message, data: {} }，不提供 token，则不设置登录态
        if (data && typeof data === 'object') {
          this.token = data.token || data.accessToken || ''
          this.user = data.user || data.userInfo || null
          // 如果后端没有返回邮箱信息，使用注册时提交的 email 做补充
          if (this.user) {
            this.user.email = this.user.email ?? registerPayload.email ?? ''
          }
        }
        if (this.token) {
          localStorage.setItem('token', this.token)
        }
        if (this.user) {
          localStorage.setItem('user', JSON.stringify(this.user))
        }
        return data
      } catch (error: any) {
        // 重新抛出错误，让调用方处理
        throw error
      } finally {
        this.loading = false
      }
    },
    logout(forceRedirect = false) {
      this.token = ''
      this.user = null
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (forceRedirect) {
        window.location.href = '/auth/login'
      }
    },
    restore() {
      const cached = localStorage.getItem('token')
      const userStr = localStorage.getItem('user')
      if (cached) this.token = cached
      if (userStr) this.user = JSON.parse(userStr)
    },
  },
})

