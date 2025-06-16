package com.example.strategyapp.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class StrategyDTO {
    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("products")
    private List<Integer> productId;
}
