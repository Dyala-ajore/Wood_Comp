package com.woodcompany.dto.service;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateServiceRequest {
    private String name;
    private String description;

    @PositiveOrZero(message = "Price must be zero or positive")
    private BigDecimal price;
}