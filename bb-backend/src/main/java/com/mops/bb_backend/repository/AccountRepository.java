package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);
}