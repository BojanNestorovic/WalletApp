<script setup>
import { ref, onMounted } from 'vue';
import axios from '../api/axios';

const categories = ref([]);
const showModal = ref(false);
const newCategory = ref({
  name: '',
  type: 'EXPENSE'
});
const loading = ref(false);
const error = ref('');

onMounted(async () => {
  await loadCategories();
});

const loadCategories = async () => {
  try {
    const response = await axios.get('/categories');
    categories.value = response.data;
  } catch (err) {
    console.error('Error loading categories:', err);
  }
};

const createCategory = async () => {
  if (!newCategory.value.name) {
    error.value = 'Unesite naziv kategorije';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    await axios.post('/categories', newCategory.value);
    await loadCategories();
    showModal.value = false;
    newCategory.value = { name: '', type: 'EXPENSE' };
  } catch (err) {
    error.value = err.response?.data?.error || 'Greška pri kreiranju kategorije';
  } finally {
    loading.value = false;
  }
};

const deleteCategory = async (catId) => {
  if (!confirm('Da li ste sigurni da želite da obrišete ovu kategoriju?')) return;

  try {
    await axios.delete(`/categories/${catId}`);
    await loadCategories();
  } catch (err) {
    alert('Greška pri brisanju kategorije');
  }
};

const incomeCategories = () => categories.value.filter(c => c.type === 'INCOME');
const expenseCategories = () => categories.value.filter(c => c.type === 'EXPENSE');
</script>

<template>
  <div class="categories-page">
    <div class="page-header">
      <h1>Kategorije</h1>
      <button @click="showModal = true" class="btn-primary">+ Nova kategorija</button>
    </div>

    <div class="categories-container">
      <div class="category-section">
        <h2>Income Categories</h2>
        <div class="categories-grid">
          <div v-for="cat in incomeCategories()" :key="cat.id" class="category-card income">
            <div class="category-info">
              <h3>{{ cat.name }}</h3>
              <span v-if="cat.predefined" class="badge">SYSTEM</span>
              <span v-else class="badge custom">CUSTOM</span>
            </div>
            <button v-if="!cat.predefined" @click="deleteCategory(cat.id)" class="btn-delete">
              Delete
            </button>
          </div>
        </div>
      </div>

      <div class="category-section">
        <h2>Expense Categories</h2>
        <div class="categories-grid">
          <div v-for="cat in expenseCategories()" :key="cat.id" class="category-card expense">
            <div class="category-info">
              <h3>{{ cat.name }}</h3>
              <span v-if="cat.predefined" class="badge">SYSTEM</span>
              <span v-else class="badge custom">CUSTOM</span>
            </div>
            <button v-if="!cat.predefined" @click="deleteCategory(cat.id)" class="btn-delete">
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Category Modal -->
    <div v-if="showModal" class="modal-overlay" @click.self="showModal = false">
      <div class="modal">
        <h2>Nova kategorija</h2>

        <form @submit.prevent="createCategory">
          <div class="form-group">
            <label>Tip kategorije</label>
            <select v-model="newCategory.type">
              <option value="INCOME">Income</option>
              <option value="EXPENSE">Expense</option>
            </select>
          </div>

          <div class="form-group">
            <label>Naziv *</label>
            <input v-model="newCategory.name" type="text" placeholder="npr. Freelance" required />
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
.categories-page {
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

.categories-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.category-section {
  background: #fff;
  padding: 1.5rem;
  border-radius: 12px;
  border: 1px solid #e6e8ec;
}

.category-section h2 {
  margin-bottom: 1.25rem;
  color: #1a1d1f;
  font-size: 1.125rem;
  font-weight: 700;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 0.75rem;
}

.category-card {
  padding: 1rem 1.25rem;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.15s ease;
  border: 1px solid;
}

.category-card.income {
  background: rgba(16, 185, 129, 0.08);
  border-color: rgba(16, 185, 129, 0.2);
  color: #059669;
}

.category-card.income:hover {
  background: rgba(16, 185, 129, 0.12);
  border-color: rgba(16, 185, 129, 0.3);
}

.category-card.expense {
  background: rgba(239, 68, 68, 0.08);
  border-color: rgba(239, 68, 68, 0.2);
  color: #dc2626;
}

.category-card.expense:hover {
  background: rgba(239, 68, 68, 0.12);
  border-color: rgba(239, 68, 68, 0.3);
}

.category-info h3 {
  margin: 0 0 0.25rem 0;
  font-size: 1rem;
  font-weight: 600;
}

.badge {
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.625rem;
  font-weight: 700;
  letter-spacing: 0.5px;
  text-transform: uppercase;
}

.category-card.income .badge {
  background: rgba(16, 185, 129, 0.15);
  color: #059669;
}

.category-card.expense .badge {
  background: rgba(239, 68, 68, 0.15);
  color: #dc2626;
}

.badge.custom {
  opacity: 0.8;
}

.btn-delete {
  background: transparent;
  border: 1px solid currentColor;
  color: inherit;
  padding: 0.5rem 0.75rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.8125rem;
  font-weight: 600;
  transition: all 0.15s ease;
  opacity: 0.7;
}

.btn-delete:hover {
  opacity: 1;
  background: rgba(0, 0, 0, 0.05);
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
  max-width: 420px;
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
