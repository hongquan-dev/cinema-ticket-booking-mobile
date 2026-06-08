package com.example.cinema_booking_backend.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.cinema_booking_backend.dto.user.ChangePasswordRequest;
import com.example.cinema_booking_backend.dto.user.UserResponse;
import com.example.cinema_booking_backend.entity.User;
import com.example.cinema_booking_backend.entity.VerificationToken;
import com.example.cinema_booking_backend.repositories.UserRepository;
import com.example.cinema_booking_backend.repositories.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.backendForEmail.url}")
    private String backendUrl;

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

    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword));
        userRepository.save(user);
    }

    public UserResponse getUserProfile(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    public UserResponse updateProfile(UUID id, User updateData) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateData.getEmail() != null && !updateData.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(updateData.getEmail())) {
                throw new RuntimeException("Email already exists in the system");
            }
            user.setEmail(updateData.getEmail());
            user.setVerified(false);
        }

        if (updateData.getPhoneNumber() != null && !updateData.getPhoneNumber().equals(user.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumber(updateData.getPhoneNumber())) {
                throw new RuntimeException("Phone number already exists in the system");
            }
            user.setPhoneNumber(updateData.getPhoneNumber());
        }

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
        tokenRepository.deleteByUser(user);

        String tokenStr = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(tokenStr, user, 3);
        tokenRepository.save(verificationToken);

        String verifyUrl = backendUrl + "/api/users/verify?token=" + tokenStr;

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
}
