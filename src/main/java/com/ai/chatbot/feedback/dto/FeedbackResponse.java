package com.ai.chatbot.feedback.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
public class FeedbackResponse {
    private UUID id;
    private UUID chatId;
    private boolean positive;
    private String status;
    private ZonedDateTime createdAt;
}
