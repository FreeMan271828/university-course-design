import request from './axios'

export interface SLAConfig {
  priority: 'HIGH' | 'MEDIUM' | 'LOW'
  responseHours: number
  resolveHours: number
}

// 获取所有SLA配置
export function getSLAConfigs() {
  return request.get('/sla')
}

// 更新SLA配置（单个优先级）
export function updateSLAConfig(config: SLAConfig) {
  return request.put('/sla', config)
}

// 删除SLA配置（指定优先级）
export function deleteSLAConfig(priority: string) {
  return request.delete(`/sla/${priority}`)
}