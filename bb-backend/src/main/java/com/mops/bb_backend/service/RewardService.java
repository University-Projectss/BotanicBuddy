package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.AchievedRewardDto;
import com.mops.bb_backend.dto.RewardDto;
import com.mops.bb_backend.dto.UserRewardsDto;
import com.mops.bb_backend.model.*;
import com.mops.bb_backend.repository.RewardActionCounterRepository;
import com.mops.bb_backend.repository.RewardRepository;
import com.mops.bb_backend.repository.UserRewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;
    private final UserRewardRepository userRewardRepository;
    private final RewardActionCounterRepository rewardActionCounterRepository;

    public void addReward(RewardAction action, RewardLevel level, int requiredActionNumber, String description, int points) {
        var reward = Reward.builder().action(action).description(description)
                .level(level).requiredActionNumber(requiredActionNumber).points(points).build();
        rewardRepository.save(reward);
    }

    public void initializeRewardCountersForUser(User user) {
        Arrays.stream(RewardAction.values()).forEach(action -> {
            var counter = RewardActionCounter.builder().action(action).counter(0).user(user).build();
            rewardActionCounterRepository.save(counter);
        });
    }

    public void handleUserReward(RewardAction action, User user) {
        var rewardActionCounter = incrementUserRewardCounter(action, user);
        var reward = rewardRepository.findByActionAndRequiredActionNumber(action, rewardActionCounter.getCounter());
        if (reward.isPresent()) {
            var userReward = userRewardRepository.findByUserAndReward(user, reward.get());
            if (userReward.isEmpty()) {
                var newUserReward = UserReward.builder().reward(reward.get())
                        .user(user).dateAchieved(LocalDate.now()).build();
                userRewardRepository.save(newUserReward);
            }
        }
    }

    public UserRewardsDto getUserRewards(User user) {
        var rewards = rewardRepository.findAll();
        var userRewards = userRewardRepository.findByUser(user);

        List<AchievedRewardDto> achievedRewards = new ArrayList<>();
        List<RewardDto> notAchievedRewards = new ArrayList<>();
        var points = 0;
        for (var reward : rewards) {
            for (var userReward : userRewards) {
                if (userReward.getReward().equals(reward)) {
                    var achievedRewardDto = mapRewardToAchievedRewardDto(reward, userReward.getDateAchieved());
                    achievedRewards.add(achievedRewardDto);
                    points += reward.getPoints();
                } else {
                    var notAchievedRewardDto = mapRewardToRewardDto(reward);
                    notAchievedRewards.add(notAchievedRewardDto);
                }
            }
        }

        return UserRewardsDto.builder()
                .notAchievedRewards(notAchievedRewards).achievedRewards(achievedRewards).points(points).build();
    }

    private RewardActionCounter incrementUserRewardCounter(RewardAction action, User user) {
        var rewardCounter = rewardActionCounterRepository.findByUserAndRewardAction(user, action);
        rewardCounter.setCounter(rewardCounter.getCounter() + 1);
        return rewardActionCounterRepository.save(rewardCounter);
    }

    private RewardDto mapRewardToRewardDto(Reward reward) {
        return RewardDto.builder()
                .action(reward.getAction())
                .description(reward.getDescription())
                .requiredActionNumber(reward.getRequiredActionNumber())
                .level(reward.getLevel())
                .points(reward.getPoints())
                .build();
    }

    private AchievedRewardDto mapRewardToAchievedRewardDto(Reward reward, LocalDate achievedDate) {
        return AchievedRewardDto.builder()
                .action(reward.getAction())
                .description(reward.getDescription())
                .requiredActionNumber(reward.getRequiredActionNumber())
                .level(reward.getLevel())
                .points(reward.getPoints())
                .date(achievedDate)
                .build();
    }

    public void seedRewards() {
        if (rewardRepository.findAll().isEmpty()) {
            addReward(RewardAction.WATERING, RewardLevel.BRONZE, 100,
                    "Congratulations! You've watered your plants 100 times and earned the Bronze badge.", 20);
            addReward(RewardAction.WATERING, RewardLevel.SILVER, 250,
                    "Amazing! 250 waterings completed. You've unlocked the Silver badge.", 40);
            addReward(RewardAction.WATERING, RewardLevel.GOLD, 500,
                    "Incredible achievement! 500 waterings earn you the Gold badge.", 80);
            addReward(RewardAction.WATERING, RewardLevel.PLATINUM, 1000,
                    "You're a true watering wizard! 1000 waterings have earned you the Platinum badge.", 100);

            addReward(RewardAction.CHANGE_SOIL, RewardLevel.BRONZE, 10,
                    "Well done! You've changed the soil 10 times and earned the Bronze badge.", 30);
            addReward(RewardAction.CHANGE_SOIL, RewardLevel.SILVER, 25,
                    "Fantastic! 25 soil changes completed. You've unlocked the Silver badge.", 60);
            addReward(RewardAction.CHANGE_SOIL, RewardLevel.GOLD, 50,
                    "Great work! 50 soil changes earn you the Gold badge.", 90);
            addReward(RewardAction.CHANGE_SOIL, RewardLevel.PLATINUM, 100,
                    "You're a master caretaker! 100 soil changes unlock the Platinum badge.", 120);

            addReward(RewardAction.REGISTER_PLANT, RewardLevel.BRONZE, 10,
                    "Congrats! You've registered 10 plants and earned the Bronze badge.", 10);
            addReward(RewardAction.REGISTER_PLANT, RewardLevel.SILVER, 20,
                    "Awesome! You've registered 20 plants and unlocked the Silver badge.", 20);
            addReward(RewardAction.REGISTER_PLANT, RewardLevel.GOLD, 30,
                    "Impressive! 30 plants registered. You've earned the Gold badge.", 50);
            addReward(RewardAction.REGISTER_PLANT, RewardLevel.PLATINUM, 50,
                    "You're a true plant enthusiast! 50 plants registered to unlock the Platinum badge.", 100);

            addReward(RewardAction.DIFFERENT_TYPES_OF_PLANTS, RewardLevel.BRONZE, 5,
                    "Great job! You've collected 5 different plant species and earned the Bronze badge.", 40);
            addReward(RewardAction.DIFFERENT_TYPES_OF_PLANTS, RewardLevel.SILVER, 10,
                    "Fantastic! 10 different plant species collected. You've unlocked the Silver badge.", 80);
            addReward(RewardAction.DIFFERENT_TYPES_OF_PLANTS, RewardLevel.GOLD, 20,
                    "Amazing! 20 plant species collected. You've earned the Gold badge.", 100);
            addReward(RewardAction.DIFFERENT_TYPES_OF_PLANTS, RewardLevel.PLATINUM, 50,
                    "You're a botanical guru! 50 plant species collected to unlock the Platinum badge.", 200);
        }
    }
}
