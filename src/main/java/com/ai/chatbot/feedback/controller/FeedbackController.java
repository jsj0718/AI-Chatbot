package com.ai.chatbot.feedback.controller;

import com.ai.chatbot.auth.jwt.JwtUserPrincipal;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.feedback.dto.FeedbackRequest;
import com.ai.chatbot.feedback.dto.FeedbackResponse;
import com.ai.chatbot.feedback.model.Feedback;
import com.ai.chatbot.feedback.model.FeedbackStatus;
import com.ai.chatbot.feedback.service.FeedbackService;
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
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<FeedbackResponse> create(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestBody FeedbackRequest request
    ) {
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        boolean isAdmin = principal.getRole() == UserRole.ADMIN;

        Feedback feedback = feedbackService.createFeedback(user, request.getChatId(), request.isPositive(), isAdmin);

        return ResponseEntity.ok(
                FeedbackResponse.builder()
                        .id(feedback.getId())
                        .chatId(feedback.getChat().getId())
                        .positive(feedback.isPositive())
                        .status(feedback.getStatus().name())
                        .createdAt(feedback.getCreatedAt())
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<Page<FeedbackResponse>> list(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam(value = "positive", required = false) Boolean isPositive,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        boolean isAdmin = principal.getRole() == UserRole.ADMIN;

        Page<FeedbackResponse> result = feedbackService.getFeedbackList(user, isAdmin, isPositive, pageable);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> changeStatus(
            @PathVariable("id") UUID feedbackId,
            @RequestParam("status") FeedbackStatus status,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        boolean isAdmin = principal.getRole() == UserRole.ADMIN;

        feedbackService.changeStatus(feedbackId, status, isAdmin);

        return ResponseEntity.noContent().build();
    }
}
