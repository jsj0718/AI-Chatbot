package com.ai.chatbot.user.repository;

import com.ai.chatbot.user.model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface LoginLogRepository extends JpaRepository<LoginLog, UUID> {
    long countByCreatedAtAfter(ZonedDateTime time);
}
