package com.example.cinema_booking_backend.services.user;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.cinema_booking_backend.dtos.user.ChangePasswordRequest;
import com.example.cinema_booking_backend.dtos.user.UserResponse;
import com.example.cinema_booking_backend.models.User;
import com.example.cinema_booking_backend.models.VerificationToken;
import com.example.cinema_booking_backend.repositories.user.UserRepository;
import com.example.cinema_booking_backend.repositories.user.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mail.javamail.JavaMailSender;


import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.backend.url}")
    private String backendUrl;

    // Helper method to map User entity to UserResponse DTO
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setAddress(user.getAddress());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole().name());
        response.setVerified(user.isVerified());
        response.setCreatedAt(user.getCreatedAt());
        response.setAvatar(user.getAvatar());

        return response;
    }

    // Process password change logic
    public void changePassword(String username, ChangePasswordRequest request) {
        // Find user by username from token
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify if old password matches database
        if (!passwordEncoder.matches(request.oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Hash and update new password
        user.setPassword(passwordEncoder.encode(request.newPassword));
        userRepository.save(user);
    }

    // Get user profile and return as DTO
    public UserResponse getUserProfile(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    // Update profile and return the updated DTO
    public UserResponse updateProfile(UUID id, User updateData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if new email is provided and different from the current one
        if (updateData.getEmail() != null && !updateData.getEmail().equalsIgnoreCase(user.getEmail())) {
            // 1. Check if the new email is already taken by someone else
            if (userRepository.existsByEmail(updateData.getEmail())) {
                throw new RuntimeException("Email already exists in the system");
            }

            // 2. Update the email
            user.setEmail(updateData.getEmail());

            // 3. Reset verification status because the email has changed
            user.setVerified(false);
        }

        // Check if new phone number is already taken by another user
        if (updateData.getPhoneNumber() != null && !updateData.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(updateData.getPhoneNumber())) {
                throw new RuntimeException("Phone number already exists in the system");
            }
            user.setPhoneNumber(updateData.getPhoneNumber());
        }

        // Update personal information
        if (updateData.getFullName() != null) user.setFullName(updateData.getFullName());
        if (updateData.getAddress() != null) user.setAddress(updateData.getAddress());
        if (updateData.getRole() != null) user.setRole(updateData.getRole());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserResponse updateAvatar(UUID id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {
            if (user.getAvatar() != null) {
                String publicId = extractPublicIdFromUrl(user.getAvatar());
                if (publicId != null) {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                }
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "userAvatars"));

            String imageUrl = (String) uploadResult.get("secure_url");

            user.setAvatar(imageUrl);
            User savedUser = userRepository.save(user);

            return mapToResponse(savedUser);

        } catch (IOException e) {
            throw new RuntimeException("Cloudinary upload failed: " + e.getMessage());
        }
    }

    private String extractPublicIdFromUrl(String url) {
        if (url == null || !url.contains("userAvatars/")) return null;
        try {
            String partAfterFolder = url.substring(url.indexOf("userAvatars/"));
            return partAfterFolder.substring(0, partAfterFolder.lastIndexOf("."));
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    public void sendVerificationEmail(User user) {
        // 1. Remove old token if exists to avoid data redundancy
        tokenRepository.deleteByUser(user);

        // 2. Generate new token (expires in 15 minutes)
        String tokenStr = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(tokenStr, user, 3);
        tokenRepository.save(verificationToken);

        // 3. Construct and send the email
        String verifyUrl = backendUrl + "/users/verify?token=" + tokenStr;

        String displayName = (user.getFullName() != null && !user.getFullName().trim().isEmpty())
                ? user.getFullName()
                : "";

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(user.getEmail());
            helper.setSubject("Xác thực tài khoản - Rạp chiếu phim quốc gia");

            String htmlContent =
                    "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; border: 1px solid #ddd; padding: 5px 25px 0px 25px; border-radius: 10px;'>" +
                            "<h3 style='color: #e50914; text-align: center;'>XÁC THỰC TÀI KHOẢN</h3>" +
                            "<p>Xin chào <strong>" + displayName + "</strong>,</p>" +
                            "<p style='text-align: justify;'>Cảm ơn bạn đã đăng ký tài khoản tại <strong>Rạp chiếu phim quốc gia</strong>. Vui lòng nhấn vào nút bên dưới để hoàn tất quá trình xác thực tài khoản của bạn:</p>" +
                            "<div style='text-align: center; margin: 30px 0;'>" +
                            "<a href='" + verifyUrl + "' style='background-color: #1e90ff; color: white; padding: 12px 25px; text-decoration: none; font-weight: bold; border-radius: 5px; display: inline-block;'>Xác thực ngay</a>" +
                            "</div>" +
                            "<p style='color: #666; font-size: 13px; text-align: justify'>Lưu ý: Liên kết này sẽ hết hạn sau <strong>3 phút</strong>. Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>" +
                            "<hr style='border: 0; border-top: 1px solid #eee;'>" +
                            "<p style='font-size: 12px; color: #999; text-align: center;'>Đây là email tự động, vui lòng không phản hồi email này.</p>" +
                            "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
        }
        catch (MessagingException e) {
            throw new RuntimeException("Error when send verification email: " + e.getMessage());
        }
    }

    public Page<UserResponse> getAllUsersPaged(int page, int size, String fullName, String email, String phoneNumber) {
        if (page < 1) throw new IllegalArgumentException("Page number must be at least 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be greater than 0");

        String nameFilter = (fullName != null && !fullName.trim().isEmpty()) ? fullName.trim() : null;
        String emailFilter = (email != null && !email.trim().isEmpty()) ? email.trim() : null;
        String phoneFilter = (phoneNumber != null && !phoneNumber.trim().isEmpty()) ? phoneNumber.trim() : null;

        try {
            int internalPage = page - 1;
            // Sorting by newest users first
            Pageable pageable = PageRequest.of(internalPage, size, Sort.by("createdAt").descending());

            // Call repository with individual filters
            Page<User> userPage = userRepository.findByFilters(nameFilter, emailFilter, phoneFilter, pageable);

            return userPage.map(this::mapToResponse);
        } catch (Exception e) {
            throw new RuntimeException("Database error while filtering users: " + e.getMessage());
        }
    }
}
