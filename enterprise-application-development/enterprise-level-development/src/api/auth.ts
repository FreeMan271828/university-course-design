import request from './axios'

type LoginPayload = {
  username: string
  password: string
}

type RegisterPayload = {
  username: string
  password: string
  realName?: string
  roleCode?: string
  email?: string
}

export function loginApi(payload: LoginPayload) {
  return request.post('/auth/login', payload)
}

export function registerApi(payload: RegisterPayload) {
  // 严格按照后端要求的格式：username, password, realName, roleCode
  const body = {
    username: payload.username,
    password: payload.password,
    realName: payload.realName ?? '',
    roleCode: payload.roleCode ?? 'USER',
    email: payload.email ?? '',
  }
  return request.post('/auth/register', body)
}

export function logoutApi() {
  return request.post('/auth/logout')
}

