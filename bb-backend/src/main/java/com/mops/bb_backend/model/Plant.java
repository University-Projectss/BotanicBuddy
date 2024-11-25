package com.mops.bb_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plants")
@Builder
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "common_name", nullable = false)
    private String commonName;

    @Column(name = "scientific_name", nullable = false)
    private String scientificName;

    @Column(name = "family", nullable = false)
    private String family;

    @Column(name = "photo_url", nullable = false)
    private String photoUrl;

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;

    @Column(name = "care_recommendation", columnDefinition = "TEXT")
    private String careRecommendation;

    @Column(name = "watering_frequency")
    private int wateringFrequency;

    @Column(name = "light")
    private String light;

    @Column(name = "soil")
    private String soil;

    @Column(name = "temperature")
    private String temperature;
}
