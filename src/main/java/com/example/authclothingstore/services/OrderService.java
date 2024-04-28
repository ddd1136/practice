package com.example.authclothingstore.services;
import com.example.authclothingstore.DTO.OrderDTO;
import com.example.authclothingstore.entity.Order;
import com.example.authclothingstore.entity.Product;
import com.example.authclothingstore.entity.User;
import com.example.authclothingstore.entity.OrderRepository;
import com.example.authclothingstore.entity.ProductRepository;
import com.example.authclothingstore.entity.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        List<Product> products = productRepository.findAllById(orderDTO.getProductId());
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + orderDTO.getUserId()));

        Order newOrder = Order.builder()
                .user(user)
                .productList(products)
                .build();

        return orderRepository.save(newOrder);
    }
}
