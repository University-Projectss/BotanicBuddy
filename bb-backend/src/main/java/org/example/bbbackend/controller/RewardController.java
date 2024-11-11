package org.example.bbbackend.controller;

import org.example.bbbackend.dto.RewardDTO;
import org.example.bbbackend.model.Reward;
import org.example.bbbackend.repository.RewardRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rewards")
public class RewardController {
    private final RewardRepository rewardRepository;

    public RewardController(RewardRepository rewardRepository) {
        this.rewardRepository = rewardRepository;
    }

    @GetMapping
    public List<Reward> getAllRewards() {
        return rewardRepository.findAll();
    }

    @PostMapping
    public Reward createReward(@RequestBody RewardDTO rewardDTO) {
        Reward reward = new Reward();
        reward.setId(rewardDTO.getId());
        reward.setType(rewardDTO.getType());
        reward.setDescription(rewardDTO.getDescription());
        reward.setAwardedDate(rewardDTO.getAwardedDate());
        return rewardRepository.save(reward);
    }
}