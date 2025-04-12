package com.ai.chatbot.chat.service;

import com.ai.chatbot.chat.dto.ChatHistoryResponse;
import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.model.Thread;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.chat.repository.ThreadRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatQueryService {

    private final ThreadRepository threadRepository;
    private final ChatRepository chatRepository;

    public Page<ChatHistoryResponse> getChatHistory(User user, boolean isAdmin, Pageable pageable) {
        Page<Thread> threads = isAdmin
                ? threadRepository.findAllByOrderByCreatedAtDesc(pageable)
                : threadRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);

        if (threads.isEmpty()) throw new ServiceException(ServiceErrorCode.THREAD_NOT_FOUND);

        return threads.map(thread -> {
            List<Chat> chats = chatRepository.findAllByThreadOrderByCreatedAtAsc(thread);

            return ChatHistoryResponse.builder()
                    .userId(isAdmin ? thread.getUser().getId() : null)
                    .threadId(thread.getId())
                    .threadCreatedAt(thread.getCreatedAt())
                    .chats(
                            chats.stream()
                                    .map(chat -> ChatHistoryResponse.ChatItem.builder()
                                            .chatId(chat.getId())
                                            .question(chat.getQuestion())
                                            .answer(chat.getAnswer())
                                            .createdAt(chat.getCreatedAt())
                                            .build())
                                    .collect(Collectors.toList())
                    )
                    .build();
        });
    }
}
