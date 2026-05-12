package com.example.cinema_booking_backend.dtos.user;

public class ChangePasswordRequest {
    public String oldPassword; // Used to verify identity
    public String newPassword; // The new password to be hashed
}
