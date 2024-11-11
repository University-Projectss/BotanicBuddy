package org.example.bbbackend.service;

import org.example.bbbackend.model.Reward;
import org.example.bbbackend.repository.RewardRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class RewardService {
    private final RewardRepository rewardRepository;

    @Autowired
    public RewardService(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    public Optional<Reward> getRewardById(String rewardId) {
        return rewardRepository.findById(rewardId);
    }

    public Reward createReward(Reward reward) {
        return rewardRepository.save(reward);
    }

    public void deleteReward(String rewardId) {
        rewardRepository.deleteById(rewardId);
    }
}
