<template>
  <div class="entertainment-page">
    <div class="entertainment-shell">
      <header class="entertainment-topbar">
        <div class="brand-block">
          <span class="brand-kicker">FUN PLAZA</span>
          <h1>娱乐广场</h1>
        </div>
        <label class="search-box">
          <span>⌕</span>
          <input v-model="keyword" type="search" placeholder="搜索商品、游戏、活动" />
        </label>
        <div class="wallet-strip">
          <span>娱乐币</span>
          <strong>{{ userCoins }}</strong>
          <button type="button" :disabled="checkedIn" @click="handleCheckIn">{{ checkedIn ? '已签' : '签到' }}</button>
        </div>
        <transition name="toast-fade">
          <div v-if="toastMessage" class="toast-bar" :class="toastType" @click="toastMessage = ''">
            {{ toastMessage }}
          </div>
        </transition>
      </header>

      <nav class="channel-tabs" aria-label="娱乐频道">
        <router-link
          v-for="item in modules"
          :key="item.key"
          :to="item.to"
          class="channel-tab"
          :class="{ active: activeSection === item.key }"
        >
          <span>{{ item.icon }}</span>
          {{ item.label }}
        </router-link>
      </nav>

      <section v-if="activeSection === 'home'" class="home-layout">
        <aside class="category-rail">
          <h2>频道分类</h2>
          <router-link v-for="item in categoryLinks" :key="item.label" :to="item.to">
            <span>{{ item.icon }}</span>
            <div>
              <strong>{{ item.label }}</strong>
              <small>{{ item.desc }}</small>
            </div>
          </router-link>
        </aside>

        <div class="hero-stage">
          <div class="promo-copy">
            <span class="promo-badge">今日热场</span>
            <h2>星光赛季开启</h2>
            <p>兑换装扮、挑战小游戏、完成任务领取娱乐币。</p>
            <div class="promo-actions">
              <router-link to="/entertainment/games">进入游戏大厅</router-link>
              <router-link to="/entertainment/mall">逛积分商城</router-link>
            </div>
          </div>
          <div class="promo-visual">
            <span class="visual-card one">GO</span>
            <span class="visual-card two">VIP</span>
            <span class="visual-card three">¥</span>
          </div>
        </div>

        <aside class="daily-board">
          <div class="daily-title">
            <span>今日任务</span>
            <strong>3/4</strong>
          </div>
          <div v-for="event in events.slice(0, 3)" :key="event.id" class="daily-item">
            <span>{{ event.icon }}</span>
            <div>
              <strong>{{ event.title }}</strong>
              <small>+{{ event.reward }} 娱乐币</small>
            </div>
          </div>
        </aside>

        <section class="shelf-section full-row">
          <div class="section-title">
            <div>
              <h2>限时兑换</h2>
              <p>高频道具和个人装扮优先展示。</p>
            </div>
            <router-link to="/entertainment/mall">更多商品</router-link>
          </div>
          <div class="product-row">
            <article v-for="item in shopItems.slice(0, 4)" :key="item.id" class="product-card">
              <button type="button" class="product-cover image-cover" :style="coverStyle(item.image)" @click="selectedItem = item">
                <span>{{ item.badge }}</span>
              </button>
              <div class="product-info">
                <h3>{{ item.name }}</h3>
                <p>{{ item.desc }}</p>
                <div class="price-line">
                  <strong>{{ item.price }}</strong>
                  <span>娱乐币</span>
                  <small>{{ item.sales }} 人想要</small>
                </div>
              </div>
            </article>
          </div>
        </section>

        <section v-if="games.length" class="game-showcase full-row">
          <div class="showcase-main">
            <span class="promo-badge">精选游戏</span>
            <h2>{{ games[0].name }}</h2>
            <p>{{ games[0].desc }}</p>
            <div class="game-tags">
              <span v-for="tag in games[0].tags" :key="tag">{{ tag }}</span>
            </div>
            <router-link class="primary-link" :to="`/entertainment/games?game=${games[0].code}`">查看详情</router-link>
          </div>
          <div class="showcase-list">
            <router-link v-for="game in games.slice(1)" :key="game.id" :to="`/entertainment/games?game=${game.code}`">
              <span class="mini-cover image-cover" :style="coverStyle(game.image)">{{ game.short }}</span>
              <div>
                <strong>{{ game.name }}</strong>
                <small>{{ game.players }} 人在玩</small>
              </div>
            </router-link>
          </div>
        </section>
      </section>

      <section v-if="activeSection === 'mall'" class="mall-layout">
        <div class="section-title wide-title">
          <div>
            <h2>积分商城</h2>
            <p>按热度、库存和兑换成本组织商品。</p>
          </div>
          <div class="filter-chips">
            <button
              v-for="sort in sortOptions"
              :key="sort.key"
              type="button"
              :class="{ active: activeSort === sort.key }"
              @click="activeSort = sort.key"
            >
              {{ sort.label }}
            </button>
          </div>
        </div>

        <aside class="mall-side">
          <h3>商城分类</h3>
          <button
            v-for="cat in mallCategories"
            :key="cat"
            type="button"
            :class="{ active: activeCategory === cat }"
            @click="activeCategory = cat"
          >
            {{ cat }}
          </button>
        </aside>

        <div v-if="filteredShopItems.length" class="shop-grid">
          <article v-for="item in filteredShopItems" :key="item.id" class="shop-card">
            <button type="button" class="shop-cover image-cover" :style="coverStyle(item.image)" @click="selectedItem = item">
              <span>{{ item.badge }}</span>
            </button>
            <div class="shop-body">
              <h3>{{ item.name }}</h3>
              <p>{{ item.desc }}</p>
              <div class="shop-stats">
                <span>库存 {{ item.stock }}</span>
                <span>{{ item.sales }} 人想要</span>
              </div>
              <div class="shop-meta">
                <div>
                  <strong>{{ item.price }}</strong>
                  <span>娱乐币</span>
                </div>
                <button type="button" @click="selectedItem = item">查看</button>
              </div>
            </div>
          </article>
        </div>
        <div v-else class="empty-state">
          <strong>没有找到匹配商品</strong>
          <span>换个分类或关键词试试。</span>
        </div>
      </section>

      <section v-if="activeSection === 'games'" class="content-section">
        <template v-if="selectedGame">
          <div class="game-detail">
            <router-link class="back-link" to="/entertainment/games">返回游戏大厅</router-link>
            <div class="game-detail-hero" :style="{ background: selectedGame.detailBg }">
              <div class="game-art game-art-large image-cover" :style="coverStyle(selectedGame.image)">
                <span>{{ selectedGame.short }}</span>
              </div>
              <div class="detail-copy">
                <span class="promo-badge">{{ selectedGame.genre }}</span>
                <h2>{{ selectedGame.name }}</h2>
                <p>{{ selectedGame.desc }}</p>
                <div class="game-tags">
                  <span v-for="tag in selectedGame.tags" :key="tag">{{ tag }}</span>
                </div>
              </div>
              <div class="detail-score">
                <span>好评率</span>
                <strong>{{ selectedGame.rating }}</strong>
                <small>{{ selectedGame.players }} 人在玩</small>
              </div>
            </div>
            <div class="playground">
              <div class="play-window">
                <strong>{{ selectedGame.demoTitle }}</strong>
                <span>{{ selectedGame.demoText }}</span>
                <button type="button" @click="startGame(selectedGame)">{{ activeGameId === selectedGame.id ? '模拟中' : '开始模拟' }}</button>
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
          <div v-if="games.length" class="game-store">
            <div class="game-hero">
              <div>
                <span class="promo-badge">GAME HUB</span>
                <h2>游戏大厅</h2>
                <p>精选小游戏、排行榜和挑战任务集中展示。</p>
              </div>
              <router-link :to="`/entertainment/games?game=${games[0].code}`">今日主推</router-link>
            </div>
            <div class="game-grid">
              <article v-for="game in games" :key="game.id" class="game-card">
                <div class="game-art image-cover" :style="coverStyle(game.image)">
                  <span>{{ game.short }}</span>
                </div>
                <div class="game-info">
                  <span>{{ game.genre }}</span>
                  <h3>{{ game.name }}</h3>
                  <p>{{ game.desc }}</p>
                  <div class="game-tags">
                    <span v-for="tag in game.tags" :key="tag">{{ tag }}</span>
                  </div>
                </div>
                <router-link class="play-btn" :to="`/entertainment/games?game=${game.code}`">进入</router-link>
              </article>
            </div>
          </div>
          <div v-else class="empty-state">
            <strong>游戏数据加载中</strong>
            <span>稍等一下就能进入游戏大厅。</span>
          </div>
        </template>
      </section>

      <section v-if="activeSection === 'events'" class="event-layout">
        <div class="event-hero">
          <div>
            <span class="promo-badge">DAILY QUEST</span>
            <h2>每日活动</h2>
            <p>签到、抽奖、挑战和浏览任务统一收纳。</p>
          </div>
          <strong>可领取 3 项</strong>
        </div>
        <div class="event-grid">
          <article v-for="event in events" :key="event.id" class="event-card">
            <div class="event-top">
              <span class="event-icon">{{ event.icon }}</span>
              <span class="event-reward">+{{ event.reward }}</span>
            </div>
            <h3>{{ event.title }}</h3>
            <p>{{ event.desc }}</p>
            <button type="button" :disabled="event.done" @click="finishEvent(event)">{{ event.done ? '已完成' : '去完成' }}</button>
          </article>
        </div>
      </section>
    </div>

    <div v-if="selectedItem" class="detail-mask" @click.self="selectedItem = null">
      <div class="detail-dialog">
        <button class="dialog-close" type="button" @click="selectedItem = null">×</button>
        <div class="detail-cover image-cover" :style="coverStyle(selectedItem.image)"></div>
        <span class="promo-badge">{{ selectedItem.category }}</span>
        <h2>{{ selectedItem.name }}</h2>
        <p>{{ selectedItem.desc }}</p>
        <div class="dialog-meta">
          <span>库存 {{ selectedItem.stock }}</span>
          <span>{{ selectedItem.sales }} 人想要</span>
        </div>
        <div class="detail-actions">
          <span>{{ selectedItem.price }} 娱乐币</span>
          <button type="button" @click="selectedItem = null">模拟兑换</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../utils/api.js'
