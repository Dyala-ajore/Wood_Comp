package com.woodcompany.controller;

import com.woodcompany.dto.productimage.CreateProductImageRequest;
import com.woodcompany.dto.productimage.ProductImageResponse;
import com.woodcompany.service.ProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    // POST /api/products/{productId}/images  (Admin/Manager فقط)
    @PostMapping("/products/{productId}/images")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductImageResponse> addImage(
            @PathVariable Long productId,
            @Valid @RequestBody CreateProductImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productImageService.addImage(productId, request));
    }

    // GET /api/products/{productId}/images  (public)
    @GetMapping("/products/{productId}/images")
    public ResponseEntity<List<ProductImageResponse>> getProductImages(@PathVariable Long productId) {
        return ResponseEntity.ok(productImageService.getProductImages(productId));
    }

    // DELETE /api/images/{imageId}  (Admin/Manager فقط)
    @DeleteMapping("/images/{imageId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        productImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}