package com.example.authclothingstore.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("products")
    private List<Integer> productId;
}