import { isLoggedIn } from '../stores/auth.js'

const route = useRoute()
const selectedItem = ref(null)
const keyword = ref('')
const userCoins = ref(2680)
const activeCategory = ref('全部')
const activeSort = ref('default')
const checkedIn = ref(false)
const activeGameId = ref('')
const toastMessage = ref('')
const toastType = ref('toast-info')
let toastTimer = null
const imageBase = '/photo/entertainment/'
const shopItems = ref([])
const games = ref([])
const events = ref([])

function coverStyle(image) {
  const url = image?.startsWith('/') ? image : `${imageBase}${image}`
  return { backgroundImage: `linear-gradient(rgba(15, 23, 42, 0.12), rgba(15, 23, 42, 0.38)), url('${url}')` }
}

const modules = [
  { key: 'home', label: '首页', icon: '⌂', to: '/entertainment' },
  { key: 'mall', label: '商城', icon: '¥', to: '/entertainment/mall' },
  { key: 'games', label: '游戏', icon: '▶', to: '/entertainment/games' },
  { key: 'events', label: '活动', icon: '★', to: '/entertainment/events' }
]

const categoryLinks = [
  { label: '积分商城', desc: '装扮、道具、券包', icon: '¥', to: '/entertainment/mall' },
  { label: '游戏大厅', desc: '休闲、反应、打字', icon: '▶', to: '/entertainment/games' },
  { label: '每日活动', desc: '签到、抽奖、任务', icon: '★', to: '/entertainment/events' },
  { label: '排行榜', desc: '本周热度和成绩', icon: '↗', to: '/entertainment/games' }
]

