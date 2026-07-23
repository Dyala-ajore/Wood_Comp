package com.woodcompany.repository;

import com.woodcompany.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE p.stock <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") int threshold);

    // يخدم الـ Dashboard: عدّ فقط بدون تحميل الكائنات الكاملة (أخف وأسرع)
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock <= :threshold")
    long countLowStockProducts(@Param("threshold") int threshold);

    List<Product> findByLengthIsNotNullAndWidthIsNotNullAndThicknessIsNotNull();

    List<Product> findByPriceBetween(BigDecimal min, BigDecimal max);

    List<Product> findByStockGreaterThan(int stock);
}