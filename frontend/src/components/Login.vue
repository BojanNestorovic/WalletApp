<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from '../api/axios';

const router = useRouter();
const emit = defineEmits(['login']);

const username = ref('');
const password = ref('');
const error = ref('');
const loading = ref(false);

const login = async () => {
  if (!username.value || !password.value) {
    error.value = 'Molimo popunite sva polja';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    const response = await axios.post('/auth/login', {
      username: username.value,
      password: password.value
    });

    // Store user in localStorage
    const userData = response.data.user;
    localStorage.setItem('user', JSON.stringify(userData));
    
    // Emit login event to parent (App.vue)
    emit('login', userData);
    
    // Redirect to dashboard
    await router.push('/dashboard');
    
  } catch (err) {
    error.value = err.response?.data?.error || 'Greška pri prijavljivanju';
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <h1>Dobrodošli nazad</h1>
        <p>Prijavite se na vaš Finance Tracker nalog</p>
      </div>
      
      <form @submit.prevent="login">
        <div class="form-group">
          <label>Korisničko ime</label>
          <input 
            v-model="username" 
            type="text" 
            placeholder="Unesite korisničko ime"
            required
          />
        </div>

        <div class="form-group">
          <label>Lozinka</label>
          <input 
            v-model="password" 
            type="password" 
            placeholder="Unesite lozinku"
            required
          />
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>

        <button type="submit" :disabled="loading" class="btn-primary">
          {{ loading ? 'Prijavljivanje...' : 'Prijavi se' }}
        </button>
      </form>

      <p class="auth-link">
        Nemate nalog? <router-link to="/register">Registrujte se</router-link>
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
  max-width: 420px;
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

.form-group input {
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

.form-group input:hover {
  border-color: #9ca3af;
}

.form-group input:focus {
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
