<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { getSLAConfigs, updateSLAConfig, deleteSLAConfig, type SLAConfig } from '@/api/sla'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const auth = useAuthStore()
const router = useRouter()

// 检查权限
if (!auth.user || auth.user.role !== 'ADMIN') {
  router.push('/tickets')
}

const slaConfigs = ref<SLAConfig[]>([])
const loading = ref(false)
const modalVisible = ref(false)
const editingConfig = ref<SLAConfig | null>(null)
const isEditMode = ref(false)

const formData = ref<SLAConfig>({
  priority: 'HIGH',
  responseHours: 0,
  resolveHours: 0
})

// 加载SLA配置
const loadConfigs = async () => {
  loading.value = true
  try {
    const data: any = await getSLAConfigs()
    slaConfigs.value = data || []
  } catch (error) {
    console.error('加载SLA配置失败:', error)
    message.error('加载SLA配置失败')
  } finally {
    loading.value = false
  }
}

// 打开添加模态框
const openAddModal = () => {
  isEditMode.value = false
  formData.value = {
    priority: 'HIGH',
    responseHours: 0,
    resolveHours: 0
  }
  modalVisible.value = true
}

// 打开编辑模态框
const openEditModal = (config: SLAConfig) => {
  isEditMode.value = true
  editingConfig.value = config
  formData.value = { ...config }
  modalVisible.value = true
}

// 保存配置
const saveConfig = async () => {
  try {
    await updateSLAConfig(formData.value)
    message.success(isEditMode.value ? 'SLA配置更新成功' : 'SLA配置添加成功')
    modalVisible.value = false
    await loadConfigs()
  } catch (error: any) {
    console.error('保存SLA配置失败:', error)
    message.error(error?.message || '保存SLA配置失败')
  }
}

// 删除配置
const deleteConfig = async (priority: string) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除 ${priority} 优先级的SLA配置吗？`,
    okText: '确定',
    cancelText: '取消',
    onOk: async () => {
      try {
        await deleteSLAConfig(priority)
        message.success('SLA配置删除成功')
        await loadConfigs()
      } catch (error: any) {
        console.error('删除SLA配置失败:', error)
        message.error(error?.message || '删除SLA配置删除失败')
      }
    }
  })
}

// 获取优先级标签
const getPriorityLabel = (priority: string) => {
  const labels = {
    HIGH: '高优先级',
    MEDIUM: '中优先级',
    LOW: '低优先级'
  }
  return labels[priority as keyof typeof labels] || priority
}

// 获取优先级颜色
const getPriorityColor = (priority: string) => {
  const colors = {
    HIGH: 'red',
    MEDIUM: 'orange',
    LOW: 'green'
  }
  return colors[priority as keyof typeof colors] || 'blue'
}

onMounted(() => {
  loadConfigs()
})
</script>

<template>
  <div class="page">
    <div class="header">
      <h2>SLA 配置管理</h2>
      <p class="description">管理不同优先级工单的响应时间和解决时间配置</p>
      <a-button type="primary" @click="openAddModal">
        添加SLA配置
      </a-button>
    </div>

    <a-spin :spinning="loading">
      <div class="config-list">
        <a-card v-for="config in slaConfigs" :key="config.priority" class="config-card">
          <div class="config-header">
            <a-tag :color="getPriorityColor(config.priority)">
              {{ getPriorityLabel(config.priority) }}
            </a-tag>
            <div class="actions">
              <a-button size="small" @click="openEditModal(config)">编辑</a-button>
              <a-button size="small" danger @click="deleteConfig(config.priority)">删除</a-button>
            </div>
          </div>
          <div class="config-content">
            <div class="config-item">
              <span class="label">响应时间：</span>
              <span class="value">{{ config.responseHours }} 小时</span>
            </div>
            <div class="config-item">
              <span class="label">解决时间：</span>
              <span class="value">{{ config.resolveHours }} 小时</span>
            </div>
          </div>
        </a-card>

        <a-empty v-if="slaConfigs.length === 0" description="暂无SLA配置" />
      </div>
    </a-spin>

    <!-- 配置模态框 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEditMode ? '编辑SLA配置' : '添加SLA配置'"
      @ok="saveConfig"
      @cancel="modalVisible = false"
    >
      <a-form layout="vertical">
        <a-form-item label="优先级" required>
          <a-select v-model:value="formData.priority" :disabled="isEditMode">
            <a-select-option value="HIGH">高优先级</a-select-option>
            <a-select-option value="MEDIUM">中优先级</a-select-option>
            <a-select-option value="LOW">低优先级</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="响应时间（小时）" required>
          <a-input-number
            v-model:value="formData.responseHours"
            :min="1"
            :max="168"
            addon-after="小时"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="解决时间（小时）" required>
          <a-input-number
            v-model:value="formData.resolveHours"
            :min="1"
            :max="720"
            addon-after="小时"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style scoped>
.page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.header {
  margin-bottom: 24px;
}

.header h2 {
  margin: 0 0 8px 0;
  color: #1890ff;
  font-weight: 600;
}

.description {
  color: #666;
  margin: 0 0 16px 0;
}

.config-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
}

.config-card {
  transition: all 0.3s;
}

.config-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.config-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.actions {
  display: flex;
  gap: 8px;
}

.config-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.config-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.label {
  font-weight: 500;
  color: #666;
}

.value {
  font-weight: 600;
  color: #1890ff;
}
</style>