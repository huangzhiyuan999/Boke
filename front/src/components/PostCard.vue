<template>
  <article class="post-card">
    <router-link :to="`/post/${post.id}`" class="card-link">
      <div class="card-cover" :style="{ background: post.cover }"></div>
      <div class="card-body">
        <time class="card-date">{{ post.date }}</time>
        <h2 class="card-title">{{ post.title }}</h2>
        <p class="card-summary">{{ post.summary }}</p>
        <div class="card-tags">
          <router-link
            v-for="tag in post.tags"
            :key="tag"
            :to="`/tag/${tag}`"
            class="tag"
            @click.stop
          >
            {{ tag }}
          </router-link>
        </div>
      </div>
    </router-link>
  </article>
</template>

<script setup>
defineProps({
  post: { type: Object, required: true }
})
</script>

<style scoped>
.post-card {
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.card-link {
  color: inherit;
  display: block;
}

.card-cover {
  height: 160px;
}

.card-body {
  padding: 24px;
}

.card-date {
  font-size: 0.85rem;
  color: var(--color-text-muted);
}

.card-title {
  font-size: 1.25rem;
  margin: 8px 0 10px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-summary {
  color: var(--color-text-secondary);
  font-size: 0.95rem;
  margin: 0 0 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
</style>
