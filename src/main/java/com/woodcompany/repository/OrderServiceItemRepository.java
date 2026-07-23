package com.woodcompany.repository;

import com.woodcompany.entity.OrderServiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderServiceItemRepository extends JpaRepository<OrderServiceItem, Long> {
    List<OrderServiceItem> findByOrderId(Long orderId);
    List<OrderServiceItem> findByProductId(Long productId);
    List<OrderServiceItem> findByServiceId(Long serviceId);

    // يخدم Dashboard: total services requested
    /*long count();*/
}