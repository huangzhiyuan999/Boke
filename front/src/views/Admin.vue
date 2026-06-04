<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="admin-brand">
        <span class="brand-icon">&#9881;</span>
        <span class="brand-text">后台管理</span>
      </div>
      <nav class="admin-nav">
        <button class="nav-item" :class="{ active: tab === 'overview' }" @click="tab = 'overview'">概览</button>
        <button class="nav-item" :class="{ active: tab === 'articles' }" @click="tab = 'articles'">文章管理</button>
        <button class="nav-item" :class="{ active: tab === 'announcements' }" @click="tab = 'announcements'">公告管理</button>
        <button class="nav-item" :class="{ active: tab === 'messages' }" @click="tab = 'messages'">留言管理</button>
        <button class="nav-item" :class="{ active: tab === 'users' }" @click="tab = 'users'">用户管理</button>
      </nav>
      <div class="admin-sidebar-footer">
        <button class="logout-btn" @click="handleLogout">退出登录</button>
      </div>
    </aside>

    <main class="admin-main">
      <header class="admin-topbar">
        <h1>{{ tabTitle }}</h1>
        <span class="admin-user">{{ currentUser?.username || '管理员' }}</span>
      </header>

      <div class="admin-content">
        <!-- ===== 概览 ===== -->
        <div v-if="tab === 'overview'">
          <div class="stat-grid">
            <div class="stat-card"><span class="stat-num">{{ stats.postCount }}</span><span class="stat-label">文章总数</span></div>
            <div class="stat-card"><span class="stat-num">{{ stats.announcementCount }}</span><span class="stat-label">公告数</span></div>
            <div class="stat-card"><span class="stat-num">{{ stats.messageCount }}</span><span class="stat-label">留言数</span></div>
            <div class="stat-card"><span class="stat-num">{{ stats.totalVisits }}</span><span class="stat-label">总访问量</span></div>
          </div>
          <div class="charts-row" v-if="stats.postTrend && stats.postTrend.length">
            <div class="chart-col">
              <LineChart title="文章阅读趋势" :labels="['5/22','5/23','5/24','5/25','5/26','5/27','5/28']" :series="stats.postTrend" />
            </div>
            <div class="chart-col">
              <LineChart title="用户活跃趋势" :labels="['5/22','5/23','5/24','5/25','5/26','5/27','5/28']" :series="stats.userTrend || []" />
            </div>
          </div>
        </div>

        <!-- ===== 文章管理 ===== -->
        <div v-if="tab === 'articles'">
          <div class="section-header">
            <h2>文章列表</h2>
            <div class="section-actions">
              <div class="admin-search">
                <input v-model="articleSearch" type="search" placeholder="搜索标题、标签、日期或ID" />
                <button v-if="articleSearch" type="button" class="search-clear" @click="articleSearch = ''">清空</button>
              </div>
              <button class="add-btn" @click="openCreateModal('post')">+ 新建文章</button>
            </div>
          </div>
          <div class="table-wrap">
            <table class="admin-table">
              <thead><tr><th>ID</th><th>标题</th><th>日期</th><th>标签</th><th>操作</th></tr></thead>
              <tbody>
                <tr v-for="p in filteredPosts" :key="p.id">
                  <td>{{ p.id }}</td>
                  <td class="td-title">{{ p.title }}</td>
                  <td>{{ p.createdAt || p.date }}</td>
                  <td><span v-for="t in (p.tags || [])" :key="t" class="mini-tag">{{ t }}</span></td>
                  <td>
                    <button class="action-btn" @click="openEditModal(p)">编辑</button>
                    <button class="action-btn danger" @click="deletePost(p)">删除</button>
                  </td>
                </tr>
                <tr v-if="posts.length > 0 && filteredPosts.length === 0">
                  <td colspan="5" class="empty-table-cell">没有找到匹配的文章</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- ===== 公告管理 ===== -->
        <div v-if="tab === 'announcements'">
          <div class="section-header">
            <h2>公告列表</h2>
            <div class="section-actions">
              <div class="admin-search">
                <input v-model="announcementSearch" type="search" placeholder="搜索标题、摘要、日期或ID" />
                <button v-if="announcementSearch" type="button" class="search-clear" @click="announcementSearch = ''">清空</button>
              </div>
              <button class="add-btn" @click="openCreateModal('announcement')">+ 新建公告</button>
            </div>
          </div>
          <div class="table-wrap">
            <table class="admin-table">
              <thead><tr><th>ID</th><th>标题</th><th>日期</th><th>摘要</th><th>操作</th></tr></thead>
              <tbody>
                <tr v-for="a in filteredAnnouncements" :key="a.id">
                  <td>{{ a.id }}</td>
                  <td class="td-title">{{ a.title }}</td>
                  <td>{{ a.createdAt || a.date }}</td>
                  <td class="td-summary">{{ a.summary }}</td>
                  <td>
                    <button class="action-btn" @click="openEditModal(a)">编辑</button>
                    <button class="action-btn danger" @click="deletePost(a)">删除</button>
                  </td>
                </tr>
                <tr v-if="announcements.length > 0 && filteredAnnouncements.length === 0">
                  <td colspan="5" class="empty-table-cell">没有找到匹配的公告</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- ===== 留言管理 ===== -->
        <div v-if="tab === 'messages'">
          <div class="section-header">
            <h2>留言列表</h2>
            <div class="message-search">
              <input v-model="messageSearch" type="search" placeholder="搜索昵称、内容、时间或ID" />
              <button v-if="messageSearch" type="button" class="search-clear" @click="messageSearch = ''">清空</button>
            </div>
          </div>
          <div v-if="adminMessages.length === 0" class="empty-msg">暂无留言</div>
          <div v-else-if="filteredMessages.length === 0" class="empty-msg">没有找到匹配的留言</div>
          <div class="msg-cards" v-else>
            <div class="msg-card" v-for="m in filteredMessages" :key="m.id">
              <div class="msg-card-header">
                <span class="msg-card-name">{{ m.name }}</span>
                <span class="msg-card-time">{{ m.time }}</span>
                <button class="action-btn danger" style="margin-left:auto" @click="deleteMessage(m)">删除</button>
              </div>
              <p class="msg-card-text">{{ m.content }}</p>
            </div>
          </div>
        </div>

        <!-- ===== 用户管理 ===== -->
        <div v-if="tab === 'users'">
          <div class="section-header">
            <h2>用户列表</h2>
            <div class="admin-search">
              <input v-model="userSearch" type="search" placeholder="搜索用户名、邮箱、状态或ID" />
              <button v-if="userSearch" type="button" class="search-clear" @click="userSearch = ''">清空</button>
            </div>
          </div>
          <div class="table-wrap">
            <table class="admin-table">
              <thead><tr><th>ID</th><th>用户名</th><th>邮箱</th><th>注册时间</th><th>状态</th><th>角色</th><th>操作</th></tr></thead>
              <tbody>
                <tr v-for="u in filteredUsers" :key="u.id">
                  <td>{{ u.id }}</td>
                  <td class="td-title">{{ u.username }}</td>
                  <td>{{ u.email }}</td>
                  <td>{{ u.createdAt }}</td>
                  <td><span class="status-dot" :class="u.status">{{ statusMap[u.status] || u.status }}</span></td>
                  <td>{{ u.role === 'admin' ? '管理员' : '用户' }}</td>
                  <td>
                    <button class="action-btn" v-if="u.status === 'active'" @click="setUserStatus(u, 'muted')">禁言</button>
                    <button class="action-btn" v-if="u.status === 'muted'" @click="setUserStatus(u, 'active')">解禁</button>
                    <button class="action-btn" v-if="u.status !== 'banned'" @click="setUserStatus(u, 'banned')">封禁</button>
                    <button class="action-btn danger" v-if="u.role !== 'admin'" @click="deleteUser(u)">删除</button>
                  </td>
                </tr>
                <tr v-if="adminUsers.length > 0 && filteredUsers.length === 0">
                  <td colspan="7" class="empty-table-cell">没有找到匹配的用户</td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="admin-section" style="margin-top:24px">
            <h2>修改管理员密码</h2>
            <form class="pwd-form" @submit.prevent="changePwd">
              <label class="pwd-field"><span>当前密码</span><input type="password" v-model="pwdForm.old" placeholder="输入当前密码" /></label>
              <label class="pwd-field"><span>新密码</span><input type="password" v-model="pwdForm.new1" placeholder="输入新密码" /></label>
              <label class="pwd-field"><span>确认新密码</span><input type="password" v-model="pwdForm.new2" placeholder="再次输入新密码" /></label>
              <button type="submit" class="add-btn" :disabled="pwdLoading">{{ pwdLoading ? '修改中...' : '修改密码' }}</button>
              <span class="pwd-msg" v-if="pwdMsg" :class="pwdMsgType">{{ pwdMsg }}</span>
            </form>
          </div>
        </div>
      </div>
    </main>

    <!-- ===== Post Edit Modal ===== -->
    <div class="modal-overlay" v-if="showPostModal" @click.self="closePostModal">
      <div class="modal-card">
        <h2>{{ editingPost ? '编辑' : '新建' }}{{ postForm.postType === 'announcement' ? '公告' : '文章' }}</h2>
        <form @submit.prevent="savePost">
          <label class="form-field">
            <span>标题</span>
            <input v-model="postForm.title" type="text" placeholder="请输入标题" required />
          </label>
          <label class="form-field">
            <span>摘要</span>
            <input v-model="postForm.summary" type="text" placeholder="简短描述（可选）" />
          </label>
          <div class="form-field">
            <span>封面渐变色</span>
            <div class="color-picker">
              <button type="button" v-for="(c, i) in coverColors" :key="i"
                class="color-dot" :class="{ active: postForm.cover === c }"
                :style="{ background: c }" @click="postForm.cover = c"
              ></button>
            </div>
          </div>
          <label class="form-field">
            <span>标签（逗号分隔）</span>
            <input v-model="postForm.tagsStr" type="text" placeholder="如：Vue, 前端, CSS" />
          </label>
          <label class="form-field">
            <span>内容（Markdown）</span>
            <textarea v-model="postForm.content" rows="12" placeholder="Markdown 正文..." required></textarea>
          </label>
          <div class="modal-actions">
            <button type="button" class="cancel-btn" @click="closePostModal">取消</button>
            <button type="submit" class="submit-btn" :disabled="savingPost">{{ savingPost ? '保存中...' : '保存' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { currentUser, logout } from '../stores/auth.js'
import { api } from '../utils/api.js'
import LineChart from '../components/LineChart.vue'

const router = useRouter()
const tab = ref('overview')
const statusMap = { active: '正常', muted: '禁言', banned: '封禁' }

const tabTitle = computed(() => {
  const map = { overview: '概览', articles: '文章管理', announcements: '公告管理', messages: '留言管理', users: '用户管理' }
  return map[tab.value] || ''
})

const stats = ref({ postCount: 0, announcementCount: 0, messageCount: 0, userCount: 0, totalVisits: 0, postTrend: [], userTrend: [] })
const posts = ref([])
const articleSearch = ref('')
const announcements = ref([])
const announcementSearch = ref('')
const adminMessages = ref([])
const messageSearch = ref('')
const adminUsers = ref([])
const userSearch = ref('')
const pwdForm = ref({ old: '', new1: '', new2: '' })
const pwdMsg = ref('')
const pwdMsgType = ref('')
const pwdLoading = ref(false)

// ---- Post modal ----
const showPostModal = ref(false)
const editingPost = ref(null)
const savingPost = ref(false)
const postForm = ref({ title: '', summary: '', cover: '', content: '', tagsStr: '', postType: 'post' })

const coverColors = [
  'linear-gradient(135deg, #667eea, #764ba2)',
  'linear-gradient(135deg, #f093fb, #f5576c)',
  'linear-gradient(135deg, #4facfe, #00f2fe)',
  'linear-gradient(135deg, #43e97b, #38f9d7)',
  'linear-gradient(135deg, #fa709a, #fee140)',
  'linear-gradient(135deg, #a18cd1, #fbc2eb)',
  'linear-gradient(135deg, #f56c6c, #e63946)',
  'linear-gradient(135deg, #5B8C5A, #7EC8A8)'
]

const filteredPosts = computed(() => {
  const keyword = articleSearch.value.trim().toLowerCase()
  if (!keyword) return posts.value

  return posts.value.filter(p => {
    const fields = [p.id, p.title, p.summary, p.content, p.createdAt, p.date, ...(p.tags || [])]
    return fields.some(v => String(v || '').toLowerCase().includes(keyword))
  })
})

const filteredMessages = computed(() => {
  const keyword = messageSearch.value.trim().toLowerCase()
  if (!keyword) return adminMessages.value

  return adminMessages.value.filter(m => {
    const fields = [m.id, m.name, m.content, m.time]
    return fields.some(v => String(v || '').toLowerCase().includes(keyword))
  })
})

const filteredAnnouncements = computed(() => {
  const keyword = announcementSearch.value.trim().toLowerCase()
  if (!keyword) return announcements.value

  return announcements.value.filter(a => {
    const fields = [a.id, a.title, a.summary, a.content, a.createdAt, a.date, ...(a.tags || [])]
    return fields.some(v => String(v || '').toLowerCase().includes(keyword))
  })
})

const filteredUsers = computed(() => {
  const keyword = userSearch.value.trim().toLowerCase()
  if (!keyword) return adminUsers.value

  return adminUsers.value.filter(u => {
    const roleText = u.role === 'admin' ? '管理员' : '用户'
    const statusText = statusMap[u.status] || u.status
    const fields = [u.id, u.username, u.email, u.createdAt, u.status, statusText, u.role, roleText]
    return fields.some(v => String(v || '').toLowerCase().includes(keyword))
  })
})

function openCreateModal(type) {
  editingPost.value = null
  postForm.value = { title: '', summary: '', cover: coverColors[0], content: '', tagsStr: '', postType: type }
  showPostModal.value = true
}

function openEditModal(p) {
  editingPost.value = p
  postForm.value = {
    title: p.title || '',
    summary: p.summary || '',
    cover: p.cover || coverColors[0],
    content: p.content || '',
    tagsStr: (p.tags || []).join(', '),
    postType: p.postType || 'post'
  }
  showPostModal.value = true
}

function closePostModal() {
  showPostModal.value = false
  editingPost.value = null
}

async function savePost() {
  savingPost.value = true
  try {
    const tags = postForm.value.tagsStr
      .split(/[,，]/)
      .map(t => t.trim())
      .filter(t => t)

    const body = {
      title: postForm.value.title,
      summary: postForm.value.summary,
      cover: postForm.value.cover,
      content: postForm.value.content,
      tags: tags.length ? tags : undefined,
      postType: postForm.value.postType,
      isPublished: 1
    }

    if (editingPost.value) {
      await api.put(`/posts/${editingPost.value.id}`, body)
    } else {
      await api.post('/posts', body)
    }

    closePostModal()
    loadPosts()
    loadAnnouncements()
  } catch (e) {
    alert(e.message || '保存失败')
  } finally {
    savingPost.value = false
  }
}

// ---- Data loading ----
async function loadStats() {
  try { stats.value = await api.get('/admin/stats') } catch (e) { /* ignore */ }
}
async function loadPosts() {
  try { posts.value = (await api.get('/posts?type=post')) || [] } catch (e) { /* ignore */ }
}
async function loadAnnouncements() {
  try { announcements.value = (await api.get('/posts?type=announcement')) || [] } catch (e) { /* ignore */ }
}
async function loadMessages() {
  try { adminMessages.value = (await api.get('/messages')) || [] } catch (e) { /* ignore */ }
}
async function loadUsers() {
  try {
    const res = await api.get('/admin/users?page=1&size=100')
    adminUsers.value = res.records || []
  } catch (e) { /* ignore */ }
}

onMounted(() => { loadStats(); loadPosts(); loadAnnouncements(); loadMessages(); loadUsers() })

async function deletePost(p) {
  if (!confirm(`确定删除 "${p.title}"?`)) return
  try {
    await api.delete(`/posts/${p.id}`)
    posts.value = posts.value.filter(x => x.id !== p.id)
    announcements.value = announcements.value.filter(x => x.id !== p.id)
  } catch (e) { alert(e.message || '删除失败') }
}

async function deleteMessage(m) {
  if (!confirm('确定删除此留言?')) return
  try {
    await api.delete(`/messages/${m.id}`)
    adminMessages.value = adminMessages.value.filter(x => x.id !== m.id)
  } catch (e) { alert(e.message || '删除失败') }
}

async function setUserStatus(user, status) {
  try {
    await api.put(`/admin/users/${user.id}/status`, { status })
    user.status = status
  } catch (e) { alert(e.message || '操作失败') }
}

async function deleteUser(user) {
  if (!confirm(`确定删除用户 "${user.username}"?`)) return
  try {
    await api.delete(`/admin/users/${user.id}`)
    adminUsers.value = adminUsers.value.filter(x => x.id !== user.id)
  } catch (e) { alert(e.message || '删除失败') }
}

async function changePwd() {
  const { old, new1, new2 } = pwdForm.value
  if (!old || !new1 || !new2) { pwdMsg.value = '请填写所有字段'; pwdMsgType.value = 'error'; return }
  if (new1 !== new2) { pwdMsg.value = '两次新密码不一致'; pwdMsgType.value = 'error'; return }
  pwdLoading.value = true
  try {
    await api.put('/auth/password', { oldPassword: old, newPassword: new1 })
    pwdMsg.value = '密码修改成功!'
    pwdMsgType.value = 'success'
    pwdForm.value = { old: '', new1: '', new2: '' }
  } catch (e) {
    pwdMsg.value = e.message || '修改失败'
    pwdMsgType.value = 'error'
  } finally { pwdLoading.value = false }
}

function handleLogout() { logout(); router.push('/') }
</script>

<style scoped>
.admin-layout { display: flex; min-height: calc(100vh - 60px); }
.admin-sidebar { width: 220px; background: #1E293B; color: #E2E8F0; display: flex; flex-direction: column; flex-shrink: 0; }
.admin-brand { display: flex; align-items: center; gap: 10px; padding: 20px 18px; border-bottom: 1px solid #334155; font-size: 1.05rem; font-weight: 600; }
.brand-icon { font-size: 1.3rem; }
.admin-nav { flex: 1; padding: 12px 0; }
.nav-item { display: block; width: 100%; padding: 12px 18px; border: none; background: none; color: #94A3B8; text-align: left; font-size: 0.9rem; font-family: inherit; cursor: pointer; transition: background 0.15s, color 0.15s; border-left: 3px solid transparent; }
.nav-item:hover { background: #334155; color: #E2E8F0; }
.nav-item.active { background: #334155; color: #fff; border-left-color: #38f9d7; }
.admin-sidebar-footer { padding: 16px 18px; border-top: 1px solid #334155; }
.logout-btn { width: 100%; padding: 8px 0; border: 1px solid #475569; background: none; color: #94A3B8; border-radius: 6px; cursor: pointer; font-family: inherit; font-size: 0.85rem; transition: background 0.2s, color 0.2s; }
.logout-btn:hover { background: #ef4444; color: #fff; border-color: #ef4444; }
.admin-main { flex: 1; background: #F1F5F9; min-width: 0; }
.admin-topbar { display: flex; justify-content: space-between; align-items: center; padding: 18px 28px; background: #fff; border-bottom: 1px solid #E2E8F0; }
.admin-topbar h1 { font-size: 1.2rem; margin: 0; }
.admin-user { font-size: 0.85rem; color: var(--color-text-muted); }
.admin-content { padding: 24px 28px; }

.stat-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card { background: #fff; border-radius: var(--radius-md); padding: 20px; text-align: center; box-shadow: var(--shadow-sm); }
.stat-num { display: block; font-size: 1.8rem; font-weight: 700; color: var(--color-primary-dark); }
.stat-label { font-size: 0.82rem; color: var(--color-text-muted); margin-top: 4px; }
.charts-row { display: flex; gap: 16px; }
.chart-col { flex: 1; min-width: 0; }

.admin-section { background: #fff; border-radius: var(--radius-md); padding: 20px 24px; box-shadow: var(--shadow-sm); }
.admin-section h2 { font-size: 1rem; margin: 0 0 14px; }

.section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.section-header h2 { font-size: 1rem; margin: 0; }
.section-actions { display: flex; align-items: center; gap: 10px; }
.message-search, .admin-search { display: flex; align-items: center; gap: 8px; }
.message-search input, .admin-search input { width: 260px; max-width: 42vw; padding: 8px 12px; border: 1px solid var(--color-border); border-radius: 6px; background: #fff; font-size: 0.85rem; font-family: inherit; outline: none; }
.message-search input:focus, .admin-search input:focus { border-color: var(--color-primary-light); box-shadow: 0 0 0 3px rgba(91,140,90,0.12); }
.search-clear { padding: 7px 12px; border: 1px solid var(--color-border); border-radius: 6px; background: #fff; color: var(--color-text-secondary); cursor: pointer; font-family: inherit; font-size: 0.82rem; }
.search-clear:hover { background: var(--color-bg); color: var(--color-text); }
.add-btn { padding: 7px 18px; background: var(--color-primary); color: #fff; border: none; border-radius: 6px; cursor: pointer; font-family: inherit; font-size: 0.85rem; font-weight: 500; transition: background 0.2s; }
.add-btn:hover { background: var(--color-primary-dark); }
.add-btn:disabled { opacity: 0.6; cursor: not-allowed; }

.table-wrap { background: #fff; border-radius: var(--radius-md); overflow: hidden; box-shadow: var(--shadow-sm); }
.admin-table { width: 100%; border-collapse: collapse; font-size: 0.88rem; }
.admin-table th, .admin-table td { padding: 12px 14px; text-align: left; border-bottom: 1px solid #E2E8F0; }
.admin-table thead { background: #F8FAFC; }
.admin-table th { color: var(--color-text-muted); font-weight: 500; font-size: 0.8rem; }
.empty-table-cell { text-align: center; padding: 36px 14px; color: var(--color-text-muted); }
.td-title { font-weight: 500; max-width: 240px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.td-summary { max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: var(--color-text-secondary); }

.mini-tag { font-size: 0.73rem; padding: 2px 8px; background: var(--color-tag-bg); color: var(--color-tag-text); border-radius: 10px; margin-right: 4px; }
.action-btn { padding: 4px 12px; border: 1px solid var(--color-border); background: none; border-radius: 4px; cursor: pointer; font-size: 0.78rem; font-family: inherit; margin-right: 6px; transition: background 0.15s; }
.action-btn:hover { background: var(--color-bg); }
.action-btn.danger { color: #ef4444; border-color: #fca5a5; }
.action-btn.danger:hover { background: #fef2f2; }

.empty-msg { text-align: center; padding: 60px 0; color: var(--color-text-muted); }
.msg-cards { display: flex; flex-direction: column; gap: 12px; }
.msg-card { background: #fff; border-radius: var(--radius-md); padding: 18px 20px; box-shadow: var(--shadow-sm); }
.msg-card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 8px; }
.msg-card-name { font-weight: 600; font-size: 0.9rem; }
.msg-card-time { font-size: 0.78rem; color: var(--color-text-muted); }
.msg-card-text { margin: 0; font-size: 0.9rem; color: var(--color-text-secondary); line-height: 1.6; }

.status-dot { font-size: 0.8rem; font-weight: 500; }
.status-dot.active { color: #22c55e; }
.status-dot.muted { color: #f59e0b; }
.status-dot.banned { color: #ef4444; }

.pwd-form { display: flex; flex-direction: column; gap: 14px; max-width: 360px; }
.pwd-field { display: flex; flex-direction: column; gap: 4px; }
.pwd-field span { font-size: 0.85rem; font-weight: 500; color: var(--color-text); }
.pwd-field input { padding: 8px 12px; border: 1px solid var(--color-border); border-radius: 6px; font-size: 0.9rem; font-family: inherit; outline: none; }
.pwd-field input:focus { border-color: var(--color-primary-light); }
.pwd-msg { font-size: 0.82rem; }
.pwd-msg.success { color: var(--color-primary); }
.pwd-msg.error { color: #ef4444; }

/* --- Modal --- */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 300; display: flex; align-items: center; justify-content: center; padding: 24px; }
.modal-card { background: #fff; border-radius: var(--radius-lg); padding: 28px 32px; width: 100%; max-width: 680px; max-height: 90vh; overflow-y: auto; box-shadow: 0 12px 48px rgba(0,0,0,0.2); }
.modal-card h2 { font-size: 1.2rem; margin: 0 0 20px; }
.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 16px; }
.form-field span { font-size: 0.88rem; font-weight: 500; }
.form-field input[type="text"], .form-field textarea { padding: 10px 14px; border: 1px solid var(--color-border); border-radius: var(--radius-sm); font-size: 0.92rem; font-family: inherit; outline: none; resize: vertical; background: #fafbfc; transition: border-color 0.2s; }
.form-field input[type="text"]:focus, .form-field textarea:focus { border-color: var(--color-primary-light); background: #fff; }
.color-picker { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.color-dot { width: 28px; height: 28px; border-radius: 50%; border: 2px solid transparent; cursor: pointer; transition: border-color 0.15s, transform 0.15s; }
.color-dot:hover { transform: scale(1.1); }
.color-dot.active { border-color: var(--color-primary); transform: scale(1.15); }
.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
.cancel-btn { padding: 9px 24px; border: 1px solid var(--color-border); background: none; border-radius: 20px; cursor: pointer; font-family: inherit; font-size: 0.9rem; color: var(--color-text-secondary); }
.cancel-btn:hover { background: var(--color-bg); }
.submit-btn { padding: 9px 28px; background: var(--color-primary); color: #fff; border: none; border-radius: 20px; cursor: pointer; font-family: inherit; font-size: 0.9rem; font-weight: 500; }
.submit-btn:hover { background: var(--color-primary-dark); }
.submit-btn:disabled { opacity: 0.6; cursor: not-allowed; }

@media (max-width: 768px) {
  .admin-sidebar { width: 180px; }
  .stat-grid { grid-template-columns: repeat(2, 1fr); }
  .admin-content { padding: 16px; }
  .section-header { align-items: flex-start; flex-direction: column; gap: 10px; }
  .section-actions { width: 100%; align-items: stretch; flex-direction: column; }
  .message-search, .admin-search { width: 100%; }
  .message-search input, .admin-search input { flex: 1; width: auto; max-width: none; }
}
</style>
