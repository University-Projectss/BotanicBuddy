package com.mops.bb_backend.controller;


import com.mops.bb_backend.dto.UserDetailsDto;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.service.AccountService;
import com.mops.bb_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.mops.bb_backend.dto.UserCreationRequestDto;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final AccountService accountService;

    @PostMapping("/users")
    public ResponseEntity<Void> createUserProfile(@Valid @RequestBody UserCreationRequestDto requestDto) {
        userService.createUserProfile(
                requestDto.nickname(),
                requestDto.firstName(),
                requestDto.lastName(),
                accountService.getAuthenticatedUserEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/users/0")
    public ResponseEntity<UserDetailsDto> getCurrentUserProfileDetails() {
        User user = userService.getAuthenticatedUserProfile();
        var userDetailsDto = new UserDetailsDto(
                user.getId(),
                user.getNickname(),
                user.getFirstName(),
                user.getLastName(),
                user.getAccount().getEmail());
        return ResponseEntity.ok(userDetailsDto);
    }
}
