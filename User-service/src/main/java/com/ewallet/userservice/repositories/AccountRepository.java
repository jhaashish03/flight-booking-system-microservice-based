package com.ewallet.userservice.repositories;

import com.ewallet.userservice.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Account findByUserName(String userName);

    boolean existsByUserName(String userName);
}
