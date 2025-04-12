package com.ai.chatbot.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminActivityResponse {
    private long signupCount;
    private long loginCount;
    private long chatCount;
}
