package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.UserRewardsDto;
import com.mops.bb_backend.service.RewardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class RewardController {

    private final RewardService rewardService;

    @GetMapping("/rewards")
    public ResponseEntity<UserRewardsDto> getUserRewards() {
        return new ResponseEntity<>(rewardService.getUserRewards(), HttpStatus.OK);
    }
}
