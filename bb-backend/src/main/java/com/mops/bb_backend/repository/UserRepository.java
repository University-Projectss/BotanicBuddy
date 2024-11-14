package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, UUID> {
    @Query("SELECT u FROM User u JOIN Account a ON u.id = a.user.id WHERE a.email = :email")
    User findUserByAccountEmail(@Param("email") String email);

    Optional<User> findByUsername(String username);
}
