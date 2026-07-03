package com.chetanrathod.portfolio.controller;

import com.chetanrathod.portfolio.dto.ApiResponse;
import com.chetanrathod.portfolio.dto.RecruiterFeedbackRequest;
import com.chetanrathod.portfolio.service.RecruiterFeedbackService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter-feedback")
@RequiredArgsConstructor
public class RecruiterFeedbackController {

    private final RecruiterFeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> submitFeedback(
            @Valid @RequestBody RecruiterFeedbackRequest request,
            HttpServletRequest httpRequest) {

        feedbackService.submit(request, resolveIp(httpRequest));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thanks — your message has been received.", null));
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
