package com.example.strategyapp.security;

import com.example.strategyapp.entity.User;
import com.example.strategyapp.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Ищем пользователя по username (без учета регистра)
        User user = userRepository.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        // Возвращаем объект UserDetails с ролью "USER" (при необходимости можно добавить роли)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(() -> "ROLE_USER")
        );
    }
}
