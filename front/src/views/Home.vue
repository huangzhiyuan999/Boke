<template>
  <div class="home" :style="{ backgroundImage: `url(${heroBg})` }">
    <div class="home-overlay"></div>
    <div class="home-content container">
      <!-- Latest Announcements -->
      <section v-if="latestAnnouncements.length" class="section" :class="{ 'slide-in-left': animated }">
        <h2 class="section-title">
          <span class="section-icon">&#128276;</span>
          最新公告
        </h2>
        <div class="announce-list">
          <div
            v-for="item in latestAnnouncements"
            :key="item.id"
            class="announce-card"
            @click="openAnnouncement(item)"
          >
            <div class="announce-badge">公告</div>
            <div class="announce-body">
              <h3 class="announce-title">{{ item.title }}</h3>
              <p class="announce-summary">{{ item.summary }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Hot Articles -->
      <section class="section" :class="{ 'slide-in-right': animated }">
        <h2 class="section-title">
          <span class="section-icon">&#128293;</span>
          热门推荐
        </h2>
        <div class="post-list" v-if="hotPosts.length">
          <router-link
            v-for="post in hotPosts"
            :key="post.id"
            :to="`/post/${post.id}`"
            class="post-item"
          >
            <div class="post-item-cover" :style="{ background: post.cover }"></div>
            <div class="post-item-body">
              <h3 class="post-item-title">{{ post.title }}</h3>
              <div class="post-item-meta">
                <span class="post-item-date">{{ post.createdAt || post.date }}</span>
                <span v-for="tag in post.tags" :key="tag" class="mini-tag">{{ tag }}</span>
              </div>
            </div>
          </router-link>
        </div>
        <div v-else class="empty">
          <p>暂无文章</p>
          <router-link to="/" class="back-link">返回首页</router-link>
        </div>
      </section>
    </div>

    <!-- Announcement Modal -->
    <teleport to="body">
      <transition name="modal">
        <div v-if="selectedAnnouncement" class="modal-overlay" @click.self="closeAnnouncement">
          <div class="modal-dialog">
            <button class="modal-close" @click="closeAnnouncement">&times;</button>
            <div class="modal-cover" :style="{ background: selectedAnnouncement.cover }"></div>
            <div class="modal-body">
              <time class="modal-date">{{ selectedAnnouncement.createdAt || selectedAnnouncement.date }}</time>
              <h2 class="modal-title">{{ selectedAnnouncement.title }}</h2>
              <div class="modal-content" v-html="renderedAnnouncement"></div>
            </div>
          </div>
        </div>
      </transition>
    </teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../utils/api.js'
import { renderMarkdown } from '../utils/markdown.js'

const route = useRoute()
const heroBg = '/hero-bg.jpg'
const animated = ref(false)
const announcements = ref([])
const posts = ref([])
const selectedAnnouncement = ref(null)

function openAnnouncement(item) {
  selectedAnnouncement.value = item
  document.body.style.overflow = 'hidden'
  document.addEventListener('keydown', handleEsc)
}

function closeAnnouncement() {
  selectedAnnouncement.value = null
  document.body.style.overflow = ''
  document.removeEventListener('keydown', handleEsc)
}

function handleEsc(e) {
  if (e.key === 'Escape') closeAnnouncement()
}

const renderedAnnouncement = computed(() =>
  renderMarkdown(selectedAnnouncement.value?.content)
)

function triggerAnimation() {
  animated.value = false
  setTimeout(() => { animated.value = true }, 100)
}

onMounted(async () => {
  triggerAnimation()
  try {
    const [a, p] = await Promise.all([
      api.get('/posts?type=announcement'),
      api.get('/posts?sort=hot')
    ])
    announcements.value = (a || []).slice(0, 2)
    posts.value = p || []
  } catch (e) {
    console.error('Failed to load posts:', e)
  }
})

watch(() => route.path, (to) => {
  if (to === '/' || to.startsWith('/tag/')) triggerAnimation()
})

const hotPosts = ref([])
const latestAnnouncements = ref([])
watch([posts, announcements, () => route.params.tag], () => {
  const tag = route.params.tag
  if (tag) {
    hotPosts.value = posts.value.filter(p => p.tags && p.tags.includes(tag))
  } else {
    hotPosts.value = posts.value.slice(0, 2)
  }
  latestAnnouncements.value = announcements.value
}, { immediate: true })
</script>

<style scoped>
.home {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background-size: cover;
  background-position: center 25%;
  background-repeat: no-repeat;
  background-attachment: fixed;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding-top: 60px;
}

.home-overlay {
  position: fixed;
  inset: 0;
  background: rgba(255, 255, 255, 0.82);
  pointer-events: none;
  z-index: 0;
}

.home-content {
  position: relative;
  z-index: 1;
  padding: 0 24px 32px;
  width: 100%;
  max-width: var(--max-width);
  margin: 0 auto;
}

.section { margin-bottom: 10px; }

.section-title {
  font-size: 1rem;
  font-weight: 600;
  margin: 0 0 6px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.section-icon { font-size: 1.1rem; }

.announce-list { display: flex; flex-direction: column; gap: 6px; }

.announce-card {
  display: flex;
  background: var(--color-surface);
  border-radius: var(--radius-sm);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: box-shadow 0.2s, transform 0.2s;
}

.announce-card:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }

.announce-badge {
  background: linear-gradient(135deg, #f56c6c, #e63946);
  color: #fff;
  font-size: 0.72rem;
  font-weight: 600;
  padding: 8px 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  writing-mode: vertical-lr;
  letter-spacing: 2px;
  flex-shrink: 0;
}

.announce-body { padding: 10px 16px; flex: 1; min-width: 0; }

.announce-title {
  font-size: 0.95rem;
  font-weight: 600;
  margin: 0 0 4px;
  color: var(--color-text);
}

.announce-summary {
  font-size: 0.82rem;
  color: var(--color-text-secondary);
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-list { display: flex; flex-direction: column; gap: 6px; }

.post-item {
  display: flex;
  background: var(--color-surface);
  border-radius: var(--radius-sm);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: box-shadow 0.2s, transform 0.2s;
  color: inherit;
}

.post-item:hover { box-shadow: var(--shadow-md); transform: translateY(-1px); }

.post-item-cover { width: 80px; flex-shrink: 0; }

.post-item-body {
  padding: 10px 16px;
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.post-item-title {
  font-size: 0.95rem;
  font-weight: 600;
  margin: 0 0 4px;
  color: var(--color-text);
}

.post-item-meta { display: flex; align-items: center; gap: 8px; }

.post-item-date {
  font-size: 0.78rem;
  color: var(--color-text-muted);
  flex-shrink: 0;
}

.mini-tag {
  font-size: 0.72rem;
  padding: 1px 7px;
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  border-radius: 10px;
}

.empty { text-align: center; padding: 40px 0; color: var(--color-text-secondary); }
.back-link { display: inline-block; margin-top: 8px; font-weight: 500; }

.slide-in-left { animation: slideLeft 1s ease-out forwards; }
.slide-in-right { animation: slideRight 1s ease-out forwards; }

@keyframes slideLeft {
  from { opacity: 0; transform: translateX(-100vw); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes slideRight {
  from { opacity: 0; transform: translateX(100vw); }
  to { opacity: 1; transform: translateX(0); }
}

@media (max-width: 480px) {
  .home { padding-top: 50px; }
  .home-content { padding: 0 16px 32px; }
  .slide-in-left,
  .slide-in-right {
    animation: none;
    opacity: 1;
    transform: none;
  }
  .section-title { font-size: 0.95rem; }
  .announce-body,
  .post-item-body { padding: 10px 12px; }
  .post-item-cover { width: 60px; }
  .post-item-meta { flex-wrap: wrap; gap: 4px 8px; }
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.modal-dialog {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  max-width: 640px;
  width: 100%;
  max-height: 85vh;
  overflow-y: auto;
  position: relative;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.2);
}

.modal-close {
  position: absolute;
  top: 12px;
  right: 16px;
  width: 36px;
  height: 36px;
  border: none;
  background: rgba(0, 0, 0, 0.05);
  border-radius: 50%;
  font-size: 1.4rem;
  color: var(--color-text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
  z-index: 10;
}

.modal-close:hover {
  background: rgba(0, 0, 0, 0.12);
  color: var(--color-text);
}

.modal-cover {
  height: 80px;
  border-radius: var(--radius-lg) var(--radius-lg) 0 0;
}

.modal-body {
  padding: 24px 28px 32px;
}

.modal-date {
  display: block;
  font-size: 0.85rem;
  color: var(--color-text-muted);
  margin-bottom: 8px;
}

.modal-title {
  font-size: 1.4rem;
  margin: 0 0 20px;
  line-height: 1.4;
}

.modal-content {
  font-size: 1rem;
  line-height: 1.9;
  color: var(--color-text);
  word-break: break-word;
}

.modal-content :deep(h1) { font-size: 1.5rem; margin: 1.8em 0 0.5em; padding-bottom: 6px; border-bottom: 1px solid var(--color-border); }
.modal-content :deep(h2) { font-size: 1.35rem; margin: 1.6em 0 0.5em; padding-bottom: 6px; border-bottom: 1px solid var(--color-border); }
.modal-content :deep(h3) { font-size: 1.15rem; margin: 1.4em 0 0.4em; }
.modal-content :deep(h4) { font-size: 1.05rem; margin: 1.2em 0 0.3em; }
.modal-content :deep(p) { margin: 0.7em 0; }
.modal-content :deep(pre) { background: #f5f5f5; padding: 14px 18px; border-radius: 8px; overflow-x: auto; font-size: 0.88rem; margin: 1em 0; line-height: 1.6; }
.modal-content :deep(code) { font-size: 0.9em; background: #f0f0f0; padding: 2px 6px; border-radius: 4px; }
.modal-content :deep(pre code) { background: none; padding: 0; border-radius: 0; }
.modal-content :deep(strong) { font-weight: 600; }
.modal-content :deep(em) { font-style: italic; }
.modal-content :deep(del) { text-decoration: line-through; opacity: 0.7; }
.modal-content :deep(img) { max-width: 100%; height: auto; border-radius: var(--radius-sm); margin: 1em 0; display: block; }
.modal-content :deep(a) { color: var(--color-primary); text-decoration: underline; }
.modal-content :deep(a:hover) { color: var(--color-primary-dark); }
.modal-content :deep(blockquote) { margin: 1em 0; padding: 10px 20px; border-left: 4px solid var(--color-primary-light); background: var(--color-tag-bg); border-radius: 0 var(--radius-sm) var(--radius-sm) 0; color: var(--color-text-secondary); }
.modal-content :deep(ul), .modal-content :deep(ol) { margin: 0.8em 0; padding-left: 1.8em; }
.modal-content :deep(li) { margin: 0.3em 0; }
.modal-content :deep(hr) { border: none; border-top: 1px solid var(--color-border); margin: 2em 0; }

.announce-card {
  cursor: pointer;
}

/* Modal transition */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.25s ease;
}

.modal-enter-active .modal-dialog,
.modal-leave-active .modal-dialog {
  transition: transform 0.25s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-dialog {
  transform: scale(0.92);
}

.modal-leave-to .modal-dialog {
  transform: scale(0.92);
}
</style>
