package com.mops.bb_backend.dto;

import java.util.List;

public record PlantPaginationDto(List<PlantDetailsDto> plants, int pageNumber, int pageSize, long totalElements, long totalPages, boolean isLast) {
}
