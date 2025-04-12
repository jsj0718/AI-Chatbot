package com.ai.chatbot.feedback.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@ToString
@Getter
@NoArgsConstructor
public class FeedbackRequest {
    private UUID chatId;
    private boolean positive;
}
