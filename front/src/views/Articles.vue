<template>
  <div class="articles-layout">
    <!-- Left Sidebar: Article List -->
    <aside class="articles-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <div class="sidebar-top">
        <div class="sidebar-header" v-show="!sidebarCollapsed">
          <div class="sidebar-title-row">
            <h2 class="sidebar-heading">文章列表</h2>
            <input
              v-model="searchQuery"
              class="sidebar-search"
              type="text"
              placeholder="搜索..."
              @click.stop
            />
          </div>
          <p class="sidebar-count">共 {{ filteredPosts.length }} 篇文章</p>
        </div>
        <button class="sidebar-toggle" @click="sidebarCollapsed = !sidebarCollapsed" :title="sidebarCollapsed ? '展开' : '收起'">
          <span v-if="sidebarCollapsed">&#9654;</span>
          <span v-else>&#9664;</span>
        </button>
      </div>
      <nav class="sidebar-list" v-show="!sidebarCollapsed">
        <button
          v-for="p in filteredPosts"
          :key="p.id"
          class="sidebar-item"
          :class="{ active: activeId === p.id }"
          @click="selectPost(p)"
        >
          <span class="sidebar-dot" :style="{ background: p.cover }"></span>
          <div class="sidebar-text">
            <span class="sidebar-title">{{ p.title }}</span>
            <span class="sidebar-date">{{ p.createdAt || p.date }}</span>
          </div>
        </button>
      </nav>
      <nav class="sidebar-list-collapsed" v-show="sidebarCollapsed">
        <button
          v-for="p in filteredPosts"
          :key="p.id"
          class="sidebar-dot-btn"
          :class="{ active: activeId === p.id }"
          :style="{ background: p.cover }"
          :title="p.title"
          @click="selectPost(p)"
        ></button>
      </nav>
    </aside>

    <!-- Right Content -->
    <main class="articles-main">
      <div v-if="selectedPost" class="post-detail">
        <header class="post-header">
          <div class="post-cover" :style="{ background: selectedPost.cover }"></div>
          <time class="post-date">{{ selectedPost.createdAt || selectedPost.date }}</time>
          <h1 class="post-title">{{ selectedPost.title }}</h1>
          <div class="post-tags">
            <span v-for="tag in (selectedPost.tags || [])" :key="tag" class="tag">{{ tag }}</span>
          </div>
        </header>
        <div class="post-content" v-html="renderedContent"></div>
      </div>
      <div v-else class="post-empty">
        <div class="empty-icon">&#128221;</div>
        <p>选择左侧文章开始阅读</p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { api } from '../utils/api.js'
import { renderMarkdown } from '../utils/markdown.js'

const activeId = ref(null)
const selectedPost = ref(null)
const sidebarCollapsed = ref(false)
const searchQuery = ref('')
const posts = ref([])

onMounted(async () => {
  try {
    const data = await api.get('/posts?type=post')
    posts.value = data || []
  } catch (e) {
    console.error('Failed to load articles:', e)
  }
})

const filteredPosts = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return posts.value
  return posts.value.filter(p =>
    p.title.toLowerCase().includes(q) ||
    (p.tags && p.tags.some(t => t.toLowerCase().includes(q)))
  )
})

async function selectPost(p) {
  activeId.value = p.id
  try {
    selectedPost.value = await api.get(`/posts/${p.id}`)
  } catch (e) {
    selectedPost.value = p
  }
}

const renderedContent = computed(() => renderMarkdown(selectedPost.value?.content))
</script>

<style scoped>
.articles-layout {
  display: flex;
  min-height: calc(100vh - 60px);
}

.articles-sidebar {
  width: 240px;
  flex-shrink: 0;
  background: #f0f4f2;
  border-right: 2px solid var(--color-primary-light);
  position: sticky;
  top: 60px;
  height: calc(100vh - 60px);
  overflow-y: auto;
  transition: width 0.25s ease;
}

.articles-sidebar.collapsed { width: 48px; }

.sidebar-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 12px 8px;
  position: sticky;
  top: 0;
  background: #f0f4f2;
  z-index: 2;
}

.sidebar-header { flex: 1; min-width: 0; }

.sidebar-heading { font-size: 1rem; font-weight: 600; margin: 0; white-space: nowrap; }

.sidebar-title-row { display: flex; align-items: center; gap: 8px; }

.sidebar-search {
  width: 90px;
  padding: 4px 8px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 0.75rem;
  font-family: inherit;
  background: #fff;
  outline: none;
  transition: border-color 0.2s, width 0.2s;
}

.sidebar-search:focus { width: 120px; border-color: var(--color-primary-light); }

.sidebar-count { font-size: 0.78rem; color: var(--color-text-muted); margin: 2px 0 0; }

.sidebar-toggle {
  width: 28px;
  height: 28px;
  border: 1px solid var(--color-border);
  background: var(--color-surface);
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: var(--color-text-secondary);
  transition: background 0.2s;
  flex-shrink: 0;
  margin-top: 2px;
}

.sidebar-toggle:hover { background: var(--color-primary); color: #fff; border-color: var(--color-primary); }

.sidebar-list { display: flex; flex-direction: column; }

.sidebar-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 12px 16px;
  border: none;
  background: none;
  cursor: pointer;
  text-align: left;
  width: 100%;
  font-family: inherit;
  color: var(--color-text);
  transition: background 0.15s;
  border-left: 3px solid transparent;
}

.sidebar-item:hover { background: rgba(91, 140, 90, 0.08); }

