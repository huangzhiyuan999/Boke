import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'

function getAuth() {
  try { return JSON.parse(localStorage.getItem('user')) } catch { return null }
}

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/post/:id',
    name: 'Post',
    component: () => import('../views/Post.vue')
  },
  {
    path: '/articles',
    name: 'Articles',
    component: () => import('../views/Articles.vue')
  },
  {
    path: '/recommend',
    name: 'Recommend',
    component: () => import('../views/Recommend.vue')
  },
  {
    path: '/following',
    name: 'Following',
    component: () => import('../views/Following.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../views/About.vue')
  },
  {
    path: '/messages',
    name: 'Messages',
    component: () => import('../views/MessageBoard.vue')
  },
  {
    path: '/entertainment/:section?',
    name: 'Entertainment',
    component: () => import('../views/Entertainment.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin.vue')
  },
  {
    path: '/tag/:tag',
    name: 'Tag',
    component: Home
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const authRequired = ['Admin', 'Following'].includes(to.name)
  const user = getAuth()
  if (authRequired && !user) {
    next('/login?msg=请先登录')
  } else {
    next()
  }
})

export default router
