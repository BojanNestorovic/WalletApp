<script setup>
import { ref, onMounted } from 'vue';
import axios from '../api/axios';

const goals = ref([]);
const wallets = ref([]);
const showModal = ref(false);
const newGoal = ref({
  name: '',
  targetAmount: 0,
  targetDate: '',
  walletId: null
});
const loading = ref(false);
const error = ref('');

onMounted(async () => {
  await Promise.all([loadGoals(), loadWallets()]);
});

const loadGoals = async () => {
  try {
    const response = await axios.get('/savings-goals');
    goals.value = response.data;
  } catch (err) {
    console.error('Error loading goals:', err);
  }
};

const loadWallets = async () => {
  try {
    const response = await axios.get('/wallets/active');
    wallets.value = response.data;
    if (wallets.value.length > 0) {
      newGoal.value.walletId = wallets.value[0].id;
    }
  } catch (err) {
    console.error('Error loading wallets:', err);
  }
};

const createGoal = async () => {
  if (!newGoal.value.name || newGoal.value.targetAmount <= 0 || !newGoal.value.targetDate) {
    error.value = 'Popunite sva polja';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    await axios.post('/savings-goals', newGoal.value);
    await loadGoals();
    showModal.value = false;
    newGoal.value = {
      name: '',
      targetAmount: 0,
      targetDate: '',
      walletId: wallets.value.length > 0 ? wallets.value[0].id : null
    };
  } catch (err) {
    error.value = err.response?.data?.error || 'Greška pri kreiranju cilja';
  } finally {
    loading.value = false;
  }
};

const deleteGoal = async (goalId) => {
  if (!confirm('Da li ste sigurni da želite da obrišete ovaj cilj?')) return;

  try {
    await axios.delete(`/savings-goals/${goalId}`);
    await loadGoals();
  } catch (err) {
    alert('Greška pri brisanju cilja');
  }
};

const getProgressColor = (percentage) => {
  if (percentage >= 100) return '#4caf50';
  if (percentage >= 75) return '#2196f3';
  if (percentage >= 50) return '#ff9800';
  return '#f44336';
};

const getDaysRemaining = (targetDate) => {
  const today = new Date();
  const target = new Date(targetDate);
  const diff = target - today;
  const days = Math.ceil(diff / (1000 * 60 * 60 * 24));
  return days;
};
</script>

<template>
  <div class="goals-page">
    <div class="page-header">
      <h1>Ciljevi štednje</h1>
      <button @click="showModal = true" class="btn-primary">+ Novi cilj</button>
    </div>

    <div class="goals-container">
      <div v-if="goals.length === 0" class="empty-state">
        <p>Nemate definisanih ciljeva štednje</p>
        <button @click="showModal = true" class="btn-primary">Kreiraj prvi cilj</button>
      </div>

      <div v-else class="goals-grid">
        <div v-for="goal in goals" :key="goal.id" class="goal-card">
          <div class="goal-header">
            <h3>{{ goal.name }}</h3>
            <div class="badges">
              <span v-if="goal.completed" class="badge completed">COMPLETED</span>
              <span v-else-if="goal.overdue" class="badge overdue">OVERDUE</span>
              <span v-else-if="goal.onTrack" class="badge on-track">ON TRACK</span>
            </div>
          </div>

          <div class="goal-amounts">
            <div class="amount-row">
              <span class="label">Current</span>
              <strong>{{ parseFloat(goal.currentAmount).toFixed(2) }}</strong>
            </div>
            <div class="amount-row">
              <span class="label">Target</span>
              <strong>{{ parseFloat(goal.targetAmount).toFixed(2) }}</strong>
            </div>
            <div class="amount-row">
              <span class="label">Remaining</span>
              <strong>{{ parseFloat(goal.remainingAmount).toFixed(2) }}</strong>
            </div>
          </div>

          <div class="progress-section">
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ 
                  width: Math.min(parseFloat(goal.progressPercentage), 100) + '%'
                }"
              ></div>
            </div>
            <p class="progress-text">{{ parseFloat(goal.progressPercentage).toFixed(1) }}%</p>
          </div>

          <div class="goal-info">
            <div class="info-row">
              <span class="label">Deadline</span>
              <span>{{ new Date(goal.targetDate).toLocaleDateString('sr-RS') }}</span>
            </div>
            <div class="info-row">
              <span class="label">Days left</span>
              <span>{{ getDaysRemaining(goal.targetDate) }}</span>
            </div>
            <div class="info-row">
              <span class="label">Wallet</span>
              <span>{{ goal.walletName }}</span>
            </div>
          </div>

          <button @click="deleteGoal(goal.id)" class="btn-delete">Delete</button>
        </div>
      </div>
    </div>

    <!-- Create Goal Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal">
        <h2>Novi cilj štednje</h2>

        <form @submit.prevent="createGoal">
          <div class="form-group">
            <label>Naziv cilja *</label>
            <input v-model="newGoal.name" type="text" placeholder="npr. Odmor, Laptop..." required />
          </div>

          <div class="form-group">
            <label>Ciljani iznos *</label>
            <input v-model.number="newGoal.targetAmount" type="number" step="0.01" min="0.01" required />
          </div>

          <div class="form-group">
            <label>Rok *</label>
            <input v-model="newGoal.targetDate" type="date" required />
          </div>

          <div class="form-group">
            <label>Štedni novčanik *</label>
            <select v-model="newGoal.walletId" required>
              <option v-for="wallet in wallets" :key="wallet.id" :value="wallet.id">
                {{ wallet.name }} ({{ wallet.currencyName }})
              </option>
            </select>
          </div>

          <div v-if="error" class="error-message">{{ error }}</div>

          <div class="modal-actions">
            <button type="button" @click="showModal = false" class="btn-secondary">Otkaži</button>
            <button type="submit" :disabled="loading" class="btn-primary">
              {{ loading ? 'Kreiranje...' : 'Kreiraj' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.goals-page {
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
}

.btn-primary {
  background: #7f5af0;
  color: white;
  padding: 0.75rem 1.25rem;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
  font-size: 0.9375rem;
}

.btn-primary:hover {
  background: #6c47d9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(127, 90, 240, 0.25);
}

.goals-container {
  background: transparent;
  padding: 0;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #6f767e;
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e6e8ec;
}

.goals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1rem;
}

.goal-card {
  background: #fff;
  border: 1px solid #e6e8ec;
  padding: 1.5rem;
  border-radius: 12px;
  transition: all 0.15s ease;
}

.goal-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.goal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.25rem;
}

