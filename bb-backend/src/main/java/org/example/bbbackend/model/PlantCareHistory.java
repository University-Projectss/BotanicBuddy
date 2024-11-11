package org.example.bbbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plants_care_history")
public class PlantCareHistory {
    @Id
    private UUID id;

    @Column(nullable = false, length = 50)
    private String careType;

    private LocalDateTime timestamp;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;
}
