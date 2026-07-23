package com.woodcompany.repository;

import com.woodcompany.entity.Order;
import com.woodcompany.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

    // يخدم الـ Dashboard: عدد الطلبات حسب الحالة (مثلاً pending)
    long countByStatus(OrderStatus status);

    // يخدم الـ Dashboard: إجمالي المبيعات (بدون الطلبات الملغية)
    @Query("SELECT COALESCE(SUM(o.totalPrice), 0) FROM Order o WHERE o.status <> com.woodcompany.entity.OrderStatus.CANCELLED")
    BigDecimal calculateTotalSales();
}