.goal-header h3 {
  font-size: 1.125rem;
  color: #1a1d1f;
  font-weight: 700;
  margin: 0;
}

.badges {
  display: flex;
  gap: 0.5rem;
}

.badge {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.625rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.badge.completed {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.badge.overdue {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.badge.on-track {
  background: rgba(127, 90, 240, 0.1);
  color: #7f5af0;
}

.goal-amounts {
  background: #f8f9fb;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  border: 1px solid #e6e8ec;
}

.amount-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  font-size: 0.875rem;
}

.amount-row .label {
  color: #6f767e;
  font-weight: 500;
}

.amount-row strong {
  color: #1a1d1f;
  font-weight: 700;
  font-size: 1rem;
}

.progress-section {
  margin: 1.25rem 0;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #e6e8ec;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #7f5af0 0%, #6c47d9 100%);
  transition: width 0.3s ease;
}

.progress-text {
  text-align: right;
  font-weight: 600;
  color: #6f767e;
  font-size: 0.8125rem;
  margin: 0;
}

.goal-info {
  margin: 1rem 0;
  padding-top: 1rem;
  border-top: 1px solid #f2f4f7;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  font-size: 0.875rem;
}

.info-row .label {
  color: #6f767e;
  font-weight: 500;
}

.info-row span:last-child {
  color: #1a1d1f;
  font-weight: 600;
}

.btn-delete {
  width: 100%;
  padding: 0.625rem;
  background: transparent;
  color: #ef4444;
  border: 1px solid #fee2e2;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
  margin-top: 1rem;
  font-size: 0.875rem;
}

.btn-delete:hover {
  background: #fef2f2;
  border-color: #fecaca;
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
  max-width: 480px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.modal h2 {
  margin-bottom: 1.5rem;
  color: #1a1d1f;
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #1a1d1f;
  font-weight: 600;
  font-size: 0.875rem;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 0.75rem 0.875rem;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-size: 0.9375rem;
  background: #fff;
  color: #1a1d1f;
  transition: all 0.15s ease;
}

.form-group input:hover,
.form-group select:hover {
  border-color: #9ca3af;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #7f5af0;
  box-shadow: 0 0 0 3px rgba(127, 90, 240, 0.08);
}

.modal-actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

.modal-actions button {
  flex: 1;
  padding: 0.875rem;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  font-size: 0.9375rem;
  transition: all 0.15s ease;
}

.btn-secondary {
  background: #fff;
  color: #1a1d1f;
  border: 1px solid #e6e8ec;
}

.btn-secondary:hover {
  background: #f8f9fb;
  border-color: #d1d5db;
}

.modal-actions .btn-primary {
  background: #7f5af0;
  color: white;
  border: none;
}

.modal-actions .btn-primary:hover:not(:disabled) {
  background: #6c47d9;
  box-shadow: 0 4px 12px rgba(127, 90, 240, 0.25);
}

.modal-actions .btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.error-message {
  background: #fef2f2;
  color: #dc2626;
  padding: 0.75rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  font-size: 0.875rem;
  border: 1px solid #fee2e2;
}
</style>
