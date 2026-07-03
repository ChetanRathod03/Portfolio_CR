package com.chetanrathod.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recruiter_feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruiterFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recruiter_name", length = 120)
    private String name;

    @Column(name = "company_name", length = 160)
    private String company;

    @Column(name = "official_email", nullable = false, length = 160)
    private String email;

    @Column(length = 300)
    private String linkedin;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 30)
    private QuestionType questionType;

    @Column(nullable = false, length = 4000)
    private String message;

    @Column
    private Integer rating; // 1-5

    @Column(nullable = false)
    private boolean anonymous;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MessageStatus status;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = MessageStatus.UNREAD;
        }
        if (this.anonymous) {
            this.name = "Anonymous";
            this.company = null;
            this.linkedin = null;
        }
    }
}