const mallCategories = ['全部', '头像装扮', '互动道具', '游戏道具', '主题权益']
const sortOptions = [
  { key: 'default', label: '综合' },
  { key: 'hot', label: '热兑' },
  { key: 'cheap', label: '低价' },
  { key: 'dress', label: '装扮' }
]

const activeSection = computed(() => {
  const section = route.params.section
  return ['mall', 'games', 'events'].includes(section) ? section : 'home'
})

const selectedGame = computed(() => {
  if (activeSection.value !== 'games') return null
  return games.value.find((game) => game.code === route.query.game || String(game.id) === String(route.query.game)) || null
})

const filteredShopItems = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  const filtered = shopItems.value.filter((item) => {
    const matchKeyword = !key || `${item.name}${item.desc}${item.category}`.toLowerCase().includes(key)
    const matchCategory = activeCategory.value === '全部' || item.category === activeCategory.value
    const matchSort = activeSort.value !== 'dress' || item.category === '头像装扮'
    return matchKeyword && matchCategory && matchSort
  })

  return [...filtered].sort((a, b) => {
    if (activeSort.value === 'hot') return b.sales - a.sales
    if (activeSort.value === 'cheap') return a.price - b.price
    if (activeSort.value === 'dress') return b.sales - a.sales
    return a.id - b.id
  })
})

function handleCheckIn() {
  if (checkedIn.value) return
  if (!isLoggedIn.value) {
    showToast('请先登录后再签到', 'toast-warn')
    return
  }
  api.post('/entertainment/check-in', {})
    .then(applyWallet)
    .catch((e) => showToast(e.message || '签到失败', 'toast-error'))
}

function startGame(game) {
  activeGameId.value = game.id
  api.post(`/entertainment/games/${game.code}/play`, { score: 0 })
    .catch((e) => showToast(e.message || '游戏请求失败', 'toast-error'))
}

function finishEvent(event) {
  if (!isLoggedIn.value) {
    showToast('请先登录后再参与活动', 'toast-warn')
    return
  }
  api.post(`/entertainment/events/${event.id}/finish`, {})
    .then((wallet) => {
      event.done = true
      applyWallet(wallet)
    })
    .catch((e) => showToast(e.message || '活动提交失败', 'toast-error'))
}

