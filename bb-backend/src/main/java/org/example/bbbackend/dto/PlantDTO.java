package org.example.bbbackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PlantDTO {
    private UUID id;
    private String name;
    private String imageUrl;
    private String careFrequency;
    private String lightRequirement;
    private String soilType;
}
