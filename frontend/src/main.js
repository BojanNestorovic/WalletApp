import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'

// Import components
import Login from './components/Login.vue'
import Register from './components/Register.vue'
import Dashboard from './components/Dashboard.vue'
import Wallets from './components/Wallets.vue'
import Transactions from './components/Transactions.vue'
import Categories from './components/Categories.vue'
import SavingsGoals from './components/SavingsGoals.vue'
import AdminDashboard from './components/AdminDashboard.vue'

// Router configuration
const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },
  { path: '/wallets', component: Wallets, meta: { requiresAuth: true } },
  { path: '/transactions', component: Transactions, meta: { requiresAuth: true } },
  { path: '/categories', component: Categories, meta: { requiresAuth: true } },
  { path: '/savings-goals', component: SavingsGoals, meta: { requiresAuth: true } },
  { path: '/admin', component: AdminDashboard, meta: { requiresAuth: true, requiresAdmin: true } }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  const user = JSON.parse(localStorage.getItem('user') || 'null');
  
  if (to.meta.requiresAuth && !user) {
    next('/login');
  } else if (to.meta.requiresAdmin && user?.role !== 'ADMINISTRATOR') {
    next('/dashboard');
  } else {
    next();
  }
});

const app = createApp(App);
app.use(router);
app.mount('#app');
