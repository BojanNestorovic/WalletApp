# Lični Finansijski Menadžer

Dobrodošli na projekat **Lični Finansijski Menadžer**, veb aplikacija razvijena kao deo kursa **Veb programiranje 2 (WEB II)** za školsku 2024/25. godinu.

Aplikacija omogućava korisnicima praćenje prihoda, rashoda, postavljanje ciljeva štednje i analizu finansija kroz interaktivne izveštaje i statistiku. Projekat je izrađen korišćenjem **Spring Boota** za backend i **Vue.js**-a za frontend, sa fokusom na sigurnost, korisničko iskustvo i funkcionalnost.

---

## Opis funkcionalnosti

### Neprijavljeni korisnik
- Pregled početne stranice sa opisom aplikacije
- Prikaz ukupnog broja registrovanih korisnika (bez detalja o pojedinačnim korisnicima)

### Registrovani korisnik (uloga: Korisnik)
- Upravljanje novčanicima: dodavanje, uređivanje, arhiviranje i praćenje stanja
- Unos i pregled transakcija (prihodi i troškovi)
- Prebacivanje sredstava između novčanika sa automatskom konverzijom valuta
- Dodavanje i upravljanje sopstvenim kategorijama (pored predefinisanih)
- Definisanje ponavljajućih transakcija (npr. plata, kirija)
- Postavljanje ciljeva štednje sa rokom i iznosom, uz vizuelni prikaz napretka
- Pregled statistike po danima, nedeljama, mesecima i godinama
- Grafički prikaz rashoda po kategorijama (Chart.js)
- Prikaz najvećih troškova (top 10) za izabrani period
- Izmena ličnih podataka i podešavanja profila (valuta, profilna slika)

### Administrator
- Blokiranje i odblokiranje korisnika
- Pregled svih transakcija svih korisnika sa mogućnošću filtera i sortiranja
- Dodavanje internih beleški o korisnicima (vidljivo samo administratoru)
- Upravljanje predefinisanim kategorijama (dodavanje, brisanje)
- Upravljanje valutama: ručno ažuriranje kursne liste ili automatsko preko javnog API-ja (npr. frankfurter.dev)
- Dashboard sa pregledom:
  - Ukupan broj korisnika
  - Broj aktivnih korisnika (u poslednjih mesec dana)
  - Prosečno i ukupno stanje svih korisnika
  - Top 10 najvećih transakcija u poslednjih 30 dana
  - Top 10 najvećih transakcija u poslednja 2 minuta (za testiranje)

---

## Tehnologije
| Deo | Tehnologije |
|-----|-------------|
| **Backend** | Java, Spring Boot, Spring Security, Spring Data JPA |
| **Frontend** | HTML, CSS, JavaScript, Vue.js |
| **Baza podataka** | MySQL ili PostgreSQL |
| **Dodatne biblioteke** | Chart.js (grafikoni), Axios (HTTP pozivi), Bootstrap (UI) |
| **Autentifikacija** | JWT tokeni |
| **Valute** | Podrška za RSD, EUR, USD i druge sa konverzijom |

---

## Model podataka
| Entitet | Atributi |
|---------|----------|
| **Korisnik** | ime, prezime, korisničko ime, email, lozinka (heširana), datum rođenja, uloga, profilna slika, valuta, datum registracije, blokiran |
| **Valuta** | naziv, vrednost u odnosu na EUR |
| **Novčanik** | naziv, početno i trenutno stanje, valuta, datum kreiranja, korisnik, štedni, arhiviran |
| **Kategorija** | naziv, tip (prihod/trošak), predefinisana, referenca na korisnika (null za globalne) |
| **Transakcija** | naziv, iznos, tip, kategorija, datum, ponavljajuća, učestalost, novčanik, korisnik |

---

## Sigurnost
- Lozinke se čuvaju heširane pomoću BCrypt algoritma
- Pristup zaštićen JWT autentifikacijom
- Autorizacija na osnovu uloge (Korisnik, Administrator)
- Korisnici imaju pristup samo sopstvenim podacima

---

## Instalacija i pokretanje
1. Klonirajte repozitorijum:
   ```bash
   git clone [link-ka-repozitorijumu]
