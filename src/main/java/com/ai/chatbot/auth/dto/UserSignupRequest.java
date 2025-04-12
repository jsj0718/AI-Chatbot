package com.ai.chatbot.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {
    private String email;
    private String password;
    private String name;
    private boolean admin;
}