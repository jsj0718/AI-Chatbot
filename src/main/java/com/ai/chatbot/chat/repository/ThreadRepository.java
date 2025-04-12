package com.ai.chatbot.chat.repository;

import com.ai.chatbot.chat.model.Thread;
import com.ai.chatbot.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ThreadRepository extends JpaRepository<Thread, UUID> {

    // 해당 유저의 마지막 생성 스레드
    Optional<Thread> findFirstByUserOrderByCreatedAtDesc(User user);

    // 30분 이내 기존 스레드가 존재하면 재사용
    Optional<Thread> findFirstByUserAndCreatedAtAfterOrderByCreatedAtDesc(User user, ZonedDateTime after);

    Page<Thread> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    Page<Thread> findAllByOrderByCreatedAtDesc(Pageable pageable); // 관리자용
}
