package com.ai.chatbot.admin.service;

import com.ai.chatbot.admin.dto.AdminActivityResponse;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import com.ai.chatbot.user.model.UserRole;
import com.ai.chatbot.user.repository.LoginLogRepository;
import com.ai.chatbot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AdminActivityService {

    private final UserRepository userRepository;
    private final LoginLogRepository loginLogRepository;
    private final ChatRepository chatRepository;

    public AdminActivityResponse getActivity(UserRole role) {
        if (role != UserRole.ADMIN) throw new ServiceException(ServiceErrorCode.ACCESS_DENIED);

        ZonedDateTime since = ZonedDateTime.now().minusDays(1);

        long signupCount = userRepository.countByCreatedAtAfter(since);
        long loginCount = loginLogRepository.countByCreatedAtAfter(since);
        long chatCount = chatRepository.countByCreatedAtAfter(since);

        return new AdminActivityResponse(signupCount, loginCount, chatCount);
    }
}
