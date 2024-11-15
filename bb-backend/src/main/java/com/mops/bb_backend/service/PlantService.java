package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.PlantDetailsDto;
import com.mops.bb_backend.dto.PlantPaginationDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.Plant;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.PlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;

import static com.mops.bb_backend.utils.Converter.convertStringToUUID;

@Service
@RequiredArgsConstructor
public class PlantService {
    private final PlantRepository plantRepository;
    private final UserService userService;

    public void addPlant(String commonName, String scientificName, String family, String photoUrl) {
        var user = userService.getAuthenticatedUser();
        var plant = mapPlantRegistrationDtoToPlant(commonName, scientificName, family, photoUrl, user);
        plantRepository.save(plant);
    }

    public PlantPaginationDto getPlantList(int pageNumber, int pageSize) {
        var user = userService.getAuthenticatedUser();
        var pageable = PageRequest.of(pageNumber, pageSize);
        var response = plantRepository.findAllByUser(pageable, user);
        var plants = response.getContent().stream().sorted(Comparator.comparing(Plant::getUploadDate)
                .thenComparing(Plant::getCommonName)).map(PlantService::mapPlantToPlantDetailsDto).toList();
        return new PlantPaginationDto(plants, response.getNumber(), response.getSize(),
                response.getTotalElements(), response.getTotalPages(), response.isLast());
    }

    public PlantDetailsDto getPlantDetails(String id) {
        var uuid = convertStringToUUID(id);
        var plant = plantRepository.findById(uuid)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "No plant found with the provided ID!"));
        return mapPlantToPlantDetailsDto(plant);
    }

    private static Plant mapPlantRegistrationDtoToPlant(String commonName, String scientificName, String family, String photoUrl, User user) {
        return Plant.builder()
                .commonName(commonName)
                .scientificName(scientificName)
                .family(family)
                .photoUrl(photoUrl)
                .uploadDate(LocalDate.now())
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
                .build();
    }
}
