package com.ai.chatbot.admin.service;

import com.ai.chatbot.admin.util.CsvReportWriter;
import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.model.Thread;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.user.model.User;
import com.ai.chatbot.user.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportServiceTest {
    private ReportService reportService;

    private ChatRepository chatRepository;
    private CsvReportWriter csvReportWriter;

    @BeforeEach
    void setUp() {
        chatRepository = mock(ChatRepository.class);
        csvReportWriter = mock(CsvReportWriter.class);

        reportService = new ReportService(chatRepository, csvReportWriter);
    }

    @Test
    void 보고서_생성_성공() {
        Chat chat = Chat.builder()
                .id(UUID.randomUUID())
                .question("질문")
                .answer("답변")
                .createdAt(ZonedDateTime.now())
                .thread(Thread.builder().user(User.builder().email("user@test.com").name("사용자").build()).build())
                .build();

        when(chatRepository.findAllByCreatedAtAfter(any())).thenReturn(List.of(chat));
        when(csvReportWriter.write(any())).thenReturn(new ByteArrayInputStream("csv content".getBytes()));

        ByteArrayInputStream result = reportService.generateDailyReport(UserRole.ADMIN);

        assertThat(result).isNotNull();
    }

    @Test
    void 보고서_생성_실패_csv예외발생시() {
        when(chatRepository.findAllByCreatedAtAfter(any())).thenReturn(List.of());
        when(csvReportWriter.write(any())).thenThrow(new RuntimeException("CSV 생성 실패"));

        assertThatThrownBy(() -> reportService.generateDailyReport(UserRole.ADMIN))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("CSV 생성 실패");
    }
}