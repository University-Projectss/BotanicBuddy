package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.Plant;
import com.mops.bb_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlantRepository extends CrudRepository<Plant, UUID> {
    @Query("SELECT p FROM Plant p WHERE p.user = :user AND p.isArchived = :isArchived")
    Page<Plant> findAllByUserAndIsArchived(Pageable pageable, User user, boolean isArchived);
}
