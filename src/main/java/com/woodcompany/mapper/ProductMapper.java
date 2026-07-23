package com.woodcompany.mapper;

import com.woodcompany.dto.product.ProductResponse;
import com.woodcompany.dto.productimage.ProductImageResponse;
import com.woodcompany.entity.Product;
import com.woodcompany.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductMapper {

    public ProductResponse toResponse(Product product) {
        List<ProductImageResponse> images = product.getImages() == null
                ? Collections.emptyList()
                : product.getImages().stream()
                    .map(this::toImageResponse)
                    .toList();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .length(product.getLength())
                .width(product.getWidth())
                .thickness(product.getThickness())
                .productType(product.getProductType())
                .createdAt(product.getCreatedAt())
                .images(images)
                .build();

            }

    public ProductImageResponse toImageResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .productId(image.getProduct().getId())
                .imageUrl(image.getImageUrl())
                .build();
    }
}