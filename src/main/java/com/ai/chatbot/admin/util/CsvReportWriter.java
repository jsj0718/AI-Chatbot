package com.ai.chatbot.admin.util;

import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Component
public class CsvReportWriter {

    public ByteArrayInputStream write(List<Chat> chats) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out),
                     CSVFormat.DEFAULT.withHeader("질문", "답변", "생성일시", "이메일", "이름"))) {

            for (Chat chat : chats) {
                csvPrinter.printRecord(
                        chat.getQuestion(),
                        chat.getAnswer(),
                        chat.getCreatedAt(),
                        chat.getThread().getUser().getEmail(),
                        chat.getThread().getUser().getName()
                );
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new ServiceException(ServiceErrorCode.CSV_GENERATION_FAILED);
        }
    }
}
