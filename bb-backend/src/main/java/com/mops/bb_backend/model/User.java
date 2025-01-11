package com.mops.bb_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeatherForecast> weatherForecasts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plant> plants;

    @Column(name = "username", nullable = false, length = 30, unique = true)
    private String username;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "location")
    private String location;

    @Column(name = "send_weather_alerts", nullable = false)
    private boolean sendWeatherAlerts;

    @ManyToMany(mappedBy = "likedBy")
    private List<Post> likedPosts;
}
