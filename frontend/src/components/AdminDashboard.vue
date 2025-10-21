<script setup>
import { ref, onMounted } from 'vue';
import axios from '../api/axios';

const metrics = ref(null);
const users = ref([]);
const transactions = ref([]);
const selectedUser = ref(null);
const userNotes = ref([]);
const newNote = ref('');
const loading = ref(true);

onMounted(async () => {
  await Promise.all([loadMetrics(), loadUsers(), loadTransactions()]);
  loading.value = false;
});

const loadMetrics = async () => {
  try {
    const response = await axios.get('/admin/dashboard');
    metrics.value = response.data;
  } catch (err) {
    console.error('Error loading metrics:', err);
  }
};

const loadUsers = async () => {
  try {
    const response = await axios.get('/admin/users');
    users.value = response.data;
  } catch (err) {
    console.error('Error loading users:', err);
  }
};

const loadTransactions = async () => {
  try {
    const response = await axios.get('/admin/transactions');
    transactions.value = response.data.slice(0, 20); // Last 20
  } catch (err) {
    console.error('Error loading transactions:', err);
  }
};

const toggleBlockUser = async (userId) => {
  try {
    await axios.put(`/admin/users/${userId}/toggle-block`);
    await loadUsers();
  } catch (err) {
    alert('Greška pri blokiranju korisnika');
  }
};

const viewUserNotes = async (user) => {
  selectedUser.value = user;
  try {
    const response = await axios.get(`/admin/users/${user.id}/notes`);
    userNotes.value = response.data;
  } catch (err) {
    console.error('Error loading notes:', err);
  }
};

const addNote = async () => {
  if (!newNote.value.trim()) return;

  try {
    await axios.post(`/admin/users/${selectedUser.value.id}/notes`, {
      note: newNote.value
    });
    newNote.value = '';
    await viewUserNotes(selectedUser.value);
  } catch (err) {
    alert('Greška pri dodavanju beleške');
  }
};

const closeNotesModal = () => {
  selectedUser.value = null;
  userNotes.value = [];
  newNote.value = '';
};
</script>

