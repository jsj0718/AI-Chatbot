package com.ai.chatbot.feedback.service;

import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.model.Thread;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.feedback.dto.FeedbackResponse;
import com.ai.chatbot.feedback.model.Feedback;
import com.ai.chatbot.feedback.model.FeedbackStatus;
import com.ai.chatbot.feedback.repository.FeedbackRepository;
import com.ai.chatbot.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class FeedbackServiceTest {

    private FeedbackService feedbackService;
    private FeedbackRepository feedbackRepository;
    private ChatRepository chatRepository;

    private final User user = User.builder().id(UUID.randomUUID()).email("user@a.com").build();
    private final User otherUser = User.builder().id(UUID.randomUUID()).email("other@b.com").build();
    private final Chat chat = Chat.builder()
            .id(UUID.randomUUID())
            .question("Q")
            .answer("A")
            .createdAt(ZonedDateTime.now())
            .thread(Thread.builder().user(user).build())
            .build();

    @BeforeEach
    void setup() {
        feedbackRepository = mock(FeedbackRepository.class);
        chatRepository = mock(ChatRepository.class);
        feedbackService = new FeedbackService(feedbackRepository, chatRepository);
    }

    @Test
    void 피드백_생성_성공_일반유저() {
        when(chatRepository.findById(chat.getId())).thenReturn(Optional.of(chat));
        when(feedbackRepository.existsByChatAndUser(chat, user)).thenReturn(false);
        when(feedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Feedback result = feedbackService.createFeedback(user, chat.getId(), true, false);

        assertThat(result.getChat()).isEqualTo(chat);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.isPositive()).isTrue();
        assertThat(result.getStatus()).isEqualTo(FeedbackStatus.PENDING);
    }

    @Test
    void 피드백_생성_성공_어드민() {
        when(chatRepository.findById(chat.getId())).thenReturn(Optional.of(chat));
        when(feedbackRepository.existsByChatAndUser(chat, otherUser)).thenReturn(false);
        when(feedbackRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Feedback result = feedbackService.createFeedback(otherUser, chat.getId(), false, true);

        assertThat(result.isPositive()).isFalse();
    }

    @Test
    void 피드백_생성_실패_없는대화() {
        UUID chatId = UUID.randomUUID();
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feedbackService.createFeedback(user, chatId, true, false))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.CHAT_NOT_FOUND.getMessage());
    }

    @Test
    void 피드백_생성_실패_타인대화() {
        Chat othersChat = Chat.builder()
                .id(UUID.randomUUID())
                .thread(Thread.builder().user(otherUser).build())
                .build();

        when(chatRepository.findById(othersChat.getId())).thenReturn(Optional.of(othersChat));

        assertThatThrownBy(() -> feedbackService.createFeedback(user, othersChat.getId(), true, false))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.UNAUTHORIZED_FEEDBACK_ACCESS.getMessage());
    }

    @Test
    void 피드백_생성_실패_중복() {
        when(chatRepository.findById(chat.getId())).thenReturn(Optional.of(chat));
        when(feedbackRepository.existsByChatAndUser(chat, user)).thenReturn(true);

        assertThatThrownBy(() -> feedbackService.createFeedback(user, chat.getId(), true, false))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.FEEDBACK_ALREADY_EXISTS.getMessage());
    }

    @Test
    void 피드백_목록_조회_성공() {
        Feedback f = Feedback.builder().id(UUID.randomUUID()).chat(chat).user(user).positive(true).status(FeedbackStatus.PENDING).createdAt(ZonedDateTime.now()).build();

        when(feedbackRepository.findByUserAndPositive(user, true, PageRequest.of(0, 5, Sort.by("createdAt").descending())))
                .thenReturn(new PageImpl<>(List.of(f)));

        List<FeedbackResponse> result = feedbackService
                .getFeedbackList(user, false, true, PageRequest.of(0, 5, Sort.by("createdAt").descending()))
                .getContent();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).isPositive()).isTrue();
    }

    @Test
    void 피드백_상태_변경_성공() {
        Feedback f = Feedback.builder().id(UUID.randomUUID()).status(FeedbackStatus.PENDING).build();
        when(feedbackRepository.findById(f.getId())).thenReturn(Optional.of(f));

        feedbackService.changeStatus(f.getId(), FeedbackStatus.RESOLVED, true);

        assertThat(f.getStatus()).isEqualTo(FeedbackStatus.RESOLVED);
    }

    @Test
    void 피드백_상태_변경_실패_존재하지않음() {
        UUID id = UUID.randomUUID();
        when(feedbackRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feedbackService.changeStatus(id, FeedbackStatus.RESOLVED, true))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.FEEDBACK_NOT_FOUND.getMessage());
    }

    @Test
    void 피드백_상태_변경_실패_권한없음() {
        Feedback f = Feedback.builder().id(UUID.randomUUID()).status(FeedbackStatus.PENDING).build();
        when(feedbackRepository.findById(f.getId())).thenReturn(Optional.of(f));

        assertThatThrownBy(() -> feedbackService.changeStatus(f.getId(), FeedbackStatus.RESOLVED, false))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.FEEDBACK_PERMISSION_DENIED.getMessage());
    }
}
