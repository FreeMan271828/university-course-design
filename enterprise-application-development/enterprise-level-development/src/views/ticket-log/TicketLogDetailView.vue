<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchTicketLogs } from '@/api/ticket'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const log = ref<Record<string, any> | null>(null)
const loading = ref(false)
const formatDate = (s: string) => {
  try {
    if (!s) return ''
    return new Date(s).toLocaleString()
  } catch (e) {
    return String(s)
  }
}

const loadDetail = async () => {
  loading.value = true
  try {
    // 使用 fetchTicketLogs 以 id 筛选（fetchTicketLogs 会自动包装 query）
    const res: any = await fetchTicketLogs({ id: route.params.id })
    // res 可能是数组或包装结构，兼容处理
    const list = res?.data ?? res?.items ?? (Array.isArray(res) ? res : [])
    log.value = list && list.length > 0 ? list[0] : null
  } catch (err: any) {
    console.error('加载操作日志详情失败', err)
    message.error(err?.message || '加载详情失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDetail()
})

const goBack = () => {
  router.back()
}
</script>

<template>
  <div style="padding: 16px">
    <a-page-header title="操作日志详情" @back="goBack" />
    <a-spin :spinning="loading">
      <div v-if="!log" style="padding: 16px">未找到对应的操作日志</div>
      <div v-else>
        <a-card>
          <a-descriptions title="基本信息" bordered column="1" size="small">
            <a-descriptions-item label="ID">{{ log.id }}</a-descriptions-item>
            <a-descriptions-item label="工单ID">{{ log.ticketId }}</a-descriptions-item>
            <a-descriptions-item label="操作人 ID">{{ log.operatorId }}</a-descriptions-item>
            <a-descriptions-item label="操作人">{{ log.operatorName }}</a-descriptions-item>
            <a-descriptions-item label="操作">{{ log.action }}</a-descriptions-item>
            <a-descriptions-item label="时间">{{ formatDate(log.createdAt) }}</a-descriptions-item>
          </a-descriptions>

          <a-divider />

          <a-collapse>
            <a-collapse-panel key="details" header="操作详情（JSON）">
              <pre style="white-space: pre-wrap; margin:0;">{{ JSON.stringify(log.details, null, 2) }}</pre>
            </a-collapse-panel>
          </a-collapse>
        </a-card>
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


