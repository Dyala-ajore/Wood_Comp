package com.woodcompany.controller;

import com.woodcompany.dto.quote.CreateQuoteRequest;
import com.woodcompany.dto.quote.QuoteResponse;
import com.woodcompany.dto.quote.UpdateQuoteStatusRequest;
import com.woodcompany.entity.User;
import com.woodcompany.service.QuoteRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quote-requests")
@RequiredArgsConstructor
public class QuoteRequestController {

    private final QuoteRequestService quoteRequestService;

    // POST /api/quote-requests
    @PostMapping
    public ResponseEntity<QuoteResponse> createQuoteRequest(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateQuoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(quoteRequestService.createQuoteRequest(currentUser, request));
    }

    // GET /api/quote-requests
    @GetMapping
    public ResponseEntity<List<QuoteResponse>> getQuoteRequests(
            @AuthenticationPrincipal User currentUser) {
        List<QuoteResponse> result = isStaff(currentUser)
                ? quoteRequestService.getAllQuoteRequests()
                : quoteRequestService.getMyQuoteRequests(currentUser.getId());

        return ResponseEntity.ok(result);
    }

    // GET /api/quote-requests/{id}  (محمي من IDOR)
    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponse> getQuoteRequestById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        QuoteResponse response = quoteRequestService.getQuoteRequestById(id);

        if (!isStaff(currentUser) && !response.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to view this quote request.");
        }

        return ResponseEntity.ok(response);
    }

    // PUT /api/quote-requests/{id}  (Admin/Manager فقط)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<QuoteResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuoteStatusRequest request) {
        return ResponseEntity.ok(quoteRequestService.updateStatus(id, request));
    }

    // ---------- Helper ----------
    private boolean isStaff(User user) {
        return "admin".equalsIgnoreCase(user.getRole()) || "manager".equalsIgnoreCase(user.getRole());
    }
}