<script setup>
import { ref, onMounted, provide } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import axios from './api/axios';

const router = useRouter();
const route = useRoute();
const user = ref(null);
const showNav = ref(false);

// Check session on mount and after navigation
onMounted(async () => {
  await checkSession();
});

const checkSession = async () => {
  try {
    // Try to get current user from backend session
    const response = await axios.get('/auth/current');
    user.value = response.data;
    localStorage.setItem('user', JSON.stringify(response.data));
    showNav.value = true;
  } catch (error) {
    // No active session, check localStorage as fallback
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      user.value = JSON.parse(storedUser);
      showNav.value = true;
    } else {
      // Not logged in
      user.value = null;
      showNav.value = false;
      if (route.meta.requiresAuth) {
        router.push('/login');
      }
    }
  }
};

const handleLogin = (userData) => {
  user.value = userData;
  localStorage.setItem('user', JSON.stringify(userData));
  showNav.value = true;
};

const logout = async () => {
  try {
    await axios.post('/auth/logout');
  } catch (error) {
    console.error('Logout error:', error);
  } finally {
    localStorage.removeItem('user');
    user.value = null;
    showNav.value = false;
    router.push('/login');
  }
};

// Provide login handler to child components
provide('handleLogin', handleLogin);
</script>

<template>
  <div id="app">
    <!-- Navigation -->
    <nav v-if="showNav" class="navbar">
      <div class="nav-container">
        <h1 class="logo">Finance Tracker</h1>
        <div class="nav-links">
          <router-link to="/dashboard">Dashboard</router-link>
          <router-link to="/wallets">Novčanici</router-link>
          <router-link to="/transactions">Transakcije</router-link>
          <router-link to="/categories">Kategorije</router-link>
          <router-link to="/savings-goals">Ciljevi</router-link>
          <router-link v-if="user?.role === 'ADMINISTRATOR'" to="/admin">Admin</router-link>
          <button @click="logout" class="btn-logout">Odjava</button>
        </div>
      </div>
    </nav>

    <!-- Router View -->
    <div class="content">
      <router-view @login="handleLogin"></router-view>
    </div>
  </div>
</template>

<style>
* {
  font-family: 'Inter', -apple-system, sans-serif;
}

.navbar {
  background: #fff;
  border-bottom: 1px solid #e6e8ec;
  padding: 0;
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(8px);
  background: rgba(255, 255, 255, 0.98);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
  height: 64px;
}

.logo {
  font-size: 1.25rem;
  color: #1a1d1f;
  font-weight: 800;
  letter-spacing: -0.5px;
}

.nav-links {
  display: flex;
  gap: 0.25rem;
  align-items: center;
}

.nav-links a {
  color: #6f767e;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-weight: 500;
  font-size: 0.875rem;
  transition: all 0.15s ease;
}

.nav-links a:hover {
  color: #1a1d1f;
  background: #f2f4f7;
}

.nav-links a.router-link-active {
  color: #7f5af0;
  background: rgba(127, 90, 240, 0.08);
}

.btn-logout {
  background: #fff;
  color: #1a1d1f;
  border: 1px solid #e6e8ec;
  padding: 0.5rem 1.25rem;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  font-size: 0.875rem;
  margin-left: 0.5rem;
}

.btn-logout:hover {
  background: #f8f9fb;
  border-color: #d1d5db;
}

.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}
</style>
