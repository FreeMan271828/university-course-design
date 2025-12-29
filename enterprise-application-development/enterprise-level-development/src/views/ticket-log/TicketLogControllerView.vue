<script setup lang="ts">
  import { onMounted, ref, reactive, computed } from 'vue'
  import { useRouter } from 'vue-router'
  import { loadFilterOptions, saveFilterOptions } from '@/views/ticket-log/controller'
  import { fetchTicketLogs } from '@/api/ticket'
  import { message } from 'ant-design-vue'
  
  // 1. 根据图片修正类型定义
  type TicketLog = {
    id: number
    ticketId: number
    operatorId: number
    action: string
    details: Record<string, unknown>
    createdAt: string
    operatorName?: string
  }
  
  const logs = ref<TicketLog[]>([])
  const loading = ref(false)
  // 过滤选项集合（从首次加载的列表中提取并持久化）
  const ticketIdOptions = ref<Array<number | string>>([])
  const actionOptions = ref<string[]>([])
  const operatorOptions = ref<string[]>([])
  
  const filterForm = reactive({
    ticketId: undefined as number | undefined,
    operatorId: undefined as number | undefined,
    action: '' as string,
    operatorName: '' as string,
  })
  
  /**
   * 2. 修改参数构造逻辑
   * 返回顶层过滤对象（reset 时直接传 { query: {} }；搜索时传顶层字段，由 loadLogs 自动包装）
   */
  const buildParamsFromForm = () => {
    const filter: Record<string, any> = {}
    
    if (filterForm.ticketId !== undefined && filterForm.ticketId !== null) filter.ticketId = filterForm.ticketId
    if (filterForm.operatorId !== undefined && filterForm.operatorId !== null) filter.operatorId = filterForm.operatorId
    if (filterForm.action) filter.action = filterForm.action
    if (filterForm.operatorName) filter.operatorName = filterForm.operatorName
    
    // 返回顶层过滤对象，loadLogs 会在必要时包装成 { query: filter }
    return filter
  }
  
  const loadLogs = async (params: any = {}) => {
    loading.value = true
    try {
      // 支持三种调用方式：
      // 1. loadLogs({ query: { ... } })  — 已经是期望结构
      // 2. loadLogs({})                 — 空查询，包装为 { query: {} }
      // 3. loadLogs({ action: 'CREATE' }) — 顶层过滤字段，自动包装为 { query: { action: 'CREATE' } }
      console.log('【loadLogs】raw params', params)

      console.log('【loadLogs】params', params)
      // 直接把 params 作为查询参数发送
      const res: any = await fetchTicketLogs(params || {})
      console.log('【loadLogs】res', res)

      // 兼容各种返回结构，最终保证 logs 是数组
      logs.value = res?.data ?? res?.items ?? (Array.isArray(res) ? res : [])
      // 从返回列表中提取可选筛选项并持久化（ticketId / action / operatorName）
      try {
        const ids = Array.from(new Set(logs.value.map((l: any) => l.ticketId).filter((v: any) => v !== undefined && v !== null)))
        const actions = Array.from(new Set(logs.value.map((l: any) => l.action).filter(Boolean)))
        const operators = Array.from(new Set(logs.value.map((l: any) => l.operatorName).filter(Boolean)))
        const merged = saveFilterOptions({ ticketIds: ids, actions, operatorNames: operators })
        ticketIdOptions.value = merged.ticketIds
        actionOptions.value = merged.actions
        operatorOptions.value = merged.operatorNames
      } catch (e) {
        console.error('extract/persist filter options failed', e)
      }
    } catch (err: any) {
      message.error(err?.message || '获取日志失败')
    } finally {
      loading.value = false
    }
  }
  
  onMounted(() => {
    /**
     * 初始进入页面：加载 action 为 CREATE 的日志
     * 通过传入顶层字段，loadLogs 会自动包装为 { query: { action: 'CREATE' } }
     */
    // 首次加载：尝试读取持久化的选项并加载列表（不传参）
    const existing = loadFilterOptions()
    ticketIdOptions.value = existing.ticketIds
    actionOptions.value = existing.actions
    operatorOptions.value = existing.operatorNames
    loadLogs()
  })
  
  // 表格分页相关
  const currentPage = ref(1)
  const pageSize = 8
  const total = computed(() => logs.value.length)
  const pagedLogs = computed(() => {
    const start = (currentPage.value - 1) * pageSize
    return logs.value.slice(start, start + pageSize)
  })

  const router = useRouter()
  const formatDate = (s: string) => {
    try {
      if (!s) return ''
      const d = new Date(s)
      return d.toLocaleString()
    } catch (e) {
      return String(s)
    }
  }
  const setPage = (p: number) => {
    currentPage.value = p
  }

  const onSearch = () => {
    const params = buildParamsFromForm()
    // buildParamsFromForm 返回顶层过滤对象，loadLogs 会包装成 { query: ... }
    loadLogs(params)
    currentPage.value = 1
  }

  const onReset = () => {
    filterForm.ticketId = undefined
    filterForm.operatorId = undefined
    filterForm.action = ''
    filterForm.operatorName = ''
    // 重置后也是获取全部（传空 query）
    loadLogs()
    currentPage.value = 1
  }

  const goToDetail = (id: number | string) => {
    router.push({ name: 'ticket-log-detail', params: { id } })
  }
 
  </script>

<template>
  <div>
    <a-page-header title="操作流程" />
    <a-spin :spinning="loading">
      <a-card style="margin-bottom:12px" :body-style="{ padding: '12px' }">
        <a-form layout="inline" style="width:100%; align-items:center;">
          <a-form-item>
            <a-select v-model:value="filterForm.ticketId" :options="ticketIdOptions.map(i => ({ label: String(i), value: i }))" allowClear placeholder="工单ID" style="min-width:140px" />
          </a-form-item>
          <a-form-item>
            <a-select v-model:value="filterForm.operatorName" :options="operatorOptions.map(i => ({ label: i, value: i }))" allowClear placeholder="操作人姓名" style="min-width:180px" />
          </a-form-item>
          <a-form-item>
            <a-select v-model:value="filterForm.action" :options="actionOptions.map(i => ({ label: i, value: i }))" allowClear placeholder="操作动作" style="min-width:160px" />
          </a-form-item>
          <a-form-item style="margin-left:auto">
            <a-button type="primary" @click="onSearch">查询</a-button>
            <a-button style="margin-left:8px" @click="onReset">重置</a-button>
          </a-form-item>
        </a-form>
      </a-card>
      <div v-if="logs.length === 0" style="padding: 16px">暂无操作流程日志</div>
      <div v-else>
        <a-table :data-source="pagedLogs" :pagination="false" rowKey="id">
          <a-table-column title="操作人" dataIndex="operatorName" />
          <a-table-column title="操作" dataIndex="action" />
        <a-table-column title="时间" key="createdAt" v-slot="{ record }">
          {{ formatDate(record.createdAt) }}
        </a-table-column>
          <a-table-column title="操作" key="actions" width="100" v-slot="{ record }">
            <a-button type="link" @click="goToDetail(record.id)">详情</a-button>
          </a-table-column>
        </a-table>

        <div style="margin-top: 12px; display:flex; justify-content:center;">
          <a-pagination :current="currentPage" :pageSize="pageSize" :total="total" @change="setPage" />
        </div>
      </div>
    </a-spin>
  </div>
</template>

<style scoped>
pre {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, "Roboto Mono", "Helvetica Neue", monospace;
  font-size: 13px;
}
</style>


