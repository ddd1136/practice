package com.example.authclothingstore.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class CommunicationService {

    private final RestTemplate restTemplate;

    @Autowired
    public CommunicationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T getForObject(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }

    public String buildUrl(String baseUrl, String endpoint, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path(endpoint);
        queryParams.forEach(builder::queryParam);
        return builder.toUriString();
    }
}