function showToast(msg, type = 'toast-info') {
  toastMessage.value = msg
  toastType.value = type
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toastMessage.value = '' }, 3000)
}

function applyWallet(wallet) {
  if (!wallet) return
  userCoins.value = wallet.coins
  checkedIn.value = wallet.checkedIn
}

function normalizeGame(game) {
  return {
    ...game,
    short: game.short || game.shortName,
    ranks: game.ranks || []
  }
}

async function loadEntertainment() {
  try {
    const data = await api.get('/entertainment')
    applyWallet(data.wallet)
    shopItems.value = data.items?.length ? data.items : fallbackShopItems
    games.value = data.games?.length ? data.games.map(normalizeGame) : fallbackGames
    events.value = data.events?.length ? data.events : fallbackEvents
  } catch (e) {
    shopItems.value = fallbackShopItems
    games.value = fallbackGames
    events.value = fallbackEvents
  }
}

onMounted(loadEntertainment)

const fallbackShopItems = [
  { id: 1, name: '薄荷头像框', category: '头像装扮', desc: '清爽绿色头像装饰，展示 7 天。', price: 320, badge: 'NEW', stock: 86, sales: 1240, image: 'product-frame.jpg' },
  { id: 2, name: '星光徽章', category: '头像装扮', desc: '个人主页装饰徽章，展示 30 天。', price: 680, badge: 'HOT', stock: 42, sales: 2680, image: 'product-badge.jpg' },
  { id: 3, name: '留言高亮卡', category: '互动道具', desc: '让你的留言在留言板置顶高亮。', price: 520, badge: 'TOP', stock: 58, sales: 1810, image: 'product-highlight.jpg' },
  { id: 4, name: '游戏复活券', category: '游戏道具', desc: '小游戏失败后可模拟复活一次。', price: 180, badge: 'FUN', stock: 230, sales: 3021, image: 'product-revive.jpg' },
  { id: 5, name: '幸运抽奖券', category: '互动道具', desc: '参与活动页抽奖一次。', price: 260, badge: 'LUCK', stock: 145, sales: 2168, image: 'product-lottery.jpg' },
  { id: 6, name: '夜间主题卡', category: '主题权益', desc: '兑换后可解锁深色主题。', price: 880, badge: 'VIP', stock: 30, sales: 960, image: 'product-theme.jpg' },
  { id: 7, name: '评论贴纸包', category: '互动道具', desc: '留言和推荐模块可用的趣味贴纸。', price: 420, badge: 'SET', stock: 76, sales: 1355, image: 'product-sticker.jpg' },
  { id: 8, name: '挑战加速卡', category: '游戏道具', desc: '完成小游戏任务时获得额外奖励。', price: 360, badge: 'PLUS', stock: 118, sales: 1542, image: 'product-boost.jpg' }
]

const fallbackGames = [
  {
    id: 'speed',
    code: 'speed',
    name: '反应力挑战',
    short: 'GO',
    genre: '街机反应',
    desc: '在倒计时结束瞬间点击，越接近 0 分数越高。',
    tags: ['反应', '单人', '排行榜'],
    rating: '96%',
    players: 1280,
    image: 'game-speed.jpg',
    detailBg: "linear-gradient(135deg, rgba(15,23,42,0.88), rgba(6,95,70,0.78)), url('/photo/entertainment/game-speed.jpg') center / cover no-repeat",
    demoTitle: '倒计时就绪',
    demoText: '模拟区域会记录点击时机，后续接入真实小游戏逻辑。',
    ranks: [{ name: '星野', score: 9820 }, { name: '小墨', score: 9340 }, { name: '阿川', score: 9020 }]
  },
  {
    id: 'memory',
    code: 'memory',
    name: '记忆翻牌',
    short: 'MEM',
    genre: '休闲益智',
    desc: '翻开卡片寻找相同图案，模拟排行数据。',
    tags: ['记忆', '休闲', '轻量'],
    rating: '93%',
    players: 860,
    image: 'game-memory.jpg',
    detailBg: "linear-gradient(135deg, rgba(15,23,42,0.88), rgba(88,28,135,0.78)), url('/photo/entertainment/game-memory.jpg') center / cover no-repeat",
    demoTitle: '翻牌局已创建',
    demoText: '这里预留卡牌矩阵和计步统计，当前为前端模拟。',
    ranks: [{ name: '南枝', score: 28 }, { name: '青禾', score: 31 }, { name: '云里', score: 36 }]
  },
  {
    id: 'typing',
    code: 'typing',
    name: '打字冲刺',
    short: 'ABC',
    genre: '键盘练习',
    desc: '限时输入随机词组，统计速度和准确率。',
    tags: ['键盘', '练习', '速度'],
    rating: '91%',
    players: 640,
    image: 'game-typing.jpg',
    detailBg: "linear-gradient(135deg, rgba(15,23,42,0.88), rgba(194,65,12,0.78)), url('/photo/entertainment/game-typing.jpg') center / cover no-repeat",
    demoTitle: '词组池准备完成',
    demoText: '模拟输入区会展示速度和准确率，后续可接真实计时。',
    ranks: [{ name: '北辰', score: 136 }, { name: '林夏', score: 124 }, { name: '知更', score: 119 }]
  }
]

