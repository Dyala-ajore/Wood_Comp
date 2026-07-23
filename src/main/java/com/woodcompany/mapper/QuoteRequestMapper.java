package com.woodcompany.mapper;

import com.woodcompany.dto.quote.QuoteResponse;
import com.woodcompany.entity.QuoteRequest;
import org.springframework.stereotype.Component;

@Component
public class QuoteRequestMapper {

    public QuoteResponse toResponse(QuoteRequest quoteRequest) {
        return QuoteResponse.builder()
                .id(quoteRequest.getId())
                .userId(quoteRequest.getUser().getId())
                .productId(quoteRequest.getProduct().getId())
                .productName(quoteRequest.getProduct().getName())
                .quantity(quoteRequest.getQuantity())
                .message(quoteRequest.getMessage())
                .status(quoteRequest.getStatus().name())
                .createdAt(quoteRequest.getCreatedAt())
                .build();
    }
}