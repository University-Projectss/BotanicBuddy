package com.mops.bb_backend.dto;

import java.util.List;

public record PostPaginationDto(
        List<PostDetailsDto> posts,
        int pageNumber,
        int pageSize,
        long totalElements,
        long totalPages,
        boolean isLast
) {
}
