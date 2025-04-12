package com.ai.chatbot.chat.service;

import com.ai.chatbot.chat.client.GptClient;
import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.model.Thread;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ThreadService threadService;

    @Mock
    private GptClient gptClient;

    @InjectMocks
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 채팅_생성_성공() {
        // given
        User user = User.builder().build();
        Thread thread = Thread.builder().user(user).build();
        String question = "Hello, what is AI?";
        String model = "gpt-3.5";
        String answer = "AI stands for Artificial Intelligence.";

        when(threadService.getOrCreateLatestThread(user)).thenReturn(thread);
        when(chatRepository.findAllByThreadOrderByCreatedAtAsc(thread)).thenReturn(Collections.emptyList());
        when(gptClient.getAnswerFromGpt(anyList(), eq(model))).thenReturn(answer);
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Chat chat = chatService.createChat(user, question, model);

        // then
        assertThat(chat.getQuestion()).isEqualTo(question);
        assertThat(chat.getAnswer()).isEqualTo(answer);
        assertThat(chat.getThread()).isEqualTo(thread);
    }

    @Test
    void 채팅_생성_실패_모델_유효하지않음() {
        // given
        User user = User.builder().build();
        String question = "Explain quantum computing.";
        String invalidModel = "gpt-unknown";

        // when & then
        assertThatThrownBy(() -> chatService.createChat(user, question, invalidModel))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ServiceErrorCode.INVALID_MODEL.getMessage());
    }

    @Test
    void 채팅_생성_실패_GPT_호출_오류() {
        // given
        User user = User.builder().build();
        Thread thread = Thread.builder().user(user).build();
        String question = "Explain gravity.";
        String model = "gpt-3.5";

        when(threadService.getOrCreateLatestThread(user)).thenReturn(thread);
        when(chatRepository.findAllByThreadOrderByCreatedAtAsc(thread)).thenReturn(Collections.emptyList());
        when(gptClient.getAnswerFromGpt(anyList(), eq(model)))
                .thenThrow(new RuntimeException("GPT 서비스 오류"));

        // when & then
        assertThatThrownBy(() -> chatService.createChat(user, question, model))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining(ServiceErrorCode.CHAT_CREATION_FAILED.getMessage());
    }
}
