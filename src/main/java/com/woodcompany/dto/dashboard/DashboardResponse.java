package com.woodcompany.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalProducts;
    private long totalUsers;
    private long totalOrders;
    private long totalQuoteRequests;
    private long pendingOrders;
    private long lowStockProductsCount;
    private BigDecimal totalSales;
}