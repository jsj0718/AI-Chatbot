package com.ai.chatbot.user.service;

import com.ai.chatbot.auth.dto.UserSignupRequest;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import com.ai.chatbot.user.model.UserRole;
import com.ai.chatbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserSignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail()))
            throw new ServiceException(ServiceErrorCode.DUPLICATED_EMAIL);

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(UserRole.fromAdminFlag(request.isAdmin()))
                .createdAt(ZonedDateTime.now())
                .build();

        userRepository.save(user);
    }
}