# Setup Guide - Prva Kontrolna Tačka

## Preduvjeti
- Java 11 ili noviji
- Maven 3.6+
- MySQL 8.0
- Git

## Postupak pokretanja

### 1. Kloniranje projekta
```bash
git clone <repository-url>
cd WalletApp
```

### 2. Konfiguracija baze podataka
1. Kreirajte MySQL bazu podataka:
```sql
CREATE DATABASE wallet_app CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Ažurirajte `src/main/resources/application.properties` sa vašim podacima za bazu:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Pokretanje aplikacije
```bash
mvn clean install
mvn spring-boot:run
```

### 4. Testiranje
Aplikacija će biti dostupna na: `http://localhost:8080/api`

Za pregled modela podataka otvorite: `http://localhost:8080/index.html`

### 5. Pokretanje testova
```bash
mvn test
```

## Struktura projekta

```
src/
├── main/
│   ├── java/com/example/WalletApp/
│   │   ├── DemoAplication.java          # Glavna aplikacija
│   │   ├── entity/                      # Entiteti
│   │   │   ├── User.java
│   │   │   ├── Currency.java
│   │   │   ├── Wallet.java
│   │   │   ├── Category.java
│   │   │   ├── Transaction.java
│   │   │   └── SavingsGoal.java
│   │   └── repository/                  # Repository sloj
│   │       ├── UserRepository.java
│   │       ├── CurrencyRepository.java
│   │       ├── WalletRepository.java
│   │       ├── CategoryRepository.java
│   │       ├── TransactionRepository.java
│   │       └── SavingsGoalRepository.java
│   └── resources/
│       ├── application.properties       # Konfiguracija
│       └── data.sql                     # Inicijalni podaci
├── test/
│   └── java/com/example/WalletApp/
│       ├── WalletAppApplicationTests.java
│       └── entity/
│           └── UserTest.java
└── index.html                           # Frontend demo
```

## Verifikacija prve kontrolne tačke

### ✅ Što je implementirano:
1. **Model podataka** - Svi entiteti prema specifikaciji
2. **Veze između entiteta** - JPA anotacije i relacije
3. **Repository sloj** - Spring Data JPA repository interfejsi
4. **Konfiguracija** - Spring Boot aplikacija sa MySQL
5. **Inicijalni podaci** - Valute i predefinisane kategorije
6. **Testovi** - Osnovni testovi za verifikaciju
7. **Dokumentacija** - README i setup guide

### 🔄 Što sledi u drugoj kontrolnoj tački:
- Service sloj
- Controller sloj (REST API)
- Spring Security konfiguracija
- JWT autentifikacija
- Validacija podataka
- Exception handling

## Troubleshooting

### Problem: Baza podataka se ne povezuje
- Proverite da li je MySQL pokrenut
- Proverite korisničko ime i lozinku u `application.properties`
- Proverite da li baza `wallet_app` postoji

### Problem: Maven ne može da preuzme dependencies
- Proverite internet konekciju
- Pokušajte `mvn clean install -U`

### Problem: Aplikacija se ne pokreće
- Proverite da li koristite Java 11+
- Proverite logove za greške
- Proverite da li je port 8080 slobodan
