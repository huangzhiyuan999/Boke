<template>
  <div class="page-bg" :style="{ backgroundImage: `url('/photo/默认背景图.jpg')` }">
    <div class="bg-overlay"></div>
    <div class="following-page container">
      <div class="page-header">
        <h1 class="page-title">我的关注</h1>
        <div class="header-right">
          <input v-model="searchQuery" class="page-search" type="text" placeholder="搜索关注..." />
          <button class="add-follow-btn" @click="openAddModal">+ 添加关注</button>
        </div>
      </div>

      <div class="follow-list">
        <div class="follow-card" v-for="user in filteredList" :key="user.id">
          <div class="follow-avatar" :style="{ background: user.avatarColor || 'linear-gradient(135deg, #5B8C5A, #7EC8A8)' }">
            {{ user.name[0] }}
          </div>
          <div class="follow-info">
            <div class="follow-header">
              <span class="follow-name">{{ user.name }}</span>
              <span class="follow-badge" v-if="user.newPosts">+{{ user.newPosts }} 更新</span>
            </div>
            <p class="follow-bio">{{ user.bio }}</p>
            <span class="follow-last">{{ user.lastPost }}</span>
          </div>
          <button class="follow-btn followed" @click="unfollow(user)">已关注</button>
        </div>
        <p v-if="!filteredList.length && !loading" class="empty-hint">暂无关注，去发现页关注一些用户吧</p>
      </div>

      <div class="suggest-section" v-if="suggestList.length">
        <h2 class="suggest-title">推荐关注</h2>
        <div class="follow-list">
          <div class="follow-card" v-for="user in suggestList" :key="user.id">
            <div class="follow-avatar" :style="{ background: user.avatarColor || 'linear-gradient(135deg, #5B8C5A, #7EC8A8)' }">
              {{ user.name[0] }}
            </div>
            <div class="follow-info">
              <span class="follow-name">{{ user.name }}</span>
              <p class="follow-bio">{{ user.bio }}</p>
              <span class="follow-fans">{{ user.fans || '' }}</span>
            </div>
            <button class="follow-btn" :class="{ followed: user.followed }" @click="toggleSuggestFollow(user)">
              {{ user.followed ? '已关注' : '+ 关注' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add Follow Modal -->
    <div class="modal-overlay" v-if="showAddModal" @click.self="showAddModal = false">
      <div class="modal-card">
        <h2>添加关注</h2>
        <input
          v-model="searchInput"
          class="modal-search"
          type="text"
          placeholder="输入用户名搜索..."
          @input="handleSearch"
          autofocus
        />
        <div class="modal-user-list" v-if="searchResults.length">
          <div class="modal-user-card" v-for="u in searchResults" :key="u.id">
            <div class="follow-avatar" :style="{ background: u.avatarColor || 'linear-gradient(135deg, #5B8C5A, #7EC8A8)' }">{{ u.name[0] }}</div>
            <div class="follow-info">
              <span class="follow-name">{{ u.name }}</span>
              <span class="follow-bio">{{ u.bio || '这个人很懒，什么都没写' }}</span>
            </div>
            <button class="follow-btn" :class="{ followed: u.followed }" @click="toggleSearchFollow(u)">
              {{ u.followed ? '已关注' : '+ 关注' }}
            </button>
          </div>
        </div>
        <p class="modal-empty" v-else-if="searchInput && !searching">未找到匹配用户</p>
        <div class="modal-actions" style="margin-top:16px">
          <button class="cancel-btn" @click="showAddModal = false">完成</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { requireAuth } from '../stores/auth.js'
import { api } from '../utils/api.js'

const router = useRouter()
const searchQuery = ref('')
const loading = ref(true)
const followList = ref([])
const suggestList = ref([])

async function loadData() {
  if (!requireAuth(router)) return
  loading.value = true
  try {
    const [following, suggestions] = await Promise.all([
      api.get('/follows/following'),
      api.get('/follows/suggestions')
    ])
    followList.value = (following || []).map(f => ({ ...f, followed: true }))
    suggestList.value = (suggestions || []).map(s => ({ ...s, followed: false }))
  } catch (e) {
    console.error('Failed to load follows:', e)
  } finally {
    loading.value = false
  }
}

onMounted(loadData)

const filteredList = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return followList.value
  return followList.value.filter(u =>
    u.name.toLowerCase().includes(q) ||
    (u.bio || '').toLowerCase().includes(q)
  )
})

