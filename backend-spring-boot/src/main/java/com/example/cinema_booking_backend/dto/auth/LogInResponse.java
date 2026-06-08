package com.example.cinema_booking_backend.dto.auth;
import com.example.cinema_booking_backend.enums.common.Role;

public class LogInResponse {
    public String message;
    public String accessToken;
    public String refreshToken;
    public Role role;
    public UserResponse data;
}