<template>
  <div class="admin-page">
    <div class="page-header">
      <h1>Admin Dashboard</h1>
      <span class="admin-badge">Administrator</span>
    </div>

    <div v-if="loading" class="loading">Učitavanje...</div>

    <div v-else>
      <!-- Metrics -->
      <div v-if="metrics" class="metrics-grid">
        <div class="metric-card">
          <div class="metric-icon">👥</div>
          <div class="metric-info">
            <h3>Ukupno korisnika</h3>
            <p class="metric-value">{{ metrics.totalUsers }}</p>
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-icon">✅</div>
          <div class="metric-info">
            <h3>Aktivni korisnici</h3>
            <p class="metric-value">{{ metrics.activeUsers }}</p>
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-icon">💰</div>
          <div class="metric-info">
            <h3>Ukupno stanje sistema</h3>
            <p class="metric-value">{{ parseFloat(metrics.totalSystemBalance).toFixed(2) }}</p>
          </div>
        </div>

        <div class="metric-card">
          <div class="metric-icon">📊</div>
          <div class="metric-info">
            <h3>Prosečno stanje</h3>
            <p class="metric-value">{{ parseFloat(metrics.averageWalletBalance).toFixed(2) }}</p>
          </div>
        </div>
      </div>

      <!-- Top Transactions -->
      <div class="section">
        <h2>🔝 Top 10 transakcija (30 dana)</h2>
        <div v-if="metrics?.top10Transactions30Days?.length > 0" class="transactions-list">
          <div v-for="(tx, idx) in metrics.top10Transactions30Days" :key="idx" class="transaction-item">
            <span class="tx-rank">{{ idx + 1 }}</span>
            <div class="tx-details">
              <strong>{{ tx.name }}</strong>
              <p>{{ tx.user }}</p>
            </div>
            <div class="tx-amount" :class="tx.type">
              {{ parseFloat(tx.amount).toFixed(2) }}
            </div>
          </div>
        </div>
        <p v-else class="empty">Nema transakcija u poslednjih 30 dana</p>
      </div>

      <div class="section">
        <h2>⚡ Top 10 transakcija (2 minuta)</h2>
        <div v-if="metrics?.top10Transactions2Minutes?.length > 0" class="transactions-list">
          <div v-for="(tx, idx) in metrics.top10Transactions2Minutes" :key="idx" class="transaction-item">
            <span class="tx-rank">{{ idx + 1 }}</span>
            <div class="tx-details">
              <strong>{{ tx.name }}</strong>
              <p>{{ tx.user }}</p>
            </div>
            <div class="tx-amount" :class="tx.type">
              {{ parseFloat(tx.amount).toFixed(2) }}
            </div>
          </div>
        </div>
        <p v-else class="empty">Nema transakcija u poslednja 2 minuta</p>
      </div>

      <!-- Users Management -->
      <div class="section">
        <h2>👥 Upravljanje korisnicima</h2>
        <div class="users-table">
          <div class="table-header">
            <div>Ime</div>
            <div>Email</div>
            <div>Uloga</div>
            <div>Status</div>
            <div>Akcije</div>
          </div>

          <div v-for="user in users" :key="user.id" class="table-row">
            <div><strong>{{ user.firstName }} {{ user.lastName }}</strong></div>
            <div>{{ user.email }}</div>
            <div>
              <span class="badge" :class="user.role === 'ADMINISTRATOR' ? 'admin' : 'user'">
                {{ user.role === 'ADMINISTRATOR' ? 'Admin' : 'Korisnik' }}
              </span>
            </div>
            <div>
              <span class="status" :class="user.blocked ? 'blocked' : 'active'">
                {{ user.blocked ? '🚫 Blokiran' : '✅ Aktivan' }}
              </span>
            </div>
            <div class="actions">
              <button 
                v-if="user.role !== 'ADMINISTRATOR'" 
                @click="toggleBlockUser(user.id)" 
                class="btn-small"
              >
                {{ user.blocked ? 'Odblokiraj' : 'Blokiraj' }}
              </button>
              <button @click="viewUserNotes(user)" class="btn-small">📝 Beleške</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- User Notes Modal -->
    <div v-if="selectedUser" class="modal-overlay" @click.self="closeNotesModal">
      <div class="modal">
        <h2>📝 Beleške o korisniku: {{ selectedUser.firstName }} {{ selectedUser.lastName }}</h2>

        <div class="notes-list">
          <div v-if="userNotes.length === 0" class="empty">Nema beleški</div>
          <div v-else v-for="note in userNotes" :key="note.id" class="note-item">
            <p class="note-text">{{ note.note }}</p>
            <p class="note-meta">
              {{ note.adminUsername }} • {{ new Date(note.dateCreated).toLocaleString('sr-RS') }}
            </p>
          </div>
        </div>

        <div class="add-note">
          <textarea 
            v-model="newNote" 
            placeholder="Dodaj novu belešku..."
            rows="3"
          ></textarea>
          <button @click="addNote" class="btn-primary">Dodaj belešku</button>
        </div>

        <button @click="closeNotesModal" class="btn-close">Zatvori</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.admin-page {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  color: #1a1d1f;
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: -0.5px;
  margin: 0;
}

.admin-badge {
  background: linear-gradient(135deg, #7f5af0 0%, #6c47d9 100%);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.loading {
  text-align: center;
  color: #6f767e;
  font-size: 1rem;
  padding: 3rem;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.metric-card {
  background: #fff;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e6e8ec;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  transition: all 0.15s ease;
}

.metric-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.metric-icon {
  font-size: 1.5rem;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #1a1d1f;
  color: #fff;
  border-radius: 10px;
  flex-shrink: 0;
}

.metric-info {
  flex: 1;
}

.metric-info h3 {
  color: #6f767e;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.5rem;
}

.metric-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1d1f;
  letter-spacing: -0.5px;
  margin: 0;
}

.section {
  background: #fff;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e6e8ec;
  margin-bottom: 1.5rem;
}

.section h2 {
  color: #1a1d1f;
  margin-bottom: 1.25rem;
  font-size: 1.125rem;
  font-weight: 700;
}

.transactions-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.transaction-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fb;
  border-radius: 10px;
  border: 1px solid #e6e8ec;
}