async function unfollow(user) {
  try {
    await api.delete(`/follows/${user.id}`)
    followList.value = followList.value.filter(f => f.id !== user.id)
  } catch (e) {
    console.error('Unfollow failed:', e)
  }
}

async function toggleSuggestFollow(user) {
  try {
    if (user.followed) {
      await api.delete(`/follows/${user.id}`)
      user.followed = false
    } else {
      await api.post('/follows', { followedId: user.id })
      user.followed = true
      followList.value.unshift({ ...user, followed: true, lastPost: '', newPosts: 0 })
    }
  } catch (e) {
    console.error('Follow toggle failed:', e)
  }
}

const showAddModal = ref(false)
const searchInput = ref('')
const searchResults = ref([])
const searching = ref(false)

let searchTimer = null
function handleSearch() {
  clearTimeout(searchTimer)
  const q = searchInput.value.trim()
  if (!q) { searchResults.value = []; return }
  searchTimer = setTimeout(async () => {
    searching.value = true
    try {
      const data = await api.get(`/follows/search?q=${encodeURIComponent(q)}`)
      searchResults.value = data || []
    } catch (e) {
      console.error('Search failed:', e)
    } finally {
      searching.value = false
    }
  }, 300)
}

async function toggleSearchFollow(user) {
  try {
    if (user.followed) {
      await api.delete(`/follows/${user.id}`)
      user.followed = false
      followList.value = followList.value.filter(f => f.id !== user.id)
    } else {
      await api.post('/follows', { followedId: user.id })
      user.followed = true
      followList.value.unshift({ ...user, followed: true, lastPost: '', newPosts: 0 })
    }
  } catch (e) {
    console.error('Follow toggle failed:', e)
  }
}

function openAddModal() {
  searchInput.value = ''
  searchResults.value = []
  showAddModal.value = true
}
</script>

