
package com.ai.chatbot.chat.service;

import com.ai.chatbot.chat.model.Thread;
import com.ai.chatbot.chat.repository.ThreadRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ThreadServiceTest {

    private ThreadRepository threadRepository;
    private ThreadService threadService;

    private final User user = User.builder()
            .id(UUID.randomUUID())
            .name("Test User")
            .build();

    @BeforeEach
    void setUp() {
        threadRepository = mock(ThreadRepository.class);
        threadService = new ThreadService(threadRepository);
    }

    @Test
    @DisplayName("최근 30분 이내 스레드가 존재하면 재사용")
    void getOrCreateLatestThread_existing() {
        Thread existing = Thread.builder().user(user).build();
        when(threadRepository.findFirstByUserAndCreatedAtAfterOrderByCreatedAtDesc(any(), any()))
                .thenReturn(Optional.of(existing));

        Thread result = threadService.getOrCreateLatestThread(user);

        assertThat(result).isEqualTo(existing);
        verify(threadRepository, never()).save(any());
    }

    @Test
    @DisplayName("30분 이내 스레드가 없으면 새로 생성")
    void getOrCreateLatestThread_createNew() {
        when(threadRepository.findFirstByUserAndCreatedAtAfterOrderByCreatedAtDesc(any(), any()))
                .thenReturn(Optional.empty());

        Thread newThread = Thread.builder().user(user).build();
        when(threadRepository.save(any())).thenReturn(newThread);

        Thread result = threadService.getOrCreateLatestThread(user);

        assertThat(result).isEqualTo(newThread);
        verify(threadRepository).save(any());
    }

    @Test
    @DisplayName("자신이 생성한 스레드 삭제 성공")
    void deleteThread_success() {
        UUID threadId = UUID.randomUUID();
        Thread thread = Thread.builder().id(threadId).user(user).build();

        when(threadRepository.findById(threadId)).thenReturn(Optional.of(thread));

        threadService.deleteThread(threadId, user);

        verify(threadRepository).delete(thread);
    }

    @Test
    @DisplayName("스레드가 존재하지 않으면 예외 발생")
    void deleteThread_notFound() {
        UUID threadId = UUID.randomUUID();
        when(threadRepository.findById(threadId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> threadService.deleteThread(threadId, user))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.THREAD_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("타인의 스레드는 삭제 불가")
    void deleteThread_forbidden() {
        UUID threadId = UUID.randomUUID();
        User otherUser = User.builder().id(UUID.randomUUID()).build();
        Thread thread = Thread.builder().id(threadId).user(otherUser).build();

        when(threadRepository.findById(threadId)).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> threadService.deleteThread(threadId, user))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.THREAD_FORBIDDEN.getMessage());
    }
}
