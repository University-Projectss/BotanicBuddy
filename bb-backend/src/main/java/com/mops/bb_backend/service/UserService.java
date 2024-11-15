package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.UserDetailsDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.AccountRepository;
import com.mops.bb_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Transactional
    public void createUserAccount(String email, String password, String username, String photoUrl) {
        validateUserCredentials(email, username);
        var account = accountService.createAccount(email, password);
        var user = createUser(account, username, photoUrl);
        account.setUser(user);
        accountRepository.save(account);
        accountService.linkUserToAccount(user, email);
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
        var email = accountService.getAuthenticatedUserEmail();
        var user = userRepository.findUserByAccountEmail(email);
        return mapUserToUserDetailsDto(user);
    }

    private UserDetailsDto mapUserToUserDetailsDto(User user) {
        return UserDetailsDto.builder().id(user.getId())
                .email(user.getAccount().getEmail())
                .username(user.getUsername())
                .photoUrl(user.getPhotoUrl())
                .build();
    }
}
