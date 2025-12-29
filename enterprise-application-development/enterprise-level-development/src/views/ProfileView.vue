<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const user = computed(() => auth.user)
</script>

<template>
  <div class="page">
    <h2>个人中心</h2>
    <a-descriptions bordered :column="1" v-if="user">
      <a-descriptions-item label="姓名">{{ user.name }}</a-descriptions-item>
      <a-descriptions-item label="邮箱">{{ user.email || '-' }}</a-descriptions-item>
      <a-descriptions-item label="角色">{{ user.role }}</a-descriptions-item>
      <a-descriptions-item label="Token">
        <div class="token">{{ auth.token || '未登录' }}</div>
      </a-descriptions-item>
    </a-descriptions>
    <a-button type="primary" danger @click="auth.logout(true)">退出登录</a-button>
    <a-alert
      message="在接入真实后端后，可在此添加修改密码、刷新令牌等功能。"
      type="info"
      show-icon
    />
  </div>
</template>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.token {
  word-break: break-all;
}
</style>

