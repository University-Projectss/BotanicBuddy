package com.mops.bb_backend.dto;

import com.mops.bb_backend.model.ActionType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record CareHistoryDto(LocalDate date, LocalTime time, ActionType action) {
}
