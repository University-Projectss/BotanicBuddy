package com.mops.bb_backend.service;

import com.mops.bb_backend.exception.AccountAlreadyHasProfile;
import com.mops.bb_backend.exception.AccountDoesNotExist;
import com.mops.bb_backend.exception.UserProfileNotFoundException;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountService accountService;

    @Transactional
    public User createUserProfile(String nickname, String firstName, String lastName, String accountEmail) {
        var account = accountService.findAccountByEmail(accountEmail).orElseThrow(AccountDoesNotExist::new);
        var user = User.builder().nickname(nickname).firstName(firstName).lastName(lastName).account(account).build();
        var userCreated = userRepository.save(user);
        var wasLinked = accountService.linkProfileToAccount(user, accountEmail);
        if (!wasLinked) {
            throw new AccountAlreadyHasProfile();
        }
        return userCreated;
    }

    public User getAuthenticatedUserProfile() {
        var userEmail = accountService.getAuthenticatedUserEmail();
        var patientProfile = userRepository.findUserProfileByEmail(userEmail);
        if (patientProfile == null) {
            throw new UserProfileNotFoundException();
        } else {
            return patientProfile;
        }
    }
}
