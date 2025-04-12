package com.ai.chatbot.auth.service;

import com.ai.chatbot.auth.dto.UserLoginRequest;
import com.ai.chatbot.auth.jwt.JwtProvider;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.LoginLog;
import com.ai.chatbot.user.model.User;
import com.ai.chatbot.user.repository.LoginLogRepository;
import com.ai.chatbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new ServiceException(ServiceErrorCode.INVALID_PASSWORD);

        loginLogRepository.save(LoginLog.builder()
                .user(user)
                .build());

        return jwtProvider.generateToken(user.getId().toString(), user.getRole());
    }
}
