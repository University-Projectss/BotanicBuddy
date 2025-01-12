package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.CareHistoryDto;
import com.mops.bb_backend.model.ActionType;
import com.mops.bb_backend.model.CareHistory;
import com.mops.bb_backend.model.Plant;
import com.mops.bb_backend.repository.CareHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CareHistoryService {

    private final CareHistoryRepository careHistoryRepository;

    public List<CareHistory> getPlantCareHistory(Plant plant) {
        return careHistoryRepository.findByPlant(plant)
                .stream().sorted(Comparator.comparing(CareHistory::getDate).reversed())
                .sorted(Comparator.comparing(CareHistory::getTime).reversed())
                .toList();
    }

    public List<CareHistoryDto> getPlantCareHistoryDto(Plant plant) {
        return getPlantCareHistory(plant).stream()
                .map(CareHistoryService::mapCareHistoryToCareHistoryDto)
                .toList();
    }

    public void changePlantSoil(Plant plant) {
        var careHistoryRegistration = CareHistory.builder()
                .date(LocalDate.now())
                .time(LocalTime.now())
                .action(ActionType.CHANGE_SOIL)
                .plant(plant).build();
        careHistoryRepository.save(careHistoryRegistration);
    }

    public void waterPlant(Plant plant) {
        var careHistoryRegistration = CareHistory.builder()
                .date(LocalDate.now())
                .time(LocalTime.now())
                .action(ActionType.WATER)
                .plant(plant).build();
        careHistoryRepository.save(careHistoryRegistration);
    }

    private static CareHistoryDto mapCareHistoryToCareHistoryDto(CareHistory careHistory) {
        LocalDate sanitizedDate = careHistory.getDate(); 
        LocalTime sanitizedTime = careHistory.getTime().withNano(0);

        return CareHistoryDto.builder()
                .date(sanitizedDate)
                .time(sanitizedTime)
                .action(careHistory.getAction())
                .build();
    }
}
