package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.UserRegistrationDto;
import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private Account testAccount;

    @Autowired
    private RequestTester requestTester;

    @BeforeEach
    public void setUp() throws Exception {
        testAccount = requestTester.createTestAccount();
        requestTester.authenticateAccount();
    }

    @AfterEach
    void tearDown() {
        requestTester.cleanupTestAccount();
    }

    @Test
    void testUserRegistration_CreatesUser() throws Exception {
        var userDto = new UserRegistrationDto("newUser@example.com", "password", "newUser", "http://example.com/new-photo.jpg");

        mockMvc.perform(post("/accounts")
                    .contentType(APPLICATION_JSON)
                    .content(new JSONObject()
                        .put("email", userDto.email())
                        .put("username", userDto.username())
                        .put("password", userDto.password())
                        .put("photoUrl", userDto.photoUrl())
                        .toString()))
                .andExpect(status().isCreated());

        var createdUser = userRepository.findByUsername("newUser").orElseThrow();
        assertEquals(userDto.username(), createdUser.getUsername());
        assertEquals(userDto.photoUrl(), createdUser.getPhotoUrl());
    }

    @Test
    void testUserRegistration_DuplicateUser() throws Exception {
        mockMvc.perform(post("/accounts")
                        .contentType(APPLICATION_JSON)
                        .content(new JSONObject()
                                .put("email", testAccount.getEmail())
                                .put("username", testAccount.getUsername())
                                .put("password", testAccount.getPassword())
                                .put("photoUrl", testAccount.getUser().getPhotoUrl())
                                .toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAuthenticatedUserDetails() throws Exception {
        mockMvc.perform(requestTester.authenticatedGet("/profile"))
                .andExpect(status().isOk())
                .andExpect(content().json(new JSONObject()
                        .put("id", testAccount.getUser().getId())
                        .put("email", testAccount.getEmail())
                        .put("photoUrl", testAccount.getUser().getPhotoUrl())
                        .put("sendWeatherAlerts", testAccount.getUser().isSendWeatherAlerts())
                        .toString()));
    }

    @Test
    void testToggleWeatherAlerts() throws Exception {
        mockMvc.perform(requestTester.authenticatedPost("/weather-alerts-toggle", null))
                .andExpect(status().isOk());
        requestTester.cleanupTestAccount();
    }

    @Test
    void testDeleteUserAccount() throws Exception {
        mockMvc.perform(requestTester.authenticatedDelete("/accounts"))
                .andExpect(status().isOk());

        var deletedUser = userRepository.findByUsername(testAccount.getUsername());
        assertEquals(deletedUser.isEmpty(), true);
        requestTester.cleanupTestAccount();
    }

}
