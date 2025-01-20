package com.mops.bb_backend.service;
import com.mops.bb_backend.dto.PostDetailsDto;
import com.mops.bb_backend.dto.PostPaginationDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.Post;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addPost_shouldSavePost() {
        // Arrange
        User mockUser = mock(User.class);
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        // Act
        postService.addPost("Test Title", "Test Content", "http://example.com/photo.jpg");

        // Assert
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void getPostList_shouldReturnPaginatedPosts() {
        // Arrange
        Post mockPost = mock(Post.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");
        when(mockPost.getUser()).thenReturn(user);
        when(mockPost.getId()).thenReturn(UUID.randomUUID());
        when(mockPost.getTimestamp()).thenReturn(LocalDateTime.now());
        Page<Post> mockPage = new PageImpl<>(List.of(mockPost));
        when(postRepository.findAll(any(PageRequest.class))).thenReturn(mockPage);

        // Act
        PostPaginationDto result = postService.getPostList(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.totalElements());
    }

    @Test
    void getPostDetails_shouldReturnPostDetails() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        Post mockPost = mock(Post.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("username");
        when(mockPost.getUser()).thenReturn(user);
        when(mockPost.getTimestamp()).thenReturn(LocalDateTime.now());
        when(mockPost.getId()).thenReturn(mockId);
        when(postRepository.findById(mockId)).thenReturn(Optional.of(mockPost));

        // Act
        PostDetailsDto result = postService.getPostDetails(mockId.toString());

        // Assert
        assertNotNull(result);
        verify(postRepository, times(1)).findById(mockId);
    }

    @Test
    void getPostDetails_shouldThrowExceptionIfPostNotFound() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        when(postRepository.findById(mockId)).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class, () -> postService.getPostDetails(mockId.toString()));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void isLiked_shouldReturnTrueIfLikedByUser() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        User mockUser = mock(User.class);
        Post mockPost = mock(Post.class);
        when(postRepository.findById(mockId)).thenReturn(Optional.of(mockPost));
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(mockPost.getLikedBy()).thenReturn(List.of(mockUser));

        // Act
        boolean result = postService.isLiked(mockId.toString());

        // Assert
        assertTrue(result);
    }

    @Test
    void isLiked_shouldReturnFalseIfNotLikedByUser() {
        // Arrange
        UUID mockId = UUID.randomUUID();
        User mockUser = mock(User.class);
        Post mockPost = mock(Post.class);
        when(postRepository.findById(mockId)).thenReturn(Optional.of(mockPost));
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);
        when(mockPost.getLikedBy()).thenReturn(List.of());

        // Act
        boolean result = postService.isLiked(mockId.toString());

        // Assert
        assertFalse(result);
    }
}
