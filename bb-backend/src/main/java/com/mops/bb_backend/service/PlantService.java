package com.mops.bb_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mops.bb_backend.dto.PlantDetailsDto;
import com.mops.bb_backend.dto.PlantPaginationDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.*;
import com.mops.bb_backend.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.mops.bb_backend.utils.Converter.convertStringToUUID;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;
    private final UserService userService;
    private final CareRecommendationService careRecommendationService;
    private final CareHistoryService careHistoryService;
    private final RewardService rewardService;

    public void addPlant(String commonName, String scientificName, String family, String photoUrl) {
        var user = userService.getAuthenticatedUser();
        var plant = mapPlantRegistrationDtoToPlant(commonName, scientificName, family, photoUrl, user);
        handleDifferentTypesOfPlantsReward(plant, user);
        setCareRecommendation(plant);
        plantRepository.save(plant);
        rewardService.handleUserReward(RewardAction.REGISTER_PLANT, user);
    }

    private void handleDifferentTypesOfPlantsReward(Plant newPlant, User user) {
        var userPlants = plantRepository.findByUser(user);
        var species = userPlants.stream().map(Plant::getScientificName).collect(Collectors.toSet());
        if (!species.contains(newPlant.getScientificName())) {
            rewardService.handleUserReward(RewardAction.DIFFERENT_TYPES_OF_PLANTS, user);
        }
    }

    public PlantPaginationDto getPlantList(int pageNumber, int pageSize, boolean isArchived) {
        var user = userService.getAuthenticatedUser();
        var pageable = PageRequest.of(pageNumber, pageSize);
        var response = plantRepository.findAllByUserAndIsArchived(pageable, user, isArchived);
        var plants = response.getContent().stream().sorted(Comparator.comparing(Plant::getUploadDate)
                .thenComparing(Plant::getCommonName)).map(PlantService::mapPlantToPlantDetailsDto).toList();
        return new PlantPaginationDto(plants, response.getNumber(), response.getSize(),
                response.getTotalElements(), response.getTotalPages(), response.isLast());
    }

    public PlantDetailsDto getPlantDetails(String id) {
        var uuid = convertStringToUUID(id);
        var plant = plantRepository.findById(uuid)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "No plant found with the provided ID!"));

        var plantDetailsDto = mapPlantToPlantDetailsDto(plant);
        var careHistory = careHistoryService.getPlantCareHistoryDto(plant);

        return plantDetailsDto.toBuilder().careHistory(careHistory).build();
    }

    public void setCareRecommendation(Plant plant) {
        var response = careRecommendationService.detectCareRecommendations(plant.getScientificName())
                .orElseThrow(() -> new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate care recommendations!"));
        var objectMapper = new ObjectMapper();
        try {
            var jsonNode = objectMapper.readTree(response);
            plant.setCareRecommendation(jsonNode.get("recommendation").asText());
            plant.setWateringFrequency(Integer.parseInt(jsonNode.get("watering_frequency").asText()));
            plant.setLight(jsonNode.get("light").asText());
            plant.setSoil(jsonNode.get("soil").asText());
            plant.setTemperature(jsonNode.get("temperature").asText());
        } catch (JsonProcessingException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to parse care recommendations: " + e.getMessage());
        }
    }

    public List<Plant> getPlantsToWater() {
        List<Plant> plantsToWater = new ArrayList<>();

        plantRepository.findAll().forEach(plant -> {
            var careHistory = careHistoryService.getPlantCareHistory(plant);
            var lastWateringRegistration = careHistory.stream()
                    .filter(registration -> ActionType.WATER.equals(registration.getAction()))
                    .max(Comparator.comparing(CareHistory::getDate));

            if (careHistory.isEmpty() || lastWateringRegistration.isEmpty()) {
                var daysBetween = ChronoUnit.DAYS.between(plant.getUploadDate(), LocalDate.now());
                if (daysBetween % plant.getWateringFrequency() == 0) {
                    plantsToWater.add(plant);
                }
            } else {
                var lastWateringDay = lastWateringRegistration.get().getDate();
                var daysBetween = ChronoUnit.DAYS.between(lastWateringDay, LocalDate.now());
                if (daysBetween % plant.getWateringFrequency() == 0) {
                    plantsToWater.add(plant);
                }
            }
        });

        return plantsToWater;
    }

    public void updatePlantDetails(String id, ActionType actionType) {
        var user = userService.getAuthenticatedUser();
        var uuid = convertStringToUUID(id);
        var plant = plantRepository.findById(uuid)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "No plant found with the provided ID!"));

        switch (actionType) {
            case WATER -> careHistoryService.waterPlant(plant, user);
            case ARCHIVE -> archivePlant(plant);
            case CHANGE_SOIL -> careHistoryService.changePlantSoil(plant, user);
        }
    }

    private void archivePlant(Plant plant) {
        plant.setArchived(true);
        plantRepository.save(plant);
    }

    private static Plant mapPlantRegistrationDtoToPlant(String commonName, String scientificName, String family, String photoUrl, User user) {
        return Plant.builder()
                .commonName(commonName)
                .scientificName(scientificName)
                .family(family)
                .photoUrl(photoUrl)
                .uploadDate(LocalDate.now())
                .isArchived(false)
                .user(user)
                .build();
    }

    private static PlantDetailsDto mapPlantToPlantDetailsDto(Plant plant) {
        return PlantDetailsDto.builder()
                .id(plant.getId().toString())
                .commonName(plant.getCommonName())
                .scientificName(plant.getScientificName())
                .family(plant.getFamily())
                .photoUrl(plant.getPhotoUrl())
                .uploadDate(plant.getUploadDate().toString())
                .careRecommendation(plant.getCareRecommendation())
                .wateringFrequency(plant.getWateringFrequency())
                .light(plant.getLight())
                .soil(plant.getSoil())
                .temperature(plant.getTemperature())
                .isArchived(plant.isArchived())
                .build();
    }
}
