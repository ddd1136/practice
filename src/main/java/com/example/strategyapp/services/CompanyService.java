package com.example.strategyapp.services;

import com.example.strategyapp.entity.Company;
import com.example.strategyapp.entity.User;
import com.example.strategyapp.entity.CompanyRepository;
import com.example.strategyapp.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    /**
     * Возвращает первую компанию, привязанную к пользователю по username.
     */
    public Company getCompanyForUser(String username) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }
        return companyRepository.findByUser(user).stream().findFirst().orElse(null);
    }
}
