package com.example.authclothingstore.services;

import com.example.authclothingstore.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.authclothingstore.DTO.StrategyDTO;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private StrategyRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Strategy createOrder(StrategyDTO strategyDTO) throws Exception {
        Strategy newStrategy = new Strategy();
        List<Company> productsForOrder = new ArrayList<>();

        // Получение пользователя
        User user = userRepository.findById(strategyDTO.getUserId())
                .orElseThrow(() -> new Exception("User not found with id: " + strategyDTO.getUserId()));

        for (Integer productId : strategyDTO.getProductId()) {
            Company company = companyRepository.findById(productId)
                    .orElseThrow(() -> new Exception("Product not found"));

            if (company.getQuantity() > 0) {
                company.setQuantity(company.getQuantity() - 1);
                productsForOrder.add(company);
                companyRepository.save(company);
            } else {
                throw new Exception("Product is out of stock");
            }
        }

        newStrategy.setCompanyList(productsForOrder);
        newStrategy.setUser(user);  // Установка пользователя
        return orderRepository.save(newStrategy);
    }
}