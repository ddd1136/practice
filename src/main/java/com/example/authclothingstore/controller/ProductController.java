package com.example.authclothingstore.controller;

import com.example.authclothingstore.DTO.ProductDTO;
import com.example.authclothingstore.entity.OrderRepository;
import com.example.authclothingstore.entity.Product;
import com.example.authclothingstore.entity.ProductRepository;
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

@Tag(name = "product_controller")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "Добавить продукт"
    )
    @PostMapping("/api/add/product")
    public void addProduct(@RequestBody ProductDTO productDTO) {
        log.info("New product added - " + productRepository.save(Product.builder()
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .quantity(productDTO.getQuantity())
                .build()));
    }
    @Operation(
            summary = "Найти продукт по ID"
    )
    @GetMapping("/api/find/product")
    public ResponseEntity<Product> getProductById(@RequestParam int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(product);
    }
    @Operation(
            summary = "Выводит все существующие продукты"
    )
    @SneakyThrows
    @GetMapping("/api/all/product")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }
    @Operation(
            summary = "Удаляет продукт по ID"
    )
    @DeleteMapping("/api/product")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productRepository.delete(product);
        return ResponseEntity.ok().build();
    }
    @Operation(
            summary = "Позволяет обновить продукт по ID"
    )
    @PutMapping("/api/update/product/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable int id, @RequestBody Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productDetails.getName() != null) product.setName(productDetails.getName());
        if (productDetails.getDescription() != null) product.setDescription(productDetails.getDescription());
        if (productDetails.getPrice() != null) product.setPrice(productDetails.getPrice());
        if (productDetails.getQuantity() != null) product.setQuantity(productDetails.getQuantity());

        final Product updatedProduct = productRepository.save(product);
        return ResponseEntity.ok(updatedProduct);
    }
}
