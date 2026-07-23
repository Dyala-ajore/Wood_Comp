package com.woodcompany.mapper;

import com.woodcompany.dto.order.OrderResponse;
import com.woodcompany.entity.Order;
import com.woodcompany.entity.OrderItem;
import com.woodcompany.entity.OrderServiceItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderResponse.ItemView> items = order.getItems() == null
                ? Collections.emptyList()
                : order.getItems().stream().map(this::toItemView).toList();

        List<OrderResponse.ServiceItemView> serviceItems = order.getServiceItems() == null
                ? Collections.emptyList()
                : order.getServiceItems().stream().map(this::toServiceItemView).toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .status(order.getStatus().name())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .items(items)
                .serviceItems(serviceItems)
                .build();
    }

    private OrderResponse.ItemView toItemView(OrderItem item) {
        return OrderResponse.ItemView.builder()
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    private OrderResponse.ServiceItemView toServiceItemView(OrderServiceItem item) {
        return OrderResponse.ServiceItemView.builder()
                .serviceId(item.getService().getId())
                .serviceName(item.getService().getName())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
}