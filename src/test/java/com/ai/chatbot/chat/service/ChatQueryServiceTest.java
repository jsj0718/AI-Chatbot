package com.ai.chatbot.chat.service;

import com.ai.chatbot.chat.dto.ChatHistoryResponse;
import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.model.Thread;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.chat.repository.ThreadRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ChatQueryServiceTest {

    @Mock
    private ThreadRepository threadRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatQueryService chatQueryService;

    private User user;
    private Thread thread;
    private Chat chat;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder()
                .id(UUID.randomUUID())
                .name("test-user")
                .build();
        thread = Thread.builder()
                .id(UUID.randomUUID())
                .user(user)
                .createdAt(ZonedDateTime.now())
                .build();
        chat = Chat.builder()
                .id(UUID.randomUUID())
                .question("Hello")
                .answer("Hi")
                .createdAt(ZonedDateTime.now())
                .thread(thread)
                .build();
        pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    }

    @Test
    @DisplayName("[Admin] 히스토리 전체 조회 시 userId 포함")
    void testGetChatHistory_Admin() {
        Page<Thread> threadPage = new PageImpl<>(List.of(thread), pageable, 1);
        when(threadRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(threadPage);
        when(chatRepository.findAllByThreadOrderByCreatedAtAsc(thread)).thenReturn(List.of(chat));

        Page<ChatHistoryResponse> result = chatQueryService.getChatHistory(user, true, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isEqualTo(user.getId());
        verify(threadRepository).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    @DisplayName("[User] 본인 히스토리 조회 시 userId는 null")
    void testGetChatHistory_User() {
        Page<Thread> threadPage = new PageImpl<>(List.of(thread), pageable, 1);
        when(threadRepository.findAllByUserOrderByCreatedAtDesc(user, pageable)).thenReturn(threadPage);
        when(chatRepository.findAllByThreadOrderByCreatedAtAsc(thread)).thenReturn(List.of(chat));

        Page<ChatHistoryResponse> result = chatQueryService.getChatHistory(user, false, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getUserId()).isNull();
        verify(threadRepository).findAllByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Test
    @DisplayName("[실패] 조회할 스레드 없음")
    void testGetChatHistory_Empty() {
        Page<Thread> emptyPage = new PageImpl<>(Collections.emptyList());
        when(threadRepository.findAllByUserOrderByCreatedAtDesc(user, pageable)).thenReturn(emptyPage);

        assertThatThrownBy(() -> chatQueryService.getChatHistory(user, false, pageable))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.THREAD_NOT_FOUND.getMessage());
    }
}
