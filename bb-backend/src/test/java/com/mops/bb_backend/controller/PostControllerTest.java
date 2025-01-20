package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.PostDetailsDto;
import com.mops.bb_backend.dto.PostPaginationDto;
import com.mops.bb_backend.dto.PostRegistrationDto;
import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.service.PostService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestTester requestTester;

    @MockBean
    private PostService postService;

    private Account account;

    @BeforeEach
    public void setUp() throws Exception {
        account = requestTester.createTestAccount();
        requestTester.authenticateAccount();
    }

    @AfterEach
    public void tearDown() {
        requestTester.cleanupTestAccount();
    }

    @Test
    void addPost_validRequest_createsPost() throws Exception {
        var postDto = new PostRegistrationDto("Test Title", "Test Content", "http://example.com/photo.jpg");

        mockMvc.perform(requestTester.authenticatedPost("/posts", postDto)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Mockito.verify(postService).addPost(
                eq(postDto.title()),
                eq(postDto.content()),
                eq(postDto.photoUrl()));
    }

    @Test
    void getPostList_validRequest_returnsPostPagination() throws Exception {
        List<PostDetailsDto> postList = new ArrayList<>();
        postList.add(new PostDetailsDto(
                UUID.randomUUID().toString(),
                "Test Title",
                "Test Content",
                "testUser",
                "http://example.com/photo.jpg",
                LocalDateTime.now().toString(),
                10,
                false
        ));
        var postPaginationDto = new PostPaginationDto(postList, 1, 10, 1, 1, true);
        Mockito.when(postService.getPostList(0, 10)).thenReturn(postPaginationDto);

        mockMvc.perform(requestTester.authenticatedGet("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNumber", "0")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.posts[0].title").value("Test Title"));

        Mockito.verify(postService).getPostList(0, 10);
    }

    @Test
    void getPostDetails_validId_returnsPostDetails() throws Exception {
        var postDetailsDto = new PostDetailsDto(
                "1",
                "Test Title",
                "Test Content",
                "testUser",
                "http://example.com/photo.jpg",
                LocalDateTime.now().toString(),
                10,
                false
        );
        Mockito.when(postService.getPostDetails("1")).thenReturn(postDetailsDto);

        mockMvc.perform(requestTester.authenticatedGet("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Test Title"));

        Mockito.verify(postService).getPostDetails("1");
    }

    @Test
    void likePost_validRequest_togglesLike() throws Exception {
        mockMvc.perform(requestTester.authenticatedPatch("/posts/1/like", null)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        Mockito.verify(postService).likePost("1");
    }

    @Test
    void isLiked_validRequest_returnsIsLikedStatus() throws Exception {
        Mockito.when(postService.isLiked("1")).thenReturn(true);

        mockMvc.perform(requestTester.authenticatedGet("/posts/1/like")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        Mockito.verify(postService).isLiked("1");
    }
}
