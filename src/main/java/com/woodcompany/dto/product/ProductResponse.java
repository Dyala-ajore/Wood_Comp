package com.woodcompany.dto.product;

import com.woodcompany.dto.productimage.ProductImageResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal thickness;
    private String productType;
    private LocalDateTime createdAt;
    private List<ProductImageResponse> images;
}