.sidebar-item.active {
  background: rgba(91, 140, 90, 0.15);
  border-left-color: var(--color-primary);
}

.sidebar-dot {
  width: 8px; height: 8px; border-radius: 50%; flex-shrink: 0; margin-top: 5px;
}

.sidebar-text { display: flex; flex-direction: column; gap: 3px; min-width: 0; }

.sidebar-title {
  font-size: 0.85rem; font-weight: 500; line-height: 1.4;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}

.sidebar-date { font-size: 0.75rem; color: var(--color-text-muted); }

.sidebar-list-collapsed { display: flex; flex-direction: column; align-items: center; gap: 10px; padding: 8px 0; }

.sidebar-dot-btn {
  width: 14px; height: 14px; border-radius: 50%;
  border: 2px solid transparent; cursor: pointer;
  transition: border-color 0.2s, transform 0.2s;
}

.sidebar-dot-btn:hover { transform: scale(1.3); }
.sidebar-dot-btn.active { border-color: var(--color-primary); transform: scale(1.2); }

.articles-main { flex: 1; min-width: 0; background: var(--color-bg); }

.post-detail { max-width: 740px; margin: 0 auto; padding: 28px 40px 60px; }

.post-cover { height: 200px; border-radius: var(--radius-lg); margin-bottom: 28px; }

.post-header { margin-bottom: 32px; }

.post-date { display: block; font-size: 0.85rem; color: var(--color-text-muted); margin-bottom: 8px; }

.post-title { font-size: 2rem; margin: 0 0 14px; line-height: 1.3; }

.post-tags { display: flex; gap: 8px; }

.tag {
  display: inline-block;
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.post-content { font-size: 1rem; line-height: 1.9; }

.post-content :deep(h2) { font-size: 1.4rem; margin: 2em 0 0.6em; padding-bottom: 8px; border-bottom: 2px solid var(--color-border); }
.post-content :deep(h3) { font-size: 1.15rem; margin: 1.6em 0 0.4em; }
.post-content :deep(h1) { font-size: 1.6rem; margin: 2em 0 0.5em; padding-bottom: 8px; border-bottom: 2px solid var(--color-border); }
.post-content :deep(h4) { font-size: 1.05rem; margin: 1.4em 0 0.3em; }
.post-content :deep(p) { margin: 0.8em 0; }
.post-content :deep(pre) { margin: 1.2em 0; background: #1E293B; color: #E2E8F0; padding: 20px 24px; border-radius: var(--radius-md); overflow-x: auto; font-family: var(--font-mono); font-size: 0.875rem; line-height: 1.6; }
.post-content :deep(code) { font-family: var(--font-mono); font-size: 0.9em; }
.post-content :deep(:not(pre) > code) { background: var(--color-tag-bg); color: var(--color-primary-dark); padding: 2px 8px; border-radius: 4px; }
.post-content :deep(strong) { font-weight: 600; }
.post-content :deep(em) { font-style: italic; }
.post-content :deep(del) { text-decoration: line-through; opacity: 0.7; }
.post-content :deep(img) { max-width: 100%; height: auto; border-radius: var(--radius-sm); margin: 1em 0; display: block; }
.post-content :deep(a) { color: var(--color-primary); text-decoration: underline; }
.post-content :deep(blockquote) { margin: 1em 0; padding: 10px 20px; border-left: 4px solid var(--color-primary-light); background: var(--color-tag-bg); border-radius: 0 var(--radius-sm) var(--radius-sm) 0; color: var(--color-text-secondary); }
.post-content :deep(ul), .post-content :deep(ol) { margin: 0.8em 0; padding-left: 1.8em; }
.post-content :deep(li) { margin: 0.3em 0; }
.post-content :deep(hr) { border: none; border-top: 1px solid var(--color-border); margin: 2em 0; }

.post-empty { display: flex; flex-direction: column; align-items: center; justify-content: center; height: calc(100vh - 60px); color: var(--color-text-muted); }
.empty-icon { font-size: 4rem; margin-bottom: 16px; opacity: 0.4; }
.post-empty p { font-size: 1rem; margin: 0; }

@media (max-width: 900px) {
  .articles-sidebar { width: 200px; }
  .post-detail { padding: 24px 24px 40px; }
  .post-title { font-size: 1.6rem; }
  .post-cover { height: 160px; }
}

@media (max-width: 640px) {
  .articles-layout { flex-direction: column; }
  .articles-sidebar {
    width: 100%;
    height: auto;
    max-height: 260px;
    position: relative;
    top: 0;
    border-right: none;
    border-bottom: 2px solid var(--color-primary-light);
  }
  .articles-sidebar.collapsed { width: 100%; max-height: 48px; overflow: hidden; }
  .sidebar-top { padding: 10px 12px; }
  .sidebar-title-row { align-items: flex-start; }
  .sidebar-heading { flex-shrink: 0; }
  .sidebar-search { width: min(120px, 38vw); }
  .sidebar-search:focus { width: min(150px, 46vw); }
  .sidebar-list { max-height: 190px; overflow-y: auto; }
  .sidebar-list-collapsed { flex-direction: row; justify-content: center; padding: 8px 12px; overflow-x: auto; }
  .post-detail { padding: 20px 16px 40px; }
  .post-title { font-size: 1.4rem; }
  .post-cover { height: 130px; }
  .post-empty {
    height: auto;
    min-height: 300px;
    padding: 48px 16px;
    text-align: center;
  }
}
</style>
