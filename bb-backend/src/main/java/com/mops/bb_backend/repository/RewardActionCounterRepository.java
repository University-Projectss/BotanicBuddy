package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.RewardAction;
import com.mops.bb_backend.model.RewardActionCounter;
import com.mops.bb_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RewardActionCounterRepository extends JpaRepository<RewardActionCounter, Long> {
    @Query("SELECT c FROM RewardActionCounter c WHERE c.user = :user AND c.action = :action")
    RewardActionCounter findByUserAndRewardAction(User user, RewardAction action);
}
