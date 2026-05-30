<template>
  <div class="page-bg" :style="{ backgroundImage: `url('/photo/默认背景图.jpg')` }">
    <div class="bg-overlay"></div>
    <div class="recommend-page container">
      <div class="recommend-header">
        <div>
          <h1 class="page-title">推荐</h1>
          <p class="page-subtitle">发现更多精彩内容</p>
        </div>
        <div class="header-right">
          <input v-model="searchQuery" class="recommend-search" type="text" placeholder="搜索动态、话题..." />
          <button class="create-btn" @click="handleCreateClick">+ 创建</button>
        </div>
      </div>

      <div class="feed-list">
        <article class="feed-card" v-for="item in filteredItems" :key="item.id">
          <div class="feed-header">
            <div class="feed-avatar" :style="{ background: item.avatarColor || 'linear-gradient(135deg, #5B8C5A, #7EC8A8)' }">
              {{ (item.author || 'A')[0] }}
            </div>
            <div class="feed-user">
              <span class="feed-name">{{ item.author }}</span>
              <span class="feed-time">{{ item.time }}</span>
            </div>
            <span v-if="isMine(item)" class="feed-me-badge" title="我的动态">&#9786;</span>
            <button v-else class="feed-follow" :class="{ followed: isFollowed(item) }" @click="toggleFollow(item)">
              {{ isFollowed(item) ? '已关注' : '+ 关注' }}
            </button>
          </div>
          <div class="feed-body">
            <p class="feed-text">{{ item.content }}</p>
            <div class="feed-images" v-if="item.images && item.images.length">
              <template v-for="(img, i) in item.images" :key="i">
                <img v-if="isImageUrl(img)" :src="getImageUrl(img)" class="feed-img" alt="" />
                <div v-else class="feed-img" :style="{ background: img }"></div>
              </template>
            </div>
            <div class="feed-topic" v-if="item.topic">
              <span class="topic-tag">#{{ item.topic }}</span>
              <span class="topic-count">{{ item.topicCount || 0 }}人讨论</span>
            </div>
          </div>
          <div class="feed-actions">
            <span class="action-item" @click="toggleLike(item)">&#9825; {{ item.likes }}</span>
            <span class="action-item" @click="openComments(item)">&#9993; {{ item.commentCount || item.comments }}</span>
          </div>
        </article>
      </div>
    </div>

    <!-- Create Modal -->
    <div class="modal-overlay" v-if="showModal" @click.self="showModal = false">
      <div class="modal-card">
        <h2>发布动态</h2>
        <form @submit.prevent="handleCreate">
          <label class="form-field">
            <span>内容</span>
            <textarea v-model="form.content" rows="4" placeholder="分享你的想法..." required></textarea>
          </label>
          <label class="form-field">
            <span>话题标签</span>
            <input v-model="form.topic" type="text" placeholder="#话题（可选）" maxlength="30" />
          </label>
          <div class="form-field">
            <span>图片</span>
            <div class="upload-grid">
              <div class="upload-item" v-for="(preview, i) in imagePreviews" :key="'img-' + i">
                <img :src="preview" alt="" />
                <button type="button" class="upload-remove" @click="removeImage(i)">&times;</button>
              </div>
              <label class="upload-add" v-if="imagePreviews.length < 9">
                <input type="file" accept="image/*" multiple hidden @change="handleImageSelect" />
                <span>+</span>
              </label>
            </div>
            <p class="upload-hint">支持 jpg/png/gif，最多9张</p>
          </div>
          <div class="form-field" v-if="imagePreviews.length === 0">
            <span>装饰色（无图片时生效）</span>
            <div class="color-picker">
              <button type="button" v-for="(c, i) in presetColors" :key="i"
                class="color-dot" :class="{ active: form.images.includes(c) }"
                :style="{ background: c }"
                @click="toggleColor(c)"
              ></button>
              <button type="button" class="color-clear" @click="form.images = []">无</button>
            </div>
          </div>
          <div class="modal-actions">
            <button type="button" class="cancel-btn" @click="showModal = false">取消</button>
            <button type="submit" class="submit-btn" :disabled="creating">{{ creating ? '发布中...' : '发布' }}</button>
          </div>
        </form>
      </div>
    </div>

    <!-- Comment Panel -->
    <div class="comment-overlay" v-if="commentFeed" @click.self="closeComments">
      <div class="comment-panel" :class="{ 'comment-panel--open': commentFeed }">
        <div class="comment-panel-header">
          <span class="comment-panel-title">{{ commentCount }} 条评论</span>
          <button class="comment-panel-close" @click="closeComments">&times;</button>
        </div>
        <div class="comment-list" ref="commentListRef">
          <div class="comment-item" v-for="c in comments" :key="c.id">
            <div class="comment-avatar" :style="{ background: c.avatarColor || 'linear-gradient(135deg, #667eea, #764ba2)' }">
              {{ (c.author || '?')[0] }}
            </div>
            <div class="comment-body">
              <div class="comment-name">{{ c.author }}</div>
              <div class="comment-text">{{ c.content }}</div>
              <div class="comment-time">{{ c.time || c.createdAt }}</div>
            </div>
          </div>
          <p v-if="!comments.length && !loadingComments" class="comment-empty">暂无评论，来抢沙发吧</p>
        </div>
        <div class="comment-input-bar">
          <input
            v-model="commentText"
            class="comment-input"
            type="text"
            placeholder="写评论..."
            @keyup.enter="postComment"
          />
          <button class="comment-send" @click="postComment" :disabled="!commentText.trim() || sendingComment">
            {{ sendingComment ? '发送中' : '发送' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { currentUser, isLoggedIn, requireAuth } from '../stores/auth.js'
import { api } from '../utils/api.js'

const router = useRouter()

function handleCreateClick() {
  if (!requireAuth(router)) return
  showModal.value = true
}

const showModal = ref(false)
const creating = ref(false)
const form = ref({ content: '', topic: '', images: [] })

const presetColors = [
  'linear-gradient(135deg, #667eea, #764ba2)',
  'linear-gradient(135deg, #f093fb, #f5576c)',
  'linear-gradient(135deg, #4facfe, #00f2fe)',
  'linear-gradient(135deg, #43e97b, #38f9d7)',
  'linear-gradient(135deg, #fa709a, #fee140)',
  'linear-gradient(135deg, #a18cd1, #fbc2eb)'
]

function toggleColor(c) {
  const idx = form.value.images.indexOf(c)
  if (idx >= 0) form.value.images.splice(idx, 1)
  else form.value.images.push(c)
}

const imageFiles = ref([])
const imagePreviews = ref([])

function handleImageSelect(e) {
  const files = Array.from(e.target.files)
  const remaining = 9 - imageFiles.value.length
  const toAdd = files.slice(0, remaining)
  toAdd.forEach(f => imagePreviews.value.push(URL.createObjectURL(f)))
  imageFiles.value.push(...toAdd)
  e.target.value = ''
}

function removeImage(index) {
  URL.revokeObjectURL(imagePreviews.value[index])
  imagePreviews.value.splice(index, 1)
  imageFiles.value.splice(index, 1)
}

async function uploadImages(files) {
  const formData = new FormData()
  files.forEach(f => formData.append('files', f))
  const token = localStorage.getItem('token')
  const res = await fetch('http://localhost:8080/api/upload', {
    method: 'POST',
    headers: token ? { 'Authorization': `Bearer ${token}` } : {},
    body: formData
  })
  const data = await res.json()
  if (data.code !== 200) throw new Error(data.message || '上传失败')
  return data.data.map(item => item.url)
}

async function handleCreate() {
  creating.value = true
  try {
    let images = form.value.images
    if (imageFiles.value.length > 0) {
      images = await uploadImages(imageFiles.value)
    }
    await api.post('/feeds', {
      content: form.value.content,
      topic: form.value.topic || undefined,
      images: images.length ? images : undefined
    })
    form.value = { content: '', topic: '', images: [] }
    imagePreviews.value.forEach(p => URL.revokeObjectURL(p))
    imageFiles.value = []
    imagePreviews.value = []
    showModal.value = false
    loadFeeds()
  } catch (e) {
    alert(e.message || '发布失败')
  } finally {
    creating.value = false
  }
}

const searchQuery = ref('')
const feedItems = ref([])
const followingSet = ref(new Set())

function isFollowed(item) {
  return followingSet.value.has(item.authorId || item.userId)
}

async function toggleFollow(item) {
  if (!requireAuth(router)) return
  const targetId = item.authorId || item.userId
  try {
    if (isFollowed(item)) {
      await api.delete(`/follows/${targetId}`)
      followingSet.value.delete(targetId)
    } else {
      await api.post('/follows', { followedId: targetId })
      followingSet.value.add(targetId)
    }
    followingSet.value = new Set(followingSet.value)
  } catch (e) {
    alert(e.message || '操作失败')
  }
}

async function loadFeeds() {
  try {
    const data = await api.get('/feeds')
    feedItems.value = data || []
  } catch (e) {
    console.error('Failed to load feeds:', e)
  }
}

async function loadFollowing() {
  try {
    const data = await api.get('/follows/following')
    if (data) data.forEach(f => followingSet.value.add(f.id))
    followingSet.value = new Set(followingSet.value)
  } catch (e) { /* ignore if not logged in */ }
}

onMounted(() => { loadFeeds(); loadFollowing() })

async function toggleLike(item) {
  if (!requireAuth(router)) return
  try {
    if (item.liked) {
      await api.delete(`/feeds/${item.id}/like`)
      item.likes--
      item.liked = false
    } else {
      await api.post(`/feeds/${item.id}/like`)
      item.likes++
      item.liked = true
    }
  } catch (e) {
    console.error('Like failed:', e)
  }
}

// --- Comments ---
const commentFeed = ref(null)
const comments = ref([])
const commentText = ref('')
const loadingComments = ref(false)
const sendingComment = ref(false)
const commentCount = ref(0)

async function openComments(item) {
  commentFeed.value = item
  commentCount.value = item.commentCount || item.comments || 0
  comments.value = []
  commentText.value = ''
  loadingComments.value = true
  try {
    const data = await api.get(`/feeds/${item.id}/comments`)
    comments.value = (data || []).map(c => ({
      ...c,
      avatarColor: c.avatarColor || presetColors[Math.floor(Math.random() * presetColors.length)]
    }))
    commentCount.value = comments.value.length
  } catch (e) {
    console.error('Failed to load comments:', e)
  } finally {
    loadingComments.value = false
  }
}

function closeComments() {
  commentFeed.value = null
  comments.value = []
  commentText.value = ''
}

async function postComment() {
  const text = commentText.value.trim()
  if (!text || sendingComment.value || !commentFeed.value) return
  if (!requireAuth(router)) return
  sendingComment.value = true
  try {
    const data = await api.post(`/feeds/${commentFeed.value.id}/comments`, { content: text })
    const newComment = {
      ...data,
      author: currentUser.value?.username || '我',
      content: text,
      time: '刚刚',
      avatarColor: 'linear-gradient(135deg, #5B8C5A, #7EC8A8)'
    }
    comments.value.push(newComment)
    commentCount.value = comments.value.length
    if (commentFeed.value.comments !== undefined) {
      commentFeed.value.comments++
    }
    commentText.value = ''
  } catch (e) {
    alert(e.message || '评论失败')
  } finally {
    sendingComment.value = false
  }
}

function getImageUrl(img) {
  if (!img) return ''
  if (img.startsWith('http') || img.startsWith('data:')) return img
  return 'http://localhost:8080' + img
}

function isImageUrl(url) {
  return url && (url.startsWith('http') || url.startsWith('/uploads') || url.startsWith('data:'))
}

function isMine(item) {
  return currentUser.value && (
    item.userId === currentUser.value.id ||
    item.author === currentUser.value.username
  )
}

const filteredItems = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return feedItems.value
  return feedItems.value.filter(item =>
    (item.content || '').toLowerCase().includes(q) ||
    (item.author || '').toLowerCase().includes(q) ||
    (item.topic || '').toLowerCase().includes(q)
  )
})
</script>

