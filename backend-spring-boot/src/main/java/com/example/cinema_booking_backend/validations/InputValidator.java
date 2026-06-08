package com.example.cinema_booking_backend.validations;

public class InputValidator {

    public static boolean isInvalid(String input) {
        if (input == null || input.trim().isEmpty()) return true;

        String pattern = ".*(['\";<>]).*";
        return input.matches(pattern) || input.length() > 30;
    }
}
