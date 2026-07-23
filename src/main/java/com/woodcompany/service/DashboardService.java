package com.woodcompany.service;

import com.woodcompany.dto.dashboard.DashboardResponse;
import com.woodcompany.entity.OrderStatus;
import com.woodcompany.repository.OrderRepository;
import com.woodcompany.repository.ProductRepository;
import com.woodcompany.repository.QuoteRequestRepository;
import com.woodcompany.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final QuoteRequestRepository quoteRequestRepository;

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    public DashboardResponse getDashboard() {
        long totalProducts = productRepository.count();
        long totalUsers = userRepository.count();
        long totalOrders = orderRepository.count();
        long totalQuoteRequests = quoteRequestRepository.count();
        long pendingOrders = orderRepository.countByStatus(OrderStatus.PENDING);
        long lowStockProducts = productRepository.countLowStockProducts(DEFAULT_LOW_STOCK_THRESHOLD);
        BigDecimal totalSales = orderRepository.calculateTotalSales();

        return DashboardResponse.builder()
                .totalProducts(totalProducts)
                .totalUsers(totalUsers)
                .totalOrders(totalOrders)
                .totalQuoteRequests(totalQuoteRequests)
                .pendingOrders(pendingOrders)
                .lowStockProductsCount(lowStockProducts)
                .totalSales(totalSales)
                .build();
    }
}