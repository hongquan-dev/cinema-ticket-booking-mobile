package com.example.cinema_booking_backend.services.admin;

import com.cloudinary.Cloudinary;
import com.example.cinema_booking_backend.enums.post.PostCategory;
import com.example.cinema_booking_backend.enums.post.PostStatus;
import com.example.cinema_booking_backend.models.Post;
import com.example.cinema_booking_backend.repositories.admin.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Transactional(rollbackFor = Exception.class)
    public Post savePostWithThumbnail(Post post, MultipartFile file) {
        try {
            if (file != null && !file.isEmpty()) {
                // Upload to 'postThumbnails' folder
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        com.cloudinary.utils.ObjectUtils.asMap("folder", "postThumbnails"));
                post.setThumbnailUrl((String) uploadResult.get("secure_url"));
            }
            return postRepository.save(post);
        } catch (IOException e) {
            throw new RuntimeException("Thumbnail upload failed: " + e.getMessage());
        }
    }

    public Page<Post> getAllPostsPaged(int page, int size, String search, PostStatus status, PostCategory category) {
        if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

        Pageable pageable = PageRequest.of(page - 1, size);

        String titleSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        return postRepository.searchPosts(titleSearch, status, category, pageable);
    }

    public Post getPostById(UUID id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
    }

    @Transactional(rollbackFor = Exception.class)
    public Post updatePost(UUID id, Post postDetails, MultipartFile file) {
        Post existingPost = getPostById(id);

        existingPost.setTitle(postDetails.getTitle());
        existingPost.setDescription(postDetails.getDescription());
        existingPost.setContent(postDetails.getContent());
        existingPost.setStatus(postDetails.getStatus());
        existingPost.setCategory(postDetails.getCategory());

        if (file != null && !file.isEmpty()) {
            try {
                // Delete old thumbnail
                String oldUrl = existingPost.getThumbnailUrl();
                String publicId = extractPublicIdFromUrl(oldUrl);
                if (publicId != null) {
                    cloudinary.uploader().destroy(publicId, com.cloudinary.utils.ObjectUtils.emptyMap());
                }

                // Upload new thumbnail
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                        com.cloudinary.utils.ObjectUtils.asMap("folder", "postThumbnails"));
                existingPost.setThumbnailUrl((String) uploadResult.get("secure_url"));
            } catch (IOException e) {
                throw new RuntimeException("Thumbnail update failed: " + e.getMessage());
            }
        }
        return postRepository.save(existingPost);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deletePost(UUID id) {
        Post post = getPostById(id);
        try {
            String publicId = extractPublicIdFromUrl(post.getThumbnailUrl());
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, com.cloudinary.utils.ObjectUtils.emptyMap());
            }
            postRepository.delete(post);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image from Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicIdFromUrl(String url) {
        if (url == null || !url.contains("postThumbnails/")) return null;
        try {
            String partAfterFolder = url.substring(url.indexOf("postThumbnails/"));
            return partAfterFolder.substring(0, partAfterFolder.lastIndexOf("."));
        } catch (Exception e) {
            return null;
        }
    }
}