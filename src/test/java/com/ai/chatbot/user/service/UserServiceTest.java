package com.ai.chatbot.user.service;

import com.ai.chatbot.auth.dto.UserSignupRequest;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import com.ai.chatbot.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {
        // given
        UserSignupRequest request = new UserSignupRequest("test@example.com", "password", "홍길동", false);

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userService.signup(request);

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("중복 이메일로 인한 회원가입 실패")
    void signupFailByDuplicateEmail() {
        // given
        UserSignupRequest request = new UserSignupRequest("test@example.com", "password", "홍길동", false);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // expect
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.DUPLICATED_EMAIL.getMessage());
    }
}