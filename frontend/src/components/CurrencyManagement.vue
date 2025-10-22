<script setup>
import { ref, onMounted } from 'vue';
import axios from '../api/axios';
import { Landmark, RefreshCw } from 'lucide-vue-next';

const currencies = ref([]);
const newCurrency = ref({ name: '', exchangeRate: '' });
const loading = ref(false);
const fetchingRate = ref(false);

const loadCurrencies = async () => {
  try {
    loading.value = true;
    const response = await axios.get('/admin/currencies');
    currencies.value = response.data;
  } catch (err) {
    console.error('Error loading currencies:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(loadCurrencies);

const fetchExchangeRate = async () => {
  if (!newCurrency.value.name.trim()) return;

  try {
    fetchingRate.value = true;
    const response = await fetch(`https://api.frankfurter.app/latest?to=${newCurrency.value.name}`);
    const data = await response.json();
    if (data.rates && data.rates[newCurrency.value.name]) {
      newCurrency.value.exchangeRate = data.rates[newCurrency.value.name];
    }
  } catch (err) {
    console.error('Error fetching exchange rate:', err);
  } finally {
    fetchingRate.value = false;
  }
};

const addCurrency = async () => {
  if (!newCurrency.value.name.trim() || !newCurrency.value.exchangeRate) return;

  try {
    loading.value = true;
    await axios.post('/admin/currencies', {
      name: newCurrency.value.name,
      value: parseFloat(newCurrency.value.exchangeRate)
    });
    newCurrency.value = { name: '', exchangeRate: '' };
    await loadCurrencies();
  } catch (err) {
    console.error('Error adding currency:', err);
    alert('Greška pri dodavanju valute');
  } finally {
    loading.value = false;
  }
};

const updateExchangeRate = async (currency) => {
  try {
    const response = await fetch(`https://api.frankfurter.app/latest?to=${currency.name}`);
    const data = await response.json();
    if (data.rates && data.rates[currency.name]) {
      currency.value = data.rates[currency.name];
      // Update on backend
      await axios.put(`/admin/currencies/${currency.id}`, {
        name: currency.name,
        value: currency.value
      });
    }
  } catch (err) {
    console.error('Error updating exchange rate:', err);
  }
};
</script>

<template>
  <div class="section">
    <h2><Landmark :size="20" /> Upravljanje valutama</h2>

    <div class="currency-management">
      <div class="add-currency">
        <input
          type="text"
          v-model="newCurrency.name"
          placeholder="Kod valute (npr. USD)"
          :disabled="loading"
          @blur="fetchExchangeRate"
        />
        <input
          type="number"
          v-model="newCurrency.exchangeRate"
          placeholder="Kurs u odnosu na EUR"
          step="0.0001"
          :disabled="loading"
        />
        <button
          @click="fetchExchangeRate"
          class="btn-secondary"
          :disabled="fetchingRate || !newCurrency.name.trim()"
          title="Preuzmi kurs sa Frankfurter API"
        >
          <RefreshCw :size="14" :class="{ 'spinning': fetchingRate }" />
        </button>
        <button
          @click="addCurrency"
          class="btn-primary"
          :disabled="loading || !newCurrency.name.trim() || !newCurrency.exchangeRate"
        >
          Dodaj valutu
        </button>
      </div>

      <div class="currencies-list">
        <div v-if="loading" class="loading">Učitavanje...</div>
        <div v-else-if="currencies.length === 0" class="empty">Nema valuta</div>
        <div
          v-else
          v-for="currency in currencies"
          :key="currency.id"
          class="currency-item"
        >
          <div class="currency-info">
            <span class="currency-name">{{ currency.name }}</span>
            <span class="currency-rate">{{ currency.value?.toFixed(4) }} EUR</span>
          </div>
          <div class="currency-actions">
            <button
              @click="updateExchangeRate(currency)"
              class="btn-small btn-secondary"
              title="Ažuriraj kurs"
            >
              <RefreshCw :size="12" />
            </button>
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

.currency-management {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.add-currency {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.add-currency input {
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.9375rem;
  transition: all 0.15s ease;
}

.add-currency input:focus {
  outline: none;
  border-color: #7f5af0;
  box-shadow: 0 0 0 3px rgba(127, 90, 240, 0.1);
}

.add-currency input:disabled {
  background-color: #f9fafb;
  cursor: not-allowed;
}

.btn-primary {
  padding: 0.75rem 1.5rem;
  background: #7f5af0;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.9375rem;
  cursor: pointer;
  transition: all 0.15s ease;
  white-space: nowrap;
}

.btn-primary:hover:not(:disabled) {
  background: #6c47d9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(127, 90, 240, 0.25);
}

.btn-primary:disabled {
  background: #d1d5db;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-secondary {
  padding: 0.75rem;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-secondary:hover:not(:disabled) {
  background: #e5e7eb;
  border-color: #9ca3af;
}

.btn-secondary:disabled {
  background: #f9fafb;
  color: #d1d5db;
  cursor: not-allowed;
}

.spinning {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.currencies-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.currency-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f8f9fb;
  border-radius: 10px;
  border: 1px solid #e6e8ec;
  transition: all 0.15s ease;
}

.currency-item:hover {
  background: #f1f3f4;
  border-color: #d1d5db;
}

.currency-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  flex: 1;
}

.currency-name {
  font-size: 0.9375rem;
  font-weight: 600;
  color: #1a1d1f;
}

.currency-rate {
  font-size: 0.8125rem;
  color: #6f767e;
  font-weight: 500;
}

.currency-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-small {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.5rem;
  background: #6b7280;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.75rem;
  font-weight: 600;
  transition: all 0.15s ease;
}

.btn-small:hover:not(:disabled) {
  background: #4b5563;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(107, 114, 128, 0.25);
}

.btn-small:disabled {
  background: #d1d5db;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.loading {
  text-align: center;
  color: #6f767e;
  padding: 2rem;
  font-size: 0.9375rem;
}

.empty {
  text-align: center;
  color: #6f767e;
  padding: 3rem 2rem;
  font-size: 0.875rem;
}
</style>
