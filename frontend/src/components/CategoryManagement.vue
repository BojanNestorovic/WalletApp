<script setup>
import { ref, onMounted } from 'vue';
import axios from '../api/axios';
import { Tag, Trash2 } from 'lucide-vue-next';

const categories = ref([]);
const newCategoryName = ref('');
const loading = ref(false);

const loadCategories = async () => {
  try {
    loading.value = true;
    const response = await axios.get('/admin/categories');
    categories.value = response.data;
  } catch (err) {
    console.error('Error loading categories:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(loadCategories);

const addCategory = async () => {
  if (!newCategoryName.value.trim()) return;

  try {
    loading.value = true;
    await axios.post('/admin/categories', {
      name: newCategoryName.value,
      type: 'EXPENSE' // Default to expense
    });
    newCategoryName.value = '';
    await loadCategories();
  } catch (err) {
    console.error('Error adding category:', err);
    alert('Greška pri dodavanju kategorije');
  } finally {
    loading.value = false;
  }
};

const deleteCategory = async (categoryId) => {
  if (!confirm('Da li ste sigurni da želite obrisati ovu kategoriju?')) return;

  try {
    loading.value = true;
    await axios.delete(`/admin/categories/${categoryId}`);
    await loadCategories();
  } catch (err) {
    console.error('Error deleting category:', err);
    alert('Greška pri brisanju kategorije');
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="section">
    <h2><Tag :size="20" /> Upravljanje kategorijama</h2>

    <div class="category-management">
      <div class="add-category">
        <input
          type="text"
          v-model="newCategoryName"
          placeholder="Naziv nove kategorije"
          :disabled="loading"
        />
        <button
          @click="addCategory"
          class="btn-primary"
          :disabled="loading || !newCategoryName.trim()"
        >
          Dodaj kategoriju
        </button>
      </div>

      <div class="categories-list">
        <div v-if="loading" class="loading">Učitavanje...</div>
        <div v-else-if="categories.length === 0" class="empty">Nema kategorija</div>
        <div
          v-else
          v-for="category in categories"
          :key="category.id"
          class="category-item"
        >
          <div class="category-info">
            <span class="category-name">{{ category.name }}</span>
            <span class="category-type" :class="category.type">
              {{ category.type === 'INCOME' ? 'Prihod' : 'Trošak' }}
            </span>
          </div>
          <button
            @click="deleteCategory(category.id)"
            class="btn-small btn-danger"
            :disabled="loading"
          >
            <Trash2 :size="14" /> Obriši
          </button>
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

.category-management {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.add-category {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.add-category input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.9375rem;
  transition: all 0.15s ease;
}

.add-category input:focus {
  outline: none;
  border-color: #7f5af0;
  box-shadow: 0 0 0 3px rgba(127, 90, 240, 0.1);
}

.add-category input:disabled {
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

.categories-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f8f9fb;
  border-radius: 10px;
  border: 1px solid #e6e8ec;
  transition: all 0.15s ease;
}

.category-item:hover {
  background: #f1f3f4;
  border-color: #d1d5db;
}

.category-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  flex: 1;
}

.category-name {
  font-size: 0.9375rem;
  font-weight: 600;
  color: #1a1d1f;
}

.category-type {
  font-size: 0.75rem;
  font-weight: 500;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  width: fit-content;
}

.category-type.INCOME {
  background: rgba(16, 185, 129, 0.1);
  color: #10b981;
}

.category-type.EXPENSE {
  background: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.btn-small {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.5rem 0.75rem;
  background: #ef4444;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.8125rem;
  font-weight: 600;
  transition: all 0.15s ease;
}

.btn-small:hover:not(:disabled) {
  background: #dc2626;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.25);
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
