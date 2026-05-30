<template>
  <div class="nav-hotzone" @mouseenter="showNav"></div>
  <header
    class="navbar"
    :class="{ visible: navVisible, scrolled }"
    @mouseleave="scheduleHide"
    @mouseenter="showNav"
  >
    <div class="navbar-inner container">
      <div class="logo-area">
        <SpinningRecord class="logo-record" />
        <router-link to="/" class="logo">
          <span class="logo-text">{{ siteName }}</span>
        </router-link>
      </div>
      <nav class="nav-links">
        <router-link to="/" active-class="active"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg> 首页</router-link>
        <router-link to="/articles" active-class="active"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"/><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"/></svg> 文章</router-link>
        <router-link to="/recommend" active-class="active"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg> 推荐</router-link>
        <router-link to="/following" active-class="active"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><line x1="19" y1="8" x2="19" y2="14"/><line x1="22" y1="11" x2="16" y2="11"/></svg> 关注</router-link>
        <router-link to="/about" active-class="active"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 16v-4"/><path d="M12 8h.01"/></svg> 关于我</router-link>
        <router-link to="/messages" active-class="active"><svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/></svg> 留言板</router-link>
        <router-link v-if="!isLoggedIn" to="/login" class="nav-btn-login">登录</router-link>
        <div v-else class="nav-user" @click.stop="showDropdown = !showDropdown">
          <span class="user-avatar">{{ avatarLetter }}</span>
          <span class="user-name">{{ displayName }}</span>
          <div class="user-dropdown" v-if="showDropdown" @click.stop>
            <button @click="goToAdmin" v-if="isAdmin">后台管理</button>
            <button @click="handleLogout">退出登录</button>
          </div>
        </div>
      </nav>
      <button class="menu-btn" @click="menuOpen = !menuOpen" aria-label="菜单">
        <span></span><span></span><span></span>
      </button>
    </div>
    <transition name="slide">
      <nav v-if="menuOpen" class="mobile-nav" @click="menuOpen = false">
        <router-link to="/">首页</router-link>
        <router-link to="/articles">文章</router-link>
        <router-link to="/recommend">推荐</router-link>
        <router-link to="/following">关注</router-link>
        <router-link to="/about">关于我</router-link>
        <router-link to="/messages">留言板</router-link>
        <router-link v-if="!isLoggedIn" to="/login">登录</router-link>
        <a v-else href="#" @click.prevent="handleLogout">退出登录 ({{ displayName }})</a>
      </nav>
    </transition>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { currentUser, isLoggedIn, logout } from '../stores/auth.js'
import { api } from '../utils/api.js'
import SpinningRecord from './SpinningRecord.vue'

const router = useRouter()
const menuOpen = ref(false)
const scrolled = ref(false)
const showDropdown = ref(false)
const navVisible = ref(false)
const siteName = ref('笔墨之间')

const displayName = computed(() => currentUser.value?.username || '')
const avatarLetter = computed(() => currentUser.value ? currentUser.value.username[0].toUpperCase() : '')
const isAdmin = computed(() => currentUser.value?.role === 'admin')

let hideTimer = null
let scrollHandler
let clickHandler

function showNav() {
  clearTimeout(hideTimer)
  navVisible.value = true
}

function scheduleHide() {
  hideTimer = setTimeout(() => { navVisible.value = false }, 500)
}

function handleLogout() {
  logout()
  showDropdown.value = false
  router.push('/')
}

function goToAdmin() {
  showDropdown.value = false
  router.push('/admin')
}

onMounted(async () => {
  scrollHandler = () => { scrolled.value = window.scrollY > 20 }
  window.addEventListener('scroll', scrollHandler, { passive: true })
  clickHandler = () => { showDropdown.value = false }
  window.addEventListener('click', clickHandler)

  try {
    const config = await api.get('/config')
    if (config && config.site_name) siteName.value = config.site_name
  } catch (e) {
    console.error('Failed to load config:', e)
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', scrollHandler)
  window.removeEventListener('click', clickHandler)
  clearTimeout(hideTimer)
})
</script>

<style scoped>
.nav-hotzone { position: fixed; top: 0; left: 0; right: 0; height: 45px; z-index: 99; }

.navbar {
  position: fixed; top: 0; left: 0; right: 0; z-index: 100;
  background: rgba(255, 255, 255, 0.25); backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px);
  transform: translateY(-100%); transition: transform 0.35s, box-shadow 0.3s ease;
}

