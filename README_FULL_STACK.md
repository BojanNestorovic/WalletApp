# Personal Finance Tracker - Full Stack Aplikacija

## 📋 Opis projekta

Full-stack veb aplikacija za lično finansijsko praćenje razvijena prema zahtevima kursa **Veb programiranje 2024/25**.

Aplikacija omogućava korisnicima:
- ✅ Upravljanje virtuelnim novčanicima
- ✅ Praćenje prihoda i rashoda
- ✅ Transfer sredstava između novčanika sa automatskom konverzijom valuta
- ✅ Postavljanje i praćenje ciljeva štednje
- ✅ Ponavljajuće transakcije
- ✅ Admin panel sa monitoring funkcionalnostima

---

## 🛠️ Tehnološki stak

### Backend
- **Java 17** (JDK 17)
- **Spring Boot 2.7.18**
- **Spring Data JPA** (Hibernate)
- **H2 Database** (In-memory)
- **Spring Security** (BCrypt password hashing)
- **Maven** build tool

### Frontend
- **Vue.js 3** (Composition API)
- **Vue Router** (SPA routing)
- **Axios** (HTTP klijent sa `withCredentials: true`)
- **Vite** (Dev server & build tool)
- **Chart.js** (Grafikoni - opciono)

### Arhitektura
- **3-tier architecture**: Model-Service-Controller
- **RESTful API** sa standardnim HTTP metodama (GET, POST, PUT, DELETE)
- **Session-based authentication** (HttpSession)
- **DTO Pattern** za razmenu podataka
- **@Transactional** za atomične operacije (posebno za transfere)
- **CORS** konfiguracija sa `allowCredentials: true`

---

## 📁 Struktura projekta

```
WalletApp/
├── src/main/java/com/example/WalletApp/
│   ├── entity/          # JPA entiteti (@Entity)
│   ├── repository/      # JpaRepository interfejsi
│   ├── service/         # Poslovna logika (@Service, @Transactional)
│   ├── controller/      # REST kontroleri (@RestController)
│   ├── dto/             # Data Transfer Objects
│   └── config/          # Konfiguracija (CORS, BCrypt)
├── src/main/resources/
│   ├── application.properties  # H2, JPA, Session config
│   └── data.sql         # Početni podaci (valute, kategorije)
└── frontend/
    ├── src/
    │   ├── components/  # Vue komponente
    │   ├── api/         # Axios konfiguracija
    │   └── main.js      # Router & app inicijalizacija
    ├── package.json
    └── vite.config.js
```

---

## 🚀 Instalacija i pokretanje

### Preduslov
- **Java 17** (JDK)
- **Node.js** (v18+)
- **Maven** (3.6+)

### Backend (Spring Boot)

```bash
# 1. Navigacija u backend direktorijum
cd /home/vuk/Downloads/WalletApp

# 2. Build projekta (preuzimanje zavisnosti)
mvn clean install

# 3. Pokretanje Spring Boot aplikacije
mvn spring-boot:run

# Backend će biti dostupan na: http://localhost:8080/api
```

**H2 Console**: http://localhost:8080/api/h2-console  
- **JDBC URL**: `jdbc:h2:mem:walletdb`
- **Username**: `sa`
- **Password**: (prazno)

### Frontend (Vue.js)

```bash
# 1. Navigacija u frontend direktorijum
cd /home/vuk/Downloads/WalletApp/frontend

# 2. Instalacija npm zavisnosti
npm install

# 3. Pokretanje development servera
npm run dev

# Frontend će biti dostupan na: http://localhost:5173
```

---

## 🔑 API Endpoints

### Autentifikacija
- `POST /api/auth/register` - Registracija korisnika
- `POST /api/auth/login` - Prijava (HttpSession)
- `GET /api/auth/current` - Trenutni korisnik
- `POST /api/auth/logout` - Odjava

### Novčanici
- `GET /api/wallets` - Lista novčanika
- `POST /api/wallets` - Kreiraj novčanik
- `GET /api/wallets/{id}` - Detalji novčanika
- `PUT /api/wallets/{id}` - Ažuriraj novčanik
- `DELETE /api/wallets/{id}` - Obriši novčanik
- `PUT /api/wallets/{id}/archive` - Arhiviraj novčanik

### Transakcije
- `GET /api/transactions` - Lista transakcija
- `POST /api/transactions` - Kreiraj transakciju
- `POST /api/transactions/transfer` - **Transfer sa konverzijom valuta**
- `DELETE /api/transactions/{id}` - Obriši transakciju
- `GET /api/transactions/range?startDate=...&endDate=...` - Filtrirane transakcije

### Kategorije
- `GET /api/categories` - Sve kategorije (predefinisane + korisničke)
- `POST /api/categories` - Kreiraj korisničku kategoriju
- `DELETE /api/categories/{id}` - Obriši korisničku kategoriju

### Valute
- `GET /api/currencies` - Sve valute
- `POST /api/currencies` - Kreiraj valutu (admin)
- `PUT /api/currencies/{id}` - Ažuriraj valutu (admin)
- `PUT /api/currencies/{id}/update-from-api` - **Bonus: Ažuriraj sa Frankfurter API**

### Ciljevi štednje
- `GET /api/savings-goals` - Lista ciljeva
- `POST /api/savings-goals` - Kreiraj cilj
- `PUT /api/savings-goals/{id}` - Ažuriraj cilj
- `DELETE /api/savings-goals/{id}` - Obriši cilj

