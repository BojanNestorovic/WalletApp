<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from '../api/axios';

const router = useRouter();
const emit = defineEmits(['login']);

const currencies = ref([]);
const formData = ref({
  firstName: '',
  lastName: '',
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  birthDate: '',
  currencyId: null
});
const error = ref('');
const loading = ref(false);

onMounted(async () => {
  try {
    const response = await axios.get('/currencies');
    currencies.value = response.data;
    if (currencies.value.length > 0) {
      formData.value.currencyId = currencies.value[0].id;
    }
  } catch (err) {
    console.error('Error loading currencies:', err);
  }
});

const register = async () => {
  // Validation
  if (!formData.value.firstName || !formData.value.lastName || !formData.value.username || 
      !formData.value.email || !formData.value.password || !formData.value.birthDate) {
    error.value = 'Molimo popunite sva obavezna polja';
    return;
  }

  if (formData.value.password !== formData.value.confirmPassword) {
    error.value = 'Lozinke se ne poklapaju';
    return;
  }

  if (formData.value.password.length < 6) {
    error.value = 'Lozinka mora imati najmanje 6 karaktera';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    const response = await axios.post('/auth/register', {
      firstName: formData.value.firstName,
      lastName: formData.value.lastName,
      username: formData.value.username,
      email: formData.value.email,
      password: formData.value.password,
      birthDate: new Date(formData.value.birthDate),
      currencyId: formData.value.currencyId || 1 // Default to first currency
    });

    // Store user in localStorage
    const userData = response.data.user;
    localStorage.setItem('user', JSON.stringify(userData));
    
    // Emit login event to parent (App.vue)
    emit('login', userData);
    
    // Redirect to dashboard
    await router.push('/dashboard');
    
  } catch (err) {
    error.value = err.response?.data?.error || 'Greška pri registraciji';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <h1>Kreirajte nalog</h1>
        <p>Pridružite se Finance Tracker platformi</p>
      </div>
      
      <form @submit.prevent="register">
        <div class="form-row">
          <div class="form-group">
            <label>Ime *</label>
            <input v-model="formData.firstName" type="text" required />
          </div>

          <div class="form-group">
            <label>Prezime *</label>
            <input v-model="formData.lastName" type="text" required />
          </div>
        </div>

        <div class="form-group">
          <label>Korisničko ime *</label>
          <input v-model="formData.username" type="text" required />
        </div>

        <div class="form-group">
          <label>Email *</label>
          <input v-model="formData.email" type="email" required />
        </div>

        <div class="form-group">
          <label>Datum rođenja *</label>
          <input v-model="formData.birthDate" type="date" required />
        </div>

        <div class="form-group">
          <label>Valuta</label>
          <select v-model="formData.currencyId">
            <option v-for="currency in currencies" :key="currency.id" :value="currency.id">
              {{ currency.name }}
            </option>
          </select>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Lozinka *</label>
            <input v-model="formData.password" type="password" required />
          </div>

          <div class="form-group">
            <label>Potvrdi lozinku *</label>
            <input v-model="formData.confirmPassword" type="password" required />
          </div>
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>

        <button type="submit" :disabled="loading" class="btn-primary">
          {{ loading ? 'Registracija...' : 'Registruj se' }}
        </button>
      </form>

      <p class="auth-link">
        Već imate nalog? <router-link to="/login">Prijavite se</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.auth-container {
  min-height: calc(100vh - 64px);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 2rem;
  background: #f8f9fb;
}

.auth-card {
  background: #fff;
  padding: 2.5rem;
  border-radius: 16px;
  border: 1px solid #e6e8ec;
  width: 100%;
  max-width: 520px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.auth-header h1 {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1a1d1f;
  margin-bottom: 0.5rem;
  letter-spacing: -0.5px;
}

.auth-header p {
  color: #6f767e;
  font-size: 0.9375rem;
  margin-bottom: 2rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
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

.form-group input::placeholder {
  color: #9ca3af;
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

.btn-primary {
  width: 100%;
  padding: 0.875rem;
  background: #7f5af0;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 0.9375rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 0.5rem;
  transition: all 0.15s ease;
}

.btn-primary:hover:not(:disabled) {
  background: #6c47d9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(127, 90, 240, 0.25);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
}

.btn-primary:disabled {
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

.auth-link {
  text-align: center;
  margin-top: 1.5rem;
  color: #6f767e;
  font-size: 0.875rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e6e8ec;
}

.auth-link a {
  color: #7f5af0;
  text-decoration: none;
  font-weight: 600;
}

.auth-link a:hover {
  text-decoration: underline;
}
</style>
