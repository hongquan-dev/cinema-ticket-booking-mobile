package com.example.cinema_booking_backend.controllers;

import com.example.cinema_booking_backend.dto.user.ChangePasswordRequest;
import com.example.cinema_booking_backend.dto.user.UserResponse;
import com.example.cinema_booking_backend.entity.User;
import com.example.cinema_booking_backend.entity.VerificationToken;
import com.example.cinema_booking_backend.repositories.UserRepository;
import com.example.cinema_booking_backend.repositories.VerificationTokenRepository;
import com.example.cinema_booking_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable UUID id, @RequestBody ChangePasswordRequest request) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            userService.changePassword(username, request);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserResponse userResponse = userService.getUserProfile(id);
            response.put("data", userResponse);
            response.put("message", "Fetch user information successfully");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable UUID id, @RequestBody User updateRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserResponse updatedUser = userService.updateProfile(id, updateRequest);
            response.put("data", updatedUser);
            response.put("message", "Update user information successfully");

            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping(value = "/{id}/avatar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateAvatar(
            @PathVariable UUID id,
            @RequestPart("file") MultipartFile file
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserResponse updatedUser = userService.updateAvatar(id, file);

            response.put("data", updatedUser);
            response.put("message", "Avatar updated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Update avatar failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/send-verification")
    public ResponseEntity<?> requestVerification(@PathVariable UUID id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.isVerified()) {
                Map<String, String> res = new HashMap<>();
                res.put("message", "Your account has already been verified");
                return ResponseEntity.ok(res);
            }

            userService.sendVerificationEmail(user);

            Map<String, String> res = new HashMap<>();
            res.put("message", "Verification link has been sent to your email");
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to send email: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam("token") String token) {
        String centerTextHtml = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { " +
                "margin: 0; " +
                "display: flex; " +
                "justify-content: center; " +
                "align-items: center; " +
                "height: 100vh; " +
                "font-family: Arial, sans-serif; " +
                "background-color: #f8f9fa; " +
                "}" +
                "p { " +
                "font-size: 24px; " +
                "font-weight: bold; " +
                "color: #333; " +
                "text-align: center; " +
                "line-height: 1.5; " +
                "white-space: pre-line; " +
                "padding: 20px; " +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<p>%s</p>" +
                "</body>" +
                "</html>";

        Optional<VerificationToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            String msg = "Đã xảy ra lỗi!\nHãy kiểm tra tình trạng xác thực và quay lại sau ít phút!";
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body(String.format(centerTextHtml, msg));
        }

        VerificationToken verificationToken = tokenOptional.get();

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            String msg = "Liên kết xác thực đã hết hạn!\nVui lòng yêu cầu gửi lại mã mới.";
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body(String.format(centerTextHtml, msg));
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        String displayName = (user.getFullName() != null && !user.getFullName().trim().isEmpty())
                ? user.getFullName()
                : "Khách hàng";

        String successHtml = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f8f9fa; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }" +
                ".card { background: white; padding: 50px; border-radius: 16px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); text-align: center; max-width: 450px; width: 90%; }" +
                ".icon { font-size: 70px; color: #2ecc71; margin-bottom: 20px; }" +
                "h1 { color: #2d3436; margin-bottom: 15px; font-size: 26px; }" +
                "p { color: #636e72; line-height: 1.6; font-size: 17px; margin: 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='card'>" +
                "<div class='icon'>✓</div>" +
                "<h1>Xác thực thành công</h1>" +
                "<p>Xin chào <strong>" + displayName + "</strong>, tài khoản của bạn tại <b>Rạp chiếu phim quốc gia</b> đã được kích hoạt thành công.</p>" +
                "</div>" +
                "</body>" +
                "</html>";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(successHtml);
    }
}