package com.example.cinema_booking_backend.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    // --- Relationship with Cinema ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cinema_id", nullable = false)
    private Cinema cinema;

    @Column(nullable = false)
    private String name;

    @Column(length = 50)
    private String roomType = "2D";

    @Column(nullable = false)
    private Integer rowsCount = 0;

    @Column(nullable = false)
    private Integer colsCount = 0;

    @Column(nullable = false)
    private Integer totalSeats = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- JPA Lifecycle Hooks ---
    @PrePersist
    protected void onCreate() {
        // Automatically set timestamps and calculate total seats if not set
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (totalSeats == 0 && rowsCount > 0 && colsCount > 0) {
            totalSeats = rowsCount * colsCount;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Refresh timestamp and recalculate total seats on update
        updatedAt = LocalDateTime.now();
        totalSeats = rowsCount * colsCount;
    }

    // --- Constructors ---
    public Room() {
    }

    public Room(Cinema cinema, String name, String roomType, Integer rowsCount, Integer colsCount) {
        this.cinema = cinema;
        this.name = name;
        this.roomType = roomType;
        this.rowsCount = rowsCount;
        this.colsCount = colsCount;
        this.totalSeats = rowsCount * colsCount;
    }

    // --- Getters and Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Cinema getCinema() {
        return cinema;
    }

    public void setCinema(Cinema cinema) {
        this.cinema = cinema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public Integer getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(Integer rowsCount) {
        this.rowsCount = rowsCount;
    }

    public Integer getColsCount() {
        return colsCount;
    }

    public void setColsCount(Integer colsCount) {
        this.colsCount = colsCount;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}