<style scoped>
.page-bg { position: relative; min-height: calc(100vh - 60px); background-size: cover; background-position: center 25%; background-repeat: no-repeat; background-attachment: fixed; }
.bg-overlay { position: fixed; inset: 0; background: rgba(255, 255, 255, 0.55); pointer-events: none; z-index: 0; }
.recommend-page { position: relative; z-index: 1; padding-top: 32px; padding-bottom: 60px; }
.recommend-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px; }
.page-title { font-size: 1.6rem; margin: 0 0 2px; }
.page-subtitle { color: var(--color-text-muted); font-size: 0.9rem; margin: 0; }
.header-right { display: flex; align-items: center; gap: 10px; }
.recommend-search { padding: 8px 16px; border: 1px solid var(--color-border); border-radius: 20px; font-size: 0.88rem; font-family: inherit; background: rgba(255,255,255,0.7); outline: none; width: 200px; transition: border-color 0.2s; }
.recommend-search:focus { border-color: var(--color-primary-light); background: #fff; }
.create-btn { padding: 8px 20px; background: var(--color-primary); color: #fff; border: none; border-radius: 20px; font-size: 0.88rem; font-weight: 500; cursor: pointer; font-family: inherit; transition: background 0.2s; white-space: nowrap; }
.create-btn:hover { background: var(--color-primary-dark); }

.feed-list { display: flex; flex-direction: column; gap: 0; }
.feed-card { background: var(--color-surface); padding: 18px 20px; border-bottom: 1px solid var(--color-border); }
.feed-card:hover { background: #f7f8fa; }
.feed-card:first-child { border-radius: var(--radius-md) var(--radius-md) 0 0; }
.feed-card:last-child { border-radius: 0 0 var(--radius-md) var(--radius-md); border-bottom: none; }

.feed-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.feed-avatar { width: 40px; height: 40px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #fff; font-weight: 700; font-size: 0.95rem; flex-shrink: 0; }
.feed-user { flex: 1; display: flex; flex-direction: column; gap: 1px; }
.feed-name { font-weight: 600; font-size: 0.95rem; }
.feed-time { font-size: 0.78rem; color: var(--color-text-muted); }
.feed-follow { padding: 4px 14px; border: 1px solid var(--color-primary); background: none; color: var(--color-primary); border-radius: 16px; font-size: 0.8rem; font-weight: 500; cursor: pointer; transition: background 0.2s, color 0.2s; flex-shrink: 0; font-family: inherit; }
.feed-follow:hover { background: var(--color-primary); color: #fff; }
.feed-follow.followed { background: var(--color-primary); color: #fff; }
.feed-follow.followed:hover { background: var(--color-primary-dark); border-color: var(--color-primary-dark); }
.feed-me-badge { width: 28px; height: 28px; border-radius: 50%; background: var(--color-primary); color: #fff; display: flex; align-items: center; justify-content: center; font-size: 1rem; flex-shrink: 0; }
.feed-body { margin-bottom: 12px; }
.feed-text { font-size: 0.95rem; line-height: 1.75; margin: 0 0 10px; color: var(--color-text); }
.feed-images { display: flex; gap: 6px; margin-bottom: 10px; flex-wrap: wrap; }
.feed-img { width: 80px; height: 60px; border-radius: var(--radius-sm); flex-shrink: 0; object-fit: cover; display: block; background-size: cover; background-position: center; }
.feed-topic { display: inline-flex; align-items: center; gap: 8px; background: var(--color-tag-bg); padding: 6px 12px; border-radius: 6px; }
.topic-tag { font-size: 0.85rem; color: var(--color-primary); font-weight: 500; }
.topic-count { font-size: 0.75rem; color: var(--color-text-muted); }
.feed-actions { display: flex; gap: 28px; }
.action-item { font-size: 0.85rem; color: var(--color-text-muted); cursor: pointer; transition: color 0.2s; }
.action-item:hover { color: var(--color-primary); }

@media (max-width: 480px) {
  .feed-card { padding: 14px 16px; }
  .feed-images { gap: 4px; }
  .feed-actions { gap: 20px; }
}

.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.4); z-index: 200; display: flex; align-items: center; justify-content: center; padding: 24px; }
.modal-card { background: #fff; border-radius: var(--radius-lg); padding: 32px; width: 100%; max-width: 560px; max-height: 90vh; overflow-y: auto; box-shadow: 0 12px 48px rgba(0,0,0,0.2); }
.modal-card h2 { font-size: 1.3rem; margin: 0 0 24px; }
.form-field { display: flex; flex-direction: column; gap: 6px; margin-bottom: 18px; }
.form-field span { font-size: 0.88rem; font-weight: 500; }
.form-field textarea, .form-field input[type="text"] { padding: 10px 14px; border: 1px solid var(--color-border); border-radius: var(--radius-sm); font-size: 0.92rem; font-family: inherit; outline: none; resize: vertical; background: #fafbfc; transition: border-color 0.2s; }
.form-field textarea:focus, .form-field input[type="text"]:focus { border-color: var(--color-primary-light); background: #fff; }
.upload-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.upload-item { position: relative; aspect-ratio: 1; border-radius: 6px; overflow: hidden; background: #f0f0f0; }
.upload-item img { width: 100%; height: 100%; object-fit: cover; display: block; }
.upload-remove { position: absolute; top: 2px; right: 2px; width: 22px; height: 22px; border-radius: 50%; border: none; background: rgba(0,0,0,0.55); color: #fff; font-size: 0.9rem; line-height: 1; cursor: pointer; display: flex; align-items: center; justify-content: center; padding: 0; }
.upload-remove:hover { background: rgba(0,0,0,0.75); }
.upload-add { aspect-ratio: 1; border: 1.5px dashed #ccc; border-radius: 6px; display: flex; align-items: center; justify-content: center; cursor: pointer; transition: border-color 0.2s, background 0.2s; }
.upload-add:hover { border-color: var(--color-primary); background: rgba(91,140,90,0.04); }
.upload-add span { font-size: 2rem; color: #ccc; transition: color 0.2s; }
.upload-add:hover span { color: var(--color-primary); }
.upload-hint { font-size: 0.75rem; color: var(--color-text-muted); margin: 6px 0 0; }
.color-picker { display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.color-dot { width: 32px; height: 32px; border-radius: 50%; border: 2px solid transparent; cursor: pointer; transition: border-color 0.15s, transform 0.15s; }
.color-dot:hover { transform: scale(1.1); }
.color-dot.active { border-color: var(--color-primary); transform: scale(1.15); }
.color-clear { font-size: 0.78rem; padding: 4px 10px; border: 1px solid var(--color-border); background: none; border-radius: 12px; cursor: pointer; font-family: inherit; color: var(--color-text-muted); }
.color-clear:hover { background: var(--color-bg); }
.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 8px; }
.cancel-btn { padding: 9px 24px; border: 1px solid var(--color-border); background: none; border-radius: 20px; cursor: pointer; font-family: inherit; font-size: 0.9rem; color: var(--color-text-secondary); }
.cancel-btn:hover { background: var(--color-bg); }
.submit-btn { padding: 9px 28px; background: var(--color-primary); color: #fff; border: none; border-radius: 20px; cursor: pointer; font-family: inherit; font-size: 0.9rem; font-weight: 500; }
.submit-btn:hover { background: var(--color-primary-dark); }
.submit-btn:disabled { opacity: 0.6; cursor: not-allowed; }

/* Comment Panel */
.comment-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 300; display: flex; align-items: flex-end; justify-content: center; }
.comment-panel { background: #fff; width: 100%; max-width: 600px; max-height: 70vh; border-radius: 16px 16px 0 0; display: flex; flex-direction: column; animation: slideUp 0.25s ease-out; }
@keyframes slideUp { from { transform: translateY(100%); } to { transform: translateY(0); } }
.comment-panel-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid var(--color-border); flex-shrink: 0; }
.comment-panel-title { font-size: 0.95rem; font-weight: 600; }
.comment-panel-close { width: 32px; height: 32px; border: none; background: none; font-size: 1.4rem; cursor: pointer; color: var(--color-text-secondary); border-radius: 50%; display: flex; align-items: center; justify-content: center; line-height: 1; }
.comment-panel-close:hover { background: var(--color-bg); }
.comment-list { flex: 1; overflow-y: auto; padding: 8px 20px; }
.comment-item { display: flex; gap: 10px; padding: 12px 0; }
.comment-item + .comment-item { border-top: 1px solid #f0f0f0; }
.comment-avatar { width: 36px; height: 36px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #fff; font-weight: 700; font-size: 0.85rem; flex-shrink: 0; }
.comment-body { flex: 1; min-width: 0; }
.comment-name { font-size: 0.85rem; font-weight: 600; margin-bottom: 2px; }
.comment-text { font-size: 0.9rem; line-height: 1.5; color: var(--color-text); word-break: break-word; }
.comment-time { font-size: 0.75rem; color: var(--color-text-muted); margin-top: 4px; }
.comment-empty { text-align: center; padding: 40px 0; color: var(--color-text-muted); font-size: 0.9rem; }
.comment-input-bar { display: flex; gap: 10px; padding: 12px 20px; border-top: 1px solid var(--color-border); background: #fafbfc; flex-shrink: 0; }
.comment-input { flex: 1; padding: 10px 14px; border: 1px solid var(--color-border); border-radius: 20px; font-size: 0.88rem; font-family: inherit; outline: none; background: #fff; }
.comment-input:focus { border-color: var(--color-primary-light); }
.comment-send { padding: 8px 18px; background: var(--color-primary); color: #fff; border: none; border-radius: 20px; font-size: 0.85rem; font-weight: 500; cursor: pointer; font-family: inherit; white-space: nowrap; transition: background 0.2s; }
.comment-send:hover { background: var(--color-primary-dark); }
.comment-send:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
