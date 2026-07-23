package com.woodcompany.service;

import com.woodcompany.dto.order.CreateOrderRequest;
import com.woodcompany.dto.order.OrderItemRequest;
import com.woodcompany.dto.order.OrderResponse;
import com.woodcompany.dto.order.OrderServiceItemRequest;
import com.woodcompany.dto.order.UpdateOrderStatusRequest;
import com.woodcompany.entity.Order;
import com.woodcompany.entity.OrderItem;
import com.woodcompany.entity.OrderServiceItem;
import com.woodcompany.entity.OrderStatus;
import com.woodcompany.entity.Product;
import com.woodcompany.entity.Service;
import com.woodcompany.entity.User;
import com.woodcompany.exception.ResourceNotFoundException;
import com.woodcompany.mapper.OrderMapper;
import com.woodcompany.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final ServiceService serviceService;
    private final OrderMapper orderMapper;

    // تسلسل الحالات المسموح به - كل حالة بترجع الحالات اللي مسموح تنتقل لها منها
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
            OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
            OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED),
            OrderStatus.DELIVERED, Set.of(),
            OrderStatus.CANCELLED, Set.of()
    );

    // ---------- CREATE ----------
    @Transactional
    public OrderResponse createOrder(User currentUser, CreateOrderRequest request) {
        Order order = Order.builder()
                .user(currentUser)
                .status(OrderStatus.PENDING)
                .totalPrice(BigDecimal.ZERO)
                .build();
        order = orderRepository.save(order);

        List<OrderItem> orderItems = createOrderItems(order, request.getItems());
        List<OrderServiceItem> orderServiceItems = createOrderServiceItems(order, request.getServiceItems());

        BigDecimal productsTotal = calculateProductsTotal(orderItems);
        BigDecimal servicesTotal = calculateServicesTotal(orderServiceItems);

        order.setItems(orderItems);
        order.setServiceItems(orderServiceItems);
        order.setTotalPrice(productsTotal.add(servicesTotal));

        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    // ---------- READ ----------
    public OrderResponse getOrderById(Long id) {
        return orderMapper.toResponse(findOrderOrThrow(id));
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(orderMapper::toResponse).toList();
    }

    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserId(userId).stream().map(orderMapper::toResponse).toList();
    }

    // ---------- UPDATE STATUS ----------
    @Transactional
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = findOrderOrThrow(id);

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        if (!ALLOWED_TRANSITIONS.getOrDefault(currentStatus, Set.of()).contains(newStatus)) {
            throw new IllegalArgumentException(
                    "Cannot change order status from " + currentStatus + " to " + newStatus + ".");
        }

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
        return orderMapper.toResponse(order);
    }

    // ---------- DELETE ----------
    @Transactional
    public void deleteOrder(Long id) {
        Order order = findOrderOrThrow(id);

        if (order.getStatus() != OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        orderRepository.delete(order);
    }

    // ---------- Helpers: بناء العناصر ----------
    private List<OrderItem> createOrderItems(Order order, List<OrderItemRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest req : requests) {
            Product product = productService.getProductEntityOrThrow(req.getProductId());
            productService.decreaseStock(product.getId(), req.getQuantity());

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(req.getQuantity())
                    .price(product.getPrice())
                    .build();

            items.add(item);
        }
        return items;
    }

    private List<OrderServiceItem> createOrderServiceItems(Order order, List<OrderServiceItemRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }

        List<OrderServiceItem> items = new ArrayList<>();
        for (OrderServiceItemRequest req : requests) {
            Product product = productService.getProductEntityOrThrow(req.getProductId());
            Service service = serviceService.getServiceEntityOrThrow(req.getServiceId());
            productService.decreaseStock(product.getId(), req.getQuantity());

            OrderServiceItem item = OrderServiceItem.builder()
                    .order(order)
                    .service(service)
                    .product(product)
                    .quantity(req.getQuantity())
                    .price(service.getPrice())
                    .build();

            items.add(item);
        }
        return items;
    }

    // ---------- Helpers: الحسابات ----------
    private BigDecimal calculateProductsTotal(List<OrderItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateServicesTotal(List<OrderServiceItem> items) {
        return items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ---------- Helper: إرجاع المخزون (عند الإلغاء أو الحذف) ----------
    private void restoreStock(Order order) {
        List<OrderItem> items = order.getItems() == null ? Collections.emptyList() : order.getItems();
        for (OrderItem item : items) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }

        List<OrderServiceItem> serviceItems = order.getServiceItems() == null
                ? Collections.emptyList() : order.getServiceItems();
        for (OrderServiceItem item : serviceItems) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
    }

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
}