package com.example.authclothingstore.controller;

import com.example.authclothingstore.DTO.CompanyDTO;
import com.example.authclothingstore.entity.StrategyRepository;
import com.example.authclothingstore.entity.Company;
import com.example.authclothingstore.entity.CompanyRepository;
import com.example.authclothingstore.entity.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "company_controller")
@Slf4j
@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final StrategyRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "Добавить компанию"
    )
    @PostMapping("/api/add/product")
    public void addProduct(@RequestBody CompanyDTO companyDTO) {
        log.info("New product added - " + companyRepository.save(Company.builder()
                .name(companyDTO.getName())
                .description(companyDTO.getDescription())
                .price(companyDTO.getPrice())
                .quantity(companyDTO.getQuantity())
                .build()));
    }
    @Operation(
            summary = "Найти компанию по ID"
    )
    @GetMapping("/api/find/product")
    public ResponseEntity<Company> getProductById(@RequestParam int id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(company);
    }
    @Operation(
            summary = "Выводит все существующие компании"
    )
    @SneakyThrows
    @GetMapping("/api/all/product")
    public ResponseEntity<List<Company>> getAllProducts() {
        List<Company> companies = companyRepository.findAll();
        return ResponseEntity.ok(companies);
    }
    @Operation(
            summary = "Удаляет компанию по ID"
    )
    @DeleteMapping("/api/product")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        companyRepository.delete(company);
        return ResponseEntity.ok().build();
    }
    @Operation(
            summary = "Позволяет обновить компанию по ID"
    )
    @PutMapping("/api/update/product/{id}")
    public ResponseEntity<Company> updateProduct(@PathVariable int id, @RequestBody Company companyDetails) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (companyDetails.getName() != null) company.setName(companyDetails.getName());
        if (companyDetails.getDescription() != null) company.setDescription(companyDetails.getDescription());
        if (companyDetails.getPrice() != null) company.setPrice(companyDetails.getPrice());
        if (companyDetails.getQuantity() != null) company.setQuantity(companyDetails.getQuantity());

        final Company updatedCompany = companyRepository.save(company);
        return ResponseEntity.ok(updatedCompany);
    }
    public double calculateROI(double gainFromInvestment, double costOfInvestment) {
        if (costOfInvestment == 0) {
            throw new IllegalArgumentException("Cost of investment cannot be zero.");
        }
        return (gainFromInvestment - costOfInvestment) / costOfInvestment * 100;
    }
    public double calculateNPV(double[] cashFlows, double discountRate) {
        double npv = 0;
        for (int t = 0; t < cashFlows.length; t++) {
            npv += cashFlows[t] / Math.pow(1 + discountRate, t + 1);
        }
        return npv;
    }
    public String generateReport (double roi, double npv) {
        StringBuilder report = new StringBuilder();
        report.append("\nFinancial Metrics:\n");
        report.append("ROI: ").append(String.format("%.2f", roi)).append("%\n");
        report.append("NPV: ").append(String.format("%.2f", npv)).append("\n");
        return report.toString();

    }

}
