package com.woodcompany.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private List<ItemView> items;
    private List<ServiceItemView> serviceItems;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemView {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceItemView {
        private Long serviceId;
        private String serviceName;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}