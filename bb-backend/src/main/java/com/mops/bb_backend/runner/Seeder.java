package com.mops.bb_backend.runner;

import com.mops.bb_backend.service.RewardService;
import com.mops.bb_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Seeder implements ApplicationRunner {
    private final UserService userService;
    private final RewardService rewardService;

    @Override
    public void run(ApplicationArguments args) {
        if (userService.getAllUsers().isEmpty()) {
            userService.createUserAccount("user1@example.com", "password", "user1", "user1photo");
            rewardService.seedRewards();
        }
    }
}