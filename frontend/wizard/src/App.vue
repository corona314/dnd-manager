<template>
  <div id="app">
    <!-- Auth Screen -->
    <transition name="fade">
      <div v-if="!authToken" class="auth-screen">
        <div class="auth-card">
          <div class="crest">⚔️</div>
          <h1 class="title-main">Grimorio del Aventurero</h1>
          <p class="subtitle">Accede a tu colección de héroes</p>

          <div class="tab-switch">
            <button :class="['tab-btn', { active: authMode === 'login' }]" @click="authMode = 'login'">Entrar</button>
            <button :class="['tab-btn', { active: authMode === 'register' }]" @click="authMode = 'register'">Registrarse</button>
          </div>

          <div class="form-group">
            <label>Usuario</label>
            <input v-model="authForm.username" type="text" placeholder="nombre de aventurero" @keyup.enter="submitAuth" />
          </div>
          <div class="form-group">
            <label>Contraseña</label>
            <input v-model="authForm.password" type="password" placeholder="••••••••" @keyup.enter="submitAuth" />
          </div>

          <div v-if="authError" class="error-msg">{{ authError }}</div>

          <button class="btn-primary" :disabled="authLoading" @click="submitAuth">
            <span v-if="authLoading" class="spinner"></span>
            <span v-else>{{ authMode === 'login' ? 'Comenzar Aventura' : 'Unirse al Gremio' }}</span>
          </button>
        </div>
      </div>
    </transition>

    <!-- Main App -->
    <transition name="fade">
      <div v-if="authToken" class="main-layout">

        <!-- Sidebar -->
        <aside class="sidebar">
          <div class="sidebar-header">
            <span class="crest-small">⚔️</span>
            <span class="brand">Grimorio</span>
          </div>

          <nav class="sidebar-nav">
            <button
              v-for="char in characters"
              :key="char.id"
              :class="['char-nav-btn', { active: selectedId === char.id }]"
              @click="selectCharacter(char.id)"
            >
              <span class="char-class-icon">🧙</span>
              <div class="char-nav-info">
                <span class="char-nav-name">{{ char.name }}</span>
                <span class="char-nav-meta">Nv {{ char.level }} · {{ char.status }}</span>
              </div>
            </button>

            <button v-if="characters.length === 0 && !listLoading" class="char-nav-empty">
              Sin personajes aún
            </button>
          </nav>

          <div class="sidebar-footer">
            <button class="btn-new" @click="openCreate">+ Nuevo Personaje</button>
            <button class="btn-logout" @click="logout">Salir</button>
          </div>
        </aside>

        <!-- Content Area -->
        <main class="content">

          <!-- Empty state -->
          <div v-if="!selectedId && !showCreate" class="empty-state">
            <div class="empty-icon">📜</div>
            <h2>Tu grimorio está vacío</h2>
            <p>Crea tu primer personaje para comenzar la aventura</p>
            <button class="btn-primary" @click="openCreate">Forjar Personaje</button>
          </div>

          <!-- Create Form -->
          <transition name="slide-up">
            <div v-if="showCreate" class="form-panel">
              <div class="form-panel-header">
                <h2>Forjar Nuevo Personaje</h2>
                <button class="btn-close" @click="closeForm">✕</button>
              </div>

              <div class="form-grid">
                <div class="form-group full-width">
                  <label>Nombre *</label>
                  <input v-model="createForm.name" type="text" placeholder="Aragorn de Gondor" />
                </div>
                <div class="form-group">
                  <label>Nivel *</label>
                  <input v-model.number="createForm.level" type="number" min="1" max="20" placeholder="1" />
                </div>
              </div>

              <div class="form-actions">
                <div v-if="formError" class="error-msg">{{ formError }}</div>
                <button class="btn-secondary" @click="closeForm">Cancelar</button>
                <button class="btn-primary" :disabled="formLoading" @click="submitCreate">
                  <span v-if="formLoading" class="spinner"></span>
                  <span v-else>Crear Personaje</span>
                </button>
              </div>
            </div>
          </transition>

          <!-- Edit Form -->
          <transition name="slide-up">
            <div v-if="showEdit" class="form-panel">
              <div class="form-panel-header">
                <h2>Editar Personaje</h2>
                <button class="btn-close" @click="closeForm">✕</button>
              </div>

              <div class="form-grid">
                <div class="form-group full-width">
                  <label>Nombre</label>
                  <input v-model="editForm.name" type="text" placeholder="Aragorn de Gondor" />
                </div>
                <div class="form-group">
                  <label>Nivel</label>
                  <input v-model.number="editForm.level" type="number" min="1" max="20" />
                </div>
                <div class="form-group">
                  <label>PV Máximos</label>
                  <input v-model.number="editForm.maxHp" type="number" min="1" />
                </div>
                <div class="form-group">
                  <label>PV Actuales</label>
                  <input v-model.number="editForm.currentHp" type="number" min="0" />
                </div>
                <div class="form-group">
                  <label>Velocidad (pie)</label>
                  <input v-model.number="editForm.walkSpeed" type="number" min="0" />
                </div>
                <div class="form-group">
                  <label>Velocidad (vuelo)</label>
                  <input v-model.number="editForm.flySpeed" type="number" min="0" />
                </div>
              </div>

              <div class="form-actions">
                <div v-if="formError" class="error-msg">{{ formError }}</div>
                <button class="btn-secondary" @click="closeForm">Cancelar</button>
                <button class="btn-primary" :disabled="formLoading" @click="submitEdit">
                  <span v-if="formLoading" class="spinner"></span>
                  <span v-else>Guardar Cambios</span>
                </button>
              </div>
            </div>
          </transition>

          <!-- Character Detail -->
          <transition name="slide-up">
            <div v-if="selectedChar && !showEdit && !showCreate" class="char-detail">

              <div class="char-detail-header">
                <div class="char-portrait">🧙</div>
                <div class="char-title-block">
                  <h1 class="char-name">{{ selectedChar.name }}</h1>
                  <p class="char-subtitle">Nivel {{ selectedChar.level }} · {{ selectedChar.status }}</p>
                </div>
                <div class="char-actions">
                  <button class="btn-icon" title="Editar" @click="openEdit">✏️</button>
                  <button class="btn-icon danger" title="Eliminar" @click="confirmDelete">🗑️</button>
                </div>
              </div>

              <div class="stats-grid">
                <div class="stat-card">
                  <span class="stat-label">❤️ PV</span>
                  <span class="stat-value">{{ selectedChar.currentHp ?? '—' }}</span>
                  <span class="stat-sub" v-if="selectedChar.maxHp">/ {{ selectedChar.maxHp }}</span>
                </div>
                <div class="stat-card">
                  <span class="stat-label">⚡ Nivel</span>
                  <span class="stat-value">{{ selectedChar.level }}</span>
                </div>
                <div class="stat-card">
                  <span class="stat-label">🏃 Velocidad</span>
                  <span class="stat-value">{{ selectedChar.walkSpeed ?? '—' }}</span>
                  <span class="stat-sub" v-if="selectedChar.walkSpeed">ft</span>
                </div>
                <div class="stat-card">
                  <span class="stat-label">🦅 Vuelo</span>
                  <span class="stat-value">{{ selectedChar.flySpeed ?? '—' }}</span>
                  <span class="stat-sub" v-if="selectedChar.flySpeed">ft</span>
                </div>
              </div>

              <div v-if="selectedChar.stats && selectedChar.stats.length" class="abilities-section">
                <h3>📊 Estadísticas</h3>
                <div class="abilities-grid">
                  <div v-for="stat in selectedChar.stats" :key="stat.statId" class="ability-card">
                    <span class="ability-name">{{ stat.statId }}</span>
                    <span class="ability-score">{{ stat.baseValue }}</span>
                  </div>
                </div>
              </div>

            </div>
          </transition>

        </main>
      </div>
    </transition>

    <!-- Delete Confirm Modal -->
    <transition name="fade">
      <div v-if="showDeleteModal" class="modal-overlay" @click.self="showDeleteModal = false">
        <div class="modal-card">
          <h3>¿Eliminar personaje?</h3>
          <p>Esta acción no se puede deshacer. <strong>{{ selectedChar?.name }}</strong> será borrado para siempre.</p>
          <div class="modal-actions">
            <button class="btn-secondary" @click="showDeleteModal = false">Cancelar</button>
            <button class="btn-danger" :disabled="deleteLoading" @click="deleteCharacter">
              <span v-if="deleteLoading" class="spinner"></span>
              <span v-else>Eliminar</span>
            </button>
          </div>
        </div>
      </div>
    </transition>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const API_BASE = 'http://localhost:8080/api'

