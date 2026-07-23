package com.woodcompany.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be zero or positive")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @PositiveOrZero(message = "Stock must be zero or positive")
    private Integer stock;

    @DecimalMin(value = "0.0", message = "Length must be positive")
    private BigDecimal length;

    @DecimalMin(value = "0.0", message = "Width must be positive")
    private BigDecimal width;

    @DecimalMin(value = "0.0", message = "Thickness must be positive")
    private BigDecimal thickness;

    // نص حر - مثلاً "wood", "hinge", "glue"، حسب ما يكتبه الأدمن/المدير
    @NotBlank(message = "Product type is required")
    private String productType;
}