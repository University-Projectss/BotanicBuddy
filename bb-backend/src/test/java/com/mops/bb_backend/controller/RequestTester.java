package com.mops.bb_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.service.AccountService;
import com.mops.bb_backend.service.UserService;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class RequestTester {
    @Autowired
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nullable
    private Account account;

    @Nullable
    private String jwtToken;

    @Autowired
    private UserService userService;

    public Account createTestAccount() {
        userService.createUserAccount("testuser@example.com", "password", "testuser", "http://example.com/photo.jpg");
        account = accountService.getAccountByEmail("testuser@example.com").orElseThrow();
        return account;
    }

    public void cleanupTestAccount() {
        accountService.deleteAccount(account);
    }

    public String authenticateAccount() throws Exception {
        assert account != null;
        var request = post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .with(httpBasic(account.getEmail(), "password"));
        jwtToken = mockMvc.perform(request).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return jwtToken;
    }

    MockHttpServletRequestBuilder addTokenToRequest(MockHttpServletRequestBuilder request) {
        assert jwtToken != null;
        return request
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
    }

    public MockHttpServletRequestBuilder authenticatedPost(String url, Object body) throws JsonProcessingException {
        var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        return addTokenToRequest(post(url).content(json));
    }

    public MockHttpServletRequestBuilder authenticatedPatch(String url, Object body) throws JsonProcessingException {
        var json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        return addTokenToRequest(patch(url).content(json));
    }

    public MockHttpServletRequestBuilder authenticatedGet(String url) {
        return addTokenToRequest(get(url));
    }

    public MockHttpServletRequestBuilder authenticatedDelete(String url) {
        return addTokenToRequest(delete(url));
    }
}