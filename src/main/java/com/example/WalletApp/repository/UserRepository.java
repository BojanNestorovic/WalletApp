package com.example.WalletApp.repository;

import com.example.WalletApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    List<User> findByRole(String role);
    
    List<User> findByBlocked(boolean blocked);
    
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.blocked = false")
    long countActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.dateOfRegistration >= :date")
    List<User> findUsersRegisteredAfter(@Param("date") Date date);
    
    @Query("SELECT u FROM User u WHERE u.lastName LIKE %:searchTerm% OR u.firstName LIKE %:searchTerm%")
    List<User> findByFirstNameOrLastNameContaining(@Param("searchTerm") String searchTerm);
}