.navbar.visible { transform: translateY(0); }
.navbar.scrolled { box-shadow: 0 1px 6px rgba(0, 0, 0, 0.08); }

.navbar-inner {
  display: flex; align-items: center; justify-content: space-between;
  height: 60px; position: relative; padding-left: 80px; padding-right: 0; max-width: none;
}

.logo-area { display: flex; align-items: center; gap: 12px; }

.logo-record { position: fixed; left: 8px; top: 8px; z-index: 101; width: 48px; height: 48px; }

.logo { display: flex; align-items: center; color: var(--color-text); font-size: 1.15rem; font-weight: 700; margin-left: 48px; }
.logo:hover { color: var(--color-primary); }

.nav-links { display: flex; gap: 24px; align-items: center; margin-left: auto; }

.nav-links a {
  display: flex; align-items: center; gap: 4px; color: var(--color-text-secondary);
  font-size: 0.92rem; font-weight: 500; position: relative; padding: 4px 0; transition: color 0.2s;
}

.nav-links a::after {
  content: ''; position: absolute; bottom: -2px; left: 0; width: 0; height: 2px;
  background: var(--color-primary); border-radius: 1px; transition: width 0.25s ease;
}

.nav-links a:hover, .nav-links a.active { color: var(--color-primary); }
.nav-links a:hover::after, .nav-links a.active::after { width: 100%; }

.nav-btn-login {
  background: var(--color-primary) !important; color: #fff !important;
  padding: 6px 18px !important; border-radius: 20px;
  transition: background 0.2s, transform 0.2s !important;
}

.nav-btn-login::after { display: none !important; }
.nav-btn-login:hover { background: var(--color-primary-dark) !important; color: #fff !important; transform: translateY(-1px); }

.nav-user {
  display: flex; align-items: center; gap: 6px; cursor: pointer; position: relative;
  padding: 4px 8px; border-radius: 20px; background: var(--color-bg); transition: background 0.2s;
}

.nav-user:hover { background: var(--color-border); }

.user-avatar {
  width: 28px; height: 28px; border-radius: 50%;
  background: linear-gradient(135deg, var(--color-primary), var(--color-primary-light));
  color: #fff; font-size: 0.78rem; font-weight: 700;
  display: flex; align-items: center; justify-content: center;
}

.user-name { font-size: 0.88rem; font-weight: 500; color: var(--color-text); }

.user-dropdown {
  position: absolute; top: calc(100% + 8px); right: 0;
  background: var(--color-surface); border-radius: var(--radius-sm);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1); overflow: hidden; z-index: 200; min-width: 120px;
}

.user-dropdown button {
  display: block; width: 100%; padding: 10px 20px; border: none; background: none;
  font-family: inherit; font-size: 0.85rem; cursor: pointer;
  color: var(--color-text-secondary); white-space: nowrap; transition: background 0.15s;
}

.user-dropdown button:hover { background: var(--color-bg); color: #ef4444; }

.menu-btn { display: none; flex-direction: column; gap: 5px; background: none; border: none; cursor: pointer; padding: 4px; }

.menu-btn span { display: block; width: 22px; height: 2px; background: var(--color-text); border-radius: 1px; transition: transform 0.2s, opacity 0.2s; }

.mobile-nav { display: none; flex-direction: column; padding: 8px 24px 16px; background: var(--color-surface); border-top: 1px solid var(--color-border); gap: 4px; }

.mobile-nav a { display: block; padding: 10px 0; color: var(--color-text-secondary); font-size: 1rem; border-bottom: 1px solid var(--color-border); }
.mobile-nav a:last-child { border-bottom: none; }

.slide-enter-active, .slide-leave-active { transition: opacity 0.2s, transform 0.2s; }
.slide-enter-from, .slide-leave-to { opacity: 0; transform: translateY(-8px); }

@media (max-width: 640px) {
  .navbar-inner { padding-left: 64px; }
  .nav-links { display: none; }
  .menu-btn { display: flex; }
  .mobile-nav { display: flex; }
}
</style>
