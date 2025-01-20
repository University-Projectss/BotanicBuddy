package com.mops.bb_backend.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDetailsDto(UUID id, String username, String email, String photoUrl, boolean sendWeatherAlerts) {
}
