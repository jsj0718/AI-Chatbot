package com.ai.chatbot.user.repository;

import com.ai.chatbot.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByCreatedAtAfter(ZonedDateTime time);
}