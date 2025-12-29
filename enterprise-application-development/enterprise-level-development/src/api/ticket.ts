import request from './axios'

export function fetchTickets(params?: Record<string, unknown>) {
  return request.get('/tickets', { params })
}

export function fetchTicketDetail(id: string | number) {
  return request.get(`/tickets/${id}`)
}

export function createTicket(payload: Record<string, unknown>) {
  console.log('【createTicket】发给 axios 的原始 payload', payload)
  return request.post('/tickets', payload)
}
 
/**
 * 获取操作流程日志
 * 适配 Swagger 要求：GET 请求参数必须包装在 query 对象中
 */
export function fetchTicketLogs(params?: Record<string, any>) {
  // 新行为：直接将传入的 params 作为查询参数发送
  // 例如: fetchTicketLogs({ ticketId: 39, action: 'CREATE' }) -> ?ticketId=39&action=CREATE
  const finalParams = params || {}
  console.log('【fetchTicketLogs】params', finalParams)
  return request.get('/ticket-logs', { params: finalParams })
}

export function updateTicket(id: string | number, payload: Record<string, unknown>) {
  return request.put(`/tickets/${id}`, payload)
}

// 更新工单状态
export function updateTicketStatus(id: string | number, payload: Record<string, unknown>) {
  return request.patch(`/tickets/${id}/status`, payload)
}

// 指派工单
export function assignTicket(id: string | number, assigneeId: number) {
  return request.patch(`/tickets/${id}/assign?assigneeId=${assigneeId}`)
}

// 更新工单指派人
export function updateTicketAssignee(id: string | number, payload: Record<string, unknown>) {
  return request.patch(`/tickets/${id}/assignee`, payload)
}

// // 添加工单评论
// export function addTicketComment(id: string | number, payload: { content: string }) {
//   return request.post(`/tickets/${id}/comment`, payload)
// }

// 评论相关 API 新
export function fetchTicketComments(id: string | number) {
  return request.get(`/tickets/${id}/comments`)
}

export function createTicketComment(id: string | number, payload: { content: string, type: string }) {
  return request.post(`/tickets/${id}/comments`, payload)
}

