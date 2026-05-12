package com.example.cinema_booking_backend.services.user;

import com.example.cinema_booking_backend.dtos.user.ChatRequest;
import com.example.cinema_booking_backend.models.User;
import com.example.cinema_booking_backend.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class ChatBotService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${app.pythonAI.url}")
    private String pythonAIUrl;

    @Autowired
    private UserRepository userRepository;

    public String processChat(ChatRequest request) {
        try {
            // Validate User existence
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

            // Validate Message (Check if null, empty, or only whitespace)
            if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
                throw new RuntimeException("Message cannot be empty or null");
            }

            // 1. Prepare the payload for Python Server
            // We send userId and message just like the Python app expects
            Map<String, Object> payload = Map.of(
                    "user_id", request.getUserId().toString(),
                    "message", request.getMessage()
            );

            // 2. Call Python FastAPI Server
            Map<String, Object> response = restTemplate.postForObject(pythonAIUrl, payload, Map.class);

            // 3. Extract the 'reply' field from Python's response
            if (response != null && response.containsKey("reply")) {
                return (String) response.get("reply");
            }
        } catch (Exception e) {
            return "Hệ thống đang bận một chút, bạn thử lại sau nhé!";
        }
        return "Xin lỗi, tôi không thể xử lý yêu cầu lúc này.";
    }

    public Map<String, Object> getChatHistory(UUID userId, int limit) {
        try {
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));

            String url = pythonAIUrl.replace("/chat", "/chat-history/") + userId + "?limit=" + limit;

            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Không thể lấy lịch sử chat: " + e.getMessage());
        }
    }
}