package org.example.bbbackend.service;

import org.example.bbbackend.model.Plant;
import org.example.bbbackend.repository.PlantRepository;
import org.example.bbbackend.dto.PlantDTO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlantService {
    private final PlantRepository plantRepository;

    @Autowired
    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    public List<PlantDTO> getAllPlants() {
        return plantRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PlantDTO> getPlantById(UUID id) {
        return plantRepository.findById(id).map(this::convertToDTO);
    }


    public PlantDTO addPlant(PlantDTO plantDTO) {
        Plant plant = convertToEntity(plantDTO);
        Plant savedPlant = plantRepository.save(plant);
        return convertToDTO(savedPlant);
    }

    public Optional<PlantDTO> updatePlant(UUID id, PlantDTO plantDTO) {
        return plantRepository.findById(id).map(existingPlant -> {
            existingPlant.setName(plantDTO.getName());
            existingPlant.setImageUrl(plantDTO.getImageUrl());
            existingPlant.setCareFrequency(plantDTO.getCareFrequency());
            existingPlant.setLightRequirement(plantDTO.getLightRequirement());
            existingPlant.setSoilType(plantDTO.getSoilType());
            Plant updatedPlant = plantRepository.save(existingPlant);
            return convertToDTO(updatedPlant);
        });
    }

    public boolean deletePlant(UUID id) {
        if (plantRepository.existsById(id)) {
            plantRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private PlantDTO convertToDTO(Plant plant) {
        PlantDTO plantDTO = new PlantDTO();
        plantDTO.setId(plant.getId());
        plantDTO.setName(plant.getName());
        plantDTO.setImageUrl(plant.getImageUrl());
        plantDTO.setCareFrequency(plant.getCareFrequency());
        plantDTO.setLightRequirement(plant.getLightRequirement());
        plantDTO.setSoilType(plant.getSoilType());
        return plantDTO;
    }

    private Plant convertToEntity(PlantDTO plantDTO) {
        Plant plant = new Plant();
        plant.setId(plantDTO.getId());
        plant.setName(plantDTO.getName());
        plant.setImageUrl(plantDTO.getImageUrl());
        plant.setCareFrequency(plantDTO.getCareFrequency());
        plant.setLightRequirement(plantDTO.getLightRequirement());
        plant.setSoilType(plantDTO.getSoilType());
        return plant;
    }
}
