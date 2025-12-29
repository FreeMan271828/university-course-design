import { http, HttpResponse } from 'msw'

let user = {
  id: 'u_1',
  name: '演示用户',
  email: 'demo@example.com',
  role: 'user',
}

let tickets = [
  {
    id: 1,
    serialNo: 'T001',
    title: '示例工单：无法登录系统',
    description: '用户反馈登录报 500，需排查。',
    status: 'PENDING',
    priority: 'HIGH',
    category: null,
    creatorId: 1,
    assigneeId: null,
    creatorName: '演示用户',
    assigneeName: null,
    createdAt: new Date().toISOString(),
  },
  {
    id: 2,
    serialNo: 'T002',
    title: '示例工单：页面样式错位',
    description: 'Chrome 版本 120 下表格列宽异常。',
    status: 'OPEN',
    priority: 'MEDIUM',
    category: null,
    creatorId: 1,
    assigneeId: null,
    creatorName: '演示用户',
    assigneeName: null,
    createdAt: new Date().toISOString(),
  },
]

export const handlers = [
  http.get('/api/v1/users', () => {
    return HttpResponse.json(users)
  }),

  http.post('/api/auth/login', async ({ request }) => {
    const body = (await request.json()) as { username: string; email?: string }
    const role = body.username === 'admin' ? ['ADMIN'] : ['USER']
    user = { ...user, name: body.username, email: body.email || user.email, role: role[0] }
    return HttpResponse.json({ token: 'mock-token-123', user })
  }),
  http.post('/api/auth/register', async ({ request }) => {
    const body = (await request.json()) as { username: string; email?: string }
    const role = body.username === 'admin' ? ['ADMIN'] : ['USER']
    user = { ...user, name: body.username, email: body.email || user.email, role: role[0] }
    return HttpResponse.json({ token: 'mock-token-123', user })
  }),
  http.get('/api/auth/profile', () => {
    return HttpResponse.json({ user })
  }),
  http.post('/api/auth/logout', () => HttpResponse.json({}, { status: 204 })),

  http.get('/api/tickets', ({ request }) => {
    const url = new URL(request.url)
    const keyword = url.searchParams.get('keyword')?.toLowerCase() || ''
    const status = url.searchParams.get('status')
    const creatorId = url.searchParams.get('creatorId')
    const assigneeId = url.searchParams.get('assigneeId')
    let list = tickets
    if (keyword) {
      list = list.filter(
        (t) => t.title.toLowerCase().includes(keyword) || t.description.toLowerCase().includes(keyword)
      )
    }
    if (status) list = list.filter((t) => t.status === status)
    if (creatorId) list = list.filter((t) => t.creatorId === parseInt(creatorId))
    if (assigneeId) list = list.filter((t) => t.assigneeId === parseInt(assigneeId))
    return HttpResponse.json({ items: list, total: list.length })
  }),

  http.get('/api/tickets/:id', ({ params }) => {
    const found = tickets.find((t) => t.id === parseInt(params.id as string))
    if (!found) return HttpResponse.json({ message: 'Not found' }, { status: 404 })
    return HttpResponse.json(found)
  }),

  http.post('/api/tickets', async ({ request }) => {
    const body = (await request.json()) as any
    const id = Date.now() // 使用数字ID
    const ticket = {
      id,
      serialNo: `T${id}`,
      title: body.title,
      description: body.description,
      priority: body.priority || 'MEDIUM',
      status: 'PENDING',
      category: body.category || null,
      customFields: body.customFields || {},
      creatorId: body.creatorId || 1, // 默认创建者ID
      assigneeId: body.assigneeId || null,
      creatorName: '演示用户', // 这里可以根据用户ID获取用户名
      assigneeName: null,
      createdAt: new Date().toISOString(),
    }
    tickets = [ticket, ...tickets]
    return HttpResponse.json(ticket)
  }),

  http.put('/api/tickets/:id', async ({ params, request }) => {
    const body = (await request.json()) as any
    tickets = tickets.map((t) => (t.id === parseInt(params.id as string) ? { ...t, ...body } : t))
    const updated = tickets.find((t) => t.id === parseInt(params.id as string))
    return HttpResponse.json(updated)
  }),

  http.post('/api/tickets/:id/comment', async ({ params, request }) => {
    await request.json()
    const found = tickets.find((t) => t.id === parseInt(params.id as string))
    if (!found) return HttpResponse.json({ message: 'Not found' }, { status: 404 })
    return HttpResponse.json({ ok: true })
  }),

  // SLA配置相关API
  http.get('/api/sla', () => {
    return HttpResponse.json([
      {
        priority: 'HIGH',
        responseHours: 2,
        resolveHours: 24
      },
      {
        priority: 'MEDIUM',
        responseHours: 4,
        resolveHours: 48
      },
      {
        priority: 'LOW',
        responseHours: 8,
        resolveHours: 168
      }
    ])
  }),

  http.put('/api/sla', async ({ request }) => {
    const config = await request.json()
    console.log('更新SLA配置:', config)
    return HttpResponse.json({ message: 'SLA配置更新成功' })
  }),

  http.delete('/api/sla/:priority', ({ params }) => {
    const { priority } = params
    console.log('删除SLA配置:', priority)
    return HttpResponse.json({ message: 'SLA配置删除成功' })
  }),
 
  // 操作日志（返回包装格式 { code, message, data }）
  http.get('/api/ticket-logs', ({ request }) => {
    const sample = [
      {
        id: 0,
        ticketId: 0,
        operatorId: 0,
        action: '创建工单',
        details: { additionalProp1: {}, additionalProp2: {}, additionalProp3: {} },
        createdAt: new Date().toISOString(),
        operatorName: '管理员'
      },
    ]
    return HttpResponse.json({ code: 200, message: 'ok', data: sample })
  }),
]

