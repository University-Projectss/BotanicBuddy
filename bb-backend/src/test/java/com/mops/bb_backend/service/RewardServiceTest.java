package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.UserRewardsDto;
import com.mops.bb_backend.model.*;
import com.mops.bb_backend.repository.RewardActionCounterRepository;
import com.mops.bb_backend.repository.RewardRepository;
import com.mops.bb_backend.repository.UserRewardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class RewardServiceTest {

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private UserRewardRepository userRewardRepository;

    @Mock
    private RewardActionCounterRepository rewardActionCounterRepository;

    @InjectMocks
    private RewardService rewardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void initializeRewardCountersForUser_shouldCreateCounters() {
        // Arrange
        User mockUser = mock(User.class);

        // Act
        rewardService.initializeRewardCountersForUser(mockUser);

        // Assert
        verify(rewardActionCounterRepository, times(RewardAction.values().length)).save(any(RewardActionCounter.class));
    }

    @Test
    void getUserRewards_shouldReturnRewardsCorrectly() {
        // Arrange
        User mockUser = mock(User.class);
        Reward mockReward = Reward.builder()
                .action(RewardAction.WATERING)
                .description("Test reward")
                .level(RewardLevel.BRONZE)
                .requiredActionNumber(10)
                .points(50)
                .build();
        UserReward mockUserReward = UserReward.builder()
                .user(mockUser)
                .reward(mockReward)
                .dateAchieved(LocalDate.now())
                .build();
        when(rewardRepository.findAll()).thenReturn(List.of(mockReward));
        when(userRewardRepository.findByUser(mockUser)).thenReturn(List.of(mockUserReward));

        // Act
        UserRewardsDto result = rewardService.getUserRewards(mockUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.achievedRewards().size());
        assertEquals(0, result.notAchievedRewards().size());
        assertEquals(50, result.points());
    }

    @Test
    void seedRewards_shouldAddDefaultRewardsIfEmpty() {
        // Arrange
        when(rewardRepository.findAll()).thenReturn(List.of());

        // Act
        rewardService.seedRewards();

        // Assert
        verify(rewardRepository, atLeast(1)).save(any(Reward.class));
    }
}
