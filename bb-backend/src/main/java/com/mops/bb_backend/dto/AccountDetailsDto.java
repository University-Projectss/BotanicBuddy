package com.mops.bb_backend.dto;

import com.mops.bb_backend.model.Role;

public record AccountDetailsDto(String email, Role role) {
}