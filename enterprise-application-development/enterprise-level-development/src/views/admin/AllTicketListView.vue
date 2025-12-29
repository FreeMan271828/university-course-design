<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useTicketStore } from '@/stores/ticket'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const ticketStore = useTicketStore()
const auth = useAuthStore()

// 检查权限
if (!auth.user || auth.user.role !== 'admin') {
  router.push('/tickets')
}

// 管理员视角可不带过滤，直接获取所有
onMounted(() => {
  ticketStore.getList()
})
</script>

<template>
  <div class="page">
    <h2>全部工单（管理员）</h2>
    <a-table :data-source="ticketStore.list" :loading="ticketStore.loading" row-key="id" bordered>
      <a-table-column title="ID" data-index="id" key="id" />
      <a-table-column title="标题" data-index="title" key="title" />
      <a-table-column title="状态" data-index="status" key="status" />
      <a-table-column title="优先级" data-index="priority" key="priority" />
      <a-table-column title="指派" data-index="assigneeName" key="assigneeName" />
      <a-table-column title="创建时间" data-index="createdAt" key="createdAt" />
    </a-table>
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
</style>

