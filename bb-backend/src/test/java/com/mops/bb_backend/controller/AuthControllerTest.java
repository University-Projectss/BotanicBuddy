package com.mops.bb_backend.service;

import com.mops.bb_backend.controller.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtEncoder;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @MockBean
    private JwtEncoder jwtEncoder;

    @Test
    void testLogin_GenerateToken() {
        // Arrange
        Authentication authentication = Mockito.mock(Authentication.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        Mockito.when(authentication.getName()).thenReturn("user@example.com");
        Mockito.when(jwtEncoder.encode(Mockito.any())).thenReturn(new JwtToken("mock-token"));

        // Act
        String token = authController.login(authentication, request);

        // Assert
        Assertions.assertNotNull(token);
        Assertions.assertEquals("mock-token", token);
    }

    @Test
    void testLogin_InvalidAuthentication() {
        // Arrange
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(jwtEncoder.encode(Mockito.any())).thenThrow(RuntimeException.class);

        // Act & Assert
        Assertions.assertThrows(RuntimeException.class, () -> {
            authController.login(null, request);
        });
    }
}