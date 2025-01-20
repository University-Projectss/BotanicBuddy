package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.AchievedRewardDto;
import com.mops.bb_backend.dto.RewardDto;
import com.mops.bb_backend.dto.UserRewardsDto;
import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.RewardAction;
import com.mops.bb_backend.model.RewardLevel;
import com.mops.bb_backend.service.AccountService;
import com.mops.bb_backend.service.RewardService;
import com.mops.bb_backend.service.UserService;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestTester requestTester;

    @MockBean
    private RewardService rewardService;

    @Autowired
    private UserService userService;

    private Account account;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    public void setUp() throws Exception {
        account = requestTester.createTestAccount();
        requestTester.authenticateAccount();

        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn(account.getEmail());

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    public void tearDown() {
        requestTester.cleanupTestAccount();
    }

    @Test
    void getUserRewards_validRequest_returnsUserRewards() throws Exception {
        // Arrange
        var mockAchievedRewards = List.of(
                new AchievedRewardDto(
                        RewardLevel.BRONZE,
                        RewardAction.CHANGE_SOIL,
                        "Well done! You've changed the soil 10 times and earned the Bronze badge.",
                        10,
                        30,
                        LocalDate.of(2025, 1, 19)
                )
        );
        var mockNotAchievedRewards = List.of(
                new RewardDto(RewardLevel.BRONZE, RewardAction.WATERING,
                        "Congratulations! You've watered your plants 100 times and earned the Bronze badge.", 100, 20),
                new RewardDto(RewardLevel.SILVER, RewardAction.WATERING,
                        "Amazing! 250 waterings completed. You've unlocked the Silver badge.", 250, 40),
                new RewardDto(RewardLevel.GOLD, RewardAction.WATERING,
                        "Incredible achievement! 500 waterings earn you the Gold badge.", 500, 80),
                new RewardDto(RewardLevel.PLATINUM, RewardAction.WATERING,
                        "You're a true watering wizard! 1000 waterings have earned you the Platinum badge.", 1000, 100),
                new RewardDto(RewardLevel.SILVER, RewardAction.CHANGE_SOIL,
                        "Fantastic! 25 soil changes completed. You've unlocked the Silver badge.", 25, 60),
                new RewardDto(RewardLevel.GOLD, RewardAction.CHANGE_SOIL,
                        "Great work! 50 soil changes earn you the Gold badge.", 50, 90),
                new RewardDto(RewardLevel.PLATINUM, RewardAction.CHANGE_SOIL,
                        "You're a master caretaker! 100 soil changes unlock the Platinum badge.", 100, 120),
                new RewardDto(RewardLevel.BRONZE, RewardAction.REGISTER_PLANT,
                        "Congrats! You've registered 10 plants and earned the Bronze badge.", 10, 10),
                new RewardDto(RewardLevel.SILVER, RewardAction.REGISTER_PLANT,
                        "Awesome! You've registered 20 plants and unlocked the Silver badge.", 20, 20),
                new RewardDto(RewardLevel.GOLD, RewardAction.REGISTER_PLANT,
                        "Impressive! 30 plants registered. You've earned the Gold badge.", 30, 50),
                new RewardDto(RewardLevel.PLATINUM, RewardAction.REGISTER_PLANT,
                        "You're a true plant enthusiast! 50 plants registered to unlock the Platinum badge.", 50, 100),
                new RewardDto(RewardLevel.BRONZE, RewardAction.DIFFERENT_TYPES_OF_PLANTS,
                        "Great job! You've collected 5 different plant species and earned the Bronze badge.", 5, 40),
                new RewardDto(RewardLevel.SILVER, RewardAction.DIFFERENT_TYPES_OF_PLANTS,
                        "Fantastic! 10 different plant species collected. You've unlocked the Silver badge.", 10, 80),
                new RewardDto(RewardLevel.GOLD, RewardAction.DIFFERENT_TYPES_OF_PLANTS,
                        "Amazing! 20 plant species collected. You've earned the Gold badge.", 20, 100),
                new RewardDto(RewardLevel.PLATINUM, RewardAction.DIFFERENT_TYPES_OF_PLANTS,
                        "You're a botanical guru! 50 plant species collected to unlock the Platinum badge.", 50, 200)
        );
        var userRewardsDto = UserRewardsDto.builder()
                .achievedRewards(mockAchievedRewards)
                .notAchievedRewards(mockNotAchievedRewards)
                .points(30)
                .build();

        when(rewardService.getUserRewards(any())).thenReturn(userRewardsDto);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        var expectedResponse = objectMapper.writeValueAsString(userRewardsDto);

        // Act & Assert
        mockMvc.perform(requestTester.authenticatedGet("/rewards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedResponse));

        Mockito.verify(rewardService, times(1)).getUserRewards(any());
    }
}
