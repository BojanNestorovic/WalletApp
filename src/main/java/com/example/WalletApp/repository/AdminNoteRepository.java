package com.example.WalletApp.repository;

import com.example.WalletApp.entity.AdminNote;
import com.example.WalletApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminNoteRepository extends JpaRepository<AdminNote, Long> {
    
    List<AdminNote> findByUser(User user);
    
    List<AdminNote> findByUserOrderByDateCreatedDesc(User user);
}
