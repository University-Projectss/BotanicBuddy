package com.mops.bb_backend.dto;

import lombok.Builder;

@Builder
public record PlantDetailsDto(String id, String commonName, String scientificName, String family, String photoUrl, String uploadDate) {
}
