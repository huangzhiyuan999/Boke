<template>
  <div class="entertainment-page">
    <div class="entertainment-bg"></div>
    <div class="entertainment-shell container">
      <header class="entertainment-header">
        <div>
          <p class="eyebrow">Fun Zone</p>
          <h1>娱乐中心</h1>
          <p class="header-copy">商城、小游戏和每日活动都在这里，当前使用模拟数据。</p>
        </div>
        <div class="score-panel">
          <span class="score-label">娱乐币</span>
          <strong>{{ userCoins }}</strong>
          <span class="score-note">今日已领 +80</span>
        </div>
      </header>

      <nav class="section-tabs" aria-label="娱乐模块">
        <router-link
          v-for="item in modules"
          :key="item.key"
          :to="item.to"
          class="section-tab"
          :class="{ active: activeSection === item.key }"
        >
          <span class="tab-icon">{{ item.icon }}</span>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>

      <section v-if="activeSection === 'home'" class="module-grid">
        <router-link
          v-for="item in modules.slice(1)"
          :key="item.key"
          :to="item.to"
          class="module-card"
        >
          <span class="module-icon">{{ item.icon }}</span>
          <div>
            <h2>{{ item.label }}</h2>
            <p>{{ item.desc }}</p>
          </div>
          <span class="module-arrow">→</span>
        </router-link>
      </section>

      <section v-if="activeSection === 'mall'" class="content-section">
        <div class="section-heading">
          <div>
            <h2>积分商城</h2>
            <p>模拟商品展示，后续可接真实库存和兑换接口。</p>
          </div>
          <span class="pill">6 件商品</span>
        </div>
        <div class="shop-grid">
          <article v-for="item in shopItems" :key="item.id" class="shop-card">
            <div class="shop-cover" :style="{ background: item.cover }">
              <span>{{ item.badge }}</span>
            </div>
            <div class="shop-body">
              <h3>{{ item.name }}</h3>
              <p>{{ item.desc }}</p>
              <div class="shop-meta">
                <strong>{{ item.price }} 娱乐币</strong>
                <button type="button" @click="selectedItem = item">查看</button>
              </div>
            </div>
          </article>
        </div>
      </section>

      <section v-if="activeSection === 'games'" class="content-section">
        <template v-if="selectedGame">
          <div class="game-detail">
            <router-link class="back-link" to="/entertainment/games">返回游戏大厅</router-link>
            <div class="game-detail-hero">
              <div class="game-art game-art-large" :style="{ background: selectedGame.cover }">
                <span>{{ selectedGame.short }}</span>
              </div>
              <div>
                <span class="pill">模拟游玩入口</span>
                <h2>{{ selectedGame.name }}</h2>
                <p>{{ selectedGame.desc }}</p>
                <div class="game-tags">
                  <span v-for="tag in selectedGame.tags" :key="tag">{{ tag }}</span>
                </div>
              </div>
            </div>
            <div class="playground">
              <div class="play-window">
                <strong>{{ selectedGame.demoTitle }}</strong>
                <span>{{ selectedGame.demoText }}</span>
                <button type="button">开始模拟</button>
              </div>
              <div class="rank-panel">
                <h3>本周排行</h3>
                <ol>
                  <li v-for="rank in selectedGame.ranks" :key="rank.name">
                    <span>{{ rank.name }}</span>
                    <strong>{{ rank.score }}</strong>
                  </li>
                </ol>
              </div>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="section-heading">
            <div>
              <h2>游戏大厅</h2>
              <p>轻量小游戏入口，点击可进入模拟游戏详情。</p>
            </div>
            <span class="pill">今日推荐</span>
          </div>
          <div class="game-list">
            <article v-for="game in games" :key="game.id" class="game-card">
              <div class="game-art" :style="{ background: game.cover }">
                <span>{{ game.short }}</span>
              </div>
              <div class="game-info">
                <h3>{{ game.name }}</h3>
                <p>{{ game.desc }}</p>
                <div class="game-tags">
                  <span v-for="tag in game.tags" :key="tag">{{ tag }}</span>
                </div>
              </div>
              <router-link class="play-btn" :to="`/entertainment/games?game=${game.id}`">进入</router-link>
            </article>
          </div>
        </template>
      </section>

      <section v-if="activeSection === 'events'" class="content-section">
        <div class="section-heading">
          <div>
            <h2>每日活动</h2>
            <p>签到、抽奖、挑战任务均为前端模拟状态。</p>
          </div>
          <span class="pill">可领取 3 项</span>
        </div>
        <div class="event-grid">
          <article v-for="event in events" :key="event.id" class="event-card">
            <div class="event-top">
              <span class="event-icon">{{ event.icon }}</span>
              <span class="event-reward">+{{ event.reward }}</span>
            </div>
            <h3>{{ event.title }}</h3>
            <p>{{ event.desc }}</p>
            <button type="button" :disabled="event.done">{{ event.done ? '已完成' : '去完成' }}</button>
          </article>
        </div>
      </section>
    </div>

    <div v-if="selectedItem" class="detail-mask" @click.self="selectedItem = null">
      <div class="detail-dialog">
        <button class="dialog-close" type="button" @click="selectedItem = null">×</button>
        <div class="detail-cover" :style="{ background: selectedItem.cover }"></div>
        <h2>{{ selectedItem.name }}</h2>
        <p>{{ selectedItem.desc }}</p>
        <div class="detail-actions">
          <span>{{ selectedItem.price }} 娱乐币</span>
          <button type="button" @click="selectedItem = null">模拟兑换</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const selectedItem = ref(null)
