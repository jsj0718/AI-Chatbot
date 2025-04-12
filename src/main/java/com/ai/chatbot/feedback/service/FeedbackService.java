package com.ai.chatbot.feedback.service;

import com.ai.chatbot.chat.model.Chat;
import com.ai.chatbot.chat.repository.ChatRepository;
import com.ai.chatbot.common.exception.ServiceErrorCode;
import com.ai.chatbot.common.exception.ServiceException;
import com.ai.chatbot.feedback.dto.FeedbackResponse;
import com.ai.chatbot.feedback.model.Feedback;
import com.ai.chatbot.feedback.model.FeedbackStatus;
import com.ai.chatbot.feedback.repository.FeedbackRepository;
import com.ai.chatbot.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ChatRepository chatRepository;

    /**
     * 피드백 생성
     * - 일반 유저는 자신의 대화에만 가능
     * - 관리자(admin)는 모든 대화에 피드백 작성 가능
     * - 유저 1명당 1 대화에 1 피드백만 허용
     */
    @Transactional
    public Feedback createFeedback(User user, UUID chatId, boolean isPositive, boolean isAdmin) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.CHAT_NOT_FOUND));

        if (!isAdmin && !chat.getThread().getUser().getId().equals(user.getId()))
            throw new ServiceException(ServiceErrorCode.UNAUTHORIZED_FEEDBACK_ACCESS);

        if (feedbackRepository.existsByChatAndUser(chat, user))
            throw new ServiceException(ServiceErrorCode.FEEDBACK_ALREADY_EXISTS);

        Feedback feedback = Feedback.builder()
                .id(UUID.randomUUID())
                .chat(chat)
                .user(user)
                .positive(isPositive)
                .status(FeedbackStatus.PENDING)
                .createdAt(ZonedDateTime.now())
                .build();

        return feedbackRepository.save(feedback);
    }

    /**
     * 피드백 목록 조회
     * - 일반 유저는 본인 피드백만
     * - 관리자는 전체 피드백 조회 가능
     * - positive 필터, 페이지네이션 적용
     */
    @Transactional(readOnly = true)
    public Page<FeedbackResponse> getFeedbackList(User user, boolean isAdmin, Boolean isPositive, Pageable pageable) {
        Page<Feedback> feedbacks;

        if (isAdmin) {
            feedbacks = (isPositive == null)
                    ? feedbackRepository.findAll(pageable)
                    : feedbackRepository.findAllByPositive(isPositive, pageable);
        } else {
            feedbacks = (isPositive == null)
                    ? feedbackRepository.findByUser(user, pageable)
                    : feedbackRepository.findByUserAndPositive(user, isPositive, pageable);
        }

        return feedbacks.map(f -> FeedbackResponse.builder()
                .id(f.getId())
                .chatId(f.getChat().getId())
                .positive(f.isPositive())
                .status(f.getStatus().name())
                .createdAt(f.getCreatedAt())
                .build());
    }

    /**
     * 피드백 상태 변경 (관리자만 가능)
     */
    @Transactional
    public void changeStatus(UUID feedbackId, FeedbackStatus status, boolean isAdmin) {
        if (!isAdmin) throw new ServiceException(ServiceErrorCode.FEEDBACK_PERMISSION_DENIED);

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.FEEDBACK_NOT_FOUND));

        feedback.setStatus(status);
    }
}
