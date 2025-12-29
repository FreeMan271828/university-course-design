<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useTicketStore } from '@/stores/ticket'
import { useAuthStore } from '@/stores/auth'
import { fetchUsers, type User } from '@/api/user'

const router = useRouter()
const ticketStore = useTicketStore()
const auth = useAuthStore()
const users = ref<User[]>([])

const filters = reactive({
  status: '',
  creatorId: undefined as number | undefined,
  assigneeId: auth.user?.id,
})

const statusOptions = [
  { label: '待处理', value: 'PENDING' },
  { label: '处理中', value: 'OPEN' },
  { label: '已解决', value: 'RESOLVED' },
  { label: '已关闭', value: 'CLOSED' },
  { label: '已取消', value: 'CANCELLED' },
]

// 获取状态标签颜色
const getStatusColor = (status: string) => {
  const statusMap: Record<string, string> = {
    PENDING: 'blue',
    OPEN: 'orange',
    RESOLVED: 'green',
    CLOSED: 'default',
    CANCELLED: 'red',
  }
  return statusMap[status.toUpperCase()] || 'default'
}

// 获取状态中文
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    PENDING: '待处理',
    OPEN: '处理中',
    RESOLVED: '已解决',
    CLOSED: '已关闭',
    CANCELLED: '已取消',
  }
  return statusMap[status.toUpperCase()] || status
}

const fetch = async () => {
  // 构建查询参数，所有有值的筛选条件都会一起传递
  const params: Record<string, unknown> = {}
  
  // 状态筛选
  if (filters.status) {
    params.status = filters.status
  }
  
  // 创建人ID筛选
  if (filters.creatorId) {
    params.creatorId = filters.creatorId
  }
  
  // 指派人ID筛选（固定为当前用户）
  if (filters.assigneeId) {
    params.assigneeId = filters.assigneeId
  }
  
  console.log('查询参数:', params)
  await ticketStore.getList(params)
}

onMounted(async () => {
  // 获取用户列表
  try {
    users.value = await fetchUsers()
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
  
  // 页面加载时自动获取工单（筛选为分配给我的）
  fetch()
})

const onSearch = () => {
  // 点击查询按钮时，传递当前筛选条件
  fetch()
}
const toCreate = () => router.push('/tickets/create')
const toDetail = (id: number | string) => router.push(`/tickets/${id}`)

// 重置筛选
const resetFilters = () => {
  filters.status = ''
  filters.creatorId = undefined
  filters.assigneeId = auth.user?.id
  fetch()
}
</script>

<template>
  <div class="page">
    <div class="toolbar">
      <a-select
        v-model:value="filters.creatorId"
        placeholder="创建人筛选"
        allow-clear
        style="width: 200px"
        show-search
        :filter-option="false"
        :options="users.map((u: any) => ({ label: u.realName, value: u.id }))"
      />
      <a-select
        v-model:value="filters.status"
        placeholder="状态筛选"
        allow-clear
        style="width: 140px"
      >
        <a-select-option v-for="opt in statusOptions" :key="opt.value" :value="opt.value">
          {{ opt.label }}
        </a-select-option>
      </a-select>
      <a-button type="primary" @click="onSearch">查询</a-button>
      <a-button @click="resetFilters">重置</a-button>
      <a-button type="dashed" @click="toCreate">新建工单</a-button>
    </div>

    <a-spin :spinning="ticketStore.loading">
      <div v-if="ticketStore.list.length === 0" class="empty">
        <a-empty description="暂无工单数据" />
      </div>
      <div v-else class="card-grid">
        <div
          v-for="ticket in ticketStore.list"
          :key="ticket.id"
          class="ticket-card"
          @click="toDetail(ticket.id)"
        >
          <div class="card-header">
            <div class="serial-no">{{ ticket.serialNo || `#${ticket.id}` }}</div>
            <a-tag :color="getStatusColor(ticket.status)">
              {{ getStatusText(ticket.status) }}
            </a-tag>
          </div>
          <div class="card-title">{{ ticket.title }}</div>
          <div class="card-info">
            <div class="info-item">
              <span class="label">创建人：</span>
              <span class="value">{{ ticket.creatorName || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">指派人：</span>
              <span class="value">{{ ticket.assigneeName || '-' }}</span>
            </div>
          </div>
          <div class="card-footer">
            <span class="time">{{ ticket.createdAt ? new Date(ticket.createdAt).toLocaleDateString() : '-' }}</span>
          </div>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.empty {
  padding: 60px 0;
  text-align: center;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  padding: 0;
}

.ticket-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ticket-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #1890ff;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.serial-no {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
  letter-spacing: 0.5px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  line-height: 1.5;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.card-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  flex: 1;
}

.info-item {
  display: flex;
  align-items: center;
  font-size: 14px;
}

.info-item .label {
  color: #8c8c8c;
  margin-right: 4px;
}

.info-item .value {
  color: #262626;
  font-weight: 500;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  margin-top: auto;
}

.time {
  font-size: 12px;
  color: #8c8c8c;
}

@media (max-width: 1200px) {
  .card-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .card-grid {
    grid-template-columns: 1fr;
  }
  
  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }
  
  .toolbar > * {
    width: 100%;
  }
}
</style>
