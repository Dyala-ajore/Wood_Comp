package com.woodcompany.service;

import com.woodcompany.dto.product.CreateProductRequest;
import com.woodcompany.dto.product.ProductResponse;
import com.woodcompany.dto.product.UpdateProductRequest;
import com.woodcompany.entity.Product;
import com.woodcompany.exception.InsufficientStockException;
import com.woodcompany.exception.ResourceNotFoundException;
import com.woodcompany.mapper.ProductMapper;
import com.woodcompany.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    // ---------- CREATE ----------
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .length(request.getLength())
                .width(request.getWidth())
                .thickness(request.getThickness())
                .productType(request.getProductType())
                .build();

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    // ---------- READ ----------
    public ProductResponse getProductById(Long id) {
        return productMapper.toResponse(findProductOrThrow(id));
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public List<ProductResponse> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public List<ProductResponse> getServiceEligibleProducts() {
        return productRepository.findByLengthIsNotNullAndWidthIsNotNullAndThicknessIsNotNull()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public List<ProductResponse> getLowStockProducts(Integer threshold) {
        int actualThreshold = threshold != null ? threshold : DEFAULT_LOW_STOCK_THRESHOLD;
        return productRepository.findLowStockProducts(actualThreshold)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public boolean exists(Long productId) {
        return productRepository.existsById(productId);
    }

    // ---------- UPDATE (partial, managed entity - no explicit save needed) ----------
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = findProductOrThrow(id);

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getLength() != null) {
            product.setLength(request.getLength());
        }
        if (request.getWidth() != null) {
            product.setWidth(request.getWidth());
        }
        if (request.getThickness() != null) {
            product.setThickness(request.getThickness());
        }
        if (request.getProductType() != null) {
            product.setProductType(request.getProductType());
        }
        // لا حاجة لـ productRepository.save(product) هنا:
        // الـ entity مُدار (managed) داخل الـ Transaction، فـ JPA
        // بيحفظ التغييرات تلقائيًا (dirty checking) عند انتهاء المعاملة.
        return productMapper.toResponse(product);
    }

    // ---------- DELETE ----------
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.delete(findProductOrThrow(id));
    }

    // ---------- INVENTORY (تُستخدم من OrderService لاحقًا - FR-04) ----------
    @Transactional
    public void decreaseStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Product product = findProductOrThrow(productId);

        if (product.getStock() < quantity) {
            throw new InsufficientStockException(
                    "Insufficient stock for product: " + product.getName());
        }

        product.setStock(product.getStock() - quantity);
        // managed entity - dirty checking بيتكفل بالحفظ
    }

    // تُستخدم عند إلغاء/حذف/تعديل Order لإرجاع الكمية للمخزون
    @Transactional
    public void increaseStock(Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        Product product = findProductOrThrow(productId);
        product.setStock(product.getStock() + quantity);
    }

    // ---------- Helpers ----------
    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
    // تُستخدم من Services تانية (QuoteRequestService, OrderService لاحقًا)
// لما نحتاج الـ Entity نفسه (Product) مش الـ DTO
    public Product getProductEntityOrThrow(Long id) {
        return findProductOrThrow(id);
    }
}