<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { useTicketStore } from '@/stores/ticket'
import { useAuthStore } from '@/stores/auth'
import { fetchUsers, type User } from '@/api/user'

const route = useRoute()
const ticketStore = useTicketStore()
const auth = useAuthStore()
const loading = ref(false)
const commentContent = ref('')
const commentType = ref('PUBLIC')
const users = ref<User[]>([])
const selectedAssigneeId = ref<number | null>(null)

const load = async () => {
  loading.value = true
  await ticketStore.getDetail(route.params.id as string)
  await ticketStore.getComments(route.params.id as string)
  
  // 获取用户列表（用于指派功能）
  try {
    users.value = await fetchUsers()
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
  
  // 如果当前用户是工单的处理员，且工单状态为PENDING，自动更新为OPEN
  if (ticketStore.detail && 
      ticketStore.detail.assigneeId && 
      ticketStore.detail.assigneeId === Number(auth.user?.id) && 
      ticketStore.detail.status === 'PENDING') {
    try {
      await ticketStore.updateStatus(route.params.id as string, { status: 'OPEN' })
      // 重新获取更新后的详情
      await ticketStore.getDetail(route.params.id as string)
    } catch (error) {
      console.error('自动更新工单状态失败:', error)
    }
  }
  
  loading.value = false
}

onMounted(load)

const priorityColor = computed(() => {
  const p = ticketStore.detail?.priority?.toLowerCase()
  if (p === 'high') return 'red'
  if (p === 'medium') return 'orange'
  return 'green'
})

const statusColor = computed(() => {
  const s = ticketStore.detail?.status?.toLowerCase()
  if (s === 'resolved') return 'green'
  if (s === 'open') return 'orange'
  if (s === 'closed') return 'gray'
  return 'blue'
})

// 检查当前用户是否是工单创建者
const isCreator = computed(() => {
  return ticketStore.detail && ticketStore.detail.creatorId === Number(auth.user?.id)
})

// 检查当前用户是否是工单处理员
const isAssignee = computed(() => {
  return ticketStore.detail && ticketStore.detail.assigneeId === Number(auth.user?.id)
})

// 标记工单为已完成
const markCompleted = async () => {
  try {
    console.log('开始更新工单状态为RESOLVED，工单ID:', route.params.id)
    await ticketStore.updateStatus(route.params.id as string, { status: 'RESOLVED' })
    console.log('工单状态更新成功')
    message.success('工单已标记为已完成')
    // 重新获取工单详情和评论
    await load()
    console.log('页面数据重新加载完成，当前状态:', ticketStore.detail?.status)
  } catch (error: any) {
    console.error('标记完成失败:', error)
    message.error(error?.message || '标记完成失败')
  }
}

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    message.warning('请输入评论内容')
    return
  }

  try {
    await ticketStore.createComment(route.params.id as string, {
      content: commentContent.value,
      type: commentType.value
    })
    message.success('评论发送成功')
    commentContent.value = ''
  } catch (error: any) {
    message.error(error?.message || '发送评论失败')
  }
}

// 指派工单给其他用户
const assignTicket = async () => {
  if (!selectedAssigneeId.value) {
    message.warning('请选择要指派的用户')
    return
  }

  try {
    await ticketStore.assignTicket(route.params.id as string, selectedAssigneeId.value)
    message.success('工单指派成功')
    selectedAssigneeId.value = null
    // 重新加载数据
    await load()
  } catch (error: any) {
    console.error('指派工单失败:', error)
    message.error(error?.message || '指派工单失败')
  }
}
</script>

