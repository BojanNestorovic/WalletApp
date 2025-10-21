<script setup>
import { ref, onMounted } from 'vue';
import axios from '../api/axios';

const wallets = ref([]);
const currencies = ref([]);
const showModal = ref(false);
const newWallet = ref({
  name: '',
  initialBalance: 0,
  currencyId: null,
  savings: false
});
const loading = ref(false);
const error = ref('');

onMounted(async () => {
  await loadWallets();
  await loadCurrencies();
});

const loadWallets = async () => {
  try {
    const response = await axios.get('/wallets');
    wallets.value = response.data;
  } catch (err) {
    console.error('Error loading wallets:', err);
  }
};

const loadCurrencies = async () => {
  try {
    const response = await axios.get('/currencies');
    currencies.value = response.data;
    if (currencies.value.length > 0) {
      newWallet.value.currencyId = currencies.value[0].id;
    }
  } catch (err) {
    console.error('Error loading currencies:', err);
  }
};

const createWallet = async () => {
  if (!newWallet.value.name || newWallet.value.initialBalance < 0) {
    error.value = 'Popunite sva polja';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    await axios.post('/wallets', newWallet.value);
    await loadWallets();
    showModal.value = false;
    resetForm();
  } catch (err) {
    error.value = err.response?.data?.error || 'Greška pri kreiranju novčanika';
  } finally {
    loading.value = false;
  }
};

const archiveWallet = async (walletId) => {
  if (!confirm('Da li ste sigurni da želite da arhivirate ovaj novčanik?')) return;

  try {
    await axios.put(`/wallets/${walletId}/archive`);
    await loadWallets();
  } catch (err) {
    alert('Greška pri arhiviranju novčanika');
  }
};

const unarchiveWallet = async (walletId) => {
  try {
    await axios.put(`/wallets/${walletId}/unarchive`);
    await loadWallets();
  } catch (err) {
    alert('Greška pri aktiviranju novčanika');
  }
};

const deleteWallet = async (walletId) => {
  if (!confirm('Da li ste sigurni da želite da obrišete ovaj novčanik? Ova akcija je nepovratna!')) return;

  try {
    await axios.delete(`/wallets/${walletId}`);
    await loadWallets();
  } catch (err) {
    alert('Greška pri brisanju novčanika');
  }
};

const resetForm = () => {
  newWallet.value = {
    name: '',
    initialBalance: 0,
    currencyId: currencies.value.length > 0 ? currencies.value[0].id : null,
    savings: false
  };
};
</script>

<template>
  <div class="wallets-page">
    <div class="page-header">
      <h1>Moji novčanici</h1>
      <button @click="showModal = true" class="btn-primary">+ Novi novčanik</button>
    </div>

    <div class="wallets-container">
      <div v-if="wallets.length === 0" class="empty-state">
        <p>Nemate kreiranih novčanika</p>
        <button @click="showModal = true" class="btn-primary">Kreiraj prvi novčanik</button>
      </div>

      <div v-else class="wallets-grid">
        <div v-for="wallet in wallets" :key="wallet.id" class="wallet-card" :class="{ archived: wallet.archived }">
          <div class="wallet-header">
            <div class="wallet-title">
              <h3>{{ wallet.name }}</h3>
              <span v-if="wallet.archived" class="badge archived">ARCHIVED</span>
              <span v-if="wallet.savings" class="badge savings">SAVINGS</span>
            </div>
          </div>

          <div class="wallet-balance">
            <span class="balance-label">Current Balance</span>
            <span class="balance-amount">{{ parseFloat(wallet.currentBalance).toFixed(2) }} <span class="currency-code">{{ wallet.currencyName }}</span></span>
          </div>

          <div class="wallet-info">
            <div class="info-row">
              <span class="info-label">Initial</span>
              <span>{{ parseFloat(wallet.initialBalance).toFixed(2) }} {{ wallet.currencyName }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">Created</span>
              <span>{{ new Date(wallet.dateOfCreation).toLocaleDateString('sr-RS') }}</span>
            </div>
          </div>

          <div class="wallet-actions">
            <button v-if="!wallet.archived" @click="archiveWallet(wallet.id)" class="btn-action">
              Archive
            </button>
            <button v-else @click="unarchiveWallet(wallet.id)" class="btn-action">
              Activate
            </button>
            <button @click="deleteWallet(wallet.id)" class="btn-danger">Delete</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Wallet Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal">
        <h2>Kreiraj novi novčanik</h2>

        <form @submit.prevent="createWallet">
          <div class="form-group">
            <label>Naziv novčanika *</label>
            <input v-model="newWallet.name" type="text" placeholder="npr. Glavni račun" required />
          </div>

          <div class="form-group">
            <label>Početno stanje *</label>
            <input v-model.number="newWallet.initialBalance" type="number" step="0.01" min="0" required />
          </div>

          <div class="form-group">
            <label>Valuta *</label>
            <select v-model="newWallet.currencyId" required>
              <option v-for="currency in currencies" :key="currency.id" :value="currency.id">
                {{ currency.name }}
              </option>
            </select>
          </div>

          <div class="form-group checkbox">
            <label>
              <input v-model="newWallet.savings" type="checkbox" />
              Štedni novčanik
            </label>
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
.wallets-page {
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

.wallets-container {
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

.wallets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 1rem;
}

.wallet-card {
  background: #fff;
  border: 1px solid #e6e8ec;
  padding: 1.5rem;
  border-radius: 12px;
  transition: all 0.15s ease;
}

.wallet-card:hover {
  border-color: #d1d5db;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.wallet-card.archived {
  opacity: 0.6;
  background: #f8f9fb;
}

.wallet-header {
  margin-bottom: 1.25rem;
}

.wallet-title {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.wallet-title h3 {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1a1d1f;
  margin: 0;
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

.badge.savings {
  background: rgba(127, 90, 240, 0.1);
  color: #7f5af0;
}

.badge.archived {
  background: rgba(107, 114, 128, 0.1);
  color: #6f767e;
}

.wallet-balance {
  margin: 1rem 0;
}

.balance-label {
  display: block;
  font-size: 0.75rem;
  color: #6f767e;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
  margin-bottom: 0.5rem;
}

.balance-amount {
  display: block;
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1d1f;
  letter-spacing: -0.5px;
}

.currency-code {
  font-size: 0.875rem;
  color: #6f767e;
  font-weight: 600;
}

.wallet-info {
  margin: 1rem 0;
  padding-top: 1rem;
  border-top: 1px solid #f2f4f7;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  font-size: 0.875rem;
}

.info-label {
  color: #6f767e;
  font-weight: 500;
}

.wallet-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1.25rem;
}

.btn-action,
.btn-danger {
  flex: 1;
  padding: 0.625rem;
  border: 1px solid #e6e8ec;
  background: #fff;
  color: #1a1d1f;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.875rem;
  transition: all 0.15s ease;
}

.btn-action:hover {
  background: #f8f9fb;
  border-color: #d1d5db;
}

.btn-danger {
  color: #ef4444;
  border-color: #fee2e2;
}

.btn-danger:hover {
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

.form-group.checkbox {
  display: flex;
  align-items: center;
}

.form-group.checkbox label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.form-group.checkbox input {
  width: auto;
  cursor: pointer;
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

.modal-actions .btn-secondary {
  background: #fff;
  color: #1a1d1f;
  border: 1px solid #e6e8ec;
}

.modal-actions .btn-secondary:hover {
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
