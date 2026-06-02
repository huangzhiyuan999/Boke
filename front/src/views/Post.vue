<template>
  <div class="post-page" v-if="post">
    <!-- Left Sidebar: Article List -->
    <aside class="post-sidebar">
      <div class="sidebar-header">
        <router-link to="/articles" class="sidebar-back">&larr; 文章</router-link>
      </div>
      <nav class="sidebar-list">
        <router-link
          v-for="p in posts"
          :key="p.id"
          :to="`/post/${p.id}`"
          class="sidebar-item"
          :class="{ active: p.id === post.id }"
        >
          <span class="sidebar-dot" :style="{ background: p.cover }"></span>
          <div class="sidebar-text">
            <span class="sidebar-title">{{ p.title }}</span>
            <span class="sidebar-date">{{ p.createdAt || p.date }}</span>
          </div>
        </router-link>
      </nav>
    </aside>

    <!-- Right Content -->
    <main class="post-main">
      <article class="post-detail">
        <header class="post-header">
          <div class="post-cover" :style="{ background: post.cover }"></div>
          <time class="post-date">{{ post.createdAt || post.date }}</time>
          <h1 class="post-title">{{ post.title }}</h1>
          <div class="post-tags">
            <router-link v-for="tag in (post.tags || [])" :key="tag" :to="`/tag/${tag}`" class="tag">{{ tag }}</router-link>
          </div>
        </header>
        <div class="post-content" v-html="renderedContent"></div>

        <nav class="post-nav">
          <router-link v-if="prevPost" :to="`/post/${prevPost.id}`" class="post-nav-link prev">
            <span class="nav-hint">上一篇</span>
            <span class="nav-title">{{ prevPost.title }}</span>
          </router-link>
          <router-link v-if="nextPost" :to="`/post/${nextPost.id}`" class="post-nav-link next">
            <span class="nav-hint">下一篇</span>
            <span class="nav-title">{{ nextPost.title }}</span>
          </router-link>
        </nav>
      </article>
    </main>
  </div>

  <div v-else class="not-found">
    <h2>{{ loading ? '加载中...' : '文章未找到' }}</h2>
    <router-link to="/articles" class="back-link">返回文章列表</router-link>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../utils/api.js'
import { renderMarkdown } from '../utils/markdown.js'

const route = useRoute()
const post = ref(null)
const posts = ref([])
const loading = ref(true)

async function load() {
  loading.value = true
  const id = Number(route.params.id)
  try {
    post.value = await api.get(`/posts/${id}`)
  } catch (e) {
    console.error('Failed to load post:', e)
    post.value = null
  } finally {
    loading.value = false
  }

  try {
    const listData = await api.get('/posts?type=post')
    posts.value = listData || []
  } catch (e) {
    console.error('Failed to load post list:', e)
  }
}

onMounted(load)
watch(() => route.params.id, load)

const renderedContent = computed(() => renderMarkdown(post.value?.content))

const prevPost = computed(() => {
  if (!post.value) return null
  const idx = posts.value.findIndex(p => p.id === post.value.id)
  return idx > 0 ? posts.value[idx - 1] : null
})

const nextPost = computed(() => {
  if (!post.value) return null
  const idx = posts.value.findIndex(p => p.id === post.value.id)
  return idx < posts.value.length - 1 ? posts.value[idx + 1] : null
})
</script>

<style scoped>
.post-page { display: flex; min-height: calc(100vh - 60px); }

.post-sidebar {
  width: 240px; flex-shrink: 0; background: #f0f4f2;
  border-right: 2px solid var(--color-primary-light);
  position: sticky; top: 60px; height: calc(100vh - 60px); overflow-y: auto;
}

.sidebar-header { padding: 18px 16px 12px; }

.sidebar-back { font-size: 0.9rem; color: var(--color-text-secondary); font-weight: 500; }
.sidebar-back:hover { color: var(--color-primary); }

.sidebar-list { display: flex; flex-direction: column; }

.sidebar-item {
  display: flex; align-items: flex-start; gap: 10px; padding: 12px 16px;
  color: var(--color-text); transition: background 0.15s; border-left: 3px solid transparent;
}

.sidebar-item:hover { background: var(--color-bg); }

.sidebar-item.active { background: var(--color-tag-bg); border-left-color: var(--color-primary); }

.sidebar-dot { width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; margin-top: 5px; }

.sidebar-text { display: flex; flex-direction: column; gap: 3px; min-width: 0; }

.sidebar-title {
  font-size: 0.85rem; font-weight: 500; line-height: 1.4; overflow: hidden;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
}

.sidebar-date { font-size: 0.75rem; color: var(--color-text-muted); }

.post-main { flex: 1; min-width: 0; }