const userCoins = ref(2680)

const modules = [
  { key: 'home', label: '总览', icon: '⌂', to: '/entertainment', desc: '娱乐模块总入口' },
  { key: 'mall', label: '商城', icon: '¥', to: '/entertainment/mall', desc: '积分商品、头像框和虚拟道具' },
  { key: 'games', label: '游戏', icon: '▶', to: '/entertainment/games', desc: '小游戏大厅和排行榜' },
  { key: 'events', label: '活动', icon: '★', to: '/entertainment/events', desc: '签到、抽奖、每日任务' }
]

const activeSection = computed(() => {
  const section = route.params.section
  return ['mall', 'games', 'events'].includes(section) ? section : 'home'
})

const selectedGame = computed(() => {
  if (activeSection.value !== 'games') return null
  return games.find((game) => game.id === route.query.game) || null
})

const shopItems = [
  { id: 1, name: '薄荷头像框', desc: '清爽绿色头像装饰，展示 7 天。', price: 320, badge: 'NEW', cover: 'linear-gradient(135deg, #7EC8A8, #5B8C5A)' },
  { id: 2, name: '星光徽章', desc: '个人主页装饰徽章，展示 30 天。', price: 680, badge: 'HOT', cover: 'linear-gradient(135deg, #facc15, #fb7185)' },
  { id: 3, name: '留言高亮卡', desc: '让你的留言在留言板置顶高亮。', price: 520, badge: 'TOP', cover: 'linear-gradient(135deg, #60a5fa, #a78bfa)' },
  { id: 4, name: '游戏复活券', desc: '小游戏失败后可模拟复活一次。', price: 180, badge: 'FUN', cover: 'linear-gradient(135deg, #34d399, #38bdf8)' },
  { id: 5, name: '幸运抽奖券', desc: '参与活动页模拟抽奖一次。', price: 260, badge: 'LUCK', cover: 'linear-gradient(135deg, #fb923c, #f43f5e)' },
  { id: 6, name: '夜间主题卡', desc: '兑换后可解锁模拟深色主题。', price: 880, badge: 'VIP', cover: 'linear-gradient(135deg, #1e293b, #64748b)' }
]

const games = [
  {
    id: 'speed',
    name: '反应力挑战',
    short: 'GO',
    desc: '在倒计时结束瞬间点击，越接近 0 分数越高。',
    tags: ['反应', '单人'],
    cover: 'linear-gradient(135deg, #22c55e, #06b6d4)',
    demoTitle: '倒计时就绪',
    demoText: '模拟区域会记录点击时机，后续接入真实小游戏逻辑。',
    ranks: [{ name: '星野', score: 9820 }, { name: '小墨', score: 9340 }, { name: '阿川', score: 9020 }]
  },
  {
    id: 'memory',
    name: '记忆翻牌',
    short: 'MEM',
    desc: '翻开卡片寻找相同图案，模拟排行数据。',
    tags: ['记忆', '休闲'],
    cover: 'linear-gradient(135deg, #8b5cf6, #ec4899)',
    demoTitle: '翻牌局已创建',
    demoText: '这里预留卡牌矩阵和计步统计，当前为前端模拟。',
    ranks: [{ name: '南枝', score: 28 }, { name: '青禾', score: 31 }, { name: '云里', score: 36 }]
  },
  {
    id: 'typing',
    name: '打字冲刺',
    short: 'ABC',
    desc: '限时输入随机词组，统计速度和准确率。',
    tags: ['键盘', '练习'],
    cover: 'linear-gradient(135deg, #f97316, #facc15)',
    demoTitle: '词组池准备完成',
    demoText: '模拟输入区会展示速度和准确率，后续可接真实计时。',
    ranks: [{ name: '北辰', score: 136 }, { name: '林夏', score: 124 }, { name: '知更', score: 119 }]
  }
]

