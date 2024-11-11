package org.example.bbbackend.repository;

import org.example.bbbackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, CrudRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
