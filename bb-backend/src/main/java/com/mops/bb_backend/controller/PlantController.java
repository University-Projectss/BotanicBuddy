package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.PlantDetailsDto;
import com.mops.bb_backend.dto.PlantPaginationDto;
import com.mops.bb_backend.dto.PlantRegistrationDto;
import com.mops.bb_backend.service.PlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PlantController {
    private final PlantService plantService;

    @PostMapping("/plants")
    public ResponseEntity<Void> addPlant(@RequestBody PlantRegistrationDto plantRegistrationDto) {
        try {
            plantService.addPlant(plantRegistrationDto.commonName(),
                    plantRegistrationDto.scientificName(),
                    plantRegistrationDto.family(),
                    plantRegistrationDto.photoUrl());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/plants")
    public ResponseEntity<PlantPaginationDto> getPlantList(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return new ResponseEntity<>(plantService.getPlantList(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/plants/{id}")
    public ResponseEntity<PlantDetailsDto> getPlantDetails(@PathVariable String id) {
        return new ResponseEntity<>(plantService.getPlantDetails(id), HttpStatus.OK);
    }
}
