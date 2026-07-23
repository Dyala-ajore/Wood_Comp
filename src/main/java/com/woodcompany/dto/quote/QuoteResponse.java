package com.woodcompany.dto.quote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private String message;
    private String status;
    private LocalDateTime createdAt;
}