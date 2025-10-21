<script setup>
import { ref, onMounted } from 'vue';
import axios from '../api/axios';

const transactions = ref([]);
const wallets = ref([]);
const categories = ref([]);
const showModal = ref(false);
const showTransferModal = ref(false);
const newTransaction = ref({
  name: '',
  amount: 0,
  type: 'EXPENSE',
  categoryId: null,
  walletId: null,
  repeating: false,
  frequency: 'MONTHLY'
});
const transferData = ref({
  fromWalletId: null,
  toWalletId: null,
  amount: 0,
  description: ''
});
const loading = ref(false);
const error = ref('');

onMounted(async () => {
  await Promise.all([loadTransactions(), loadWallets(), loadCategories()]);
});

const loadTransactions = async () => {
  try {
    const response = await axios.get('/transactions');
    transactions.value = response.data.sort((a, b) => new Date(b.dateOfTransaction) - new Date(a.dateOfTransaction));
  } catch (err) {
    console.error('Error loading transactions:', err);
  }
};

const loadWallets = async () => {
  try {
    const response = await axios.get('/wallets/active');
    wallets.value = response.data;
    if (wallets.value.length > 0) {
      newTransaction.value.walletId = wallets.value[0].id;
      transferData.value.fromWalletId = wallets.value[0].id;
      if (wallets.value.length > 1) {
        transferData.value.toWalletId = wallets.value[1].id;
      }
    }
  } catch (err) {
    console.error('Error loading wallets:', err);
  }
};

const loadCategories = async () => {
  try {
    const response = await axios.get('/categories');
    categories.value = response.data;
    if (categories.value.length > 0) {
      newTransaction.value.categoryId = categories.value[0].id;
    }
  } catch (err) {
    console.error('Error loading categories:', err);
  }
};

const filteredCategories = () => {
  return categories.value.filter(c => c.type === newTransaction.value.type);
};

const createTransaction = async () => {
  if (!newTransaction.value.name || newTransaction.value.amount <= 0) {
    error.value = 'Popunite sva polja';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    await axios.post('/transactions', newTransaction.value);
    await loadTransactions();
    await loadWallets(); // Refresh wallet balances
    showModal.value = false;
    resetForm();
  } catch (err) {
    error.value = err.response?.data?.error || 'Greška pri kreiranju transakcije';
  } finally {
    loading.value = false;
  }
};

const transferFunds = async () => {
  if (transferData.value.fromWalletId === transferData.value.toWalletId) {
    error.value = 'Izaberite različite novčanike';
    return;
  }

  if (transferData.value.amount <= 0) {
    error.value = 'Unesite ispravnu sumu';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    await axios.post('/transactions/transfer', transferData.value);
    await loadTransactions();
    await loadWallets();
    showTransferModal.value = false;
    transferData.value = {
      fromWalletId: wallets.value[0]?.id || null,
      toWalletId: wallets.value[1]?.id || null,
      amount: 0,
      description: ''
    };
  } catch (err) {
    error.value = err.response?.data?.error || 'Greška pri transferu';
  } finally {
    loading.value = false;
  }
};

const deleteTransaction = async (txId) => {
  if (!confirm('Da li ste sigurni da želite da obrišete ovu transakciju?')) return;

  try {
    await axios.delete(`/transactions/${txId}`);
    await loadTransactions();
    await loadWallets();
  } catch (err) {
    alert(err.response?.data?.error || 'Greška pri brisanju transakcije');
  }
};

const resetForm = () => {
  newTransaction.value = {
    name: '',
    amount: 0,
    type: 'EXPENSE',
    categoryId: categories.value.length > 0 ? categories.value[0].id : null,
    walletId: wallets.value.length > 0 ? wallets.value[0].id : null,
    repeating: false,
    frequency: 'MONTHLY'
  };
};
</script>

