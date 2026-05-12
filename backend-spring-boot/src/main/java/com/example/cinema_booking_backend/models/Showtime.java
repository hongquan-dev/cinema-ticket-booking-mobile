package com.example.cinema_booking_backend.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    // --- Relationships ---

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // --- Fields ---

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false, length = 50)
    private String format; // e.g., "2D", "3D", "IMAX"

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- JPA Lifecycle Hooks ---

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateEndTime();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateEndTime();
    }

    /**
     * Logic to calculate end time: StartTime + Movie Duration + 20 mins cleaning
     */
    private void calculateEndTime() {
        if (this.movie != null && this.startTime != null && this.movie.getDuration() != null) {
            // Buffer 20 minutes for theater cleaning and trailers
            this.endTime = this.startTime.plusMinutes(this.movie.getDuration() + 20);
        }
    }

    // --- Constructors ---

    public Showtime() {
    }

    public Showtime(Movie movie, Room room, LocalDateTime startTime, String format, BigDecimal basePrice) {
        this.movie = movie;
        this.room = room;
        this.startTime = startTime;
        this.format = format;
        this.basePrice = basePrice;
        calculateEndTime();
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        calculateEndTime();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
