package com.mops.bb_backend.dto;

import java.util.UUID;

public record UserDetailsDto(
        UUID id,
        String nickname,
        String firstName,
        String lastName,
        String email
) {
}
