package com.ai.chatbot.auth.jwt;

import com.ai.chatbot.user.model.UserRole;
import lombok.Getter;

import java.util.UUID;

@Getter
public class JwtUserPrincipal {
    private final UUID userId;
    private final UserRole role;

    public JwtUserPrincipal(UUID userId, UserRole role) {
        this.userId = userId;
        this.role = role;
    }
}