.post-detail { max-width: 740px; margin: 0 auto; padding: 28px 40px 60px; }

.post-cover { height: 200px; border-radius: var(--radius-lg); margin-bottom: 28px; }

.post-header { margin-bottom: 32px; }

.post-date { display: block; font-size: 0.85rem; color: var(--color-text-muted); margin-bottom: 8px; }

.post-title { font-size: 2rem; margin: 0 0 14px; line-height: 1.3; }

.post-tags { display: flex; gap: 8px; }

.tag { background: var(--color-tag-bg); color: var(--color-tag-text); padding: 3px 12px; border-radius: 20px; font-size: 0.8rem; font-weight: 500; }

.post-content { font-size: 1rem; line-height: 1.9; word-break: break-word; }

.post-content :deep(h1) { font-size: 1.6rem; margin: 2em 0 0.5em; padding-bottom: 8px; border-bottom: 2px solid var(--color-border); }
.post-content :deep(h2) { font-size: 1.4rem; margin: 2em 0 0.6em; padding-bottom: 8px; border-bottom: 2px solid var(--color-border); }
.post-content :deep(h3) { font-size: 1.15rem; margin: 1.6em 0 0.4em; }
.post-content :deep(h4) { font-size: 1.05rem; margin: 1.4em 0 0.3em; }
.post-content :deep(p) { margin: 0.8em 0; }
.post-content :deep(pre) { background: #f5f5f5; padding: 14px 18px; border-radius: 8px; overflow-x: auto; font-size: 0.88rem; margin: 1.2em 0; line-height: 1.6; }
.post-content :deep(code) { font-size: 0.9em; background: #f0f0f0; padding: 2px 6px; border-radius: 4px; }
.post-content :deep(pre code) { background: none; padding: 0; border-radius: 0; }
.post-content :deep(strong) { font-weight: 600; }
.post-content :deep(em) { font-style: italic; }
.post-content :deep(del) { text-decoration: line-through; opacity: 0.7; }
.post-content :deep(img) { max-width: 100%; height: auto; border-radius: var(--radius-sm); margin: 1em 0; display: block; }
.post-content :deep(a) { color: var(--color-primary); text-decoration: underline; }
.post-content :deep(a:hover) { color: var(--color-primary-dark); }
.post-content :deep(blockquote) { margin: 1em 0; padding: 10px 20px; border-left: 4px solid var(--color-primary-light); background: var(--color-tag-bg); border-radius: 0 var(--radius-sm) var(--radius-sm) 0; color: var(--color-text-secondary); }
.post-content :deep(ul), .post-content :deep(ol) { margin: 0.8em 0; padding-left: 1.8em; }
.post-content :deep(li) { margin: 0.3em 0; }
.post-content :deep(hr) { border: none; border-top: 1px solid var(--color-border); margin: 2em 0; }

.post-nav { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 60px; padding-top: 32px; border-top: 1px solid var(--color-border); }

.post-nav-link {
  display: block; padding: 16px 20px; background: var(--color-surface);
  border-radius: var(--radius-md); box-shadow: var(--shadow-sm);
  transition: box-shadow 0.2s, transform 0.2s;
}

.post-nav-link:hover { box-shadow: var(--shadow-md); transform: translateY(-2px); }
.post-nav-link.next { text-align: right; margin-left: auto; }

.nav-hint { display: block; font-size: 0.78rem; color: var(--color-text-muted); margin-bottom: 4px; }
.nav-title { color: var(--color-text); font-weight: 500; font-size: 0.9rem; }

.not-found { text-align: center; padding: 120px 0; }
.not-found h2 { color: var(--color-text-secondary); margin-bottom: 16px; }
.back-link { color: var(--color-primary); font-weight: 500; }

@media (max-width: 900px) {
  .post-sidebar { width: 200px; }
  .post-detail { padding: 24px 24px 40px; }
  .post-title { font-size: 1.6rem; }
  .post-cover { height: 160px; }
}

@media (max-width: 640px) {
  .post-page { flex-direction: column; }
  .post-sidebar {
    width: 100%;
    height: auto;
    max-height: 260px;
    position: relative;
    top: 0;
    border-right: none;
    border-bottom: 2px solid var(--color-primary-light);
  }
  .sidebar-header { padding: 12px 16px 8px; }
  .sidebar-list { max-height: 210px; overflow-y: auto; }
  .sidebar-item { padding: 10px 16px; }
  .post-detail { padding: 20px 16px 40px; }
  .post-title { font-size: 1.4rem; }
  .post-cover { height: 130px; }
  .post-nav { grid-template-columns: 1fr; }
  .post-nav-link.next { margin-left: 0; text-align: left; }
}
</style>