const events = [
  { id: 1, icon: '✓', title: '每日签到', desc: '登录娱乐中心即可领取奖励。', reward: 80, done: true },
  { id: 2, icon: '🎲', title: '幸运抽奖', desc: '消耗抽奖券参与模拟转盘。', reward: 120, done: false },
  { id: 3, icon: '⌁', title: '小游戏挑战', desc: '完成任意一局游戏即可领取。', reward: 160, done: false },
  { id: 4, icon: '✦', title: '商城逛逛', desc: '查看任意商品详情完成任务。', reward: 60, done: false }
]
</script>

<style scoped>
.entertainment-page {
  position: relative;
  min-height: calc(100vh - 60px);
  overflow-x: hidden;
  background: #f7f8fa;
}

.entertainment-bg {
  position: fixed;
  inset: 0;
  background:
    linear-gradient(rgba(255,255,255,0.72), rgba(255,255,255,0.9)),
    url('/hero-bg.jpg') center 22% / cover no-repeat;
  z-index: 0;
}

.entertainment-shell {
  position: relative;
  z-index: 1;
  min-width: 0;
  padding-top: 36px;
  padding-bottom: 64px;
}

.entertainment-header {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 24px;
  align-items: end;
  margin-bottom: 20px;
}

.entertainment-header > * {
  min-width: 0;
}

.eyebrow {
  margin: 0 0 4px;
  color: var(--color-primary);
  font-size: 0.78rem;
  font-weight: 700;
  letter-spacing: 0;
  text-transform: uppercase;
}

.entertainment-header h1 {
  margin: 0;
  font-size: 2rem;
}

.header-copy {
  margin: 8px 0 0;
  color: var(--color-text-secondary);
  overflow-wrap: anywhere;
  white-space: normal;
}

.score-panel {
  min-width: 150px;
  padding: 16px 18px;
  border-radius: var(--radius-md);
  background: rgba(255,255,255,0.86);
  box-shadow: var(--shadow-sm);
}

.score-label,
.score-note {
  display: block;
  color: var(--color-text-muted);
  font-size: 0.78rem;
}

.score-panel strong {
  display: block;
  margin: 2px 0;
  color: var(--color-primary-dark);
  font-size: 1.6rem;
}

.section-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 22px;
  overflow-x: auto;
  padding-bottom: 2px;
}

.section-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 9px 14px;
  border-radius: 999px;
  background: rgba(255,255,255,0.82);
  color: var(--color-text-secondary);
  box-shadow: var(--shadow-sm);
  white-space: nowrap;
}

.section-tab.active {
  background: var(--color-primary);
  color: #fff;
}

.tab-icon {
  width: 20px;
  height: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.module-card,
.shop-card,
.game-card,
.event-card {
  min-width: 0;
  background: rgba(255,255,255,0.9);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.module-card {
  min-height: 150px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 14px;
  align-items: center;
  padding: 22px;
  color: var(--color-text);
}

.module-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--color-tag-bg);
  color: var(--color-primary-dark);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
}

.module-card h2,
.section-heading h2,
.shop-card h3,
.game-card h3,
.event-card h3 {
  margin: 0;
}

.module-card p,
.section-heading p,
.shop-card p,
.game-card p,
.event-card p {
  color: var(--color-text-secondary);
  overflow-wrap: anywhere;
}

.module-arrow {
  color: var(--color-primary);
  font-size: 1.3rem;
}

.content-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.section-heading p {
  margin: 4px 0 0;
}

.pill {
  padding: 5px 12px;
  border-radius: 999px;
  background: var(--color-tag-bg);
  color: var(--color-primary-dark);
  font-size: 0.8rem;
  font-weight: 600;
  white-space: nowrap;
}

.shop-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.shop-card {
  overflow: hidden;
}

.shop-cover {
  height: 112px;
  padding: 12px;
  color: #fff;
  font-weight: 800;
}

.shop-cover span {
  padding: 3px 8px;
  border-radius: 999px;
  background: rgba(255,255,255,0.24);
}

.shop-body {
  padding: 16px;
}

.shop-body p {
  min-height: 48px;
  margin: 8px 0 12px;
}

.shop-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.shop-meta strong {
  color: var(--color-primary-dark);
}

.shop-meta button,
.event-card button,
.detail-actions button,
.play-btn {
  border: none;
  border-radius: var(--radius-sm);
  background: var(--color-primary);
  color: #fff;
  cursor: pointer;
  font-family: inherit;
  font-weight: 600;
}

