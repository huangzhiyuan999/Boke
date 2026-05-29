const BASE = 'http://localhost:8080/api'

function getToken() {
  return localStorage.getItem('token')
}

async function request(method, path, body = null) {
  const headers = { 'Content-Type': 'application/json' }
  const token = getToken()
  if (token) headers['Authorization'] = `Bearer ${token}`

  const opts = { method, headers }
  if (body) opts.body = JSON.stringify(body)

  const res = await fetch(`${BASE}${path}`, opts)
  const data = await res.json()

  if (data.code !== 200) {
    if (data.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
    throw new Error(data.message || '请求失败')
  }
  return data.data
}

export const api = {
  get(path) { return request('GET', path) },
  post(path, body) { return request('POST', path, body) },
  put(path, body) { return request('PUT', path, body) },
  delete(path) { return request('DELETE', path) }
}
