package com.mops.bb_backend.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserRewardsDto (List<AchievedRewardDto> achievedRewards, List<RewardDto> notAchievedRewards, int points){
}
