package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.Plant;
import com.mops.bb_backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PlantRepository extends CrudRepository<Plant, UUID> {
    @Query("SELECT p from Plant p JOIN User u ON p.user.id = u.id")
    Page<Plant> findAllByUser(Pageable pageable, User user);
}
