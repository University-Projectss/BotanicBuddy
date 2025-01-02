package com.mops.bb_backend.dto;

import lombok.Builder;

@Builder
public record PostDetailsDto (
        String id,
        String title,
        String content,
        String photoUrl,
        String uploadDate,
        String author,
        int likes
) {
}

