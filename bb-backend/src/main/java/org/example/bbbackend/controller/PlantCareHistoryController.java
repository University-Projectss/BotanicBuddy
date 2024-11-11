package org.example.bbbackend.controller;

import org.example.bbbackend.dto.PlantCareHistoryDTO;
import org.example.bbbackend.model.PlantCareHistory;
import org.example.bbbackend.repository.PlantCareHistoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plant-care-history")
public class PlantCareHistoryController {
    private final PlantCareHistoryRepository plantCareHistoryRepository;

    public PlantCareHistoryController(PlantCareHistoryRepository plantCareHistoryRepository) {
        this.plantCareHistoryRepository = plantCareHistoryRepository;
    }

    @GetMapping
    public List<PlantCareHistory> getAllCareHistory() {
        return plantCareHistoryRepository.findAll();
    }

    @PostMapping
    public PlantCareHistory addCareHistory(@RequestBody PlantCareHistoryDTO historyDTO) {
        PlantCareHistory history = new PlantCareHistory();
        history.setId(historyDTO.getId());
        history.setCareType(historyDTO.getCareType());
        history.setTimestamp(historyDTO.getTimestamp());
        history.setNotes(historyDTO.getNotes());
        return plantCareHistoryRepository.save(history);
    }
}