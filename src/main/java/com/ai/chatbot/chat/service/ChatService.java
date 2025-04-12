package com.ai.chatbot.chat.service;

import com.ai.chatbot.chat.client.GptClient;
import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.model.Thread;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final Set<String> SUPPORTED_MODELS = Set.of("gpt-3.5", "gpt-4");

    private final ChatRepository chatRepository;
    private final ThreadService threadService;
    private final GptClient gptClient;

    public Chat createChat(User user, String question, String model) {
        // 1. model 유효성 검사
        if (model == null || model.isBlank()) model = "gpt-3.5"; // fallback
        else if (!SUPPORTED_MODELS.contains(model)) throw new ServiceException(ServiceErrorCode.INVALID_MODEL);

        try {
            // 2. 스레드 조회
            Thread thread = threadService.getOrCreateLatestThread(user);

            // 3. 이전 대화 context 생성
            List<Chat> chatHistory = chatRepository.findAllByThreadOrderByCreatedAtAsc(thread);
            List<Map<String, String>> messages = new ArrayList<>();
            for (Chat chat : chatHistory) {
                messages.add(Map.of("role", "user", "content", chat.getQuestion()));
                messages.add(Map.of("role", "assistant", "content", chat.getAnswer()));
            }
            messages.add(Map.of("role", "user", "content", question));

            // 4. GPT 호출
            String answer = gptClient.getAnswerFromGpt(messages, model);

            // 5. 저장 및 반환
            Chat chat = Chat.builder()
                    .thread(thread)
                    .question(question)
                    .answer(answer)
                    .build();

            return chatRepository.save(chat);
        } catch (Exception e) {
            throw new ServiceException(ServiceErrorCode.CHAT_CREATION_FAILED);
        }
    }
}
