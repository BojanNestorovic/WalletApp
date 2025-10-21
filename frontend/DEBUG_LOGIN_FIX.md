# 🔧 Login/Session Fix -Deblokirano

## Problem koji je rešen

Korisnik se uspešno registrovao/prijavljivao, ali sesija se nije održavala i korisnik se vraćao na login stranicu.

## Uzrok problema

1. **Mešavina API-ja u App.vue**: Postojao je `<script setup>` (Composition API) i obični `<script>` (Options API) u istoj komponenti, što je izazivalo konflikt state-a
2. **window.location.reload()**: Login i Register komponente su koristile `window.location.reload()` što je resetovalo ceo Vue state
3. **Axios interceptor**: Automatski redirekcija na `/login` za svaki 401 error je ometala normalan flow

## Promene

### 1. App.vue - Kompletna Composition API refaktorizacija

**Dodato:**
- ✅ `checkSession()` funkcija koja proverava backend sesiju pri mount-u
- ✅ Provera da li korisnik ima aktivnu HttpSession na backendu (`GET /auth/current`)
- ✅ `provide('handleLogin')` za propagiranje login event-a deca komponentama
- ✅ Pravilno čišćenje state-a u `logout()` funkciji

**Uklonjeno:**
- ❌ Options API `<script>` blok
- ❌ Duplirani state management

**Pre:**
```vue
<script setup>
// Composition API state
</script>

<script>
export default {
  methods: {
    handleLogin() {
      // Options API metode - KONFLIKT!
    }
  }
}
</script>
```

**Posle:**
```vue
<script setup>
// Sve u Composition API
const handleLogin = (userData) => {
  user.value = userData;
  localStorage.setItem('user', JSON.stringify(userData));
  showNav.value = true;
};

// Provera sesije pri mount-u
const checkSession = async () => {
  try {
    const response = await axios.get('/auth/current');
    user.value = response.data;
    localStorage.setItem('user', JSON.stringify(response.data));
    showNav.value = true;
  } catch (error) {
    // Fallback na localStorage
  }
};
</script>
```

### 2. Login.vue - Emit event umesto reload

**Dodato:**
- ✅ `defineEmits(['login'])` za emitovanje event-a ka parent-u
- ✅ `emit('login', userData)` nakon uspešne prijave
- ✅ `await router.push('/dashboard')` sa async/await

**Uklonjeno:**
- ❌ `window.location.reload()` - ovo je resetovalo sve

**Pre:**
```javascript
localStorage.setItem('user', JSON.stringify(response.data.user));
router.push('/dashboard');
window.location.reload(); // ❌ Resetuje ceo state!
```

**Posle:**
```javascript
const userData = response.data.user;
localStorage.setItem('user', JSON.stringify(userData));
emit('login', userData); // ✅ Obaveštava App.vue
await router.push('/dashboard'); // ✅ Navigacija bez reload-a
```

### 3. Register.vue - Iste izmene kao Login

**Dodato:**
- ✅ `defineEmits(['login'])`
- ✅ Default currencyId postavljanje u onMounted
- ✅ Emit event nakon registracije

**Uklonjeno:**
- ❌ `window.location.reload()`

### 4. axios.js - Uklonjena auto-redirekcija

**Uklonjeno:**
```javascript
if (error.response && error.response.status === 401) {
  window.location.href = '/login'; // ❌ Preterana redirekcija
}
```

**Posle:**
```javascript
// Komponente same odlučuju šta da rade sa 401 errorom
return Promise.reject(error);
```

## Kako sada funkcioniše

### Flow registracije/prijave:

1. **Korisnik submitu-je formu** (Login/Register)
2. **Axios šalje POST** na `/auth/login` ili `/auth/register` **sa `withCredentials: true`**
3. **Backend kreira HttpSession** i vraća user podatke
4. **Frontend:**
   - Čuva user u `localStorage`
   - **Emit-uje `'login'` event** ka `App.vue`
   - Router naviguje na `/dashboard`
5. **App.vue prima event:**
   - `handleLogin(userData)` postavlja `user.value`
   - `showNav.value = true` prikazuje navigaciju
6. **Navigacija** se odvija **bez reload-a** (zadržava se Vue state)

### Session održavanje:

- **Pri svakom mount-u** `App.vue` poziva `checkSession()`
- **checkSession()** pokušava `GET /auth/current` da proveri backend sesiju
- Ako backend vrati korisnika → sesija je aktivna ✅
- Ako backend vrati 401 → fallback na localStorage

### CORS i Credentials:

```javascript
// axios.js
withCredentials: true // BITNO: Šalje session cookie
```

```java
// WebConfig.java (backend)
.allowCredentials(true) // BITNO: Prima session cookie
.allowedOrigins("http://localhost:5173")
```

## Testiranje

### 1. Registracija
```
1. Otvorite http://localhost:5173
2. Kliknite "Registrujte se"
3. Popunite formu
4. Nakon submit-a:
   ✅ Korisnik se ODMAH vidi u navigaciji
   ✅ Nema reload-a
   ✅ Dashboard se prikazuje
```

### 2. Prijava
```
1. Logout
2. Login sa kreiranim kredencijalima
3. Nakon submit-a:
   ✅ Navigacija se pojavljuje
   ✅ Korisnik ostaje prijavljen
```

### 3. Refresh stranice
```
1. Dok ste prijavljeni, F5 (refresh)
2. App.vue.checkSession() poziva backend
3. ✅ Sesija se održava (HttpSession na backendu)
4. ✅ Korisnik ostaje prijavljen
```

### 4. Navigacija između ruta
```
1. Kliknite Dashboard → Novčanici → Transakcije
2. ✅ Nijedna navigacija ne resetuje state
3. ✅ Korisnik ostaje prijavljen
```

## Dijagnostika problema (ako se ponovo javi)

### Browser Developer Tools:

1. **Network tab:**
   - Proverite da li `/auth/login` vraća `Set-Cookie` header
   - Proverite da li sledeći zahtevi šalju `Cookie` header

2. **Application tab → Cookies:**
   - Treba da vidite `JSESSIONID` cookie za `localhost:8080`

3. **Application tab → Local Storage:**
   - Treba da vidite `user` objekat

4. **Console:**
   - Proverite da li ima grešaka
   - Proverite da li `checkSession()` uspeva

### Backend provera:

```bash
# H2 Console: http://localhost:8080/api/h2-console
SELECT * FROM users;
```

## Sažetak konvencija

✅ **Composition API** (`<script setup>`) u svim komponentama  
✅ **ref()** za reaktivnost  
✅ **emit()** za parent-child komunikaciju  
✅ **Router.push()** umesto window.location  
✅ **withCredentials: true** za session cookies  
✅ **Backend session validation** pri mount-u  

❌ **Izbegavati window.location.reload()**  
❌ **Ne mešati Composition i Options API**  
❌ **Ne hardkodirati redirect u axios interceptor**  
