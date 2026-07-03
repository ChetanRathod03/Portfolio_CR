package com.chetanrathod.portfolio.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contacts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(length = 160)
    private String company;

    @Column(length = 120)
    private String designation;

    @Column(length = 30)
    private String phone;

    @Column(length = 200)
    private String subject;

    @Column(nullable = false, length = 4000)
    private String message;

    @Column(name = "resume_requested")
    private boolean resumeRequested;

    @Column(name = "schedule_interview_requested")
    private boolean scheduleInterviewRequested;

    @Column(length = 20)
    private String priority; // NORMAL, HIGH, URGENT

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
        if (this.priority == null) {
            this.priority = "NORMAL";
        }
    }
}
