package com.chetanrathod.portfolio.controller;

import com.chetanrathod.portfolio.dto.ApiResponse;
import com.chetanrathod.portfolio.dto.ContactRequest;
import com.chetanrathod.portfolio.entity.Contact;
import com.chetanrathod.portfolio.service.ContactService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> submitContact(
            @Valid @RequestBody ContactRequest request,
            HttpServletRequest httpRequest) {

        Contact saved = contactService.submit(request, resolveIp(httpRequest));

        // A negative id means the honeypot caught a bot - respond success so the
        // bot doesn't learn its submission was rejected, but nothing was stored.
        String message = saved.getId() != null && saved.getId() > 0
                ? "Thanks for reaching out — I'll get back to you soon."
                : "Thanks for reaching out — I'll get back to you soon.";

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(message, null));
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
