package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends CrudRepository<Account, UUID> {
    Optional<Account> findByEmail(String email);

    @Modifying
    @Query("update Account set user = :user where email = :email and user = null")
    int updateAccountWithUser(@Param("email") String email, @Param("user") User user);
}