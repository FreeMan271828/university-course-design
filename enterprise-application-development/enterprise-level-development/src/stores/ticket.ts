import { defineStore } from 'pinia'
import { createTicket, fetchTicketDetail, fetchTickets, updateTicket, fetchTicketComments, createTicketComment, updateTicketStatus, assignTicket, updateTicketAssignee } from '@/api/ticket'
import { useAuthStore } from '@/stores/auth'

export type Ticket = {
  id: number
  serialNo: string
  description: string
  title: string
  priority: string
  status: string
  category: string | null
  customFields?: Record<string, unknown>
  responseDeadline?: string | null
  resolveDeadline?: string | null
  responseAt?: string | null
  resolveAt?: string | null
  creatorId: number
  assigneeId: number | null
  creatorName: string | null
  assigneeName: string | null
  createdAt: string
}

export const useTicketStore = defineStore('ticket', {
  state: () => ({
    list: [] as Ticket[],
    total: 0,
    loading: false,
    detail: null as Ticket | null,
    comments: [] as any[],
  }),
  actions: {
    async getList(params?: Record<string, unknown>) {
      this.loading = true
      try {
        // 后端返回格式: { code: 200, data: [...], message: "success" }
        // axios 拦截器已提取 data 字段，所以这里返回的是数组
        const data: any = await fetchTickets(params)
        if (Array.isArray(data)) {
          this.list = data
          this.total = data.length
        } else {
          // 兼容其他格式
          this.list = data?.items ?? data?.list ?? []
          this.total = data?.total ?? this.list.length
        }
      } catch (error) {
        console.error('获取工单列表失败:', error)
        this.list = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    async getDetail(id: string | number) {
      this.loading = true
      try {
        const data: any = await fetchTicketDetail(id)
        this.detail = data.item ?? data
      } finally {
        this.loading = false
      }
    },
    async create(payload: Record<string, unknown>) {
      const auth = useAuthStore()
      
      // 自动添加创建者ID，确保是数字类型
      const creatorId = payload.creatorId || auth.user?.id
      const payloadWithCreator = {
        ...payload,
        // creatorId: creatorId ? parseInt(String(creatorId)) : 1, // 确保是数字类型
      }
      
      console.log('用户信息:', auth.user)
      console.log('原始payload:', payload)

      console.log('【store】即将传给 API 的 payloadWithCreator', payloadWithCreator)
      
      // 后端返回格式: { code: 0, message: "string", data: 0 }
      // axios 拦截器已提取 data 字段，所以这里返回的是工单 ID (number)
      const ticketId = await createTicket(payloadWithCreator)
      // 创建成功后，可以选择刷新列表或返回工单 ID
      return ticketId
    },
    async update(id: string | number, payload: Record<string, unknown>) {
      return updateTicket(id, payload)
    },
    async updateStatus(id: string | number, payload: Record<string, unknown>) {
      return updateTicketStatus(id, payload)
    },
    async assignTicket(id: string | number, assigneeId: number) {
      return assignTicket(id, assigneeId)
    },
    async updateAssignee(id: string | number, payload: Record<string, unknown>) {
      return updateTicketAssignee(id, payload)
    },
    async getComments(id: string | number) {
      try {
        const data = await fetchTicketComments(id)
        this.comments = Array.isArray(data) ? data : []
      } catch (error) {
        console.error('获取评论失败:', error)
        this.comments = []
      }
    },
    async createComment(id: string | number, payload: { content: string, type: string }) {
      const result = await createTicketComment(id, payload)
      // 创建评论后刷新评论列表
      await this.getComments(id)
      return result
    },

  },
})

