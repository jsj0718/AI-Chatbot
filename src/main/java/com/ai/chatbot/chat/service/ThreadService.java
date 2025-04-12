package com.ai.chatbot.chat.service;

import com.ai.chatbot.chat.model.Thread;
import com.ai.chatbot.chat.repository.ThreadRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ThreadService {

    private final ThreadRepository threadRepository;

    private static final long EXPIRATION_MINUTES = 30;

    /**
     * 마지막 질문이 30분 이내면 기존 스레드 재사용, 아니면 새로 생성
     */
    public Thread getOrCreateLatestThread(User user) {
        ZonedDateTime threshold = ZonedDateTime.now().minusMinutes(EXPIRATION_MINUTES);

        return threadRepository.findFirstByUserAndCreatedAtAfterOrderByCreatedAtDesc(user, threshold)
                .orElseGet(() -> threadRepository.save(
                        Thread.builder()
                                .user(user)
                                .build()
                ));
    }

    public void deleteThread(UUID threadId, User user) {
        Thread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.THREAD_NOT_FOUND));

        if (!thread.getUser().getId().equals(user.getId()))
            throw new ServiceException(ServiceErrorCode.THREAD_FORBIDDEN);

        threadRepository.delete(thread);
    }
}
