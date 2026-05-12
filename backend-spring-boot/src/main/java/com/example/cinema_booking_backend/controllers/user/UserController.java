package com.example.cinema_booking_backend.controllers.user;

import com.example.cinema_booking_backend.dtos.user.ChangePasswordRequest;
import com.example.cinema_booking_backend.dtos.user.UserResponse;
import com.example.cinema_booking_backend.models.User;
import com.example.cinema_booking_backend.models.VerificationToken;
import com.example.cinema_booking_backend.repositories.user.UserRepository;
import com.example.cinema_booking_backend.repositories.user.VerificationTokenRepository;
import com.example.cinema_booking_backend.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    // Update user password with token validation
    @PostMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable UUID id, @RequestBody ChangePasswordRequest request) {
        try {
            // Get username from the authentication object set by JwtFilter
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            userService.changePassword(username, request);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully!");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Fetch user details by ID and return clean UserResponse DTO
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserResponse userResponse = userService.getUserProfile(id);
            response.put("data", userResponse);
            response.put("message", "Fetch user information successfully!");

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Update user profile by ID and return updated DTO
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable UUID id, @RequestBody User updateRequest) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserResponse updatedUser = userService.updateProfile(id, updateRequest);
            response.put("data", updatedUser);
            response.put("message", "Update user information successfully!");

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
            response.put("message", "Avatar updated successfully!");

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
            // 1. Find user by ID
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 2. Check if the user is already verified
            if (user.isVerified()) {
                Map<String, String> res = new HashMap<>();
                res.put("message", "Your account has already been verified.");
                return ResponseEntity.ok(res);
            }

            // 3. Call service to generate token and send email
            userService.sendVerificationEmail(user);

            Map<String, String> res = new HashMap<>();
            res.put("message", "Verification link has been sent to your email!");
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

        // 1. Look up the token in the database
        Optional<VerificationToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            String msg = "Đã xảy ra lỗi!\nHãy kiểm tra tình trạng xác thực và quay lại sau ít phút!";
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body(String.format(centerTextHtml, msg));
        }

        VerificationToken verificationToken = tokenOptional.get();

        // 2. Check if the token has expired
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            String msg = "Liên kết xác thực đã hết hạn!\nVui lòng yêu cầu gửi lại mã mới.";
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_HTML)
                    .body(String.format(centerTextHtml, msg));
        }

        // 3. Update user verification status
        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        // 4. Remove the used token
        tokenRepository.delete(verificationToken);

        // 5. SUCCESS: Return beautiful HTML interface
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

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phoneNumber) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Call service to get paged data
            Page<UserResponse> userPage = userService.getAllUsersPaged(page, size, fullName, email, phoneNumber);

            if (userPage.isEmpty()) {
                response.put("message", "No users found with the provided filters");
            } else {
                response.put("message", "Fetched users successfully");
            }

            response.put("users", userPage.getContent());
            response.put("currentPage", userPage.getNumber() + 1);
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());
            response.put("pageSize", userPage.getSize());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("message", "Invalid parameters: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", "Could not fetch user list: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}