package org.example.bbbackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class PostDTO {
    private UUID id;
    private String title;
    private String description;
    private String imageUrl;
    private UUID userId;
}
