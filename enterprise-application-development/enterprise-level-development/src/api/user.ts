import request from './axios'

export interface User {
  id: number
  name: string
  // 添加其他字段如果需要
}

// 获取用户列表
export function fetchUsers(): Promise<User[]> {
  return request.get('/users')
}