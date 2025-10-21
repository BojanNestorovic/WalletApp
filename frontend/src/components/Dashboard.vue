<script setup>
import { ref, onMounted } from 'vue';
import axios from '../api/axios';

const user = ref(null);
const wallets = ref([]);
const transactions = ref([]);
const savingsGoals = ref([]);
const totalBalance = ref(0);
const loading = ref(true);

onMounted(async () => {
  const userData = JSON.parse(localStorage.getItem('user'));
  user.value = userData;
  
  try {
    // Load wallets
    const walletsResponse = await axios.get('/wallets/active');
    wallets.value = walletsResponse.data;
    
    // Calculate total balance
    totalBalance.value = wallets.value.reduce((sum, w) => sum + parseFloat(w.currentBalance), 0);
    
    // Load recent transactions
    const txResponse = await axios.get('/transactions');
    transactions.value = txResponse.data.slice(0, 5); // Last 5
    
    // Load savings goals
    const goalsResponse = await axios.get('/savings-goals');
    savingsGoals.value = goalsResponse.data;
    
  } catch (error) {
    console.error('Error loading dashboard:', error);
  } finally {
    loading.value = false;
  }
});

const getProgressColor = (percentage) => {
  if (percentage >= 100) return '#4caf50';
  if (percentage >= 75) return '#2196f3';
  if (percentage >= 50) return '#ff9800';
  return '#f44336';
};
</script>

<template>
  <div class="dashboard">
    <h1>Dobrodošli, {{ user?.firstName }}</h1>
    
    <div v-if="loading" class="loading">Učitavanje...</div>
    
    <div v-else>
      <!-- Stats Cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">$</div>
          <div class="stat-info">
            <h3>Ukupno stanje</h3>
            <p class="stat-value">{{ totalBalance.toFixed(2) }} <span class="currency">RSD</span></p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">#</div>
          <div class="stat-info">
            <h3>Broj novčanika</h3>
            <p class="stat-value">{{ wallets.length }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">↕</div>
          <div class="stat-info">
            <h3>Transakcije</h3>
            <p class="stat-value">{{ transactions.length }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">★</div>
          <div class="stat-info">
            <h3>Ciljevi štednje</h3>
            <p class="stat-value">{{ savingsGoals.length }}</p>
          </div>
        </div>
      </div>

      <!-- Wallets Section -->
      <div class="section">
        <h2>Moji novčanici</h2>
        <div v-if="wallets.length === 0" class="empty-state">
          Nemate aktivnih novčanika. <router-link to="/wallets">Kreirajte prvi novčanik</router-link>
        </div>
        <div v-else class="wallets-grid">
          <div v-for="wallet in wallets" :key="wallet.id" class="wallet-card">
            <div class="wallet-badge">{{ wallet.savings ? 'SAVINGS' : 'WALLET' }}</div>
            <h3>{{ wallet.name }}</h3>
            <p class="wallet-balance">{{ parseFloat(wallet.currentBalance).toFixed(2) }} <span class="wallet-currency">{{ wallet.currencyName }}</span></p>
          </div>
        </div>
      </div>

      <!-- Recent Transactions -->
      <div class="section">
        <h2>Nedavne transakcije</h2>
        <div v-if="transactions.length === 0" class="empty-state">
          Nemate transakcija.
        </div>
        <div class="transactions-list">
          <div v-for="tx in transactions" :key="tx.id" class="transaction-item">
            <div class="tx-info">
              <span class="tx-icon" :class="tx.type.toLowerCase()">{{ tx.type === 'INCOME' ? '↑' : '↓' }}</span>
              <div>
                <strong>{{ tx.name }}</strong>
                <p class="tx-category">{{ tx.categoryName }}</p>
              </div>
            </div>
            <div class="tx-amount" :class="tx.type">
              {{ tx.type === 'INCOME' ? '+' : '-' }}{{ parseFloat(tx.amount).toFixed(2) }}
            </div>
          </div>
        </div>
      </div>

      <!-- Savings Goals Progress -->
      <div v-if="savingsGoals.length > 0" class="section">
        <h2>Napredak ciljeva štednje</h2>
        <div class="goals-list">
          <div v-for="goal in savingsGoals" :key="goal.id" class="goal-item">
            <div class="goal-header">
              <h3>{{ goal.name }}</h3>
              <span class="goal-amount">{{ parseFloat(goal.currentAmount).toFixed(2) }} / {{ parseFloat(goal.targetAmount).toFixed(2) }}</span>
            </div>
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: Math.min(goal.progressPercentage, 100) + '%', backgroundColor: getProgressColor(goal.progressPercentage) }"
              ></div>
            </div>
            <p class="goal-percentage">{{ parseFloat(goal.progressPercentage).toFixed(1) }}% postignuto</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Squirrel Mascot -->
    <div class="squirrel-mascot">
      <div class="acorn acorn-1">🌰</div>
      <div class="acorn acorn-2">🌰</div>
      <div class="acorn acorn-3">🌰</div>
      <div class="squirrel">🐿️</div>
      <div class="speech-bubble">Skupljam!</div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 0;
}

