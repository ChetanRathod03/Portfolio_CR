package com.chetanrathod.portfolio.service;

import com.chetanrathod.portfolio.entity.Contact;
import com.chetanrathod.portfolio.entity.RecruiterFeedback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * Fire-and-forget email notifications. Runs @Async so a slow/misconfigured
 * SMTP server never delays or breaks the API response to the caller - the
 * contact/feedback record is already saved before this is invoked.
 *
 * Requires MAIL_USERNAME / MAIL_PASSWORD env vars to be set (see .env.example).
 * For Gmail, MAIL_PASSWORD must be a 16-character App Password, not your
 * normal account password (Google blocks plain-password SMTP login).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.notify-email}")
    private String notifyEmail;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    @Async
    public void sendContactNotification(Contact contact) {
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("MAIL_USERNAME not configured - skipping contact notification email for id={}", contact.getId());
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(notifyEmail);
            msg.setSubject("New Contact Form Submission: " + safe(contact.getSubject(), "(no subject)"));
            msg.setText(String.format("""
                    New message from your portfolio contact form.

                    Name: %s
                    Email: %s
                    Company: %s
                    Designation: %s
                    Phone: %s
                    Priority: %s
                    Resume requested: %s
                    Schedule interview requested: %s
                    Date: %s

                    Message:
                    %s
                    """,
                    contact.getName(), contact.getEmail(), safe(contact.getCompany(), "-"),
                    safe(contact.getDesignation(), "-"), safe(contact.getPhone(), "-"),
                    contact.getPriority(), contact.isResumeRequested(), contact.isScheduleInterviewRequested(),
                    contact.getCreatedAt().format(FMT), contact.getMessage()));
            mailSender.send(msg);

            sendAutoReply(contact.getEmail(), contact.getName());
        } catch (Exception ex) {
            log.error("Failed to send contact notification email for id={}: {}", contact.getId(), ex.getMessage());
        }
    }

    @Async
    public void sendRecruiterFeedbackNotification(RecruiterFeedback feedback) {
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("MAIL_USERNAME not configured - skipping recruiter feedback email for id={}", feedback.getId());
            return;
        }
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromAddress);
            msg.setTo(notifyEmail);
            msg.setSubject("New Recruiter Inquiry: " + feedback.getQuestionType());
            msg.setText(String.format("""
                    New recruiter inquiry from your portfolio.

                    Recruiter Name: %s
                    Company: %s
                    Email: %s
                    LinkedIn: %s
                    Question Type: %s
                    Rating: %s
                    Anonymous: %s
                    Date: %s

                    Message:
                    %s
                    """,
                    feedback.getName(), safe(feedback.getCompany(), "-"), feedback.getEmail(),
                    safe(feedback.getLinkedin(), "-"), feedback.getQuestionType(),
                    feedback.getRating() != null ? feedback.getRating() + "/5" : "-",
                    feedback.isAnonymous(), feedback.getCreatedAt().format(FMT), feedback.getMessage()));
            mailSender.send(msg);

            if (!feedback.isAnonymous()) {
                sendAutoReply(feedback.getEmail(), feedback.getName());
            }
        } catch (Exception ex) {
            log.error("Failed to send recruiter feedback email for id={}: {}", feedback.getId(), ex.getMessage());
        }
    }

    private void sendAutoReply(String toEmail, String toName) {
        try {
            SimpleMailMessage reply = new SimpleMailMessage();
            reply.setFrom(fromAddress);
            reply.setTo(toEmail);
            reply.setSubject("Thank you for reaching out");
            reply.setText(String.format("""
                    Hello%s,

                    Thank you for contacting me.

                    I appreciate your interest and will respond as soon as possible.

                    Regards,
                    Chetan Rathod
                    """, toName != null && !toName.isBlank() ? " " + toName : ""));
            mailSender.send(reply);
        } catch (Exception ex) {
            log.error("Failed to send auto-reply to {}: {}", toEmail, ex.getMessage());
        }
    }

    private String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
