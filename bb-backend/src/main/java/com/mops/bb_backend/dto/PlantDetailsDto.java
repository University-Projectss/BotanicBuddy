package com.mops.bb_backend.dto;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record PlantDetailsDto(
        String id,
        String commonName,
        String scientificName,
        String family,
        String photoUrl,
        String uploadDate,
        String careRecommendation,
        int wateringFrequency,
        String light,
        String soil,
        String temperature,
        boolean isArchived,
        List<CareHistoryDto> careHistory
) {
}
