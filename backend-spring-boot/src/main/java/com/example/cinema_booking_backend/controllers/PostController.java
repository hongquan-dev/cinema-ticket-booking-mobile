package com.example.cinema_booking_backend.controllers;

import com.example.cinema_booking_backend.enums.post.PostCategory;
import com.example.cinema_booking_backend.enums.post.PostStatus;
import com.example.cinema_booking_backend.entity.Post;
import com.example.cinema_booking_backend.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

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
}