// ── Auth ─────────────────────────────────────────────────
const authToken = ref(localStorage.getItem('dnd_token') || '')
const authMode = ref('login')
const authForm = ref({ username: '', password: '' })
const authError = ref('')
const authLoading = ref(false)

// ── Characters ────────────────────────────────────────────
const characters = ref([])
const listLoading = ref(false)
const selectedId = ref(null)
const selectedChar = ref(null)

// ── Forms ─────────────────────────────────────────────────
const showCreate = ref(false)
const showEdit = ref(false)
const createForm = ref({ name: '', level: 1 })
const editForm = ref({})
const formLoading = ref(false)
const formError = ref('')

// ── Delete ────────────────────────────────────────────────
const showDeleteModal = ref(false)
const deleteLoading = ref(false)

// ── Helpers ───────────────────────────────────────────────
function authHeader() {
  return { Authorization: `Bearer ${authToken.value}`, 'Content-Type': 'application/json' }
}

// Limpia el objeto de campos nulos/vacíos antes de enviar
function cleanPayload(obj) {
  return Object.fromEntries(
    Object.entries(obj).filter(([, v]) => v !== '' && v !== null && v !== undefined)
  )
}

// ── Auth ──────────────────────────────────────────────────
async function submitAuth() {
  authError.value = ''
  authLoading.value = true
  try {
    const endpoint = authMode.value === 'login' ? '/auth/login' : '/auth/register'
    const res = await fetch(`${API_BASE}${endpoint}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(authForm.value)
    })
    if (!res.ok) {
      const err = await res.json().catch(() => ({}))
      throw new Error(err.message || 'Error de autenticación')
    }
    const data = await res.json()
    authToken.value = data.token
    localStorage.setItem('dnd_token', data.token)
    await loadCharacters()
  } catch (e) {
    authError.value = e.message
  } finally {
    authLoading.value = false
  }
}

function logout() {
  authToken.value = ''
  localStorage.removeItem('dnd_token')
  characters.value = []
  selectedId.value = null
  selectedChar.value = null
  showCreate.value = false
  showEdit.value = false
}

// ── Characters API ────────────────────────────────────────
async function loadCharacters() {
  listLoading.value = true
  try {
    const res = await fetch(`${API_BASE}/characters/me`, { headers: authHeader() })
    if (res.status === 401) { logout(); return }
    characters.value = await res.json()
  } catch (e) {
    console.error(e)
  } finally {
    listLoading.value = false
  }
}

async function selectCharacter(id) {
  selectedId.value = id
  showCreate.value = false
  showEdit.value = false
  try {
    const res = await fetch(`${API_BASE}/characters/${id}`, { headers: authHeader() })
    selectedChar.value = await res.json()
  } catch (e) {
    console.error(e)
  }
}

// ── Create ────────────────────────────────────────────────
function openCreate() {
  createForm.value = { name: '', level: 1 }
  showCreate.value = true
  showEdit.value = false
  selectedId.value = null
  selectedChar.value = null
  formError.value = ''
}

async function submitCreate() {
  formError.value = ''
  if (!createForm.value.name) { formError.value = 'El nombre es obligatorio'; return }
  if (!createForm.value.level) { formError.value = 'El nivel es obligatorio'; return }
  formLoading.value = true
  try {
    const res = await fetch(`${API_BASE}/characters`, {
      method: 'POST',
      headers: authHeader(),
      body: JSON.stringify(createForm.value)
    })
    if (!res.ok) {
      const err = await res.json().catch(() => ({}))
      throw new Error(err.message || 'Error al crear')
    }
    const created = await res.json()
    await loadCharacters()
    await selectCharacter(created.id)
    showCreate.value = false
  } catch (e) {
    formError.value = e.message
  } finally {
    formLoading.value = false
  }
}

// ── Edit ──────────────────────────────────────────────────
function openEdit() {
  if (!selectedChar.value) return
  editForm.value = {
    name: selectedChar.value.name,
    level: selectedChar.value.level,
    maxHp: selectedChar.value.maxHp ?? null,
    currentHp: selectedChar.value.currentHp ?? null,
    walkSpeed: selectedChar.value.walkSpeed ?? null,
    flySpeed: selectedChar.value.flySpeed ?? null,
  }
  showEdit.value = true
  showCreate.value = false
  formError.value = ''
}

async function submitEdit() {
  formError.value = ''
  formLoading.value = true
  try {
    const res = await fetch(`${API_BASE}/characters/${selectedId.value}`, {
      method: 'PATCH',
      headers: authHeader(),
      body: JSON.stringify(cleanPayload(editForm.value))
    })
    if (!res.ok) {
      const err = await res.json().catch(() => ({}))
      throw new Error(err.message || 'Error al guardar')
    }
    selectedChar.value = await res.json()
    const idx = characters.value.findIndex(c => c.id === selectedId.value)
    if (idx !== -1) characters.value[idx] = { ...characters.value[idx], ...selectedChar.value }
    showEdit.value = false
  } catch (e) {
    formError.value = e.message
  } finally {
    formLoading.value = false
  }
}

function closeForm() {
  showCreate.value = false
  showEdit.value = false
  formError.value = ''
}

// ── Delete ────────────────────────────────────────────────
function confirmDelete() { showDeleteModal.value = true }

async function deleteCharacter() {
  deleteLoading.value = true
  try {
    await fetch(`${API_BASE}/characters/${selectedId.value}`, {
      method: 'DELETE',
      headers: authHeader()
    })
    characters.value = characters.value.filter(c => c.id !== selectedId.value)
    selectedId.value = null
    selectedChar.value = null
    showDeleteModal.value = false
  } catch (e) {
    console.error(e)
  } finally {
    deleteLoading.value = false
  }
}

onMounted(() => {
  if (authToken.value) loadCharacters()
})
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600;700&family=Crimson+Pro:ital,wght@0,300;0,400;0,600;1,400&display=swap');

*, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

:root {
  --bg:        #0d0b08;
  --bg2:       #16120e;
  --bg3:       #1e1812;
  --border:    #3a2e1e;
  --gold:      #c9a84c;
  --gold-dim:  #8a6e2f;
  --red:       #8b2020;
  --red-bright:#c0392b;
  --text:      #e8dcc8;
  --text-dim:  #9a8870;
  --radius:    8px;
  --shadow:    0 4px 24px rgba(0,0,0,.6);
}

body {
  background: var(--bg);
  color: var(--text);
  font-family: 'Crimson Pro', Georgia, serif;
  font-size: 16px;
  line-height: 1.6;
  min-height: 100vh;
}

#app { min-height: 100vh; }

.fade-enter-active, .fade-leave-active { transition: opacity .35s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.slide-up-enter-active { transition: all .3s ease; }
.slide-up-enter-from { opacity: 0; transform: translateY(16px); }

/* ── Auth ── */
.auth-screen {
  min-height: 100vh;
  display: flex; align-items: center; justify-content: center;
  background: radial-gradient(ellipse at 50% 30%, #2a1f0e 0%, #0d0b08 70%);
}
.auth-card {
  background: var(--bg2);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 2.5rem 2rem;
  width: min(420px, 92vw);
  box-shadow: var(--shadow), 0 0 60px rgba(201,168,76,.08);
  display: flex; flex-direction: column; gap: 1.1rem;
}
.crest { font-size: 2.4rem; text-align: center; }
.title-main {
  font-family: 'Cinzel', serif;
  font-size: 1.6rem; font-weight: 700;
  color: var(--gold); text-align: center; letter-spacing: .04em;
}
.subtitle { text-align: center; color: var(--text-dim); font-size: .95rem; }

.tab-switch { display: flex; border: 1px solid var(--border); border-radius: var(--radius); overflow: hidden; }
.tab-btn { flex: 1; padding: .5rem; background: transparent; border: none; color: var(--text-dim); cursor: pointer; font-family: 'Cinzel', serif; font-size: .85rem; transition: all .2s; }
.tab-btn.active { background: var(--gold); color: #0d0b08; font-weight: 600; }

/* ── Form ── */
.form-group { display: flex; flex-direction: column; gap: .4rem; }
.form-group label { font-size: .82rem; color: var(--gold-dim); letter-spacing: .06em; text-transform: uppercase; font-family: 'Cinzel', serif; }
input, select, textarea {
  background: var(--bg3); border: 1px solid var(--border); border-radius: var(--radius);
  color: var(--text); padding: .6rem .85rem; font-family: 'Crimson Pro', serif;
  font-size: 1rem; width: 100%; transition: border-color .2s; outline: none;
}
input:focus, select:focus, textarea:focus { border-color: var(--gold-dim); }
input::placeholder, textarea::placeholder { color: var(--text-dim); }
select option { background: var(--bg2); }
textarea { resize: vertical; min-height: 90px; }

.error-msg {
  background: rgba(139,32,32,.2); border: 1px solid var(--red); color: #e07070;
  padding: .5rem .75rem; border-radius: var(--radius); font-size: .9rem;
}

/* ── Buttons ── */
.btn-primary {
  background: linear-gradient(135deg, #b8922a, var(--gold)); color: #0d0b08;
  border: none; border-radius: var(--radius); padding: .7rem 1.4rem;
  font-family: 'Cinzel', serif; font-weight: 600; font-size: .9rem;
  cursor: pointer; letter-spacing: .05em;
  display: flex; align-items: center; justify-content: center; gap: .5rem;
  transition: filter .2s, transform .1s;
}
.btn-primary:hover:not(:disabled) { filter: brightness(1.1); }
.btn-primary:active:not(:disabled) { transform: scale(.98); }
.btn-primary:disabled { opacity: .5; cursor: not-allowed; }

.btn-secondary {
  background: transparent; border: 1px solid var(--border); color: var(--text-dim);
  border-radius: var(--radius); padding: .65rem 1.2rem;
  font-family: 'Cinzel', serif; font-size: .85rem; cursor: pointer; transition: border-color .2s, color .2s;
}
.btn-secondary:hover { border-color: var(--gold-dim); color: var(--text); }

.btn-danger {
  background: var(--red); border: none; border-radius: var(--radius); color: #fff;
  padding: .65rem 1.2rem; font-family: 'Cinzel', serif; font-size: .85rem;
  cursor: pointer; transition: background .2s; display: flex; align-items: center; gap: .4rem;
}
.btn-danger:hover:not(:disabled) { background: var(--red-bright); }
.btn-danger:disabled { opacity: .5; cursor: not-allowed; }

.btn-icon {
  background: transparent; border: 1px solid var(--border); border-radius: var(--radius);
  width: 38px; height: 38px; cursor: pointer; font-size: 1.1rem;
  display: flex; align-items: center; justify-content: center; transition: background .2s, border-color .2s;
}
.btn-icon:hover { background: var(--bg3); border-color: var(--gold-dim); }
.btn-icon.danger:hover { background: rgba(139,32,32,.3); border-color: var(--red); }

.btn-new {
  width: 100%; background: transparent; border: 1px dashed var(--gold-dim); color: var(--gold);
  border-radius: var(--radius); padding: .55rem; font-family: 'Cinzel', serif; font-size: .82rem;
  cursor: pointer; transition: all .2s;
}
.btn-new:hover { background: rgba(201,168,76,.08); border-style: solid; }

.btn-logout {
  width: 100%; background: transparent; border: none; color: var(--text-dim); font-size: .82rem;
  cursor: pointer; padding: .4rem; transition: color .2s;
}
.btn-logout:hover { color: var(--text); }

.btn-close {
  background: transparent; border: none; color: var(--text-dim); font-size: 1.1rem;
  cursor: pointer; padding: .2rem .5rem; transition: color .2s; line-height: 1;
}
.btn-close:hover { color: var(--text); }

.spinner {
  width: 14px; height: 14px; border: 2px solid rgba(0,0,0,.3); border-top-color: #0d0b08;
  border-radius: 50%; animation: spin .6s linear infinite; display: inline-block;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* ── Layout ── */
.main-layout { display: flex; min-height: 100vh; }

.sidebar {
  width: 260px; min-width: 260px; background: var(--bg2); border-right: 1px solid var(--border);
  display: flex; flex-direction: column; position: sticky; top: 0; height: 100vh;
}
.sidebar-header { padding: 1.25rem 1rem; border-bottom: 1px solid var(--border); display: flex; align-items: center; gap: .6rem; }
.crest-small { font-size: 1.3rem; }
.brand { font-family: 'Cinzel', serif; font-weight: 700; font-size: 1rem; color: var(--gold); letter-spacing: .08em; }

.sidebar-nav { flex: 1; overflow-y: auto; padding: .75rem .6rem; display: flex; flex-direction: column; gap: .3rem; }
.sidebar-nav::-webkit-scrollbar { width: 4px; }
.sidebar-nav::-webkit-scrollbar-thumb { background: var(--border); border-radius: 2px; }

.char-nav-btn {
  width: 100%; text-align: left; background: transparent; border: 1px solid transparent;
  border-radius: var(--radius); padding: .6rem .75rem; cursor: pointer;
  display: flex; align-items: center; gap: .6rem; transition: all .15s; color: var(--text);
}
.char-nav-btn:hover { background: var(--bg3); border-color: var(--border); }
.char-nav-btn.active { background: rgba(201,168,76,.1); border-color: var(--gold-dim); }

.char-class-icon { font-size: 1.3rem; flex-shrink: 0; }
.char-nav-info { display: flex; flex-direction: column; min-width: 0; }
.char-nav-name { font-family: 'Cinzel', serif; font-size: .88rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.char-nav-meta { font-size: .78rem; color: var(--text-dim); }

.char-nav-empty {
  background: transparent; border: 1px dashed var(--border); border-radius: var(--radius);
  padding: .75rem; color: var(--text-dim); font-size: .88rem; text-align: center; cursor: default; width: 100%;
}
.sidebar-footer { padding: .75rem .6rem; border-top: 1px solid var(--border); display: flex; flex-direction: column; gap: .4rem; }

.content { flex: 1; overflow-y: auto; padding: 2rem; }

/* ── Empty state ── */
.empty-state { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 1rem; min-height: 70vh; text-align: center; }
.empty-icon { font-size: 4rem; }
.empty-state h2 { font-family: 'Cinzel', serif; color: var(--gold); font-size: 1.4rem; }
.empty-state p { color: var(--text-dim); }

/* ── Form panel ── */
.form-panel { max-width: 680px; margin: 0 auto; background: var(--bg2); border: 1px solid var(--border); border-radius: 12px; padding: 2rem; box-shadow: var(--shadow); }
.form-panel-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; }
.form-panel-header h2 { font-family: 'Cinzel', serif; color: var(--gold); font-size: 1.2rem; }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.form-grid .full-width { grid-column: 1 / -1; }
.form-actions { display: flex; justify-content: flex-end; align-items: center; gap: .75rem; margin-top: 1.5rem; flex-wrap: wrap; }

/* ── Character detail ── */
.char-detail { max-width: 740px; margin: 0 auto; }
.char-detail-header { display: flex; align-items: center; gap: 1.2rem; margin-bottom: 1.75rem; }
.char-portrait { width: 72px; height: 72px; background: var(--bg2); border: 2px solid var(--gold-dim); border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 2rem; flex-shrink: 0; }
.char-title-block { flex: 1; }
.char-name { font-family: 'Cinzel', serif; font-size: 2rem; color: var(--gold); line-height: 1.2; }
.char-subtitle { color: var(--text-dim); font-size: 1rem; margin-top: .2rem; }
.char-actions { display: flex; gap: .5rem; }

/* ── Stats ── */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: .75rem; margin-bottom: 1.75rem; }
.stat-card { background: var(--bg2); border: 1px solid var(--border); border-radius: var(--radius); padding: 1rem .75rem; display: flex; flex-direction: column; align-items: center; gap: .2rem; transition: border-color .2s; }
.stat-card:hover { border-color: var(--gold-dim); }
.stat-label { font-size: .8rem; color: var(--text-dim); letter-spacing: .05em; }
.stat-value { font-family: 'Cinzel', serif; font-size: 1.7rem; color: var(--gold); font-weight: 700; line-height: 1; }
.stat-sub { font-size: .78rem; color: var(--text-dim); }

/* ── Abilities ── */
.abilities-section { margin-bottom: 1.5rem; }
.abilities-section h3 { font-family: 'Cinzel', serif; color: var(--gold); font-size: .95rem; margin-bottom: .75rem; }
.abilities-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: .6rem; }
.ability-card { background: var(--bg2); border: 1px solid var(--border); border-radius: var(--radius); padding: .75rem .5rem; display: flex; flex-direction: column; align-items: center; gap: .15rem; transition: border-color .2s; }
.ability-card:hover { border-color: var(--gold-dim); }
.ability-name { font-size: .72rem; color: var(--text-dim); text-transform: uppercase; letter-spacing: .06em; }
.ability-score { font-family: 'Cinzel', serif; font-size: 1.4rem; color: var(--text); font-weight: 600; }

/* ── Modal ── */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,.7); display: flex; align-items: center; justify-content: center; z-index: 100; padding: 1rem; backdrop-filter: blur(2px); }
.modal-card { background: var(--bg2); border: 1px solid var(--border); border-radius: 12px; padding: 2rem; width: min(420px, 90vw); box-shadow: var(--shadow); }
.modal-card h3 { font-family: 'Cinzel', serif; color: var(--gold); font-size: 1.15rem; margin-bottom: .75rem; }
.modal-card p { color: var(--text-dim); font-size: .97rem; line-height: 1.6; }
.modal-actions { display: flex; justify-content: flex-end; gap: .75rem; margin-top: 1.5rem; }

/* ── Responsive ── */
@media (max-width: 640px) {
  .sidebar { width: 220px; min-width: 220px; }
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .abilities-grid { grid-template-columns: repeat(3, 1fr); }
  .form-grid { grid-template-columns: 1fr; }
  .form-grid .full-width { grid-column: 1; }
  .content { padding: 1rem; }
  .char-name { font-size: 1.4rem; }
}
@media (max-width: 480px) {
  .main-layout { flex-direction: column; }
  .sidebar { width: 100%; height: auto; position: static; }
  .sidebar-nav { max-height: 180px; }
}
</style>