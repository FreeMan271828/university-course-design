<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const selectedKeys = computed(() => {
  const path = route.path
  if (path.startsWith('/tickets/my-created')) return ['/tickets/my-created']
  if (path.startsWith('/tickets/assigned-to-me')) return ['/tickets/assigned-to-me']
  if (path.startsWith('/admin/all-ticket-list')) return ['/admin/all-ticket-list']
  if (path.startsWith('/admin/sla-config')) return ['/admin/sla-config']
  if (path.startsWith('/ticket-log-controller')) return ['/ticket-log-controller']
  if (path.startsWith('/profile')) return ['/profile']
  return []
})

const isAdmin = computed(() => auth.user?.role === 'ADMIN')
const displayName = computed(() => auth.user?.name || '未登录')
const displayRole = computed(() => {
  if (!auth.user) return '未登录'
  return auth.user.role === 'ADMIN' ? '管理员' : '普通用户'
})

const to = (path: string) => router.push(path)
const logout = () => {
  auth.logout(true)
}
</script>

<template>
  <a-layout style="min-height: 100vh">
    <a-layout-sider breakpoint="lg" collapsed-width="0">
      <div class="logo">Workorder</div>
      <a-menu theme="dark" mode="inline" :selected-keys="selectedKeys">
        <a-menu-item key="/tickets/my-created" @click="to('/tickets/my-created')">我创建的工单</a-menu-item>
        <a-menu-item key="/tickets/assigned-to-me" @click="to('/tickets/assigned-to-me')">分配给我的工单</a-menu-item>
        <a-menu-item v-if="isAdmin" key="/admin/all-ticket-list" @click="to('/admin/all-ticket-list')">全部工单（管理员）</a-menu-item>
        <a-menu-item v-if="isAdmin" key="/ticket-log-controller" @click="to('/ticket-log-controller')">操作日志（管理员）</a-menu-item>
        <a-menu-item v-if="isAdmin" key="/admin/sla-config" @click="to('/admin/sla-config')">SLA 配置（管理员）</a-menu-item> 
        <a-menu-item key="/profile" @click="to('/profile')">个人中心</a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="header">
        <div class="header__title">工单管理系统</div>
        <div class="header__actions">
          <span class="user">{{ displayName }} ({{ displayRole }})</span>
          <a-button type="link" @click="logout">退出</a-button>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<style scoped>
.logo {
  height: 48px;
  margin: 16px;
  color: #fff;
  font-weight: 600;
  display: flex;
  align-items: center;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.header__actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.content {
  margin: 16px;
  padding: 16px;
  background: #fff;
  min-height: calc(100vh - 112px);
}
</style>

