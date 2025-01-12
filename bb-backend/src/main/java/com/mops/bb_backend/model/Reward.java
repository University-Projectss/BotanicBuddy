package com.mops.bb_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rewards")
@Builder
public class Reward {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private RewardLevel level;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private RewardAction action;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "required_action_number", nullable = false)
    private int requiredActionNumber;

    @Column(name = "points", nullable = false)
    private int points;
}
