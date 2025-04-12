package com.ai.chatbot.auth.service;

import com.ai.chatbot.auth.dto.UserLoginRequest;
import com.ai.chatbot.auth.jwt.JwtProvider;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.LoginLog;
import com.ai.chatbot.user.model.User;
import com.ai.chatbot.user.model.UserRole;
import com.ai.chatbot.user.repository.LoginLogRepository;
import com.ai.chatbot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private UserRepository userRepository;
    private LoginLogRepository loginLogRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        loginLogRepository = mock(LoginLogRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtProvider = mock(JwtProvider.class);

        authService = new AuthService(userRepository, loginLogRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void 로그인_성공() {
        // given
        UserLoginRequest request = new UserLoginRequest("test@example.com", "password");
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.MEMBER)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtProvider.generateToken(anyString(), eq(UserRole.MEMBER))).thenReturn("mockedToken");

        // when
        String token = authService.login(request);

        // then
        assertThat(token).isEqualTo("mockedToken");
        verify(loginLogRepository, times(1)).save(any(LoginLog.class));
    }

    @Test
    void 로그인_실패_이메일없음() {
        // given
        UserLoginRequest request = new UserLoginRequest("notfound@example.com", "password");
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 로그인_실패_비밀번호불일치() {
        // given
        UserLoginRequest request = new UserLoginRequest("test@example.com", "wrongpass");
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.MEMBER)
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encodedPassword")).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.INVALID_PASSWORD.getMessage());
    }
}