### Admin (samo za ADMINISTRATOR)
- `GET /api/admin/users` - Svi korisnici
- `PUT /api/admin/users/{id}/toggle-block` - Blokiraj/odblokiraj
- `POST /api/admin/users/{id}/notes` - Dodaj belešku o korisniku
- `GET /api/admin/users/{id}/notes` - Beleške o korisniku
- `GET /api/admin/transactions` - Sve transakcije
- `GET /api/admin/dashboard` - **Dashboard metrike**:
  - Ukupan broj korisnika
  - Broj aktivnih korisnika
  - Prosečno stanje novčanika
  - Ukupno stanje sistema
  - Top 10 transakcija (30 dana)
  - Top 10 transakcija (2 minuta) - za testiranje

---

## 🔐 Sigurnost

- **BCrypt** za heširanje lozinki
- **HttpSession** za održavanje sesije (bez JWT)
- **CORS** konfiguracija sa `allowCredentials: true`
- **Axios** sa `withCredentials: true` (neophodan za slanje session cookie)

### Uloge korisnika
- **USER**: Može upravljati svojim podacima
- **ADMINISTRATOR**: Puni pristup svim podacima i korisnicima

---

## 💡 Ključne funkcionalnosti

### 1. Transfer sredstava sa konverzijom valuta
```java
@Transactional // KRITIČNO: Atomična operacija
public void transferFunds(TransferDTO transferDTO, Long userId) {
    // 1. Validacija novčanika
    // 2. Provera stanja
    // 3. Konverzija kroz EUR kao baznu valutu
    // 4. Atomično ažuriranje oba novčanika
    // 5. Kreiranje dve transakcije (EXPENSE i INCOME)
}
```

### 2. Ponavljajuće transakcije
Transakcije sa flegom `repeating=true` i definisanom učestalošću (WEEKLY, MONTHLY, QUARTERLY, YEARLY).

### 3. Ciljevi štednje sa napretkom
- Grafički prikaz napretka
- Indikatori: `onTrack`, `overdue`, `completed`
- Automatsko praćenje stanja štednog novčanika

### 4. Admin monitoring
- Dashboard sa realtime metrikama
- Beleške o korisnicima (vidljive samo adminu)
- Top 10 transakcija u različitim vremenskim periodima

---

## 📊 Model podataka

### Entiteti
1. **User** - Korisnik sa profilom i ulogom
2. **Currency** - Valuta sa kursom u odnosu na EUR
3. **Wallet** - Novčanik sa stanjem i valutom
4. **Category** - Kategorija (prihod/rashod), predefinisana ili korisnička
5. **Transaction** - Transakcija sa svim detaljima
6. **SavingsGoal** - Cilj štednje sa napretkom
7. **AdminNote** - Beleške admina o korisnicima

### Relacije
- User 1:N Wallet
- User 1:N Transaction
- User 1:N Category (custom)
- User 1:N SavingsGoal
- Wallet 1:N Transaction
- Wallet 1:N SavingsGoal
- Category 1:N Transaction
- Currency 1:N Wallet
- Currency 1:N User

---

## 🧪 Testiranje

### Preporučena sekvenca testiranja

1. **Registracija korisnika**
   - Otvorite http://localhost:5173
   - Kliknite "Registrujte se"
   - Popunite formu

2. **Kreiranje novčanika**
   - Nakon prijave, idite na "Novčanici"
   - Kreirajte 2 novčanika sa različitim valutama (npr. RSD i EUR)

3. **Dodavanje transakcija**
   - Dodajte nekoliko INCOME i EXPENSE transakcija

4. **Transfer sa konverzijom**
   - Idite na "Transakcije" -> "Transfer"
   - Transferujte sredstva između novčanika različitih valuta
   - Proverite automatsku konverziju

5. **Cilj štednje**
   - Kreirajte štedni novčanik
   - Dodajte cilj štednje
   - Dodajte transakciju u štedni novčanik i proverite napredak

6. **Admin funkcionalnosti** (kreirajte admin korisnika ručno u bazi)
   - Pristupite http://localhost:5173/admin
   - Proverite Dashboard metrike
   - Dodajte belešku o korisniku

---

## ⚙️ Konfiguracija

### application.properties

```properties
# H2 Database
spring.datasource.url=jdbc:h2:mem:walletdb
spring.h2.console.enabled=true

# JPA
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.defer-datasource-initialization=true

# Session
server.servlet.session.timeout=30m
server.servlet.session.cookie.http-only=true

# Server
server.port=8080
server.servlet.context-path=/api
```

### CORS (WebConfig.java)

```java
registry.addMapping("/**")
    .allowedOrigins("http://localhost:5173")
    .allowedMethods("GET", "POST", "PUT", "DELETE")
    .allowCredentials(true); // KRITIČNO
```

### Axios (axios.js)

```javascript
const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true // KRITIČNO za sesiju
});
```

---

## 📝 Git konvencije (za ocenjivanje)

- Minimalno **2 grane** i **5 komita** po članu tima za CT2 i CT3
- Kontinuitet rada minimum **1 nedelja**
- Dokazati razvoj kroz Git istoriju

---

## 👥 Tim

Projekat razvijen kao deo kursa **Veb programiranje 2024/25**.

---

## 📄 Licenca

Projekat je kreiran u edukativne svrhe.
