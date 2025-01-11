package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.CareHistory;
import com.mops.bb_backend.model.Plant;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface CareHistoryRepository extends CrudRepository<CareHistory, UUID> {
    @Query("SELECT ch FROM CareHistory ch WHERE ch.plant = :plant")
    List<CareHistory> findByPlant(Plant plant);
}
