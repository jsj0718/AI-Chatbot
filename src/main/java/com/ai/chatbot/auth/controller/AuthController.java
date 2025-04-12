package com.ai.chatbot.auth.controller;

import com.ai.chatbot.auth.dto.UserSignupRequest;
import com.ai.chatbot.auth.dto.UserLoginRequest;
import com.ai.chatbot.auth.service.AuthService;
import com.ai.chatbot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * 회원가입 API
     * POST /api/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody UserSignupRequest request) {
        userService.signup(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 로그인 API
     * POST /api/login
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(token);
    }
}
