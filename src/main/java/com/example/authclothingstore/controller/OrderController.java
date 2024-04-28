package com.example.authclothingstore.controller;
import com.example.authclothingstore.DTO.OrderDTO;
import com.example.authclothingstore.entity.*;
import com.example.authclothingstore.kafka.CommunicationService;
import com.example.authclothingstore.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "order_controller")
@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final String productServiceUrl = "http://localhost:8081/api";
    private final RestTemplate restTemplate;
    @Autowired
    private CommunicationService communicationService;
    @Operation(
            summary = "Добавить заказ"
    )
    @PostMapping("/api/add/order")
    public ResponseEntity<Order> addOrder(@RequestBody OrderDTO orderDTO) {
        try {
            Order order = productService.createOrder(orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);  // Возвращаем статус 400 с пустым телом, если возникла ошибка
        }
    }

    @Operation(
            summary = "Показать все заказы"
    )
    @GetMapping("/api/orders")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    @Operation(
            summary = "Показать заказ по ID"
    )
    @GetMapping("/api/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(
            summary = "Показать все заказы конкретного пользователя - Пример: \"userId\": 4,\n" +
                    "  \"products\": [1, 2]"
    )
    @GetMapping("/api/user/{userId}/orders")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Integer userId) {
        List<Order> orders = orderRepository.findAllByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Получаем список идентификаторов заказов
        String orderIds = orders.stream()
                .map(order -> String.valueOf(order.getId()))
                .collect(Collectors.joining(", "));

        // Возвращаем и список заказов, и строку с идентификаторами
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orders);
        response.put("orderIds", orderIds);

        return ResponseEntity.ok("Найдены следующие ID заказов пользователя - " + orderIds);  // Возвращает список заказов и строку с идентификаторами
    }
    @Operation(
            summary = "Удалить заказ по ID"
    )
    @DeleteMapping("/api/orders/{orderId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            return ResponseEntity.notFound().build(); // Возвращает HTTP 404, если заказ не найден
        }
        orderRepository.deleteById(orderId); // Удаляет заказ из базы данных
        return ResponseEntity.ok("Order - " + orderId +  " deleted successfully");
    }
    @GetMapping("/getProductInfo/{userId}")
    public ResponseEntity<String> getUserInfo(@PathVariable Integer userId) {
        String url = productServiceUrl + "/find/user?id=" + userId;
        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }

}