.dashboard h1 {
  color: #1a1d1f;
  margin-bottom: 2rem;
  font-size: 2rem;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.loading {
  text-align: center;
  color: #6f767e;
  font-size: 1rem;
  padding: 3rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: #fff;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e6e8ec;
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  transition: all 0.15s ease;
}

.stat-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.stat-icon {
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
  font-weight: 700;
  font-family: 'Inter', monospace;
}

.stat-info {
  flex: 1;
}

.stat-info h3 {
  color: #6f767e;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.5rem;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1d1f;
  letter-spacing: -0.5px;
}

.currency {
  font-size: 0.875rem;
  color: #6f767e;
  font-weight: 600;
  margin-left: 0.25rem;
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

.empty-state {
  text-align: center;
  padding: 3rem 2rem;
  color: #6f767e;
  font-size: 0.875rem;
}

.empty-state a {
  color: #7f5af0;
  font-weight: 600;
  text-decoration: none;
}

.empty-state a:hover {
  text-decoration: underline;
}

.wallets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1rem;
}

.wallet-card {
  background: linear-gradient(135deg, #7f5af0 0%, #6c47d9 100%);
  color: white;
  padding: 1.5rem;
  border-radius: 12px;
  transition: all 0.15s ease;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.wallet-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(127, 90, 240, 0.25);
}

.wallet-badge {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 4px;
  font-size: 0.625rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-bottom: 0.75rem;
}

.wallet-card h3 {
  margin-bottom: 0.75rem;
  font-size: 1rem;
  font-weight: 600;
}

.wallet-balance {
  font-size: 1.75rem;
  font-weight: 700;
  margin: 0.5rem 0;
  letter-spacing: -0.5px;
}

.wallet-currency {
  font-size: 0.875rem;
  opacity: 0.8;
  font-weight: 600;
}

.transactions-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #f2f4f7;
}

.transaction-item:last-child {
  border-bottom: none;
}

.tx-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex: 1;
}

.tx-info div {
  flex: 1;
}

.tx-info strong {
  font-size: 0.9375rem;
  color: #1a1d1f;
  font-weight: 600;
}

.tx-icon {
  font-size: 1.25rem;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fb;
  border-radius: 10px;
  font-weight: 700;
  color: #6f767e;
}

.tx-icon.income {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.tx-icon.expense {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.tx-category {
  font-size: 0.8125rem;
  color: #9ca3af;
  margin-top: 0.25rem;
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

.goals-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.goal-item {
  background: #f8f9fb;
  padding: 1.25rem;
  border-radius: 10px;
  border: 1px solid #e6e8ec;
}

.goal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.goal-header h3 {
  font-size: 1rem;
  color: #1a1d1f;
  font-weight: 600;
}

.goal-amount {
  font-weight: 700;
  color: #7f5af0;
  font-size: 0.875rem;
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
  transition: width 0.3s ease;
  background: linear-gradient(90deg, #7f5af0 0%, #6c47d9 100%);
}

.goal-percentage {
  text-align: right;
  font-size: 0.8125rem;
  color: #6f767e;
  font-weight: 600;
  margin: 0;
}

/* Squirrel Mascot */
.squirrel-mascot {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 100;
  pointer-events: none;
}

.squirrel {
  font-size: 3rem;
  animation: squirrelBounce 2s ease-in-out infinite;
  display: inline-block;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
}

.acorn {
  position: absolute;
  font-size: 1.5rem;
  animation: acornFloat 3s ease-in-out infinite;
}

.acorn-1 {
  top: -30px;
  left: -20px;
  animation-delay: 0s;
}

.acorn-2 {
  top: -40px;
  left: 30px;
  animation-delay: 1s;
}

.acorn-3 {
  top: -25px;
  left: 60px;
  animation-delay: 2s;
}

.speech-bubble {
  position: absolute;
  bottom: 60px;
  left: -30px;
  background: #fff;
  color: #1a1d1f;
  padding: 0.5rem 0.75rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
  border: 1px solid #e6e8ec;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  white-space: nowrap;
  opacity: 0;
  animation: bubblePop 4s ease-in-out infinite;
}

.speech-bubble::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 6px solid #fff;
}

@keyframes squirrelBounce {
  0%, 100% {
    transform: translateY(0) rotate(-5deg);
  }
  50% {
    transform: translateY(-10px) rotate(5deg);
  }
}

@keyframes acornFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-15px) rotate(180deg);
    opacity: 1;
  }
}

@keyframes bubblePop {
  0%, 70%, 100% {
    opacity: 0;
    transform: scale(0.8) translateY(10px);
  }
  75%, 95% {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>
