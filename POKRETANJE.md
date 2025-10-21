# 🚀 Brze instrukcije za pokretanje

## 1️⃣ Backend (Spring Boot)

```bash
cd /home/vuk/Downloads/WalletApp
mvn spring-boot:run
```

**Backend pokrenut na**: `http://localhost:8080/api`

## 2️⃣ Frontend (Vue.js)

```bash
cd /home/vuk/Downloads/WalletApp/frontend
npm install
npm run dev
```

**Frontend pokrenut na**: `http://localhost:5173`

---

## 🎯 Brzi test aplikacije

### 1. Registracija
1. Otvorite browser: http://localhost:5173
2. Kliknite "Registrujte se"
3. Popunite formu (korisničko ime: `test`, lozinka: `test123`)
4. Odaberite valutu (npr. RSD)

### 2. Kreiraj novčanike
1. Idite na "Novčanici"
2. Kreirajte prvi novčanik:
   - Naziv: "Glavni račun"
   - Početno stanje: 50000
   - Valuta: RSD
3. Kreirajte drugi novčanik:
   - Naziv: "EUR štednja"
   - Početno stanje: 500
   - Valuta: EUR

### 3. Dodaj transakcije
1. Idite na "Transakcije"
2. Kliknite "+ Nova transakcija"
3. Dodajte prihod (plata 100000 RSD)
4. Dodajte rashod (kirija 20000 RSD)

### 4. Transfer sa konverzijom
1. Na "Transakcije" kliknite "💸 Transfer"
2. Izborni novčanik: "Glavni račun" (RSD)
3. Odredišni: "EUR štednja" (EUR)
4. Iznos: 10000 RSD
5. **Aplikacija automatski konvertuje kroz EUR kurs!**

### 5. Cilj štednje
1. Idite na "Ciljevi"
2. Kreirajte cilj:
   - Naziv: "Odmor"
   - Iznos: 100000 RSD
   - Rok: Izaberite datum u budućnosti
3. Pratite grafički napredak

---

## 🔑 Test korisnici

### Obični korisnik (kreirati kroz registraciju)
- **Username**: test
- **Password**: test123
- **Uloga**: USER

### Admin korisnik (kreirati ručno kroz H2 Console)
1. Otvorite: http://localhost:8080/api/h2-console
2. JDBC URL: `jdbc:h2:mem:walletdb`
3. Username: `sa`, Password: (prazno)
4. SQL:
```sql
INSERT INTO users (first_name, last_name, username, email, password, birth_date, role, date_of_registration, blocked, currency_id) 
VALUES ('Admin', 'User', 'admin', 'admin@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '1990-01-01', 'ADMINISTRATOR', CURRENT_TIMESTAMP, false, 1);
```
- **Username**: admin
- **Password**: admin123

Zatim pristupite: http://localhost:5173/admin

---

## ✅ Šta je implementirano

### Backend (Spring Boot)
- ✅ **JDK 17** sa H2 in-memory bazom
- ✅ **Model-Service-Controller** arhitektura
- ✅ **6 entiteta**: User, Wallet, Transaction, Category, Currency, SavingsGoal, AdminNote
- ✅ **7 repository** interfejsa sa custom queries
- ✅ **6 service** klasa sa `@Transactional` logikom
- ✅ **6 RESTful controller** sa svim CRUD operacijama
- ✅ **DTO pattern** za sve entitete
- ✅ **BCrypt** heširanje lozinki
- ✅ **HttpSession** autentifikacija
- ✅ **CORS** sa `allowCredentials: true`
- ✅ **Atomični transferi** sa konverzijom valuta
- ✅ **Admin dashboard** sa metrikama
- ✅ **Admin notes** sistem
- ✅ **Bonus**: Frankfurter API integracija za valute

### Frontend (Vue.js)
- ✅ **Vue 3 Composition API** (`<script setup>`)
- ✅ **Vue Router** sa navigation guards
- ✅ **Axios** sa `withCredentials: true`
- ✅ **8 komponenti**: Login, Register, Dashboard, Wallets, Transactions, Categories, SavingsGoals, AdminDashboard
- ✅ **Reactive state** sa `ref()` i `reactive()`
- ✅ **v-if**, **v-for**, **v-model** direktive
- ✅ **Moderni UI** sa gradijentima i animacijama
- ✅ **Responsivan dizajn**

### Funkcionalne celina
- ✅ **CRUD** za sve entitete
- ✅ **Transfer sredstava** između novčanika sa konverzijom
- ✅ **Ponavljajuće transakcije**
- ✅ **Ciljevi štednje** sa napretkom
- ✅ **Admin monitoring**: Top 10 transakcija (30 dana i 2 minuta)
- ✅ **Blokiraje/odblokiraje korisnika**
- ✅ **Admin beleške**
- ✅ **Statistika** i dashboard metrike

---

## 📊 API Endpoints (primer testiranja)

### Testiranje kroz curl

```bash
# 1. Registracija
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{"firstName":"Test","lastName":"User","username":"test","email":"test@test.com","password":"test123","birthDate":"1990-01-01","currencyId":1}'

# 2. Prijava
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{"username":"test","password":"test123"}'

# 3. Kreiranje novčanika (koristi sesiju iz cookies)
curl -X POST http://localhost:8080/api/wallets \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{"name":"Test Wallet","initialBalance":10000,"currencyId":1,"savings":false}'

# 4. Pregled novčanika
curl -X GET http://localhost:8080/api/wallets \
  -b cookies.txt
```

---

## 🐛 Troubleshooting

### Backend ne startuje
```bash
# Proverite Java verziju
java -version  # Treba biti 17

# Očistite Maven cache
mvn clean install -U
```

### Frontend greška sa CORS
- Proverite da li je backend pokrenut
- Proverite `axios.js` - mora imati `withCredentials: true`
- Proverite `WebConfig.java` - mora imati `allowCredentials(true)`

### Session se ne održava
- Axios **MORA** imati `withCredentials: true`
- CORS **MORA** imati `allowCredentials(true)`
- Cookies moraju biti omogućeni u browseru

---

## 📦 Zavisnosti

### Backend (pom.xml)
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- spring-boot-starter-security (samo za BCrypt)
- h2 database

### Frontend (package.json)
- vue@^3.4.0
- vue-router@^4.2.5
- axios@^1.6.0
- chart.js@^4.4.0 (opciono)

---

## 🎓 Akademske konvencije

### Ispunjeni zahtevi
- ✅ JDK 17
- ✅ Spring Boot
- ✅ H2 baza
- ✅ JPA/Hibernate sa FetchType.LAZY
- ✅ RESTful principi (GET, POST, PUT, DELETE)
- ✅ Path Variables i Query Params
- ✅ DTO objekti
- ✅ @Transactional za atomične operacije
- ✅ HttpSession autentifikacija
- ✅ Vue.js SPA
- ✅ Axios sa withCredentials
- ✅ CORS konfiguracija
- ✅ BCrypt password hashing

---

**Aplikacija je spremna za testiranje i odbranu! 🎉**
