package com.example.WalletApp.service.impl;

import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.entity.Role;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.exception.ValidationException;
import com.example.WalletApp.repository.CurrencyRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementacija servisa za upravljanje korisnicima
 * 
 * Ova klasa sadrži sve poslovne logike vezane za korisnike:
 * - Registraciju novih korisnika
 * - Ažuriranje postojećih korisnika
 * - Brisanje korisnika
 * - Pretragu i filtriranje korisnika
 * - Upravljanje statusom korisnika (blokiranje/odblokiranje)
 * - Konverziju između entiteta i DTO objekata
 * 
 * @author vuksta
 * @version 1.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    /**
     * Registruje novog korisnika u sistem
     * 
     * Pre čuvanja korisnika, proverava da li korisničko ime i email već postoje.
     * Ovo je važno za održavanje jedinstvenosti podataka u bazi.
     * 
     * @param user - korisnik koji se registruje
     * @return sačuvan korisnik sa generisanim ID-jem
     * @throws RuntimeException ako korisničko ime ili email već postoje
     */
    @Override
    public User registerUser(User user) {
        // Proverava jedinstvenost korisničkog imena
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        // Proverava jedinstvenost email adrese
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        return userRepository.save(user);
    }
    
    /**
     * Ažurira postojećeg korisnika
     * 
     * Metoda pronalazi korisnika po ID-u i ažurira njegove podatke.
     * Ne ažurira korisničko ime, lozinku ili datum registracije jer su to
     * nepromenljivi podaci.
     * 
     * @param id - ID korisnika koji se ažurira
     * @param user - novi podaci korisnika
     * @return ažuriran korisnik
     * @throws RuntimeException ako korisnik sa datim ID-jem ne postoji
     */
    @Override
    public User updateUser(Long id, User user) {
        // Pronalazi postojećeg korisnika ili baca izuzetak
        User existingUser = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Ažurira samo dozvoljene podatke
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setBirthDate(user.getBirthDate());
        existingUser.setImageLink(user.getImageLink());
        
        // Ažurira valutu samo ako je prosleđena
        if (user.getCurrency() != null) {
            existingUser.setCurrency(user.getCurrency());
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * Briše korisnika iz sistema
     * 
     * @param id - ID korisnika koji se briše
     * @throws RuntimeException ako korisnik sa datim ID-jem ne postoji
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Pronalazi korisnika po ID-u
     * 
     * @param id - ID korisnika
     * @return Optional<User> - korisnik ako postoji, prazan Optional ako ne
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Vraća sve korisnike iz baze
     * 
     * @return lista svih korisnika
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Vraća korisnike sa paginacijom
     * 
     * Paginacija omogućava efikasno učitavanje velikog broja korisnika
     * po stranicama umesto odjednom.
     * 
     * @param pageable - parametri paginacije (broj stranice, veličina stranice)
     * @return stranica korisnika
     */
    @Override
    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    /**
     * Validira korisničke podatke za prijavu
     * 
     * Proverava da li korisničko ime i lozinka odgovaraju onima u bazi.
     * Ova metoda se koristi za autentifikaciju korisnika.
     * 
     * @param username - korisničko ime
     * @param password - lozinka
     * @return true ako su podaci ispravni, false ako nisu
     */
    @Override
    @Transactional(readOnly = true)
    public boolean validateUserCredentials(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && user.get().getPassword().equals(password);
    }
    
    /**
     * Pronalazi korisnika po korisničkom imenu
     * 
     * @param username - korisničko ime
     * @return Optional<User> - korisnik ako postoji
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Pronalazi korisnika po email adresi
     * 
     * @param email - email adresa
     * @return Optional<User> - korisnik ako postoji
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Vraća korisnike sa određenom ulogom
     * 
     * Konvertuje string uloge u enum vrednost i pronalazi sve korisnike
     * sa tom ulogom. Ovo je korisno za filtriranje korisnika po privilegijama.
     * 
     * @param role - uloga korisnika (USER, ADMINISTRATOR)
     * @return lista korisnika sa datom ulogom
     * @throws ValidationException ako je uloga neispravna
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(String role) {
        try {
            // Konvertuje string u enum vrednost
            Role roleEnum = Role.valueOf(role.toUpperCase());
            return userRepository.findByRole(roleEnum);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid role: " + role);
        }
    }
    
    /**
     * Vraća sve blokirane korisnike
     * 
     * @return lista blokiranih korisnika
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getBlockedUsers() {
        return userRepository.findByBlocked(true);
    }
    
    /**
     * Vraća sve aktivne (neblokirane) korisnike
     * 
     * @return lista aktivnih korisnika
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findByBlocked(false);
    }
    
    /**
     * Vraća ukupan broj korisnika u sistemu
     * 
     * @return broj svih korisnika
     */
    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.countAllUsers();
    }
    
    /**
     * Vraća broj aktivnih korisnika
     * 
     * @return broj aktivnih korisnika
     */
    @Override
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }
    
    /**
     * Vraća korisnike registrovane nakon određenog datuma
     * 
     * @param date - datum od koga se traže korisnici
     * @return lista korisnika registrovana nakon datuma
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersRegisteredAfter(java.util.Date date) {
        return userRepository.findUsersRegisteredAfter(date);
    }
    
    /**
     * Pretražuje korisnike po imenu ili prezimenu
     * 
     * Pretraga je case-insensitive i pronalazi korisnike čije ime ili prezime
     * sadrži zadati termin.
     * 
     * @param searchTerm - termin za pretragu
     * @return lista korisnika koji odgovaraju pretrazi
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String searchTerm) {
        return userRepository.findByFirstNameOrLastNameContaining(searchTerm);
    }
    
    /**
     * Blokira korisnika
     * 
     * Blokiran korisnik ne može da se prijavi u sistem.
     * 
     * @param userId - ID korisnika koji se blokira
     * @throws RuntimeException ako korisnik ne postoji
     */
    @Override
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setBlocked(true);
        userRepository.save(user);
    }
    
    /**
     * Odblokira korisnika
     * 
     * @param userId - ID korisnika koji se odblokira
     * @throws RuntimeException ako korisnik ne postoji
     */
    @Override
    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setBlocked(false);
        userRepository.save(user);
    }
    
    /**
     * Ažurira valutu korisnika
     * 
     * @param userId - ID korisnika
     * @param currencyId - ID valute
     * @throws RuntimeException ako korisnik ili valuta ne postoje
     */
    @Override
    public void updateUserCurrency(Long userId, Long currencyId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        Currency currency = currencyRepository.findById(currencyId)
            .orElseThrow(() -> new RuntimeException("Currency not found with id: " + currencyId));
        
        user.setCurrency(currency);
        userRepository.save(user);
    }
    
    /**
     * Konvertuje User entitet u UserDTO
     * 
     * DTO (Data Transfer Object) se koristi za prenos podataka između slojeva
     * aplikacije. Ova metoda izdvaja potrebne podatke iz entiteta.
     * 
     * @param user - korisnički entitet
     * @return UserDTO objekat
     */
    @Override
    public UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthDate());
        dto.setRole(user.getRole() != null ? user.getRole().toString() : null);
        dto.setImageLink(user.getImageLink());
        dto.setDateOfRegistration(user.getDateOfRegistration());
        dto.setBlocked(user.isBlocked());
        
        // Postavlja ID valute ako korisnik ima valutu
        if (user.getCurrency() != null) {
            dto.setCurrencyId(user.getCurrency().getId());
        }
        
        return dto;
    }
    
    /**
     * Konvertuje UserDTO u User entitet
     * 
     * @param dto - DTO objekat sa korisničkim podacima
     * @return User entitet
     */
    @Override
    public User convertToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setBirthDate(dto.getBirthDate());
        user.setImageLink(dto.getImageLink());
        user.setBlocked(dto.isBlocked());
        
        // Konvertuje string uloge u enum, sa fallback na USER
        if (dto.getRole() != null) {
            try {
                user.setRole(com.example.WalletApp.entity.Role.valueOf(dto.getRole()));
            } catch (IllegalArgumentException e) {
                // Ako je uloga neispravna, postavlja default ulogu
                user.setRole(com.example.WalletApp.entity.Role.USER);
            }
        } else {
            user.setRole(com.example.WalletApp.entity.Role.USER);
        }
        
        // Postavlja valutu ako je prosleđen ID valute
        if (dto.getCurrencyId() != null) {
            Currency currency = currencyRepository.findById(dto.getCurrencyId()).orElse(null);
            user.setCurrency(currency);
        }
        
        return user;
    }
}
