package com.woodcompany.controller;

import com.woodcompany.dto.product.CreateProductRequest;
import com.woodcompany.dto.product.ProductResponse;
import com.woodcompany.dto.product.UpdateProductRequest;
import com.woodcompany.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/products  (public - FR-... View Products)
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // GET /api/products/{id}  (public)
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // GET /api/products/search?name=...  (public - FR-05)
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String name) {
        return ResponseEntity.ok(productService.searchProducts(name));
    }

    // GET /api/products/service-eligible  (منتجات صالحة لخدمات القص/الكبس)
    @GetMapping("/service-eligible")
    public ResponseEntity<List<ProductResponse>> getServiceEligibleProducts() {
        return ResponseEntity.ok(productService.getServiceEligibleProducts());
    }

    // GET /api/products/low-stock?threshold=5  (Admin/Manager فقط - يخدم الـ Dashboard)
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts(
            @RequestParam(required = false) Integer threshold) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }

    // POST /api/products  (Admin/Manager فقط - إضافة منتج)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    // PUT /api/products/{id}  (Admin/Manager فقط - تعديل منتج)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // DELETE /api/products/{id}  (Admin فقط - حذف منتج، عملية حساسة)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}