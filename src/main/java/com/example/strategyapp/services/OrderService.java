package com.example.strategyapp.services;
import com.example.strategyapp.DTO.StrategyDTO;
import com.example.strategyapp.entity.Strategy;
import com.example.strategyapp.entity.Company;
import com.example.strategyapp.entity.User;
import com.example.strategyapp.entity.StrategyRepository;
import com.example.strategyapp.entity.CompanyRepository;
import com.example.strategyapp.entity.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Data
@Service
public class OrderService {

    @Autowired
    private StrategyRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Transactional
    public Strategy createOrder(StrategyDTO strategyDTO) {
        List<Company> companies = companyRepository.findAllById(strategyDTO.getProductId());
        User user = userRepository.findById(strategyDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + strategyDTO.getUserId()));

        Strategy newStrategy = Strategy.builder()
                .user(user)
                .companyList(companies)
                .build();

        return orderRepository.save(newStrategy);
    }
}
