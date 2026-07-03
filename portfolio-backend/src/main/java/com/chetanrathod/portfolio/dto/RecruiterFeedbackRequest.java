package com.chetanrathod.portfolio.dto;

import com.chetanrathod.portfolio.entity.QuestionType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RecruiterFeedbackRequest {

    @Size(max = 120)
    private String name;

    @Size(max = 160)
    private String company;

    @NotBlank(message = "Official email is required")
    @Email(message = "Enter a valid email address")
    @Size(max = 160)
    private String email;

    @Size(max = 300)
    private String linkedin;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @NotBlank(message = "Message is required")
    @Size(max = 4000)
    private String message;

    @Min(1)
    @Max(5)
    private Integer rating;

    private boolean anonymous;

    private String website; // honeypot, see ContactRequest
}
