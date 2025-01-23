package com.example.authclothingstore.controller;

import com.example.authclothingstore.DTO.UserDTO;
import com.example.authclothingstore.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "user_controller")
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final StrategyRepository orderRepository;
    private final ObjectMapper objectMapper;


    @Operation(
            summary = "Добавляет пользователя в базу"
    )
    @PostMapping("/api/add/user")
    public void addUser(@RequestBody UserDTO userDTO) {
        log.info("New user - " + userRepository.save(User.builder()
                        .username(userDTO.getUsername())
                        .password(userDTO.getPassword())
                .build()));
    }

    @Operation(
            summary = "Показывает всех пользователей"
    )
    @SneakyThrows
    @GetMapping("/api/all/user")
    public  List<User> getAllUsers() {
        return userRepository.findAll();
    }


    @Operation(
            summary = "Позволяет найти пользователя по ID"
    )
    @GetMapping("/api/find/user")
    public User getUsers(@RequestParam int id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Operation(
            summary = "Удаляет пользователя по ID"
    )
    @DeleteMapping("/api/user")
    public void deleteUser(@RequestParam int id) {
        userRepository.deleteById(id);
    }
    @Operation(
            summary = "Обновляет пользователя"
    )
    @PutMapping("/api/update/user")
    public String changeUser(@RequestBody User user) {
        if(!userRepository.existsById(user.getId())) {
            return "User not found";
        }
        return userRepository.save(user).toString();
    }

}
