package org.example.bbbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PlantCareHistoryDTO {
    private UUID id;
    private String careType;
    private LocalDateTime timestamp;
    private String notes;
    private UUID plantId;
}
