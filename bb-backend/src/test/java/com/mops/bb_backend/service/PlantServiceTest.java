package com.mops.bb_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mops.bb_backend.dto.PlantDetailsDto;
import com.mops.bb_backend.dto.PlantPaginationDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.*;
import com.mops.bb_backend.repository.PlantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlantServiceTest {

    @Mock
    private PlantRepository plantRepository;

    @Mock
    private UserService userService;

    @Mock
    private CareRecommendationService careRecommendationService;

    @Mock
    private CareHistoryService careHistoryService;

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private PlantService plantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addPlant_shouldSavePlantAndHandleRewards() {
        // Arrange
        User mockUser = mock(User.class);
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        Plant mockPlant = mock(Plant.class);
        doNothing().when(rewardService).handleUserReward(any(), any());

        when(plantRepository.save(any(Plant.class))).thenReturn(mockPlant);
        when(careRecommendationService.detectCareRecommendations(anyString())).thenReturn(Optional.of("{ \"recommendation\": \"Keep moist\", \"watering_frequency\": \"7\", \"light\": \"Bright\", \"soil\": \"Loamy\", \"temperature\": \"20-25\" }"));
        // Act
        plantService.addPlant("Rose", "Rosa", "Rosaceae", "photoUrl");

        // Assert
        verify(plantRepository, times(1)).save(any(Plant.class));
        verify(rewardService, times(2)).handleUserReward(any(), eq(mockUser));
    }

    @Test
    void getPlantList_shouldReturnPaginatedPlantList() {
        // Arrange
        User mockUser = new User(UUID.randomUUID(),
                mock(Account.class),
                List.of(mock(WeatherForecast.class)),
                List.of(mock(Plant.class)),
                "testUser",
                "http://example.com/photo.jpg",
                "New York, USA",
                true,
                List.of(mock(Post.class)),
                List.of(mock(RewardActionCounter.class)),
                List.of(mock(UserReward.class)));
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        Plant mockPlant = new Plant(
                UUID.randomUUID(),
                mock(User.class),
                "Rose",
                "Rosa",
                "Rosaceae",
                "http://example.com/photo.jpg",
                LocalDate.now(),
                "Keep moist. Water weekly. Bright light.",
                7,
                "Bright",
                "Loamy",
                "20-25°C",
                false,
                List.of(mock(CareHistory.class))
        );
        Page<Plant> mockPage = new PageImpl<>(List.of(mockPlant));
        when(plantRepository.findAllByUserAndIsArchived(any(PageRequest.class), eq(mockUser), eq(false)))
                .thenReturn(mockPage);

        // Act
        PlantPaginationDto result = plantService.getPlantList(0, 10, false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.totalElements());
    }

    @Test
    void getPlantDetails_shouldReturnPlantDetails() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        Plant mockPlant = new Plant(
                UUID.randomUUID(),
                mock(User.class),
                "Rose",
                "Rosa",
                "Rosaceae",
                "http://example.com/photo.jpg",
                LocalDate.now(),
                "Keep moist. Water weekly. Bright light.",
                7,
                "Bright",
                "Loamy",
                "20-25°C",
                false,
                List.of(mock(CareHistory.class))
        );
        when(plantRepository.findById(mockId)).thenReturn(Optional.of(mockPlant));
        when(plantRepository.findById(mockId)).thenReturn(Optional.of(mockPlant));

        // Act
        PlantDetailsDto result = plantService.getPlantDetails(mockId.toString());

        // Assert
        assertNotNull(result);
        assertEquals("Rose", result.commonName());
    }

    @Test
    void getPlantDetails_shouldThrowExceptionIfPlantNotFound() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        when(plantRepository.findById(mockId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> plantService.getPlantDetails(mockId.toString()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void setCareRecommendation_shouldSetRecommendations() throws JsonProcessingException {
        // Arrange
        Plant mockPlant = mock(Plant.class);
        String mockResponse = "{\"recommendation\":\"Keep moist\",\"watering_frequency\":\"7\",\"light\":\"Bright\",\"soil\":\"Loamy\",\"temperature\":\"20-25\"}";
        when(careRecommendationService.detectCareRecommendations(any()))
                .thenReturn(Optional.of(mockResponse));

        // Act
        plantService.setCareRecommendation(mockPlant);

        // Assert
        verify(mockPlant).setCareRecommendation("Keep moist");
        verify(mockPlant).setWateringFrequency(7);
    }

    @Test
    void setCareRecommendation_shouldThrowExceptionOnParseError() {
        // Arrange
        Plant mockPlant = mock(Plant.class);
        String invalidResponse = "{\"invalid_json\":}";
        when(careRecommendationService.detectCareRecommendations(anyString()))
                .thenReturn(Optional.of(invalidResponse));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> plantService.setCareRecommendation(mockPlant));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    void updatePlantDetails_shouldArchivePlant() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        Plant mockPlant = mock(Plant.class);
        when(plantRepository.findById(mockId)).thenReturn(Optional.of(mockPlant));

        // Act
        plantService.updatePlantDetails(mockId.toString(), ActionType.ARCHIVE);

        // Assert
        verify(mockPlant).setArchived(true);
        verify(plantRepository).save(mockPlant);
    }
}