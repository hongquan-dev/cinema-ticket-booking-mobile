package com.example.cinema_booking_backend.services;

import com.example.cinema_booking_backend.enums.post.PostCategory;
import com.example.cinema_booking_backend.enums.post.PostStatus;
import com.example.cinema_booking_backend.entity.Post;
import com.example.cinema_booking_backend.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Page<Post> getAllPostsPaged(int page, int size, String search, PostStatus status, PostCategory category) {
        if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

        Pageable pageable = PageRequest.of(page - 1, size);
        String titleSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        return postRepository.searchPosts(titleSearch, status, category, pageable);
    }
}