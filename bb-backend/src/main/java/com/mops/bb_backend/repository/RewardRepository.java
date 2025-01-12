package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.Reward;
import com.mops.bb_backend.model.RewardAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    @Query("SELECT r FROM Reward r WHERE r.action = :action AND r.requiredActionNumber = :requiredActionNumber")
    Optional<Reward> findByActionAndRequiredActionNumber(RewardAction action, int requiredActionNumber);
}
