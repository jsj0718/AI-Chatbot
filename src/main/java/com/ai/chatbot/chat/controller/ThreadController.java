package com.ai.chatbot.chat.controller;

import com.ai.chatbot.auth.jwt.JwtUserPrincipal;
import com.ai.chatbot.chat.service.ThreadService;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import com.ai.chatbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/thread")
@RequiredArgsConstructor
public class ThreadController {

    private final ThreadService threadService;
    private final UserRepository userRepository;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteThread(
            @PathVariable("id") UUID threadId,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        User user = userRepository.findById(principal.getUserId())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        // 관리자여도 삭제는 자기 스레드만 가능
        threadService.deleteThread(threadId, user);

        return ResponseEntity.noContent().build();
    }
}