<style scoped>
.page-bg { position: relative; min-height: calc(100vh - 60px); background-size: cover; background-position: center 25%; background-repeat: no-repeat; background-attachment: fixed; }
.bg-overlay { position: fixed; inset: 0; background: rgba(255, 255, 255, 0.85); pointer-events: none; z-index: 0; }
.following-page { position: relative; z-index: 1; padding-top: 32px; padding-bottom: 60px; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-title { font-size: 1.6rem; margin: 0; }
.header-right { display: flex; align-items: center; gap: 10px; }
.page-search { padding: 8px 16px; border: 1px solid var(--color-border); border-radius: 20px; font-size: 0.88rem; font-family: inherit; background: rgba(255,255,255,0.7); outline: none; width: 200px; transition: border-color 0.2s; }
.page-search:focus { border-color: var(--color-primary-light); background: #fff; }
.add-follow-btn { padding: 8px 20px; background: var(--color-primary); color: #fff; border: none; border-radius: 20px; font-size: 0.88rem; font-weight: 500; cursor: pointer; font-family: inherit; white-space: nowrap; transition: background 0.2s; }
.add-follow-btn:hover { background: var(--color-primary-dark); }
.follow-list { display: flex; flex-direction: column; }
.follow-card { display: flex; align-items: center; gap: 14px; padding: 16px 0; border-bottom: 1px solid var(--color-border); }
.follow-avatar { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #fff; font-weight: 700; font-size: 1.1rem; flex-shrink: 0; }
.follow-info { flex: 1; min-width: 0; }
.follow-header { display: flex; align-items: center; gap: 8px; margin-bottom: 2px; }
.follow-name { font-weight: 600; font-size: 0.95rem; }
.follow-badge { font-size: 0.72rem; background: #e63946; color: #fff; padding: 1px 8px; border-radius: 10px; }
.follow-bio { font-size: 0.85rem; color: var(--color-text-secondary); margin: 2px 0; }
.follow-last { font-size: 0.8rem; color: var(--color-text-muted); }
.follow-fans { font-size: 0.8rem; color: var(--color-text-muted); }
.follow-btn { padding: 6px 18px; border: 1px solid var(--color-primary); background: none; color: var(--color-primary); border-radius: 16px; font-size: 0.82rem; font-weight: 500; cursor: pointer; transition: all 0.2s; white-space: nowrap; flex-shrink: 0; }
.follow-btn:hover { background: var(--color-primary); color: #fff; }
.follow-btn.followed { background: var(--color-border); border-color: var(--color-border); color: var(--color-text-secondary); }
.follow-btn.followed:hover { background: #e8e8e8; color: #666; }
.empty-hint { text-align: center; padding: 40px 0; color: var(--color-text-muted); }
.suggest-section { margin-top: 36px; }
.suggest-title { font-size: 1.1rem; margin: 0 0 4px; color: var(--color-text-secondary); }
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 200; display: flex; align-items: center; justify-content: center; padding: 24px; }
.modal-card { background: #fff; border-radius: var(--radius-lg); padding: 28px 32px; width: 100%; max-width: 480px; max-height: 80vh; overflow-y: auto; box-shadow: 0 12px 48px rgba(0,0,0,0.2); }
.modal-card h2 { font-size: 1.2rem; margin: 0 0 16px; }
.modal-actions { display: flex; justify-content: center; }
.cancel-btn { padding: 8px 32px; border: 1px solid var(--color-border); background: none; border-radius: 20px; cursor: pointer; font-family: inherit; font-size: 0.9rem; color: var(--color-text-secondary); }
.cancel-btn:hover { background: var(--color-bg); }
.modal-search { width: 100%; padding: 10px 16px; border: 1px solid var(--color-border); border-radius: 20px; font-size: 0.92rem; font-family: inherit; outline: none; margin-bottom: 16px; transition: border-color 0.2s; }
.modal-search:focus { border-color: var(--color-primary-light); }
.modal-user-list { display: flex; flex-direction: column; gap: 4px; margin-bottom: 8px; }
.modal-user-card { display: flex; align-items: center; gap: 12px; padding: 12px 14px; border-radius: var(--radius-sm); transition: background 0.15s; }
.modal-user-card:hover { background: var(--color-bg); }
.modal-empty { text-align: center; padding: 32px 0; color: var(--color-text-muted); font-size: 0.9rem; }

@media (max-width: 640px) {
  .page-bg {
    background-attachment: scroll;
  }

  .following-page {
    padding-top: 20px;
    padding-bottom: 44px;
  }

  .page-header {
    align-items: stretch;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 18px;
  }

  .page-title {
    width: 100%;
    font-size: 1.45rem;
    line-height: 1.25;
    padding: 0 2px;
    white-space: nowrap;
  }

  .header-right {
    width: 100%;
    display: grid;
    grid-template-columns: minmax(0, 1fr) auto;
    gap: 8px;
  }

  .page-search {
    width: 100%;
    min-width: 0;
    height: 38px;
    padding: 8px 12px;
    border-radius: 10px;
    background: rgba(255,255,255,0.9);
  }

  .add-follow-btn {
    height: 38px;
    padding: 0 12px;
    border-radius: 10px;
    font-size: 0.82rem;
  }

  .follow-card {
    align-items: flex-start;
    gap: 10px;
    padding: 14px 0;
  }

  .follow-avatar {
    width: 42px;
    height: 42px;
    font-size: 1rem;
  }

  .follow-header {
    align-items: flex-start;
    flex-direction: column;
    gap: 4px;
  }

  .follow-name {
    line-height: 1.3;
  }

  .follow-bio {
    line-height: 1.5;
  }

  .follow-btn {
    padding: 5px 12px;
    border-radius: 12px;
    font-size: 0.78rem;
  }

  .suggest-section {
    margin-top: 28px;
  }

  .modal-overlay {
    align-items: flex-start;
    padding: 12px;
  }

  .modal-card {
    padding: 20px;
    max-height: calc(100vh - 24px);
    border-radius: 10px;
  }

  .modal-user-card {
    align-items: flex-start;
    padding: 12px 0;
  }
}

@media (max-width: 380px) {
  .header-right {
    grid-template-columns: 1fr;
  }

  .add-follow-btn {
    width: 100%;
  }
}
</style>
