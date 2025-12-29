<script setup lang="ts">
import { reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const form = reactive({
  username: '',
  password: '',
})

const onFinish = async () => {
  try {
    console.log(form);
    
    await auth.login(form)
    if (!auth.token) {
      message.error('登录失败，未获取到凭证')
      return
    }
    const redirect = (route.query.redirect as string) || '/tickets/my-created' 
    router.push(redirect)
    message.success('登录成功')
  } catch (err: any) {
    message.error(err?.response?.data?.message || '登录失败，请重试')
  }
}

const goRegister = () => router.push('/auth/register')
</script>

<template>
  <div id="login" class="loginContainer">
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
              <h1>欢迎登录</h1>
            </a-form-item>
            <a-form-item name="username" :rules="[{ required: true, message: '请输入账号' }]">
              <a-input v-model:value="form.username" placeholder="账号" autocomplete="username" />
            </a-form-item>
            <a-form-item name="password" :rules="[{ required: true, message: '请输入密码' }]">
              <a-input-password v-model:value="form.password" placeholder="密码" autocomplete="current-password" />
            </a-form-item>
            <div class="adminTip">
              <a-alert
                message="管理员登录提示"
                description="使用用户名 'admin' 登录可获得管理员权限，查看SLA配置等功能"
                type="info"
                show-icon
                closable
              />
            </div>
            <div class="rememberMeWrapper">
              <a-checkbox disabled>记住密码</a-checkbox>
              <a-button type="link" size="small" disabled>忘记密码</a-button>
            </div>
            <a-form-item>
              <a-button type="primary" html-type="submit" block :loading="auth.loading">立即登录</a-button>
            </a-form-item>
            <div class="registerTip">
              还没有账号?
              <a-button type="link" @click="goRegister">立即注册</a-button>
            </div>
            <a-divider>其他登录方式</a-divider>
            <div class="otherLogin">微博 / QQ / GitHub（占位）</div>
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
  background: linear-gradient(135deg, #111827, #1f2937);
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
  background: rgba(37, 99, 235, 0.12);
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
.rememberMeWrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.adminTip {
  margin-bottom: 16px;
}
.registerTip {
  display: flex;
  justify-content: center;
  gap: 8px;
  align-items: center;
  margin-top: -8px;
}
.otherLogin {
  text-align: center;
  color: #999;
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

