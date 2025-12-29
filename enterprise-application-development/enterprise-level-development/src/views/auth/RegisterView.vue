<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const form = reactive({
  username: '',
  email: '',
  password: '',
})

const onFinish = async () => {
  try {
    console.log(form);
    const res = await auth.register(form)
    console.log('【RegisterView 注册返回】', res)
    // 注册成功后，如果返回了 token，说明自动登录了，跳转到工单列表
    // 如果没有 token，说明需要手动登录，跳转到登录页
    if (auth.token) {
      message.success('注册成功，已自动登录')
      router.push('/tickets')
    } else {
      message.success('注册成功，请登录')
      // router.push('/auth/login')
    }
  } catch (error: any) {
    message.error(error?.message || '注册失败，请重试')
  }
}

const goLogin = () => router.push('/auth/login')
</script>

<template>
  <div id="register" class="loginContainer">
    <div class="loginHeader">
      <span>Workorder 后台管理系统</span>
    </div>
    <div class="loginMainWrapper">
      <div class="loginWrapper">
        <div class="loginTipsWrapper">
          <span class="siteSummaryTitle">工单管理系统</span>
          <div class="siteSummary">
            <ul>
              <li>✓ 统一方便的工单管理</li>
              <li>✓ 基于 Ant Design Vue 的界面</li>
              <li>✓ 角色与权限区分</li>
              <li>✓ 前后端分离便于维护</li>
              <li>✓ 可平滑接入真实后端</li>
            </ul>
          </div>
        </div>
        <div class="loginBoxWrapper">
          <a-form layout="vertical" :model="form" @finish="onFinish" class="loginForm">
            <a-form-item style="text-align: center">
              <h1>注册新账号</h1>
            </a-form-item>
            <a-form-item name="username" :rules="[{ required: true, message: '请输入账号' }]">
              <a-input v-model:value="form.username" placeholder="账号" autocomplete="username" />
            </a-form-item>
            <a-form-item
              name="email"
              :rules="[{ type: 'email', message: '请输入正确邮箱', required: true }]"
            >
              <a-input v-model:value="form.email" placeholder="邮箱" autocomplete="email" />
            </a-form-item>
            <a-form-item name="password" :rules="[{ required: true, message: '请输入密码' }]">
              <a-input-password v-model:value="form.password" placeholder="密码" autocomplete="new-password" />
            </a-form-item>
            <a-form-item>
              <a-button type="primary" html-type="submit" block :loading="auth.loading">立即注册</a-button>
            </a-form-item>
            <div class="registerTip">
              已有账号?
              <a-button type="link" @click="goLogin">去登录</a-button>
            </div>
          </a-form>
        </div>
      </div>
      <div class="loginFooter">© 2025 Workorder. All rights reserved.</div>
    </div>
  </div>
</template>

<style scoped>
.loginContainer {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a, #1e293b);
  color: #fff;
}
.loginHeader {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 32px;
  font-size: 18px;
  font-weight: 600;
}
.loginMainWrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.loginWrapper {
  width: 100%;
  max-width: 960px;
  margin: 48px 48px 32px;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 32px;
}
.loginTipsWrapper {
  background: rgba(34, 197, 94, 0.12);
  border-radius: 12px;
  padding: 28px;
  color: #fff;
  min-height: 380px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.15);
}
.siteSummaryTitle {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 16px;
}
.siteSummary ul {
  list-style: none;
  padding: 0;
  margin: 0;
  line-height: 1.9;
  font-size: 15px;
}
.siteSummary li {
  margin: 4px 0;
}
.loginBoxWrapper {
  background: #fff;
  color: #111;
  border-radius: 12px;
  padding: 28px 30px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
}
.loginForm h1 {
  margin: 0;
}
.registerTip {
  display: flex;
  justify-content: center;
  gap: 8px;
  align-items: center;
  margin-top: -4px;
}
.loginFooter {
  color: #9ca3af;
  font-size: 12px;
  margin: 16px 0 24px;
  text-align: center;
}
@media (max-width: 960px) {
  .loginWrapper {
    grid-template-columns: 1fr;
  }
}
</style>

