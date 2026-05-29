<template>
  <div class="login-page" :style="{ backgroundImage: `url('/photo/默认背景图.jpg')` }">
    <div class="bg-overlay"></div>
    <div class="login-card">
      <h1 class="login-title">登录</h1>
      <p class="login-desc">欢迎回来</p>
      <form class="login-form" @submit.prevent="handleLogin">
        <label class="field">
          <span>用户名</span>
          <input v-model="form.username" type="text" placeholder="请输入用户名" required />
        </label>
        <label class="field">
          <span>密码</span>
          <input v-model="form.password" type="password" placeholder="请输入密码" required />
        </label>
        <p class="login-msg" v-if="redirectMsg" :class="'warn'">{{ redirectMsg }}</p>
        <p class="login-msg" v-if="msg" :class="msgType">{{ msg }}</p>
        <button type="submit" class="login-btn" :disabled="loading">登 录</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '../stores/auth.js'

const router = useRouter()
const route = useRoute()
const form = ref({ username: '', password: '' })
const msg = ref('')
const msgType = ref('')
const loading = ref(false)

const redirectMsg = route.query.msg || ''

async function handleLogin() {
  loading.value = true
  msg.value = ''
  try {
    const data = await login(form.value.username, form.value.password)
    msg.value = '登录成功'
    msgType.value = 'success'
    setTimeout(() => {
      if (data.role === 'admin') router.push('/admin')
      else router.push('/')
    }, 300)
  } catch (e) {
    msg.value = e.message || '用户名或密码错误'
    msgType.value = 'error'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: calc(100vh - 60px);
  background-size: cover;
  background-position: center 25%;
  background-repeat: no-repeat;
  background-attachment: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.bg-overlay {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.85);
  pointer-events: none;
  z-index: 0;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 380px;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  padding: 40px 32px;
  box-shadow: var(--shadow-md);
  text-align: center;
}

.login-title {
  font-size: 1.6rem;
  margin: 0;
}

.login-desc {
  color: var(--color-text-secondary);
  font-size: 0.9rem;
  margin: 6px 0 28px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
  text-align: left;
}

.field span {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--color-text);
}

.field input {
  padding: 10px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  font-family: var(--font-sans);
  color: var(--color-text);
  background: var(--color-bg);
  outline: none;
  transition: border-color 0.2s;
}

.field input:focus {
  border-color: var(--color-primary-light);
}

.login-msg {
  font-size: 0.85rem;
  margin: 0;
}

.login-msg.success { color: var(--color-primary); }
.login-msg.error { color: #ef4444; }
.login-msg.warn { color: #f59e0b; background: #fef3c7; padding: 8px 12px; border-radius: 6px; }

.login-btn {
  margin-top: 6px;
  padding: 11px 0;
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.login-btn:hover { background: var(--color-primary-dark); }
.login-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.login-hint {
  margin-top: 20px;
  font-size: 0.78rem;
  color: var(--color-text-muted);
}
</style>