const fallbackEvents = [
  { id: 1, icon: '✓', title: '每日签到', desc: '登录娱乐广场即可领取奖励。', reward: 80, done: true },
  { id: 2, icon: '🎲', title: '幸运抽奖', desc: '消耗抽奖券参与转盘。', reward: 120, done: false },
  { id: 3, icon: '⌁', title: '小游戏挑战', desc: '完成任意一局游戏即可领取。', reward: 160, done: false },
  { id: 4, icon: '✦', title: '商城逛逛', desc: '查看任意商品详情完成任务。', reward: 60, done: false }
]
</script>

<style scoped>
.entertainment-page {
  min-height: calc(100vh - 60px);
  overflow-x: hidden;
  background:
    radial-gradient(circle at 12% 0%, rgba(255, 216, 128, 0.35), transparent 24%),
    radial-gradient(circle at 92% 12%, rgba(96, 165, 250, 0.28), transparent 28%),
    #f4f5f7;
}

.entertainment-shell {
  width: min(1180px, calc(100vw - 32px));
  margin: 0 auto;
  padding: 26px 0 72px;
}

.entertainment-topbar {
  display: grid;
  grid-template-columns: minmax(190px, auto) minmax(280px, 1fr) auto;
  gap: 16px;
  align-items: center;
  margin-bottom: 14px;
}

.brand-kicker,
.promo-badge {
  display: inline-flex;
  align-items: center;
  width: fit-content;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
  color: #b45309;
  font-size: 0.72rem;
  font-weight: 800;
  letter-spacing: 0;
  padding: 4px 9px;
}

.brand-block h1,
.hero-stage h2,
.section-title h2,
.game-hero h2,
.event-hero h2,
.game-detail-hero h2 {
  margin: 4px 0 0;
  color: #172033;
}

.brand-block h1 {
  font-size: 1.8rem;
}

.search-box {
  height: 44px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
}

.search-box span {
  color: #64748b;
  font-size: 1.1rem;
}

.search-box input {
  width: 100%;
  border: none;
  outline: none;
  background: transparent;
  color: #172033;
  font: inherit;
}

.wallet-strip {
  min-width: 188px;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 2px 12px;
  align-items: center;
  padding: 10px 12px;
  border-radius: 12px;
  background: #172033;
  color: #fff;
  box-shadow: 0 14px 28px rgba(15, 23, 42, 0.18);
}

.wallet-strip span {
  color: rgba(255,255,255,0.68);
  font-size: 0.78rem;
}

.wallet-strip strong {
  font-size: 1.45rem;
}

.wallet-strip button {
  grid-row: 1 / span 2;
  grid-column: 2;
  border: none;
  border-radius: 999px;
  background: #ff7a1a;
  color: #fff;
  cursor: pointer;
  font: inherit;
  font-weight: 700;
  padding: 7px 12px;
}

.wallet-strip button:disabled {
  background: rgba(255,255,255,0.16);
  color: rgba(255,255,255,0.7);
  cursor: default;
}

.channel-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 18px;
  overflow-x: auto;
  padding-bottom: 2px;
}

.channel-tab {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 38px;
  padding: 8px 16px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.78);
  color: #475569;
  font-weight: 700;
  white-space: nowrap;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.05);
}

.channel-tab.active {
  background: #172033;
  color: #fff;
}

.home-layout {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr) 230px;
  gap: 16px;
}

.category-rail,
.hero-stage,
.daily-board,
.shelf-section,
.game-showcase,
.mall-side,
.shop-card,
.game-hero,
.game-card,
.event-hero,
.event-card,
.play-window,
.rank-panel,
.detail-dialog {
  border: 1px solid rgba(15, 23, 42, 0.07);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 14px 32px rgba(15, 23, 42, 0.07);
}

.category-rail {
  padding: 16px;
}

.category-rail h2,
.daily-title span,
.mall-side h3,
.rank-panel h3,
.event-card h3,
.shop-card h3,
.game-card h3 {
  margin: 0;
  color: #172033;
}

