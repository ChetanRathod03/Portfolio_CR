package com.chetanrathod.portfolio.service;

import com.chetanrathod.portfolio.dto.RecruiterFeedbackRequest;
import com.chetanrathod.portfolio.entity.MessageStatus;
import com.chetanrathod.portfolio.entity.RecruiterFeedback;
import com.chetanrathod.portfolio.repository.RecruiterFeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruiterFeedbackService {

    private final RecruiterFeedbackRepository feedbackRepository;
    private final EmailService emailService;

    public RecruiterFeedback submit(RecruiterFeedbackRequest req, String ipAddress) {
        if (req.getWebsite() != null && !req.getWebsite().isBlank()) {
            return RecruiterFeedback.builder().id(-1L).build();
        }

        RecruiterFeedback feedback = RecruiterFeedback.builder()
                .name(trimOrNull(req.getName()))
                .company(trimOrNull(req.getCompany()))
                .email(req.getEmail().trim())
                .linkedin(trimOrNull(req.getLinkedin()))
                .questionType(req.getQuestionType())
                .message(req.getMessage().trim())
                .rating(req.getRating())
                .anonymous(req.isAnonymous())
                .status(MessageStatus.UNREAD)
                .ipAddress(ipAddress)
                .build();

        RecruiterFeedback saved = feedbackRepository.save(feedback);
        emailService.sendRecruiterFeedbackNotification(saved);
        return saved;
    }

    private String trimOrNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