.shop-meta button,
.play-btn {
  padding: 7px 14px;
}

.game-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.game-card {
  display: grid;
  grid-template-columns: 96px 1fr auto;
  gap: 16px;
  align-items: center;
  padding: 16px;
}

.game-art {
  width: 96px;
  height: 76px;
  border-radius: var(--radius-sm);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
}

.game-info p {
  margin: 6px 0 10px;
}

.game-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.game-tags span {
  padding: 2px 8px;
  border-radius: 999px;
  background: var(--color-tag-bg);
  color: var(--color-tag-text);
  font-size: 0.76rem;
}

.play-btn {
  text-align: center;
}

.game-detail {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.back-link {
  width: fit-content;
  color: var(--color-primary-dark);
  font-weight: 600;
}

.game-detail-hero,
.playground {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(280px, 0.9fr);
  gap: 16px;
}

.game-detail-hero,
.play-window,
.rank-panel {
  background: rgba(255,255,255,0.9);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.game-detail-hero {
  grid-template-columns: 150px 1fr;
  align-items: center;
  padding: 18px;
}

.game-detail-hero h2 {
  margin: 10px 0 6px;
}

.game-detail-hero p {
  margin: 0 0 12px;
  color: var(--color-text-secondary);
}

.game-art-large {
  width: 150px;
  height: 120px;
  font-size: 1.6rem;
}

.play-window,
.rank-panel {
  padding: 18px;
}

.play-window {
  min-height: 210px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 12px;
}

.play-window strong {
  font-size: 1.2rem;
}

.play-window span {
  color: var(--color-text-secondary);
}

.play-window button {
  border: none;
  border-radius: var(--radius-sm);
  background: var(--color-primary);
  color: #fff;
  cursor: pointer;
  font-family: inherit;
  font-weight: 600;
  padding: 9px 18px;
}

.rank-panel h3 {
  margin: 0 0 12px;
}

.rank-panel ol {
  margin: 0;
  padding: 0;
  list-style: none;
}

.rank-panel li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 9px 0;
  border-bottom: 1px solid var(--color-border);
}

.rank-panel li:last-child {
  border-bottom: none;
}

.rank-panel strong {
  color: var(--color-primary-dark);
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.event-card {
  padding: 18px;
}

.event-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.event-icon {
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: var(--color-tag-bg);
  color: var(--color-primary-dark);
  display: flex;
  align-items: center;
  justify-content: center;
}

.event-reward {
  color: var(--color-primary);
  font-weight: 700;
}

.event-card p {
  min-height: 64px;
}

.event-card button {
  width: 100%;
  padding: 8px 0;
}

.event-card button:disabled {
  background: var(--color-border);
  color: var(--color-text-muted);
  cursor: default;
}

.detail-mask {
  position: fixed;
  inset: 0;
  z-index: 300;
  background: rgba(0,0,0,0.42);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.detail-dialog {
  position: relative;
  width: min(420px, 100%);
  background: #fff;
  border-radius: var(--radius-md);
  padding: 22px;
  box-shadow: var(--shadow-lg);
}

.dialog-close {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 50%;
  background: rgba(0,0,0,0.08);
  cursor: pointer;
}

.detail-cover {
  height: 120px;
  border-radius: var(--radius-sm);
  margin-bottom: 16px;
}

.detail-dialog p {
  color: var(--color-text-secondary);
}

.detail-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.detail-actions span {
  color: var(--color-primary-dark);
  font-weight: 700;
}

.detail-actions button {
  padding: 8px 16px;
}

@media (max-width: 760px) {
  .entertainment-shell {
    padding-top: 24px;
  }

  .entertainment-header h1 {
    font-size: 1.8rem;
  }

  .header-copy {
    max-width: calc(100vw - 32px);
    line-height: 1.6;
  }

  .entertainment-header {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .score-panel {
    min-width: 0;
  }

  .module-card {
    grid-template-columns: 44px minmax(0, 1fr);
  }

  .module-arrow {
    display: none;
  }

  .module-grid,
  .shop-grid,
  .event-grid {
    grid-template-columns: 1fr;
  }

  .section-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .game-card {
    grid-template-columns: 72px 1fr;
  }

  .game-art {
    width: 72px;
    height: 64px;
  }

  .play-btn {
    grid-column: 1 / -1;
  }

  .game-detail-hero,
  .playground {
    grid-template-columns: 1fr;
  }

  .game-detail-hero {
    align-items: flex-start;
  }

  .game-art-large {
    width: 100%;
  }
}
</style>
