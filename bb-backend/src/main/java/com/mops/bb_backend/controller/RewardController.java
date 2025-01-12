package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.UserRewardsDto;
import com.mops.bb_backend.service.RewardService;
import com.mops.bb_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RewardController {

    private final RewardService rewardService;
    private final UserService userService;

    @GetMapping("/rewards")
    public ResponseEntity<UserRewardsDto> getUserRewards() {
        var user = userService.getAuthenticatedUser();
        return new ResponseEntity<>(rewardService.getUserRewards(user), HttpStatus.OK);
    }
}
