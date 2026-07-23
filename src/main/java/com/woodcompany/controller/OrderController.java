package com.woodcompany.controller;

import com.woodcompany.dto.order.CreateOrderRequest;
import com.woodcompany.dto.order.OrderResponse;
import com.woodcompany.dto.order.UpdateOrderStatusRequest;
import com.woodcompany.entity.User;
import com.woodcompany.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // POST /api/orders  (أي مستخدم مسجّل دخول)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(currentUser, request));
    }

    // GET /api/orders  (Admin/Manager فقط - كل الطلبات)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // GET /api/orders/my  (الزبون - طلباته هو بس)
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(orderService.getMyOrders(currentUser.getId()));
    }

    // GET /api/orders/{id}  (محمي من IDOR)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        OrderResponse response = orderService.getOrderById(id);

        if (!isStaff(currentUser) && !response.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not allowed to view this order.");
        }

        return ResponseEntity.ok(response);
    }

    // PUT /api/orders/{id}/status  (Admin/Manager فقط)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request));
    }

    // DELETE /api/orders/{id}  (Admin فقط)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isStaff(User user) {
        return "admin".equalsIgnoreCase(user.getRole()) || "manager".equalsIgnoreCase(user.getRole());
    }
}