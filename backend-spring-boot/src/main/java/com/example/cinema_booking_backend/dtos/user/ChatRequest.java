package com.example.cinema_booking_backend.dtos.user;

import lombok.Data;
import java.util.UUID;

@Data
public class ChatRequest {
    private UUID userId;
    private String message;
}