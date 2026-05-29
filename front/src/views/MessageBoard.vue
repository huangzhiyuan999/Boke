<template>
  <div class="page-bg" :style="{ backgroundImage: `url('/photo/默认背景图.jpg')` }">
    <div class="bg-overlay"></div>
    <div class="message-board container">
      <div class="page-header">
        <div>
          <h1 class="page-title">留言板</h1>
          <p class="page-desc">留下你想说的话~</p>
        </div>
        <input v-model="searchQuery" class="page-search" type="text" placeholder="搜索留言..." />
      </div>

      <div class="msg-list" v-if="filteredMessages.length">
        <div class="msg-card" v-for="msg in filteredMessages" :key="msg.id">
          <div class="msg-header">
            <span class="msg-avatar">{{ msg.name[0] }}</span>
            <div class="msg-meta">
              <span class="msg-name">{{ msg.name }}</span>
              <span class="msg-time">{{ msg.time }}</span>
            </div>
          </div>
          <p class="msg-content">{{ msg.content }}</p>
        </div>
      </div>
      <div class="msg-empty" v-else>
        <p>{{ loading ? '加载中...' : '暂无留言，来留下第一条吧' }}</p>
      </div>

      <form class="msg-form" @submit.prevent="submit">
        <input v-model="form.name" placeholder="你的昵称" maxlength="20" required />
        <textarea v-model="form.content" placeholder="说点什么..." rows="4" maxlength="500" required></textarea>
        <button type="submit" class="submit-btn" :disabled="submitting">{{ submitting ? '发布中...' : '发布留言' }}</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from '../utils/api.js'

const searchQuery = ref('')
const messages = ref([])
const loading = ref(true)
const submitting = ref(false)

async function loadMessages() {
  loading.value = true
  try {
    const data = await api.get('/messages')
    messages.value = data || []
  } catch (e) {
    console.error('Failed to load messages:', e)
  } finally {
    loading.value = false
  }
}

onMounted(loadMessages)

const filteredMessages = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return messages.value
  return messages.value.filter(m =>
    m.name.toLowerCase().includes(q) ||
    m.content.toLowerCase().includes(q)
  )
})

const form = ref({ name: '', content: '' })

async function submit() {
  if (!form.value.name.trim() || !form.value.content.trim()) return
  submitting.value = true
  try {
    await api.post('/messages', { guestName: form.value.name.trim(), content: form.value.content.trim() })
    form.value = { name: '', content: '' }
    await loadMessages()
  } catch (e) {
    alert(e.message || '发布失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-bg { position: relative; min-height: calc(100vh - 60px); background-size: cover; background-position: center 25%; background-repeat: no-repeat; background-attachment: fixed; }
.bg-overlay { position: fixed; inset: 0; background: rgba(255, 255, 255, 0.85); pointer-events: none; z-index: 0; }
.message-board { position: relative; z-index: 1; padding-top: 48px; padding-bottom: 60px; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-title { font-size: 1.8rem; margin: 0 0 4px; }
.page-desc { color: var(--color-text-muted); margin: 0; font-size: 0.95rem; }
.page-search { padding: 8px 16px; border: 1px solid var(--color-border); border-radius: 20px; font-size: 0.88rem; font-family: inherit; background: rgba(255,255,255,0.7); outline: none; width: 200px; transition: border-color 0.2s; }
.page-search:focus { border-color: var(--color-primary-light); background: #fff; }
.msg-list { display: flex; flex-direction: column; gap: 16px; margin-bottom: 40px; }
.msg-card { background: var(--color-surface); border-radius: var(--radius-md); padding: 20px 24px; box-shadow: var(--shadow-sm); }
.msg-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.msg-avatar { width: 36px; height: 36px; border-radius: 50%; background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light)); color: #fff; font-weight: 600; font-size: 0.9rem; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.msg-meta { display: flex; flex-direction: column; gap: 2px; }
.msg-name { font-weight: 600; font-size: 0.95rem; }
.msg-time { font-size: 0.8rem; color: var(--color-text-muted); }
.msg-content { margin: 0; color: var(--color-text-secondary); line-height: 1.7; }
.msg-empty { text-align: center; padding: 48px 0; color: var(--color-text-muted); }
.msg-form { display: flex; flex-direction: column; gap: 12px; background: var(--color-surface); padding: 24px; border-radius: var(--radius-md); box-shadow: var(--shadow-sm); }
.msg-form input, .msg-form textarea { width: 100%; padding: 10px 14px; border: 1px solid var(--color-border); border-radius: var(--radius-sm); font-family: var(--font-sans); font-size: 0.95rem; color: var(--color-text); background: var(--color-bg); outline: none; transition: border-color 0.2s; resize: vertical; }
.msg-form input:focus, .msg-form textarea:focus { border-color: var(--color-primary-light); }
.submit-btn { align-self: flex-end; padding: 8px 24px; background: var(--color-primary); color: #fff; border: none; border-radius: var(--radius-sm); font-size: 0.95rem; font-weight: 500; cursor: pointer; transition: background 0.2s; }
.submit-btn:hover { background: var(--color-primary-dark); }
.submit-btn:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
