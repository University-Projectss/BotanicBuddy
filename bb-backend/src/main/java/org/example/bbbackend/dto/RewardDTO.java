package org.example.bbbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RewardDTO {
    private UUID id;
    private String type;
    private String description;
    private LocalDateTime awardedDate;
    private UUID userId;
}
