package com.example.strategyapp.controller;
import com.example.strategyapp.services.CompanyService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import com.example.strategyapp.DTO.*;
import com.example.strategyapp.entity.*;
import com.example.strategyapp.http.CommunicationService;
import com.example.strategyapp.services.AIService;
import com.example.strategyapp.services.ProductService;
import com.example.strategyapp.services.StrategyDeterminationService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final StrategyDeterminationService strategyDeterminationService;
    private final AIService aiService;
    private final CompanyService companyService;
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
    @Operation(summary = "Определить IT-стратегию на основе ID компании и вернуть только AI-вывод")
    @PostMapping("/api/strategy/calculate")
    public ResponseEntity<String> calculateStrategy(@RequestBody CompanyIdDTO companyIdDTO) {
        String aiOutput = strategyDeterminationService.getAIOutputForStrategy(companyIdDTO.getCompanyId());
        System.out.println("Вывод: " + aiOutput);
        return ResponseEntity.ok(aiOutput);
    }
    @Operation(summary = "Добавить компанию для текущего пользователя")
    @PostMapping("/api/add/company")
    public ResponseEntity<Company> addCompany(@RequestBody CompanyDTO companyDTO) {
        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));

        // Создаем компанию и связываем её с пользователем
        Company company = Company.builder()
                .name(companyDTO.getName())
                .description(companyDTO.getDescription())
                .price(companyDTO.getPrice())
                .quantity(companyDTO.getQuantity())
                .numberOfEmployees(companyDTO.getNumberOfEmployees())
                .initialInvestment(companyDTO.getInitialInvestment())
                .gainFromInvestment(companyDTO.getGainFromInvestment())
                .discountRate(companyDTO.getDiscountRate())
                .cashFlows(companyDTO.getCashFlows())
                .opex(companyDTO.getOpex())
                .capex(companyDTO.getCapex())
                .uptime(companyDTO.getUptime())
                .downtime(companyDTO.getDowntime())
                .user(user)
                .build();

        Company savedCompany = companyRepository.save(company);
        log.info("Компания добавлена для пользователя {}: {}", username, savedCompany);
        return ResponseEntity.ok(savedCompany);
    }
    @Operation(summary = "Удаляет компанию по ID")
    @DeleteMapping("/api/delete/company/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable Integer id) {
        return companyRepository.findById(id)
                .map(company -> {
                    companyRepository.delete(company);
                    return ResponseEntity.ok("Компания с ID " + id + " успешно удалена");
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Компания с ID " + id + " не найдена"));
    }
    @Operation(summary = "Задает вопрос с учетом данных компании, привязанной к текущему пользователю")
    @PostMapping("/api/ai/ask")
    public ResponseEntity<String> askQuestion(@RequestBody QuestionDTO questionDTO) {
        // Извлекаем имя текущего пользователя из SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // имя текущего пользователя

        // Получаем компанию, привязанную к данному пользователю
        Company company = companyService.getCompanyForUser(username);
        StringBuilder prompt = new StringBuilder();

        if (company != null) {
            prompt.append("Данные компании:\n");
            prompt.append("Название: ").append(company.getName()).append("\n");
            prompt.append("Описание: ").append(company.getDescription()).append("\n");
            prompt.append("Количество сотрудников: ").append(company.getNumberOfEmployees()).append("\n");
            if (company.getInitialInvestment() != null) {
                prompt.append("Начальные инвестиции: ").append(company.getInitialInvestment()).append("\n");
            }
            if (company.getGainFromInvestment() != null) {
                prompt.append("Выручка от инвестиций: ").append(company.getGainFromInvestment()).append("\n");
            }
            if (company.getDiscountRate() != null) {
                prompt.append("Ставка дисконтирования: ").append(company.getDiscountRate()).append("\n");
            }
            if (company.getCashFlows() != null) {
                prompt.append("Денежные потоки: ").append(company.getCashFlows()).append("\n");
            }
            // Можно добавить другие данные по необходимости
            prompt.append("\nВопрос: ").append(questionDTO.getQuestion());
        } else {
            // Если компания не найдена, формируем prompt только с вопросом
            prompt.append("Данные компании не найдены. ");
            prompt.append("Вопрос пользователя: ").append(questionDTO.getQuestion());
        }

        // Вызываем AI-сервис для генерации ответа
        String aiResponse = aiService.generateResponse(prompt.toString());
        return ResponseEntity.ok(aiResponse);
    }
    @Operation(summary = "Получить компании для заданного пользователя по username")
    @GetMapping("/api/companies/user/{username}")
    public ResponseEntity<List<CompanySummaryDTO>> getCompaniesForUser(@PathVariable String username) {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден: " + username));
        List<Company> companies = companyRepository.findByUser(user);
        List<CompanySummaryDTO> summaries = companies.stream()
                .map(company -> new CompanySummaryDTO(company.getId(), company.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(summaries);
    }


}
