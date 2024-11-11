package org.example.bbbackend.service;

import org.example.bbbackend.model.PlantCareHistory;
import org.example.bbbackend.repository.PlantCareHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class PlantCareHistoryService {
    private final PlantCareHistoryRepository plantCareHistoryRepository;

    @Autowired
    public PlantCareHistoryService(PlantCareHistoryRepository plantCareHistoryRepository) {
        this.plantCareHistoryRepository = plantCareHistoryRepository;
    }

    public List<PlantCareHistory> getAllCareHistory() {
        return plantCareHistoryRepository.findAll();
    }

    public Optional<PlantCareHistory> getCareHistoryById(String careId) {
        return plantCareHistoryRepository.findById(careId);
    }

    public PlantCareHistory createCareHistory(PlantCareHistory careHistory) {
        return plantCareHistoryRepository.save(careHistory);
    }

    public void deleteCareHistory(String careId) {
        plantCareHistoryRepository.deleteById(careId);
    }
}
