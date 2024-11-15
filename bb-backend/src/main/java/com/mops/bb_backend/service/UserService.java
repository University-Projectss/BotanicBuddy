package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.UserDetailsDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountService accountService;

    public void createUserAccount(String email, String password, String username, String photoUrl) {
        validateUserCredentials(email, username);
        var account = accountService.createAccount(email, password);
        var user = createUser(account, username, photoUrl);
        accountService.linkUserToAccount(user, account);
    }

    private User createUser(Account account, String username, String photoUrl) {
        var user = User.builder().account(account).username(username).photoUrl(photoUrl).build();
        return userRepository.save(user);
    }

    private void validateUserCredentials(String email, String username) {
        if (accountService.getAccountByEmail(email).isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "An account with the provided email already exists!");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "An account with the provided username already exists!");
        }
    }

    public UserDetailsDto getAuthenticatedUserProfile() {
        var user = getAuthenticatedUser();
        return mapUserToUserDetailsDto(user);
    }

    public User getAuthenticatedUser() {
        var email = accountService.getAuthenticatedUserEmail();
        return userRepository.findUserByAccountEmail(email);
    }

    private UserDetailsDto mapUserToUserDetailsDto(User user) {
        return UserDetailsDto.builder().id(user.getId())
                .email(user.getAccount().getEmail())
                .username(user.getUsername())
                .photoUrl(user.getPhotoUrl())
                .build();
    }
}
