<script setup>
import { ref, onMounted, watch } from 'vue';
import axios from '../api/axios';
import { Search, ArrowUpDown, ArrowUp, ArrowDown } from 'lucide-vue-next';

const transactions = ref([]);
const filters = ref({
  user: '',
  category: '',
  minAmount: '',
  maxAmount: '',
  startDate: '',
  endDate: '',
});
const sortBy = ref('dateCreated');
const sortOrder = ref('desc');
const loading = ref(false);

const loadTransactions = async () => {
  try {
    loading.value = true;
    const params = {
      ...filters.value,
      sortBy: sortBy.value,
      sortOrder: sortOrder.value,
    };
    const response = await axios.get('/admin/transactions/search', { params });
    transactions.value = response.data;
  } catch (err) {
    console.error('Error loading transactions:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(loadTransactions);

watch(filters, loadTransactions, { deep: true });
watch([sortBy, sortOrder], loadTransactions);

const setSortBy = (field) => {
  if (sortBy.value === field) {
    sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc';
  } else {
    sortBy.value = field;
    sortOrder.value = 'asc';
  }
};

const getSortIcon = (field) => {
  if (sortBy.value !== field) return ArrowUpDown;
  return sortOrder.value === 'asc' ? ArrowUp : ArrowDown;
};

const clearFilters = () => {
  filters.value = {
    user: '',
    category: '',
    minAmount: '',
    maxAmount: '',
    startDate: '',
    endDate: '',
  };
};
</script>

<template>
  <div class="section">
    <h2><Search :size="20" /> Nadgledanje transakcija</h2>

    <div class="transaction-monitoring">
      <!-- Filters -->
      <div class="filters">
        <div class="filter-group">
          <label>Korisnik</label>
          <input
            type="text"
            v-model="filters.user"
            placeholder="Ime ili email"
            :disabled="loading"
          />
        </div>

        <div class="filter-group">
          <label>Kategorija</label>
          <input
            type="text"
            v-model="filters.category"
            placeholder="Naziv kategorije"
            :disabled="loading"
          />
        </div>

        <div class="filter-group">
          <label>Min. iznos</label>
          <input
            type="number"
            v-model="filters.minAmount"
            placeholder="0"
            step="0.01"
            :disabled="loading"
          />
        </div>

        <div class="filter-group">
          <label>Maks. iznos</label>
          <input
            type="number"
            v-model="filters.maxAmount"
            placeholder="100000"
            step="0.01"
            :disabled="loading"
          />
        </div>

        <div class="filter-group">
          <label>Od datuma</label>
          <input
            type="date"
            v-model="filters.startDate"
            :disabled="loading"
          />
        </div>

        <div class="filter-group">
          <label>Do datuma</label>
          <input
            type="date"
            v-model="filters.endDate"
            :disabled="loading"
          />
        </div>

        <div class="filter-actions">
          <button @click="clearFilters" class="btn-secondary" :disabled="loading">
            Očisti filtere
          </button>
        </div>
      </div>

      <!-- Transactions Table -->
      <div class="transactions-table">
        <div v-if="loading" class="loading">Učitavanje transakcija...</div>
        <div v-else-if="transactions.length === 0" class="empty">
          Nema transakcija koje odgovaraju filterima
        </div>
        <div v-else>
          <!-- Table Header -->
          <div class="table-header">
            <div class="sortable" @click="setSortBy('user')">
              <span>Korisnik</span>
              <component :is="getSortIcon('user')" :size="14" />
            </div>
            <div class="sortable" @click="setSortBy('category')">
              <span>Kategorija</span>
              <component :is="getSortIcon('category')" :size="14" />
            </div>
            <div class="sortable" @click="setSortBy('amount')">
              <span>Iznos</span>
              <component :is="getSortIcon('amount')" :size="14" />
            </div>
            <div class="sortable" @click="setSortBy('dateCreated')">
              <span>Datum</span>
              <component :is="getSortIcon('dateCreated')" :size="14" />
            </div>
            <div>Tip</div>
          </div>

          <!-- Table Rows -->
          <div
            v-for="tx in transactions"
            :key="tx.id"
            class="table-row"
          >
            <div class="user-info">
              <span class="user-name">{{ tx.user?.firstName }} {{ tx.user?.lastName }}</span>
              <span class="user-email">{{ tx.user?.email }}</span>
            </div>
            <div class="category-name">{{ tx.category?.name }}</div>
            <div class="amount" :class="tx.type">
              {{ parseFloat(tx.amount).toFixed(2) }} {{ tx.wallet?.currency?.name }}
            </div>
            <div class="date">
              {{ new Date(tx.dateCreated).toLocaleString('sr-RS') }}
            </div>
            <div class="type-badge" :class="tx.type">
              {{ tx.type === 'INCOME' ? 'Prihod' : 'Trošak' }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.section h2 {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.transaction-monitoring {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.filters {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  padding: 1.5rem;
  background: #f8f9fb;
  border-radius: 12px;
  border: 1px solid #e6e8ec;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 0.8125rem;
  font-weight: 600;
  color: #374151;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.filter-group input {
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 0.9375rem;
  transition: all 0.15s ease;
}

.filter-group input:focus {
  outline: none;
  border-color: #7f5af0;
  box-shadow: 0 0 0 3px rgba(127, 90, 240, 0.1);
}

.filter-group input:disabled {
  background-color: #f9fafb;
  cursor: not-allowed;
}

.filter-actions {
  display: flex;
  align-items: end;
}

.btn-secondary {
  padding: 0.75rem 1rem;
  background: #6b7280;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 0.8125rem;
  cursor: pointer;
  transition: all 0.15s ease;
}

.btn-secondary:hover:not(:disabled) {
  background: #4b5563;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(107, 114, 128, 0.25);
}

.btn-secondary:disabled {
  background: #d1d5db;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.transactions-table {
  background: white;
  border-radius: 12px;
  border: 1px solid #e6e8ec;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1.5fr 1fr 1.5fr 1fr;
  gap: 1rem;
  padding: 1rem 1.5rem;
  background: #f8f9fb;
  border-bottom: 1px solid #e6e8ec;
  font-weight: 600;
  color: #374151;
  font-size: 0.8125rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.sortable {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  user-select: none;
  transition: color 0.15s ease;
}

.sortable:hover {
  color: #7f5af0;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1.5fr 1fr 1.5fr 1fr;
  gap: 1rem;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #f3f4f6;
  transition: background-color 0.15s ease;
}

.table-row:hover {
  background-color: #f8f9fb;
}

.table-row:last-child {
  border-bottom: none;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.user-name {
  font-weight: 600;
  color: #1a1d1f;
  font-size: 0.9375rem;
}

.user-email {
  color: #6f767e;
  font-size: 0.8125rem;
}

.category-name {
  font-weight: 500;
  color: #374151;
}

.amount {
  font-weight: 600;
  font-size: 0.9375rem;
}

.amount.INCOME {
  color: #10b981;
}

.amount.EXPENSE {
  color: #ef4444;
}

.date {
  color: #6f767e;
  font-size: 0.8125rem;
}

.type-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  width: fit-content;
}

.type-badge.INCOME {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.type-badge.EXPENSE {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.loading {
  text-align: center;
  color: #6f767e;
  padding: 3rem;
  font-size: 0.9375rem;
}

.empty {
  text-align: center;
  color: #6f767e;
  padding: 4rem 2rem;
  font-size: 0.9375rem;
}
</style>
