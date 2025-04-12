package com.ai.chatbot.chat.repository;

import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.model.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    List<Chat> findAllByThreadOrderByCreatedAtAsc(Thread thread);

    List<Chat> findAllByCreatedAtAfter(ZonedDateTime time);

    long countByCreatedAtAfter(ZonedDateTime time);
}
