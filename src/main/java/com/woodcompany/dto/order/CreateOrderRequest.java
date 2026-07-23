package com.woodcompany.dto.order;

import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    // اختياري - ممكن الزبون يطلب خدمات قص/كبس مع نفس الطلب
    @Valid
    private List<OrderServiceItemRequest> serviceItems;

}