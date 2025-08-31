# Prva Kontrolna Tačka - Lični Finansijski Menadžer

## 📋 Pregled Implementacije

### ✅ Šta je implementirano:

#### 1. **Model Podataka (Entiteti)**
- **User** - Korisnik sa svim potrebnim poljima i vezaма
- **Currency** - Valuta sa konverzijom u EUR
- **Wallet** - Novčanik sa stanjem i valutom
- **Category** - Kategorija transakcija (prihod/trošak)
- **Transaction** - Transakcija sa svim detaljima
- **SavingsGoal** - Cilj štednje sa napretkom

#### 2. **JPA Anotacije**
- `@Entity` - za sve entitete
- `@ManyToOne` - za veze sa roditeljskim entitetima
- `@OneToMany` - za veze sa child entitetima
- `@JoinColumn` - za specifikaciju stranih ključeva
- `@Enumerated` - za enum tipove
- `@Temporal` - za datum polja
- `@Column` - za konfiguraciju kolona

#### 3. **Repository Sloj**
- **UserRepository** - upravljanje korisnicima
- **CurrencyRepository** - upravljanje valutama
- **WalletRepository** - upravljanje novčanicima
- **CategoryRepository** - upravljanje kategorijama
- **TransactionRepository** - upravljanje transakcijama
- **SavingsGoalRepository** - upravljanje ciljevima štednje

#### 4. **Konfiguracija**
- **H2 Database** - in-memory baza za testiranje
- **Spring Security** - osnovna konfiguracija
- **Maven** - sve potrebne zavisnosti
- **Testovi** - unit testovi za entitete

## 🚀 Kako pokrenuti aplikaciju:

### 1. **Pokretanje aplikacije:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### 2. **Verifikacija:**
- **Status stranica:** http://localhost:8080/api/
- **H2 Console:** http://localhost:8080/api/h2-console
- **Health check:** http://localhost:8080/api/health

### 3. **H2 Console pristup:**
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** (prazno)

## 📊 Šta možete videti:

### **Status stranica** prikazuje:
- Vreme pokretanja
- Status baze podataka
- Listu svih entiteta
- Repository sloj status
- Dostupne endpointove
- Status kontrolne tačke

### **H2 Console** omogućava:
- Pregled svih tabela
- SQL upite
- Pregled podataka
- Verifikaciju strukture baze

## 🔗 Veze između entiteta:

```
User (1) ←→ (N) Wallet
User (1) ←→ (N) Transaction  
User (1) ←→ (N) Category
User (1) ←→ (N) SavingsGoal
User (N) ←→ (1) Currency

Wallet (1) ←→ (N) Transaction
Wallet (1) ←→ (N) SavingsGoal
Wallet (N) ←→ (1) Currency

Category (1) ←→ (N) Transaction
```

## 📝 Enums:

- **Role:** USER, ADMINISTRATOR
- **CategoryType:** INCOME, EXPENSE
- **TransactionType:** INCOME, EXPENSE
- **Frequency:** WEEKLY, MONTHLY, QUARTERLY, YEARLY

## 🎯 Prva kontrolna tačka je ZAVRŠENA!

Svi zahtevi su ispunjeni:
- ✅ Model podataka sa JPA anotacijama
- ✅ Repository sloj implementiran
- ✅ Aplikacija se pokreće i radi
- ✅ Testovi prolaze
- ✅ Dokumentacija dostupna

## 📁 Struktura projekta:

```
src/main/java/com/example/WalletApp/
├── DemoAplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   └── HomeController.java
├── entity/
│   ├── User.java
│   ├── Currency.java
│   ├── Wallet.java
│   ├── Category.java
│   ├── Transaction.java
│   └── SavingsGoal.java
└── repository/
    ├── UserRepository.java
    ├── CurrencyRepository.java
    ├── WalletRepository.java
    ├── CategoryRepository.java
    ├── TransactionRepository.java
    └── SavingsGoalRepository.java
```

## 🔄 GitHub Pull Request:

- **Branch:** `feature/prva-kontrolna-tacka`
- **Status:** Spreman za review
- **Commit:** `06cde89` - Prva kontrolna tačka implementacija

---

**Napomena:** Ova implementacija koristi H2 in-memory bazu podataka za testiranje, što je dovoljno za prvu kontrolnu tačku. MySQL konfiguracija je pripremljena za kasnije faze projekta.
