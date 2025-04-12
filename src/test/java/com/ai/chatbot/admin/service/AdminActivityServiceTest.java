package com.ai.chatbot.admin.service;

import com.ai.chatbot.admin.dto.AdminActivityResponse;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.user.model.UserRole;
import com.ai.chatbot.user.repository.LoginLogRepository;
import com.ai.chatbot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminActivityServiceTest {
    private AdminActivityService adminActivityService;
    private UserRepository userRepository;
    private LoginLogRepository loginLogRepository;
    private ChatRepository chatRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        loginLogRepository = mock(LoginLogRepository.class);
        chatRepository = mock(ChatRepository.class);

        adminActivityService = new AdminActivityService(userRepository, loginLogRepository, chatRepository);
    }

    @Test
    void 관리자_활동_조회_성공() {
        when(userRepository.countByCreatedAtAfter(any())).thenReturn(5L);
        when(loginLogRepository.countByCreatedAtAfter(any())).thenReturn(10L);
        when(chatRepository.countByCreatedAtAfter(any())).thenReturn(20L);

        AdminActivityResponse response = adminActivityService.getActivity(UserRole.ADMIN);

        assertThat(response.getSignupCount()).isEqualTo(5L);
        assertThat(response.getLoginCount()).isEqualTo(10L);
        assertThat(response.getChatCount()).isEqualTo(20L);
    }
}