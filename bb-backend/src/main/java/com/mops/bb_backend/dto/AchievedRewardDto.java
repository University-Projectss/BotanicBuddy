package com.mops.bb_backend.dto;

import com.mops.bb_backend.model.RewardAction;
import com.mops.bb_backend.model.RewardLevel;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record AchievedRewardDto(RewardLevel level, RewardAction action, String description,
                                int requiredActionNumber, int points, LocalDate date) {
}
