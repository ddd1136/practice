package com.example.strategyapp.DTO;

import lombok.Data;
import java.util.List;

@Data
public class ITStrategyResultDTO {
    private String strategyType;
    private String recommendedMethodology;
    private List<String> recommendedMetrics;
    private String financialExplanation;
}