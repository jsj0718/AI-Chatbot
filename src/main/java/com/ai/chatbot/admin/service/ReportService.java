package com.ai.chatbot.admin.service;

import com.ai.chatbot.admin.util.CsvReportWriter;
import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.user.model.User;
import com.ai.chatbot.user.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ChatRepository chatRepository;
    private final CsvReportWriter csvReportWriter;

    public ByteArrayInputStream generateDailyReport(UserRole role) {
        if (role != UserRole.ADMIN) throw new ServiceException(ServiceErrorCode.ACCESS_DENIED);

        ZonedDateTime since = ZonedDateTime.now().minusDays(1);
        List<Chat> chats = chatRepository.findAllByCreatedAtAfter(since);

        return csvReportWriter.write(chats);
    }
}
