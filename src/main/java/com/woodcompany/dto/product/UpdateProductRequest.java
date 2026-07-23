package com.woodcompany.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductRequest {

    private String name;
    private String description;

    @PositiveOrZero(message = "Price must be zero or positive")
    private BigDecimal price;

    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;

    @DecimalMin(value = "0.0", message = "Length must be positive")
    private BigDecimal length;

    @DecimalMin(value = "0.0", message = "Width must be positive")
    private BigDecimal width;

    @DecimalMin(value = "0.0", message = "Thickness must be positive")
    private BigDecimal thickness;

    private String productType;
}