package com.woodcompany.dto.productimage;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProductImageRequest {

    // productId عادة بيجي من الـ path variable (/products/{productId}/images)
    // مش من الـ body، بس منخليه هون لو حبيت تستخدمه بطريقة تانية
    private Long productId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;
}