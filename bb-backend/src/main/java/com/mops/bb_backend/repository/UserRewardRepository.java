package com.mops.bb_backend.repository;

import com.mops.bb_backend.model.Reward;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.model.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRewardRepository extends JpaRepository<UserReward, Long> {
    @Query("SELECT ur FROM UserReward ur WHERE ur.user = :user AND ur.reward = :reward")
    Optional<UserReward> findByUserAndReward(User user, Reward reward);

    @Query("SELECT ur FROM UserReward ur WHERE ur.user = :user")
    List<UserReward> findByUser(User user);
}
