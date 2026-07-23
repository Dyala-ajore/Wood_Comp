package com.woodcompany.service;

import com.woodcompany.dto.productimage.CreateProductImageRequest;
import com.woodcompany.dto.productimage.ProductImageResponse;
import com.woodcompany.entity.Product;
import com.woodcompany.entity.ProductImage;
import com.woodcompany.exception.ResourceNotFoundException;
import com.woodcompany.mapper.ProductMapper;
import com.woodcompany.repository.ProductImageRepository;
import com.woodcompany.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    // ---------- ADD ----------
    @Transactional
    public ProductImageResponse addImage(Long productId, CreateProductImageRequest request) {
        Product product = findProductOrThrow(productId);

        ProductImage image = ProductImage.builder()
                .product(product)
                .imageUrl(request.getImageUrl())
                .build();

        ProductImage saved = productImageRepository.save(image);
        return productMapper.toImageResponse(saved);
    }

    // ---------- READ ----------
    public List<ProductImageResponse> getProductImages(Long productId) {
        findProductOrThrow(productId); // يتأكد من وجود المنتج قبل إرجاع صوره

        return productImageRepository.findByProductId(productId)
                .stream()
                .map(productMapper::toImageResponse)
                .toList();
    }

    // ---------- DELETE ----------
    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        productImageRepository.delete(image);
    }

    // ---------- Helper ----------
    private Product findProductOrThrow(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }
}