package com.chetanrathod.portfolio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ContactRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 120)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 160)
    private String email;

    @Size(max = 160)
    private String company;

    @Size(max = 120)
    private String designation;

    @Pattern(regexp = "^$|^[+0-9 ()-]{6,20}$", message = "Enter a valid phone number")
    private String phone;

    @Size(max = 200)
    private String subject;

    @NotBlank(message = "Message is required")
    @Size(max = 4000)
    private String message;

    private boolean resumeRequested;

    private boolean scheduleInterviewRequested;

    @Pattern(regexp = "NORMAL|HIGH|URGENT", message = "Priority must be NORMAL, HIGH or URGENT")
    private String priority = "NORMAL";

    // Honeypot field: real users never fill this in. Bots that auto-fill every
    // input on a page will, so a non-blank value here is a strong spam signal.
    private String website;
}
