package com.mops.bb_backend.controller;

import com.mops.bb_backend.dto.*;
import com.mops.bb_backend.service.PlantService;
import com.mops.bb_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<Void> addPost(@RequestBody PostRegistrationDto postRegistrationDto) {
        try {
            postService.addPost(postRegistrationDto.title(),
                    postRegistrationDto.content(),
                    postRegistrationDto.photoUrl());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/posts")
    public ResponseEntity<PostPaginationDto> getPostList(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        return new ResponseEntity<>(postService.getPostList(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostDetailsDto> getPostDetails(@PathVariable String id) {
        return new ResponseEntity<>(postService.getPostDetails(id), HttpStatus.OK);
    }

    @PostMapping("/posts/{id}/like")
    public ResponseEntity<Void> likePost(@PathVariable String id) {
        postService.likePost(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/posts/{id}/like")
    public ResponseEntity<Boolean> isLiked(@PathVariable String id) {
        return new ResponseEntity<>(postService.isLiked(id), HttpStatus.OK);
    }
}