.category-rail a {
  display: grid;
  grid-template-columns: 36px 1fr;
  gap: 10px;
  align-items: center;
  padding: 12px 0;
  color: #172033;
  border-bottom: 1px solid rgba(15, 23, 42, 0.06);
}

.category-rail a:last-child {
  border-bottom: none;
}

.category-rail a > span,
.daily-item > span,
.event-icon {
  width: 36px;
  height: 36px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: #fff3df;
  color: #c2410c;
  font-weight: 900;
}

.category-rail small,
.daily-item small,
.showcase-list small,
.section-title p,
.shop-card p,
.game-card p,
.event-card p,
.game-detail-hero p,
.play-window span {
  color: #64748b;
}

.hero-stage {
  min-height: 285px;
  display: grid;
  grid-template-columns: 1fr 240px;
  gap: 16px;
  align-items: center;
  overflow: hidden;
  padding: 30px;
  background:
    linear-gradient(135deg, rgba(255, 122, 26, 0.94), rgba(236, 72, 153, 0.78)),
    url('/photo/entertainment/hero-gaming.jpg') center / cover no-repeat;
  color: #fff;
}

.hero-stage h2 {
  color: #fff;
  font-size: 2rem;
}

.hero-stage p {
  max-width: 430px;
  margin: 10px 0 18px;
  color: rgba(255,255,255,0.86);
}

.promo-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.promo-actions a,
.primary-link,
.game-hero a {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  background: #fff;
  color: #172033;
  font-weight: 800;
  padding: 9px 15px;
}

.promo-actions a + a {
  background: rgba(255,255,255,0.18);
  color: #fff;
  border: 1px solid rgba(255,255,255,0.38);
}

.promo-visual {
  position: relative;
  min-height: 220px;
}

.visual-card {
  position: absolute;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 900;
  box-shadow: 0 22px 46px rgba(15, 23, 42, 0.22);
}

