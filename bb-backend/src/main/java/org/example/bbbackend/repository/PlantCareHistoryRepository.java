package org.example.bbbackend.repository;

import org.example.bbbackend.model.PlantCareHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlantCareHistoryRepository extends JpaRepository<PlantCareHistory, String> {
}