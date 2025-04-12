package com.ai.chatbot.chat.controller;

import com.ai.chatbot.auth.jwt.JwtUserPrincipal;
import com.ai.chatbot.chat.dto.ChatHistoryResponse;
import com.ai.chatbot.chat.dto.ChatRequest;
import com.ai.chatbot.chat.dto.ChatResponse;
import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.service.ChatQueryService;
import com.ai.chatbot.chat.service.ChatService;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import com.ai.chatbot.user.model.UserRole;
import com.ai.chatbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ChatQueryService chatQueryService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody ChatRequest request
    ) {
        UUID userId = principal.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        Chat chat = chatService.createChat(user, request.getQuestion(), request.getModel());
        return ResponseEntity.ok(new ChatResponse(chat.getAnswer()));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<ChatHistoryResponse>> getHistory(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        boolean isAdmin = principal.getRole() == UserRole.ADMIN;

        Page<ChatHistoryResponse> result = chatQueryService.getChatHistory(user, isAdmin, pageable);
        return ResponseEntity.ok(result);
    }
}
