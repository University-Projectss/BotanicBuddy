package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.UserDetailsDto;
import com.mops.bb_backend.dto.UserRegistrationDto;
import com.mops.bb_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping("/accounts")
    public ResponseEntity<Void> createUserAccount(@RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            userService.createUserAccount(userRegistrationDto.email(),
                    userRegistrationDto.password(),
                    userRegistrationDto.username(),
                    userRegistrationDto.photoUrl());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDetailsDto> getAuthenticatedUserDetails() {
        return ResponseEntity.ok(userService.getAuthenticatedUserProfile());
    }

    @PostMapping("/weather-alerts-toggle")
    public ResponseEntity<Void> toggleWeatherAlerts() {
        userService.toggleWeatherAlerts();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/accounts")
    public void deleteUserAccount() {
        userService.deleteUserAccount();
    }
}
