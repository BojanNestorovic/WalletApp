package com.example.WalletApp.repository;

import com.example.WalletApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findAllByPositionOrderByFirstName(String position);

    List<User> findByFirstNameOrLastName(String firstName, String lastName);

}
