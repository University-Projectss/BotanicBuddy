package org.example.bbbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "plants")
public class Plant {
    @Id
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @Column(nullable = false, length = 20)
    private String careFrequency;

    @Column(length = 20)
    private String lightRequirement;

    @Column(length = 50)
    private String soilType;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL)
    private List<PlantCareHistory> careHistory;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL)
    private List<Reminder> reminders;
}
