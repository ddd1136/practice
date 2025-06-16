package com.example.strategyapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Отправляет запрос в OpenAI API и возвращает ответ модели.
     */
    public String generateResponse(String prompt) {
        String url = "https://api.openai.com/v1/chat/completions";

        // Формируем тело запроса согласно спецификации OpenAI Chat API
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        // Добавляем системное сообщение для задания тона
        messages.add(Map.of("role", "system", "content", "You are a helpful assistant that explains financial calculation results."));
        messages.add(Map.of("role", "user", "content", prompt));

        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Map body = response.getBody();
                List choices = (List) body.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map firstChoice = (Map) choices.get(0);
                    Map message = (Map) firstChoice.get("message");
                    if (message != null) {
                        return (String) message.get("content");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Извините, не удалось получить дополнительное объяснение от модели.";
    }
}
