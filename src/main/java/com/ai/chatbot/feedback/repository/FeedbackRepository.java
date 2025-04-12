package com.ai.chatbot.feedback.repository;

import com.ai.chatbot.feedback.model.Feedback;
import com.ai.chatbot.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    boolean existsByChatAndUser(com.ai.chatbot.chat.model.Chat chat, User user);

    Page<Feedback> findByUser(User user, Pageable pageable);

    Page<Feedback> findByUserAndPositive(User user, boolean positive, Pageable pageable);

    Page<Feedback> findAllByPositive(boolean positive, Pageable pageable);
}
