package com.example.authclothingstore.controller;

import com.example.authclothingstore.DTO.StrategyDTO;
import com.example.authclothingstore.entity.Strategy;
import com.example.authclothingstore.entity.StrategyRepository;
import com.example.authclothingstore.entity.CompanyRepository;
import com.example.authclothingstore.entity.UserRepository;
import com.example.authclothingstore.http.CommunicationService;
import com.example.authclothingstore.services.ProductService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "strategy_controller")
@Slf4j
@RestController
@RequiredArgsConstructor
public class StrategyController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final StrategyRepository orderRepository;
    private final ProductService productService;
    private final String productServiceUrl = "http://localhost:8081/api";
    private final RestTemplate restTemplate;

    @Autowired
    private CommunicationService communicationService;

    // Добавляем MeterRegistry для регистрации метрик
    // MeterRegistry используется для создания и хранения метрик, таких как счётчики и таймеры.
    private final MeterRegistry meterRegistry;

    @Operation(summary = "Добавить стратегию")
    @PostMapping("/api/add/strategy")
    public ResponseEntity<Strategy> addOrder(@RequestBody StrategyDTO strategyDTO) {
        // Начинаем измерение времени выполнения запроса
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            Strategy strategy = productService.createOrder(strategyDTO);

            // Увеличиваем счётчик запросов, чтобы отслеживать количество вызовов метода `addOrder`
            meterRegistry.counter("order.add.count").increment();

            // Завершаем измерение времени и регистрируем его в метрике `order.add.time`
            sample.stop(meterRegistry.timer("order.add.time"));

            return ResponseEntity.ok(strategy);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null); // Возвращаем статус 400 при ошибке
        }
    }

    @Operation(summary = "Показать все стратегии")
    @GetMapping("/api/strats")
    public List<Strategy> getAllOrders() {
        // Увеличиваем счётчик вызовов метода `getAllOrders`
        meterRegistry.counter("orders.get.count").increment();
        return orderRepository.findAll();
    }

    @Operation(summary = "Показать стратегию компании по ID компании")
    @GetMapping("/api/orders/{id}")
    public ResponseEntity<Strategy> getOrderById(@PathVariable Integer id) {
        // Увеличиваем счётчик вызовов метода `getOrderById`
        meterRegistry.counter("orders.getById.count").increment();
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Показать стратегии пользователя")
    @GetMapping("/api/user/{userId}/orders")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Integer userId) {
        // Увеличиваем счётчик вызовов метода `getOrdersByUserId`
        meterRegistry.counter("orders.getByUserId.count").increment();
        List<Strategy> strategies = orderRepository.findAllByUserId(userId);
        if (strategies.isEmpty()) {
            return ResponseEntity.notFound().build();
        }


        // Собираем идентификаторы
        String orderIds = strategies.stream()
                .map(order -> String.valueOf(order.getId()))
                .collect(Collectors.joining(", "));

        Map<String, Object> response = new HashMap<>();
        response.put("orders", strategies);
        response.put("orderIds", orderIds);

        return ResponseEntity.ok("Найдены следующие ID заказов пользователя - " + orderIds);
    }

    @Operation(summary = "Удалить данные о стратегии по ID")
    @DeleteMapping("/api/strats/{stratId}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer orderId) {
        // Увеличиваем счётчик вызовов метода `deleteOrder`
        meterRegistry.counter("orders.delete.count").increment();
        if (!orderRepository.existsById(orderId)) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(orderId);
        return ResponseEntity.ok("Order - " + orderId + " deleted successfully");
    }



}