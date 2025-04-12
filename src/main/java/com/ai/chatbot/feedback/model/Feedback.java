package com.ai.chatbot.feedback.model;

import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "feedbacks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"chat_id", "user_id"})
})
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Chat chat;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private boolean positive;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedbackStatus status;

    @Column(nullable = false)
    private ZonedDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }
}
