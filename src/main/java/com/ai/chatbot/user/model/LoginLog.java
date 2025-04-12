package com.ai.chatbot.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "login_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginLog {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private ZonedDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = ZonedDateTime.now();
    }
}
