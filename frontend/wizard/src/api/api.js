// api.js — Capa de servicio para el backend D&D Manager
const API_BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

function headers(token) {
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }
}

async function request(method, path, token, body) {
  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers: headers(token),
    ...(body !== undefined ? { body: JSON.stringify(body) } : {})
  })

  if (res.status === 204) return null
  const data = await res.json().catch(() => ({}))
  if (!res.ok) throw new Error(data.message ?? `HTTP ${res.status}`)
  return data
}

// ── Auth ─────────────────────────────────────────────────
export const authApi = {
  login:    (form) => request('POST', '/auth/login',    null, form),
  register: (form) => request('POST', '/auth/register', null, form),
}

// ── Characters ───────────────────────────────────────────
export const characterApi = {
  getAll:  (token)           => request('GET',    '/characters/me',    token),
  getById: (token, id)       => request('GET',    `/characters/${id}`, token),
  create:  (token, dto)      => request('POST',   '/characters',       token, dto),
  patch:   (token, id, dto)  => request('PATCH',  `/characters/${id}`, token, dto),
  delete:  (token, id)       => request('DELETE', `/characters/${id}`, token),
}