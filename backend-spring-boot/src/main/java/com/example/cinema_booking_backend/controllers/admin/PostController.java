package com.example.cinema_booking_backend.controllers.admin;

import com.example.cinema_booking_backend.enums.post.PostCategory;
import com.example.cinema_booking_backend.enums.post.PostStatus;
import com.example.cinema_booking_backend.models.Post;
import com.example.cinema_booking_backend.services.admin.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> createPost(
            @RequestPart("post") String postJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Post post = objectMapper.readValue(postJson, Post.class);

            Post savedPost = postService.savePostWithThumbnail(post, file);

            response.put("message", "Post created successfully!");
            response.put("data", savedPost);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("message", "Error creating post: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false) PostCategory category
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            Page<Post> postPage = postService.getAllPostsPaged(page, size, search, status, category);

            response.put("message", "Fetched posts successfully");
            response.put("posts", postPage.getContent());
            response.put("currentPage", postPage.getNumber() + 1);
            response.put("totalItems", postPage.getTotalElements());
            response.put("totalPages", postPage.getTotalPages());
            response.put("pageSize", postPage.getSize());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", "Invalid parameters: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", "Could not fetch posts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPostById(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Post post = postService.getPostById(id);
            response.put("message", "Fetched post details successfully");
            response.put("data", post);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Map<String, Object>> updatePost(
            @PathVariable UUID id,
            @RequestPart("post") String postJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Post postDetails = objectMapper.readValue(postJson, Post.class);

            Post updatedPost = postService.updatePost(id, postDetails, file);

            response.put("message", "Post updated successfully!");
            response.put("data", updatedPost);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Update error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            postService.deletePost(id);
            response.put("message", "Post deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "System error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}