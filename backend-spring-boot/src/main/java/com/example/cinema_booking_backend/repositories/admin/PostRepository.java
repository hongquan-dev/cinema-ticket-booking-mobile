package com.example.cinema_booking_backend.repositories.admin;

import com.example.cinema_booking_backend.enums.post.PostCategory;
import com.example.cinema_booking_backend.enums.post.PostStatus;
import com.example.cinema_booking_backend.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    // Search posts by title (case-insensitive) with pagination
    Page<Post> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    // Find all and sort by creation date descending
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE " +
            "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%'))) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:category IS NULL OR p.category = :category) " +
            "ORDER BY " +
            "CASE WHEN p.status = com.example.cinema_booking_backend.enums.post.PostStatus.PUBLISHED THEN 1 " +
            "     WHEN p.status = com.example.cinema_booking_backend.enums.post.PostStatus.DRAFT THEN 2 " +
            "     WHEN p.status = com.example.cinema_booking_backend.enums.post.PostStatus.HIDDEN THEN 3 " +
            "     ELSE 4 END ASC, " +
            "p.createdAt DESC")
    Page<Post> searchPosts(
            @Param("title") String title,
            @Param("status") PostStatus status,
            @Param("category") PostCategory category,
            Pageable pageable);
}