<template>
  <div class="transactions-page">
    <div class="page-header">
      <h1>Transakcije</h1>
      <div class="header-actions">
        <button @click="showTransferModal = true" class="btn-secondary">Transfer</button>
        <button @click="showModal = true" class="btn-primary">+ Nova transakcija</button>
      </div>
    </div>

    <div class="transactions-container">
      <div v-if="transactions.length === 0" class="empty-state">
        <p>Nemate transakcija</p>
      </div>

      <div v-else class="transactions-table">
        <div class="table-header">
          <div>Datum</div>
          <div>Naziv</div>
          <div>Kategorija</div>
          <div>Novčanik</div>
          <div>Iznos</div>
          <div>Akcije</div>
        </div>

        <div v-for="tx in transactions" :key="tx.id" class="table-row">
          <div>{{ new Date(tx.dateOfTransaction).toLocaleDateString('sr-RS') }}</div>
          <div>
            <strong>{{ tx.name }}</strong>
            <span v-if="tx.repeating" class="badge">RECURRING</span>
          </div>
          <div class="category-cell">{{ tx.categoryName }}</div>
          <div class="wallet-cell">{{ tx.walletName }}</div>
          <div class="amount" :class="tx.type">
            {{ tx.type === 'INCOME' ? '+' : '-' }}{{ parseFloat(tx.amount).toFixed(2) }}
          </div>
          <div>
            <button @click="deleteTransaction(tx.id)" class="btn-delete">Delete</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Transaction Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal">
        <h2>Nova transakcija</h2>

        <form @submit.prevent="createTransaction">
          <div class="form-group">
            <label>Tip transakcije</label>
            <select v-model="newTransaction.type">
              <option value="INCOME">Income</option>
              <option value="EXPENSE">Expense</option>
            </select>
          </div>

          <div class="form-group">
            <label>Naziv *</label>
            <input v-model="newTransaction.name" type="text" placeholder="npr. Plata" required />
          </div>

          <div class="form-group">
            <label>Iznos *</label>
            <input v-model.number="newTransaction.amount" type="number" step="0.01" min="0.01" required />
          </div>

          <div class="form-group">
            <label>Kategorija *</label>
            <select v-model="newTransaction.categoryId" required>
              <option v-for="cat in filteredCategories()" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Novčanik *</label>
            <select v-model="newTransaction.walletId" required>
              <option v-for="wallet in wallets" :key="wallet.id" :value="wallet.id">
                {{ wallet.name }} ({{ wallet.currencyName }})
              </option>
            </select>
          </div>

          <div class="form-group checkbox">
            <label>
              <input v-model="newTransaction.repeating" type="checkbox" />
              Ponavljajuća transakcija
            </label>
          </div>

          <div v-if="newTransaction.repeating" class="form-group">
            <label>Učestalost</label>
            <select v-model="newTransaction.frequency">
              <option value="WEEKLY">Nedeljno</option>
              <option value="MONTHLY">Mesečno</option>
              <option value="QUARTERLY">Kvartalno</option>
              <option value="YEARLY">Godišnje</option>
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

    <!-- Transfer Modal -->
    <div v-if="showTransferModal" class="modal-overlay" @click.self="showTransferModal = false">
      <div class="modal">
        <h2>💸 Transfer sredstava</h2>

        <form @submit.prevent="transferFunds">
          <div class="form-group">
            <label>Izvorni novčanik *</label>
            <select v-model="transferData.fromWalletId" required>
              <option v-for="wallet in wallets" :key="wallet.id" :value="wallet.id">
                {{ wallet.name }} ({{ parseFloat(wallet.currentBalance).toFixed(2) }} {{ wallet.currencyName }})
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Odredišni novčanik *</label>
            <select v-model="transferData.toWalletId" required>
              <option v-for="wallet in wallets" :key="wallet.id" :value="wallet.id">
                {{ wallet.name }} ({{ wallet.currencyName }})
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Iznos *</label>
            <input v-model.number="transferData.amount" type="number" step="0.01" min="0.01" required />
          </div>

          <div class="form-group">
            <label>Opis</label>
            <input v-model="transferData.description" type="text" placeholder="Opciono" />
          </div>

          <div v-if="error" class="error-message">{{ error }}</div>

          <div class="modal-actions">
            <button type="button" @click="showTransferModal = false" class="btn-secondary">Otkaži</button>
            <button type="submit" :disabled="loading" class="btn-primary">
              {{ loading ? 'Transfer...' : 'Izvrši transfer' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.transactions-page {
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

.header-actions {
  display: flex;
  gap: 0.75rem;
}

.btn-primary,
.btn-secondary {
  padding: 0.75rem 1.25rem;
  border-radius: 10px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
  font-size: 0.9375rem;
}

.btn-primary {
  background: #7f5af0;
  color: white;
  border: none;
}

.btn-primary:hover {
  background: #6c47d9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(127, 90, 240, 0.25);
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

.transactions-container {
  background: #fff;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e6e8ec;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #6f767e;
  font-size: 0.9375rem;
}

.transactions-table {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.table-header,
.table-row {
  display: grid;
  grid-template-columns: 110px 2fr 1.5fr 1.5fr 140px 100px;
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
  background: rgba(127, 90, 240, 0.1);
  color: #7f5af0;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.625rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  margin-left: 0.5rem;
  text-transform: uppercase;
}

.category-cell,
.wallet-cell {
  color: #6f767e;
  font-size: 0.875rem;
}

.amount {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: -0.25px;
}

.amount.INCOME {
  color: #10b981;
}

.amount.EXPENSE {
  color: #ef4444;
}

.btn-delete {
  background: transparent;
  border: 1px solid #fee2e2;
  color: #ef4444;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.8125rem;
  font-weight: 600;
  transition: all 0.15s ease;
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
