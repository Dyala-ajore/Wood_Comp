package com.woodcompany.service;

import com.woodcompany.dto.quote.CreateQuoteRequest;
import com.woodcompany.dto.quote.QuoteResponse;
import com.woodcompany.dto.quote.UpdateQuoteStatusRequest;
import com.woodcompany.entity.Product;
import com.woodcompany.entity.QuoteRequest;
import com.woodcompany.entity.QuoteStatus;
import com.woodcompany.entity.User;
import com.woodcompany.exception.ResourceNotFoundException;
import com.woodcompany.mapper.QuoteRequestMapper;
import com.woodcompany.repository.QuoteRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuoteRequestService {

    private final QuoteRequestRepository quoteRequestRepository;
    private final ProductService productService;
    private final QuoteRequestMapper quoteRequestMapper;

    // ---------- CREATE ----------
    @Transactional
    public QuoteResponse createQuoteRequest(User currentUser, CreateQuoteRequest request) {
        Product product = productService.getProductEntityOrThrow(request.getProductId());

        QuoteRequest quoteRequest = QuoteRequest.builder()
                .user(currentUser)
                .product(product)
                .quantity(request.getQuantity())
                .message(request.getMessage())
                .status(QuoteStatus.PENDING)
                .build();

        QuoteRequest saved = quoteRequestRepository.save(quoteRequest);
        return quoteRequestMapper.toResponse(saved);
    }

    // ---------- READ ----------
    public QuoteResponse getQuoteRequestById(Long id) {
        return quoteRequestMapper.toResponse(findQuoteRequestOrThrow(id));
    }

    public List<QuoteResponse> getAllQuoteRequests() {
        return quoteRequestRepository.findAll()
                .stream()
                .map(quoteRequestMapper::toResponse)
                .toList();
    }

    public List<QuoteResponse> getMyQuoteRequests(Long userId) {
        return quoteRequestRepository.findByUserId(userId)
                .stream()
                .map(quoteRequestMapper::toResponse)
                .toList();
    }

    public List<QuoteResponse> getQuoteRequestsByStatus(String status) {
        return quoteRequestRepository.findByStatus(parseStatus(status))
                .stream()
                .map(quoteRequestMapper::toResponse)
                .toList();
    }

    // ---------- UPDATE STATUS ----------
    @Transactional
    public QuoteResponse updateStatus(Long id, UpdateQuoteStatusRequest request) {
        QuoteRequest quoteRequest = findQuoteRequestOrThrow(id);
        quoteRequest.setStatus(parseStatus(request.getStatus()));
        return quoteRequestMapper.toResponse(quoteRequest);
    }

    // ---------- Helpers ----------
    private QuoteStatus parseStatus(String status) {
        try {
            return QuoteStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    private QuoteRequest findQuoteRequestOrThrow(Long id) {
        return quoteRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote request not found with id: " + id));
    }
}