package com.example.authclothingstore.services;

import com.example.authclothingstore.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authclothingstore.DTO.OrderDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws Exception {
        Order newOrder = new Order();
        List<Product> productsForOrder = new ArrayList<>();

        // Получение пользователя
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new Exception("User not found with id: " + orderDTO.getUserId()));

        for (Integer productId : orderDTO.getProductId()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new Exception("Product not found"));

            if (product.getQuantity() > 0) {
                product.setQuantity(product.getQuantity() - 1);
                productsForOrder.add(product);
                productRepository.save(product);
            } else {
                throw new Exception("Product is out of stock");
            }
        }

        newOrder.setProductList(productsForOrder);
        newOrder.setUser(user);  // Установка пользователя
        return orderRepository.save(newOrder);
    }
}