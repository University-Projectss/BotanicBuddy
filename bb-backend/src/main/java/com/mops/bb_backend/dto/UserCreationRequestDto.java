package com.mops.bb_backend.dto;

public record UserCreationRequestDto(
        String firstName,
        String lastName,
        String nickname,
        String email) {
}
