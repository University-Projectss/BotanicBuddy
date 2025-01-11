package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.UserDetailsDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final LocationService locationService;

    @Transactional
    public void createUserAccount(String email, String password, String username, String photoUrl) {
        validateUserCredentials(email, username);
        var account = accountService.createAccount(email, password);
        var user = createUser(account, username, photoUrl);
        accountService.linkUserToAccount(user, account);
    }

    private User createUser(Account account, String username, String photoUrl) {
        var user = User.builder().account(account).username(username).photoUrl(photoUrl).sendWeatherAlerts(true).build();
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

    public void setUserLocation(HttpServletRequest request) {
        var user = getAuthenticatedUser();
        if (user.getLocation() != null) {
            return;
        }
        Map<String, String> userLocation = locationService.getLocation(request);
        if (userLocation.containsKey("loc")) {
            String location = userLocation.get("loc");
            user.setLocation(location);
            userRepository.save(user);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAllUsers();
    }

    public void toggleWeatherAlerts() {
        var user = getAuthenticatedUser();
        var sendWeatherAlerts = user.isSendWeatherAlerts();
        user.setSendWeatherAlerts(!sendWeatherAlerts);
        userRepository.save(user);
    }

    public void deleteUserAccount() {
        var user = getAuthenticatedUser();
        userRepository.delete(user);
    }

    private UserDetailsDto mapUserToUserDetailsDto(User user) {
        return UserDetailsDto.builder().id(user.getId())
                .email(user.getAccount().getEmail())
                .username(user.getUsername())
                .photoUrl(user.getPhotoUrl())
                .build();
    }
}
