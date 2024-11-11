package org.example.bbbackend.repository;

import org.example.bbbackend.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlantRepository extends JpaRepository<Plant, UUID> {
}