<template>
  <a-spin :spinning="loading">
    <div class="page" v-if="ticketStore.detail">
      <a-card class="glass">
        <div class="header">
          <div class="title-block">
            <div class="eyebrow">工单编号 {{ ticketStore.detail.serialNo || '-' }}</div>
            <h2>{{ ticketStore.detail.title }}</h2>
            <div class="tags">
              <a-tag :color="statusColor">{{ ticketStore.detail.status || '-' }}</a-tag>
              <a-tag :color="priorityColor">
                {{ ticketStore.detail.priority || '-' }}
              </a-tag>
            </div>
            <!-- 操作按钮 -->
            <div class="actions">
              <!-- 处理员操作：标记完成 -->
              <a-button 
                v-if="isAssignee && ticketStore.detail.status === 'OPEN'" 
                type="primary" 
                @click="markCompleted"
                :loading="ticketStore.loading"
              >
                标记完成
              </a-button>
              
              <!-- 处理员操作：指派工单 -->
              <div v-if="isAssignee" class="assignee-actions">
                <a-select
                  v-model:value="selectedAssigneeId"
                  placeholder="选择要指派的用户"
                  style="width: 200px; margin-right: 8px;"
                  :options="users.map((u: any) => ({ label: u.realName, value: u.id }))"
                />
                <a-button 
                  type="default" 
                  @click="assignTicket" 
                  :loading="ticketStore.loading"
                  :disabled="!selectedAssigneeId"
                >
                  指派工单
                </a-button>
              </div>
            </div>
          </div>
          <div class="meta">
            <div class="meta-item">
              <span class="label">创建人</span>
              <span class="value">{{ ticketStore.detail.creatorName || '-' }}</span>
            </div>
            <div class="meta-item">
              <span class="label">指派给</span>
              <span class="value">{{ ticketStore.detail.assigneeName || '-' }}</span>
            </div>
            <div class="meta-item">
              <span class="label">创建时间</span>
              <span class="value">{{ ticketStore.detail.createdAt || '-' }}</span>
            </div>
          </div>
        </div>

        <a-descriptions bordered size="middle" :column="2" class="desc-card">
          <a-descriptions-item label="分类">{{ ticketStore.detail.category || '-' }}</a-descriptions-item>
          <a-descriptions-item label="工单ID">{{ ticketStore.detail.id }}</a-descriptions-item>
          <a-descriptions-item label="响应截止">{{ ticketStore.detail.responseDeadline || '-' }}</a-descriptions-item>
          <a-descriptions-item label="解决截止">{{ ticketStore.detail.resolveDeadline || '-' }}</a-descriptions-item>
        </a-descriptions>

        <div class="description-panel">
          <div class="panel-title">描述</div>
          <div class="panel-body">
            <pre class="desc-text">{{ ticketStore.detail.description || '暂无描述' }}</pre>
          </div>
        </div>

        <!-- 评论区域 -->
        <div class="comments-panel">
          <div class="panel-title">评论</div>
          <div class="panel-body">
            <!-- 评论列表 -->
            <div class="comments-list" v-if="ticketStore.comments.length > 0">
              <div
                v-for="comment in ticketStore.comments"
                :key="comment.id"
                class="comment-item"
              >
                <div class="comment-header">
                  <span class="comment-author">{{ comment.creatorName || '匿名用户' }}</span>
                  <span class="comment-time">{{ comment.createdAt }}</span>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
                <div class="comment-type" v-if="comment.type">{{ comment.type }}</div>
              </div>
            </div>
            <div v-else class="no-comments">暂无评论</div>

            <!-- 添加评论 -->
            <div class="add-comment">
              <a-textarea
                v-model:value="commentContent"
                placeholder="请输入评论内容..."
                :rows="3"
                :maxlength="500"
                show-count
              />
              <div class="comment-actions">
                <a-select v-model:value="commentType" style="width: 120px; margin-right: 12px;">
                  <a-select-option value="PUBLIC">公开</a-select-option>
                </a-select>
                <a-button type="primary" @click="submitComment" :loading="ticketStore.loading">
                  发送评论
                </a-button>
              </div>
            </div>
          </div>
        </div>
      </a-card>
    </div>
  </a-spin>
</template>

<style scoped>
.page {
  padding: 16px;
}
.glass {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(245, 247, 250, 0.95));
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.6);
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}
.title-block .eyebrow {
  font-size: 12px;
  color: #8c8c8c;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  margin-bottom: 4px;
}
h2 {
  margin: 0;
  font-size: 22px;
  font-weight: 700;
}
.tags {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}
.meta {
  display: grid;
  grid-template-columns: repeat(2, max-content);
  gap: 8px 16px;
  text-align: right;
}
.meta-item .label {
  display: block;
  color: #8c8c8c;
  font-size: 12px;
}
.meta-item .value {
  font-size: 14px;
  font-weight: 600;
}
.desc-card {
  margin-top: 16px;
}
.description-panel {
  margin-top: 16px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  padding: 12px 14px;
}
.panel-title {
  font-weight: 600;
  margin-bottom: 8px;
}
.panel-body {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 12px;
}
.desc-text {
  margin: 0;
  white-space: pre-wrap;
  font-family: var(--font-family, 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace);
  color: #444;
}
.alert {
  margin-top: 16px;
}
.comments-panel {
  margin-top: 16px;
  background: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  padding: 12px 14px;
}
.comments-list {
  margin-bottom: 16px;
}
.comment-item {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 12px;
}
.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.comment-author {
  font-weight: 600;
  color: #1890ff;
}
.comment-time {
  font-size: 12px;
  color: #8c8c8c;
}
.comment-content {
  margin-bottom: 4px;
  white-space: pre-wrap;
}
.comment-type {
  font-size: 12px;
  color: #8c8c8c;
  text-transform: uppercase;
}
.no-comments {
  text-align: center;
  color: #8c8c8c;
  padding: 20px;
}
.add-comment {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 12px;
}
.comment-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 8px;
}
.actions {
  margin-top: 12px;
}
.assignee-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
