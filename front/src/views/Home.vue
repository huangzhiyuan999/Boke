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
          <router-link
            v-for="item in latestAnnouncements"
            :key="item.id"
            :to="`/post/${item.id}`"
            class="announce-card"
          >
            <div class="announce-badge">公告</div>
            <div class="announce-body">
              <h3 class="announce-title">{{ item.title }}</h3>
              <p class="announce-summary">{{ item.summary }}</p>
            </div>
          </router-link>
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../utils/api.js'

const route = useRoute()
const heroBg = '/hero-bg.jpg'
const animated = ref(false)
const announcements = ref([])
const posts = ref([])

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
  .post-item-cover { width: 60px; }
}
</style>
