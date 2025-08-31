package com.example.WalletApp.repository;

import com.example.WalletApp.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    
    Optional<Currency> findByName(String name);
    
    boolean existsByName(String name);
    
    @Query("SELECT c FROM Currency c WHERE c.name = :name")
    Optional<Currency> findByCurrencyName(@Param("name") String name);
    
    @Query("SELECT c FROM Currency c ORDER BY c.name")
    List<Currency> findAllOrderedByName();
    
    @Query("SELECT c FROM Currency c WHERE c.valueToEur > 0 ORDER BY c.valueToEur DESC")
    List<Currency> findCurrenciesOrderedByValue();
}
