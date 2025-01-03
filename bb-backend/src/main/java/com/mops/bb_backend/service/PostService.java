package com.mops.bb_backend.service;

import com.mops.bb_backend.dto.PostDetailsDto;
import com.mops.bb_backend.dto.PostPaginationDto;
import com.mops.bb_backend.exception.CustomException;
import com.mops.bb_backend.model.Post;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;

import static com.mops.bb_backend.utils.Converter.convertStringToUUID;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    public void addPost(String title, String content, String photoUrl) {
        var user = userService.getAuthenticatedUser();
        var post = mapPostRegistrationDtoToPost(title, content, photoUrl, user);
        postRepository.save(post);
    }

    public PostPaginationDto getPostList(int pageNumber, int pageSize) {
        var pageable = PageRequest.of(pageNumber, pageSize);
        var response = postRepository.findAll(pageable);
        var posts = response.getContent().stream().sorted(Comparator.comparing(Post::getUploadDate)
                .thenComparing(Post::getTitle)).map(PostService::mapPostToPostDetailsDto).toList();
        return new PostPaginationDto(posts, response.getNumber(), response.getSize(),
                response.getTotalElements(), response.getTotalPages(), response.isLast());
    }

    public PostDetailsDto getPostDetails(String id) {
        var uuid = convertStringToUUID(id);
        var plant = postRepository.findById(uuid)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "No post found with the provided ID!"));

        return mapPostToPostDetailsDto(plant);
    }

    public void likePost(String id) {
        var user = userService.getAuthenticatedUser();
        var post = postRepository.findById(convertStringToUUID(id))
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "No post found with the provided ID!"));
        
        if (post.getLikedBy().contains(user)) {
            post.getLikedBy().remove(user);
            postRepository.save(post);
            return;
        }
        post.getLikedBy().add(user);
        postRepository.save(post);
    }

    public boolean isLiked(String id) {
        var user = userService.getAuthenticatedUser();
        var post = postRepository.findById(convertStringToUUID(id))
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "No post found with the provided ID!"));
        return post.getLikedBy().contains(user);
    }

    private static Post mapPostRegistrationDtoToPost(String title, String content, String photoUrl, User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .photoUrl(photoUrl)
                .uploadDate(LocalDate.now())
                .user(user)
                .build();
    }

    private static PostDetailsDto mapPostToPostDetailsDto(Post post) {
        return PostDetailsDto.builder()
                .id(post.getId().toString())
                .title(post.getTitle())
                .content(post.getContent())
                .author(post.getUser().getUsername())
                .photoUrl(post.getPhotoUrl())
                .uploadDate(post.getUploadDate().toString())
                .totalLikes(post.getLikedBy().size())
                .likedByUser(post.getLikedBy().contains(post.getUser()))
                .build();
    }
}
