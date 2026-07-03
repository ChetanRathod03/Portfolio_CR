package com.chetanrathod.portfolio.service;

import com.chetanrathod.portfolio.dto.ContactRequest;
import com.chetanrathod.portfolio.entity.Contact;
import com.chetanrathod.portfolio.entity.MessageStatus;
import com.chetanrathod.portfolio.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final EmailService emailService;

    public Contact submit(ContactRequest req, String ipAddress) {
        // Honeypot: if the hidden 'website' field was filled in, silently treat it
        // as spam - accept the request but don't persist or email anything.
        if (req.getWebsite() != null && !req.getWebsite().isBlank()) {
            return Contact.builder().id(-1L).build();
        }

        Contact contact = Contact.builder()
                .name(req.getName().trim())
                .email(req.getEmail().trim())
                .company(trimOrNull(req.getCompany()))
                .designation(trimOrNull(req.getDesignation()))
                .phone(trimOrNull(req.getPhone()))
                .subject(trimOrNull(req.getSubject()))
                .message(req.getMessage().trim())
                .resumeRequested(req.isResumeRequested())
                .scheduleInterviewRequested(req.isScheduleInterviewRequested())
                .priority(req.getPriority() == null ? "NORMAL" : req.getPriority())
                .status(MessageStatus.UNREAD)
                .ipAddress(ipAddress)
                .build();

        Contact saved = contactRepository.save(contact);
        emailService.sendContactNotification(saved);
        return saved;
    }

    private String trimOrNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
