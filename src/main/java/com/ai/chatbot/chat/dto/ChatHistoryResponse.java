package com.ai.chatbot.chat.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ChatHistoryResponse {
    private UUID userId;
    private UUID threadId;
    private ZonedDateTime threadCreatedAt;
    private List<ChatItem> chats;

    @Getter
    @Builder
    public static class ChatItem {
        private UUID chatId;
        private String question;
        private String answer;
        private ZonedDateTime createdAt;
    }
}
