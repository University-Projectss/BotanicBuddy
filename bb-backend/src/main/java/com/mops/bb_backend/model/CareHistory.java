package com.mops.bb_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "care_history")
@Builder
public class CareHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "action", nullable = false)
    private ActionType action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", referencedColumnName = "id")
    private Plant plant;
}
