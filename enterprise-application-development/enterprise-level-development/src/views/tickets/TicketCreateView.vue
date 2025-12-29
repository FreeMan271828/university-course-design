<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useTicketStore } from '@/stores/ticket'
import { useAuthStore } from '@/stores/auth'
import { fetchUsers, type User } from '@/api/user'

const router = useRouter()
const ticketStore = useTicketStore()
const auth = useAuthStore()

const users = ref<User[]>([])

const form = reactive({
  title: '',
  description: '',
  priority: 'MEDIUM',
  category: '',
  device_id: '',
  location: '',
  monitor_model: '',
  assigneeId: null,
})

const priorityOptions = [
  { label: '低', value: 'LOW' },
  { label: '中', value: 'MEDIUM' },
  { label: '高', value: 'HIGH' },
]

const categoryOptions = [
  { label: '硬件', value: 'HARDWARE' },
  { label: '软件', value: 'SOFTWARE' },
  { label: '网络', value: 'NETWORK' },
  { label: '其他', value: 'OTHER' },
]

onMounted(async () => {
  try {
    users.value = await fetchUsers()
  } catch (error) {
    message.error('获取用户列表失败')
  }
})

// 构建 customFields 对象
const buildCustomFields = (): Record<string, unknown> => {
  return {
    device_id: form.device_id,
    location: form.location,
    monitor_model: form.monitor_model,
  }
}

const onFinish = async () => {
  try {
    // 只提交后端要求的字段：title, description, priority, category, customFields
    const payload: Record<string, unknown> = {
      title: form.title,
      description: form.description,
      priority: form.priority.toUpperCase(), // 确保是大写
      category: form.category.toUpperCase(), // 确保是大写
      customFields: buildCustomFields(),
      assigneeId: form.assigneeId || null,
    }

    console.log('【组件】即将传给 store 的 payload', payload)
    

    // 创建工单，返回工单 ID
    const ticketId = await ticketStore.create(payload)
    message.success('工单创建成功')
    // 跳转到工单详情页
    router.push(`/tickets/${ticketId}`)
  } catch (error: any) {
    message.error(error?.message || '创建工单失败，请重试')
  }
}
</script>

<template>
  <div class="page">
    <h2>新建工单</h2>
    <a-form :model="form" layout="vertical" @finish="onFinish">
      <a-form-item label="标题" name="title" :rules="[{ required: true, message: '请输入标题' }]">
        <a-input v-model:value="form.title" placeholder="请输入工单标题" />
      </a-form-item>

      <a-form-item
        label="描述"
        name="description"
        :rules="[{ required: true, message: '请输入描述' }]"
      >
        <a-textarea
          v-model:value="form.description"
          :rows="6"
          placeholder="请详细描述工单内容"
          show-count
          :maxlength="2000"
        />
      </a-form-item>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-item label="优先级" name="priority" :rules="[{ required: true }]">
            <a-select v-model:value="form.priority" :options="priorityOptions" />
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="分类" name="category" :rules="[{ required: true, message: '请选择分类' }]">
            <a-select
              v-model:value="form.category"
              :options="categoryOptions"
              placeholder="请选择分类"
            />
          </a-form-item>
        </a-col>
      </a-row>

      <a-form-item label="处理人" name="assigneeId">
        <a-select
          v-model:value="form.assigneeId"
          placeholder="请选择处理人"
          :options="users.map((u: any) => ({ label: u.name || u.realName, value: u.id }))"
          allow-clear
        />
      </a-form-item>

      <!-- <a-form-item label="创建人">
        <a-input
          :value="auth.user?.id || '未知用户ID'"
          disabled
          placeholder="创建人将自动设置为当前登录用户"
        />
      </a-form-item> -->

      <a-form-item label="当前位置">
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item
              name="device_id"
              :rules="[{ required: true, message: '请输入设备ID' }]"
              style="margin-bottom: 0"
            >
              <a-input
                v-model:value="form.device_id"
                placeholder="设备ID（如：ASSET-CN-2024-001）"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item
              name="location"
              :rules="[{ required: true, message: '请输入位置' }]"
              style="margin-bottom: 0"
            >
              <a-input
                v-model:value="form.location"
                placeholder="位置（如：工位 3-205）"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item
              name="monitor_model"
              :rules="[{ required: true, message: '请输入显示器型号' }]"
              style="margin-bottom: 0"
            >
              <a-input
                v-model:value="form.monitor_model"
                placeholder="显示器型号（如：Dell U2720Q）"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form-item>

      <a-form-item>
        <a-space>
          <a-button type="primary" html-type="submit" :loading="ticketStore.loading">
            提交
          </a-button>
          <a-button @click="router.back()">取消</a-button>
        </a-space>
      </a-form-item>
    </a-form>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}
h2 {
  margin-bottom: 24px;
}
</style>
