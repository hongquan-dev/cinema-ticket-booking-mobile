package com.example.cinema_booking_backend.controllers.user;

import com.example.cinema_booking_backend.dtos.user.ChatRequest;
import com.example.cinema_booking_backend.services.user.ChatBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    @Autowired
    private ChatBotService chatBotService;

    @PostMapping("/ask")
    public ResponseEntity<Map<String, Object>> askChatbot(@RequestBody ChatRequest request) {
        // This endpoint will be called by your Frontend or Python AI server
        String response = chatBotService.processChat(request);
        return ResponseEntity.ok(Map.of("reply", response));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<Map<String, Object>> getHistory(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "20") int limit) {

        Map<String, Object> history = chatBotService.getChatHistory(userId, limit);
        return ResponseEntity.ok(history);
    }
}