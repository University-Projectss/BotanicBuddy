package org.example.bbbackend.repository;

import org.example.bbbackend.model.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, String> {
}
