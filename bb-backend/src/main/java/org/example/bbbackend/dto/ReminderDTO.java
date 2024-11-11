package org.example.bbbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReminderDTO {
    private UUID id;
    private LocalDateTime reminderDate;
    private String careType;
    private String status;
    private UUID userId;
    private UUID plantId;
}
