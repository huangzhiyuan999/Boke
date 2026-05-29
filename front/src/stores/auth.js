import { ref, computed } from 'vue'
import { api } from '../utils/api.js'

export const currentUser = ref(null)
export const isLoggedIn = computed(() => currentUser.value !== null)

function loadFromStorage() {
  const u = localStorage.getItem('user')
  if (u) {
    try { currentUser.value = JSON.parse(u) } catch {}
  }
}

export async function login(username, password) {
  const data = await api.post('/auth/login', { username, password })
  localStorage.setItem('token', data.token)
  const user = { id: data.userId, username: data.username, role: data.role }
  localStorage.setItem('user', JSON.stringify(user))
  currentUser.value = user
  return data
}

export function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  currentUser.value = null
}

export async function fetchMe() {
  try {
    const data = await api.get('/auth/me')
    currentUser.value = { id: data.id, username: data.username, role: data.role }
    localStorage.setItem('user', JSON.stringify(currentUser.value))
  } catch {
    logout()
  }
}

export function requireAuth(router) {
  if (!isLoggedIn.value) {
    router.push('/login?msg=请先登录')
    return false
  }
  return true
}

// Init
loadFromStorage()
