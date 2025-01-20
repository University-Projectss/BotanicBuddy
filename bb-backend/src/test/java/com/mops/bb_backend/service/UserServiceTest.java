package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.UserDetailsDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.Role;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private LocationService locationService;

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserAccount_Success() {
        String email = "test@example.com";
        String password = "password123";
        String username = "testuser";
        String photoUrl = "http://example.com/photo.jpg";

        Account mockAccount = new Account(email, password, Role.USER);
        when(accountService.createAccount(email, password)).thenReturn(mockAccount);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(accountService.getAccountByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        userService.createUserAccount(email, password, username, photoUrl);

        verify(accountService).createAccount(email, password);
        verify(userRepository).save(any(User.class));
        verify(rewardService).initializeRewardCountersForUser(any(User.class));
    }

    @Test
    void createUserAccount_EmailAlreadyExists() {
        String email = "test@example.com";
        String username = "testuser";
        when(accountService.getAccountByEmail(email)).thenReturn(Optional.of(new Account(email, "password123", Role.USER)));

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.createUserAccount(email, "password123", username, "http://example.com/photo.jpg")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("An account with the provided email already exists!", exception.getMessage());
    }

    @Test
    void createUserAccount_UsernameAlreadyExists() {
        String email = "test@example.com";
        String username = "testuser";
        when(accountService.getAccountByEmail(email)).thenReturn(Optional.empty());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        CustomException exception = assertThrows(CustomException.class, () ->
                userService.createUserAccount(email, "password123", username, "http://example.com/photo.jpg")
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals("An account with the provided username already exists!", exception.getMessage());
    }

    @Test
    void getAuthenticatedUserProfile_Success() {
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setAccount(new Account(email, "password123", Role.USER));
        mockUser.getAccount().setEmail(email);
        mockUser.setUsername("testuser");
        mockUser.setPhotoUrl("http://example.com/photo.jpg");
        mockUser.setSendWeatherAlerts(true);

        when(accountService.getAuthenticatedUserEmail()).thenReturn(email);
        when(userRepository.findUserByAccountEmail(email)).thenReturn(mockUser);

        UserDetailsDto result = userService.getAuthenticatedUserProfile();

        assertNotNull(result);
        assertEquals(email, result.email());
        assertEquals("testuser", result.username());
        assertEquals("http://example.com/photo.jpg", result.photoUrl());
        assertTrue(result.sendWeatherAlerts());
    }

    @Test
    void setUserLocation_Success() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        User mockUser = new User();
        when(userRepository.findUserByAccountEmail(anyString())).thenReturn(mockUser);
        when(locationService.getLocation(mockRequest)).thenReturn(Map.of("loc", "Sample Location"));
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        userService.setUserLocation(mockRequest);

        verify(userRepository).save(mockUser);
        assertEquals("Sample Location", mockUser.getLocation());
    }

    @Test
    void toggleWeatherAlerts_Success() {
        User mockUser = new User();
        mockUser.setSendWeatherAlerts(false);
        when(userRepository.findUserByAccountEmail(anyString())).thenReturn(mockUser);
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        userService.toggleWeatherAlerts();

        verify(userRepository).save(mockUser);
        assertTrue(mockUser.isSendWeatherAlerts());
    }

    @Test
    void deleteUserAccount_Success() {
        User mockUser = new User();
        when(accountService.getAuthenticatedUserEmail()).thenReturn("email");
        when(userRepository.findUserByAccountEmail(anyString())).thenReturn(mockUser);

        userService.deleteUserAccount();

        verify(userRepository).delete(mockUser);
    }

    @Test
    void getAllUsers_Success() {
        List<User> mockUsers = List.of(new User(), new User());
        when(userRepository.findAllUsers()).thenReturn(mockUsers);

        List<User> result = userService.getAllUsers();

        assertEquals(mockUsers.size(), result.size());
        verify(userRepository).findAllUsers();
    }
}