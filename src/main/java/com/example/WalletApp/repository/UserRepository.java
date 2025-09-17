package com.example.WalletApp.repository;

import com.example.WalletApp.entity.Role;
import com.example.WalletApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repository interfejs za User entitet
 * 
 * Ovaj interfejs nasleđuje JpaRepository i omogućava CRUD operacije
 * nad User entitetom. Takođe definiše custom metode za pretragu
 * korisnika po različitim kriterijumima.
 * 
 * @author vuksta
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Pronalazi korisnika po korisničkom imenu
     * 
     * @param username - korisničko ime
     * @return Optional<User> - korisnik ako postoji
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Pronalazi korisnika po email adresi
     * 
     * @param email - email adresa
     * @return Optional<User> - korisnik ako postoji
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Proverava da li korisnik sa datim korisničkim imenom postoji
     * 
     * @param username - korisničko ime
     * @return true ako postoji, false ako ne
     */
    boolean existsByUsername(String username);
    
    /**
     * Proverava da li korisnik sa datom email adresom postoji
     * 
     * @param email - email adresa
     * @return true ako postoji, false ako ne
     */
    boolean existsByEmail(String email);
    
    /**
     * Pronalazi sve korisnike sa određenom ulogom
     * 
     * @param role - uloga korisnika
     * @return lista korisnika sa datom ulogom
     */
    List<User> findByRole(Role role);
    
    /**
     * Pronalazi korisnike po statusu blokiranja
     * 
     * @param blocked - status blokiranja
     * @return lista korisnika sa datim statusom
     */
    List<User> findByBlocked(boolean blocked);
    
    /**
     * Broji ukupan broj korisnika u sistemu
     * 
     * @return broj svih korisnika
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();
    
    /**
     * Broji broj aktivnih (neblokiranih) korisnika
     * 
     * @return broj aktivnih korisnika
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.blocked = false")
    long countActiveUsers();
    
    /**
     * Pronalazi korisnike registrovane nakon određenog datuma
     * 
     * @param date - datum od koga se traže korisnici
     * @return lista korisnika registrovana nakon datuma
     */
    @Query("SELECT u FROM User u WHERE u.dateOfRegistration >= :date")
    List<User> findUsersRegisteredAfter(@Param("date") Date date);
    
    /**
     * Pretražuje korisnike po imenu ili prezimenu
     * 
     * @param searchTerm - termin za pretragu
     * @return lista korisnika koji odgovaraju pretrazi
     */
    @Query("SELECT u FROM User u WHERE u.lastName LIKE %:searchTerm% OR u.firstName LIKE %:searchTerm%")
    List<User> findByFirstNameOrLastNameContaining(@Param("searchTerm") String searchTerm);
}