.tx-rank {
  font-size: 1.25rem;
  font-weight: 700;
  color: #7f5af0;
  min-width: 36px;
  text-align: center;
}

.tx-details {
  flex: 1;
}

.tx-details strong {
  font-size: 0.9375rem;
  color: #1a1d1f;
  font-weight: 600;
}

.tx-details p {
  margin: 0.25rem 0 0 0;
  font-size: 0.8125rem;
  color: #9ca3af;
}

.tx-amount {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: -0.25px;
}

.tx-amount.INCOME {
  color: #10b981;
}

.tx-amount.EXPENSE {
  color: #ef4444;
}

.empty {
  text-align: center;
  color: #6f767e;
  padding: 3rem 2rem;
  font-size: 0.875rem;
}

.users-table {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: 2fr 2fr 1fr 1fr 2fr;
  gap: 1rem;
  padding: 1rem 0.75rem;
  align-items: center;
}

.table-header {
  font-weight: 600;
  color: #6f767e;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #e6e8ec;
}

.table-row {
  background: transparent;
  border-bottom: 1px solid #f2f4f7;
  border-radius: 0;
  transition: all 0.15s ease;
  font-size: 0.9375rem;
}

.table-row:last-child {
  border-bottom: none;
}

.table-row:hover {
  background: #f8f9fb;
}

.table-row strong {
  color: #1a1d1f;
  font-weight: 600;
}

.badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.625rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.badge.admin {
  background: rgba(127, 90, 240, 0.1);
  color: #7f5af0;
}

.badge.user {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.status {
  font-size: 0.875rem;
  font-weight: 600;
}

.status.active {
  color: #10b981;
}

.status.blocked {
  color: #ef4444;
}

.actions {
  display: flex;
  gap: 0.5rem;
}

.btn-small {
  padding: 0.5rem 0.75rem;
  background: #7f5af0;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.8125rem;
  font-weight: 600;
  transition: all 0.15s ease;
}

.btn-small:hover {
  background: #6c47d9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(127, 90, 240, 0.25);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: #fff;
  padding: 2rem;
  border-radius: 16px;
  border: 1px solid #e6e8ec;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.modal h2 {
  margin-bottom: 1.5rem;
  color: #1a1d1f;
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.notes-list {
  margin-bottom: 1.5rem;
  max-height: 300px;
  overflow-y: auto;
}

.note-item {
  background: #f8f9fb;
  padding: 1.25rem;
  border-radius: 10px;
  border: 1px solid #e6e8ec;
  margin-bottom: 1rem;
}

.note-text {
  margin: 0 0 0.5rem 0;
  color: #1a1d1f;
  font-size: 0.9375rem;
  line-height: 1.5;
}

.note-meta {
  margin: 0;
  font-size: 0.8125rem;
  color: #9ca3af;
  font-weight: 600;
}

.add-note {
  margin-bottom: 1rem;
}

.add-note textarea {
  width: 100%;
  padding: 0.75rem 0.875rem;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-family: inherit;
  font-size: 0.9375rem;
  margin-bottom: 0.75rem;
  resize: vertical;
  color: #1a1d1f;
  transition: all 0.15s ease;
}

.add-note textarea:hover {
  border-color: #9ca3af;
}

.add-note textarea:focus {
  outline: none;
  border-color: #7f5af0;
  box-shadow: 0 0 0 3px rgba(127, 90, 240, 0.08);
}

.btn-primary {
  width: 100%;
  padding: 0.875rem;
  background: #7f5af0;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.9375rem;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-primary:hover {
  background: #6c47d9;
  box-shadow: 0 4px 12px rgba(127, 90, 240, 0.25);
}

.btn-close {
  width: 100%;
  padding: 0.875rem;
  background: #fff;
  color: #1a1d1f;
  border: 1px solid #e6e8ec;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.9375rem;
  cursor: pointer;
  transition: all 0.15s ease;
  margin-top: 0.75rem;
}

.btn-close:hover {
  background: #f8f9fb;
  border-color: #d1d5db;
}
</style>