.visual-card.one {
  width: 142px;
  height: 142px;
  right: 34px;
  top: 6px;
  border-radius: 26px;
  background: linear-gradient(135deg, #16a34a, #06b6d4);
  font-size: 2.2rem;
}

.visual-card.two {
  width: 108px;
  height: 74px;
  right: 0;
  bottom: 20px;
  border-radius: 18px;
  background: linear-gradient(135deg, #0f172a, #6366f1);
}

.visual-card.three {
  width: 78px;
  height: 78px;
  left: 4px;
  bottom: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #facc15, #f97316);
  font-size: 1.8rem;
}

.daily-board {
  padding: 16px;
}

.daily-title,
.daily-item,
.price-line,
.shop-stats,
.shop-meta,
.event-top,
.detail-actions,
.dialog-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.daily-title {
  margin-bottom: 12px;
}

.daily-title strong {
  color: #ff7a1a;
}

.daily-item {
  justify-content: flex-start;
  padding: 10px 0;
  border-top: 1px solid rgba(15, 23, 42, 0.06);
}

.daily-item strong {
  display: block;
  color: #172033;
  line-height: 1.2;
}

.full-row {
  grid-column: 1 / -1;
}

.shelf-section {
  padding: 18px;
}

.section-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 14px;
}

.section-title h2 {
  font-size: 1.35rem;
}

.section-title p {
  margin: 4px 0 0;
}

.section-title > a {
  color: #c2410c;
  font-weight: 800;
  white-space: nowrap;
}

.product-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.product-card,
.shop-card,
.game-card,
.event-card {
  min-width: 0;
  overflow: hidden;
}

.product-card {
  border-radius: 10px;
  background: #fff;
  border: 1px solid rgba(15, 23, 42, 0.06);
}

.product-cover,
.shop-cover {
  width: 100%;
  border: none;
  color: #fff;
  cursor: pointer;
  text-align: left;
  font: inherit;
  font-weight: 900;
}

.image-cover {
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
}

.product-cover {
  height: 112px;
  padding: 12px;
}

.product-cover span,
.shop-cover span {
  display: inline-flex;
  border-radius: 999px;
  background: rgba(255,255,255,0.25);
  padding: 3px 9px;
}

.product-info {
  padding: 12px;
}

.product-info h3 {
  margin: 0;
  font-size: 1rem;
}

.product-info p {
  min-height: 42px;
  margin: 6px 0 10px;
  color: #64748b;
  font-size: 0.88rem;
}

.price-line {
  justify-content: flex-start;
}

.price-line strong,
.shop-meta strong {
  color: #e11d48;
  font-size: 1.25rem;
}

.price-line small {
  margin-left: auto;
  color: #94a3b8;
}

.game-showcase {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 18px;
  padding: 22px;
  background:
    linear-gradient(135deg, rgba(15, 23, 42, 0.96), rgba(30, 41, 59, 0.92)),
    radial-gradient(circle at 80% 20%, rgba(59, 130, 246, 0.5), transparent 34%);
}

.showcase-main h2 {
  color: #fff;
  font-size: 1.8rem;
}

.showcase-main p {
  color: #cbd5e1;
}

.showcase-list {
  display: grid;
  gap: 10px;
}

.showcase-list a {
  display: grid;
  grid-template-columns: 72px 1fr;
  gap: 12px;
  align-items: center;
  padding: 10px;
  border-radius: 10px;
  background: rgba(255,255,255,0.08);
  color: #fff;
}

.mini-cover,
.game-art {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 900;
}

.mini-cover {
  height: 52px;
  border-radius: 9px;
}

.mall-layout {
  display: grid;
  grid-template-columns: 190px minmax(0, 1fr);
  gap: 16px;
}

.wide-title {
  grid-column: 1 / -1;
  margin-bottom: 0;
}

.filter-chips,
.game-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-chips button,
.game-tags span {
  border: none;
  border-radius: 999px;
  background: #fff;
  color: #475569;
  font: inherit;
  font-size: 0.82rem;
  font-weight: 700;
  padding: 6px 11px;
}

.filter-chips .active,
.mall-side button.active {
  background: #ff7a1a;
  color: #fff;
}

.mall-side {
  align-self: start;
  padding: 14px;
}

.mall-side h3 {
  margin-bottom: 10px;
}

.mall-side button {
  width: 100%;
  display: block;
  border: none;
  border-radius: 9px;
  background: transparent;
  color: #475569;
  cursor: pointer;
  font: inherit;
  text-align: left;
  padding: 9px 10px;
}

.mall-side button:hover {
  background: #fff3df;
  color: #c2410c;
  font-weight: 800;
}

.mall-side button.active:hover {
  background: #ff7a1a;
  color: #fff;
}

.shop-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.empty-state {
  min-height: 260px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: 1px dashed rgba(15, 23, 42, 0.16);
  border-radius: 12px;
  background: rgba(255,255,255,0.7);
  color: #64748b;
}

.empty-state strong {
  color: #172033;
}

.shop-cover {
  height: 138px;
  padding: 12px;
}

.shop-body {
  padding: 14px;
}

.shop-body p {
  min-height: 46px;
  margin: 7px 0 10px;
}

.shop-stats {
  justify-content: flex-start;
  color: #94a3b8;
  font-size: 0.78rem;
  margin-bottom: 10px;
}

.shop-meta button,
.event-card button,
.detail-actions button,
.play-btn,
.play-window button {
  border: none;
  border-radius: 9px;
  background: #172033;
  color: #fff;
  cursor: pointer;
  font: inherit;
  font-weight: 800;
  padding: 8px 13px;
}

.play-window button:hover,
.shop-meta button:hover,
.event-card button:not(:disabled):hover,
.detail-actions button:hover,
.play-btn:hover {
  background: #0f172a;
}

.game-store {
  display: grid;
  gap: 16px;
}

.game-hero {
  min-height: 200px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 18px;
  padding: 24px;
  color: #fff;
  background:
    linear-gradient(135deg, rgba(15, 23, 42, 0.94), rgba(37, 99, 235, 0.78)),
    url('/photo/entertainment/hero-gaming.jpg') center / cover no-repeat;
}

.game-hero h2 {
  color: #fff;
  font-size: 2rem;
}

.game-hero p {
  color: #dbeafe;
}

.game-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.game-card {
  display: flex;
  flex-direction: column;
}

.game-art {
  height: 150px;
  font-size: 1.8rem;
}

.game-info {
  padding: 14px;
}

.game-info > span {
  color: #ff7a1a;
  font-size: 0.78rem;
  font-weight: 800;
}

.game-info h3 {
  margin: 4px 0 6px;
}

.play-btn {
  margin: auto 14px 14px;
  text-align: center;
}

.game-detail {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.back-link {
  width: fit-content;
  color: #c2410c;
  font-weight: 800;
}

.game-detail-hero {
  display: grid;
  grid-template-columns: 180px minmax(0, 1fr) 140px;
  gap: 18px;
  align-items: center;
  padding: 22px;
  border-radius: 14px;
  color: #fff;
}

.game-detail-hero h2,
.game-detail-hero p {
  color: #fff;
}

.game-art-large {
  width: 180px;
  height: 135px;
  border-radius: 12px;
  font-size: 2rem;
  box-shadow: 0 24px 42px rgba(15, 23, 42, 0.22);
}

.detail-score {
  justify-self: end;
  min-width: 120px;
  padding: 14px;
  border-radius: 12px;
  background: rgba(255,255,255,0.18);
}

.detail-score span,
.detail-score small {
  display: block;
  color: rgba(255,255,255,0.78);
}

.detail-score strong {
  display: block;
  font-size: 2rem;
  color: #fff;
}

.playground {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 16px;
}

.play-window,
.rank-panel {
  padding: 18px;
}

.play-window {
  min-height: 220px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 12px;
}

.play-window strong {
  color: #172033;
  font-size: 1.25rem;
}

.rank-panel ol {
  margin: 0;
  padding: 0;
}

.rank-panel li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid rgba(15, 23, 42, 0.08);
}

.rank-panel li:last-child {
  border-bottom: none;
}

.rank-panel strong {
  color: #16a34a;
}

.event-layout {
  display: grid;
  gap: 16px;
}

.event-hero {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  background: linear-gradient(135deg, #fff, #eef2ff);
}

.event-hero strong {
  border-radius: 999px;
  background: #172033;
  color: #fff;
  padding: 8px 13px;
  white-space: nowrap;
}

.event-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.event-card {
  padding: 18px;
}

.event-reward {
  color: #e11d48;
  font-weight: 900;
}

.event-card p {
  min-height: 58px;
}

.event-card button {
  width: 100%;
}

.event-card button:disabled {
  background: #e2e8f0;
  color: #94a3b8;
  cursor: default;
}

.detail-mask {
  position: fixed;
  inset: 0;
  z-index: 300;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: rgba(15, 23, 42, 0.52);
}

.detail-dialog {
  position: relative;
  width: min(440px, 100%);
  padding: 18px;
  background: #fff;
}

.dialog-close {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: rgba(255,255,255,0.86);
  cursor: pointer;
  font-size: 1.1rem;
}

.detail-cover {
  height: 150px;
  border-radius: 10px;
  margin-bottom: 14px;
}

.detail-dialog h2 {
  margin: 10px 0 6px;
}

.detail-dialog p {
  color: #64748b;
}

.dialog-meta {
  justify-content: flex-start;
  color: #94a3b8;
  font-size: 0.84rem;
  margin: 10px 0 14px;
}

.detail-actions span {
  color: #e11d48;
  font-weight: 900;
}

@media (max-width: 980px) {
  .entertainment-topbar,
  .home-layout,
  .mall-layout,
  .game-showcase,
  .playground {
    grid-template-columns: 1fr;
  }

  .wallet-strip {
    width: 100%;
    min-width: 0;
  }

  .category-rail {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 8px 14px;
  }

  .category-rail h2 {
    grid-column: 1 / -1;
  }

  .product-row,
  .shop-grid,
  .game-grid,
  .event-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .game-detail-hero {
    grid-template-columns: 150px minmax(0, 1fr);
  }

  .detail-score {
    grid-column: 1 / -1;
    justify-self: stretch;
  }
}

@media (max-width: 640px) {
  .entertainment-shell {
    width: min(100% - 24px, 1180px);
    padding-top: 20px;
    padding-bottom: 96px;
  }

  .brand-block h1,
  .hero-stage h2,
  .game-hero h2 {
    font-size: 1.65rem;
  }

  .wallet-strip {
    grid-template-columns: 1fr;
    gap: 2px;
    overflow: hidden;
  }

  .wallet-strip button {
    display: none;
  }

  .wallet-strip strong {
    font-size: 1.35rem;
  }

  .hero-stage,
  .game-hero,
  .event-hero,
  .section-title,
  .wide-title,
  .game-detail-hero {
    grid-template-columns: 1fr;
  }

  .hero-stage,
  .game-hero {
    padding: 20px;
  }

  .promo-visual {
    min-height: 150px;
  }

  .visual-card.one {
    right: 60px;
  }

  .category-rail,
  .product-row,
  .shop-grid,
  .game-grid,
  .event-grid {
    grid-template-columns: 1fr;
  }

  .filter-chips {
    width: 100%;
    overflow-x: auto;
    flex-wrap: nowrap;
    padding-bottom: 2px;
  }

  .mall-side {
    display: flex;
    gap: 8px;
    overflow-x: auto;
  }

  .mall-side h3 {
    display: none;
  }

  .mall-side button {
    width: auto;
    white-space: nowrap;
  }

  .game-art-large {
    width: 100%;
  }
}

.toast-bar {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  z-index: 9999;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}
.toast-info { background: #1e293b; color: #e2e8f0; }
.toast-warn { background: #f59e0b; color: #0f172a; }
.toast-error { background: #ef4444; color: #fff; }
.toast-fade-enter-active, .toast-fade-leave-active { transition: opacity 0.3s ease; }
.toast-fade-enter-from, .toast-fade-leave-to { opacity: 0; }
</style>
