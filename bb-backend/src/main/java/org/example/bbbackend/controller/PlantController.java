package org.example.bbbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.bbbackend.dto.PlantDTO;
import org.example.bbbackend.service.PlantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/plants")
@Tag(name = "Plant Controller", description = "API for managing plants")
public class PlantController {

    private final PlantService plantService;

    @Autowired
    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping
    @Operation(summary = "Get all plants", description = "Retrieve a list of all plants")
    public ResponseEntity<List<PlantDTO>> getAllPlants() {
        List<PlantDTO> plants = plantService.getAllPlants();
        return ResponseEntity.ok(plants);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get plant by ID", description = "Retrieve a plant by its ID")
    public ResponseEntity<PlantDTO> getPlantById(@PathVariable UUID id) {
        Optional<PlantDTO> plant = plantService.getPlantById(id);
        return plant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    @Operation(summary = "Add a new plant", description = "Add a new plant to the system")
    public ResponseEntity<PlantDTO> addPlant(@RequestBody PlantDTO plantDTO) {
        PlantDTO savedPlant = plantService.addPlant(plantDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlant);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update plant by ID", description = "Update details of an existing plant by its ID")
    public ResponseEntity<PlantDTO> updatePlant(@PathVariable UUID id, @RequestBody PlantDTO plantDTO) {
        Optional<PlantDTO> updatedPlant = plantService.updatePlant(id, plantDTO);
        return updatedPlant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete plant by ID", description = "Remove a plant from the system by its ID")
    public ResponseEntity<Void> deletePlant(@PathVariable UUID id) {
        boolean deleted = plantService.deletePlant(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
