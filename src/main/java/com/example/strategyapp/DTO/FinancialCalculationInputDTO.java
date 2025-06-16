package com.example.strategyapp.DTO;

import lombok.Data;
import java.util.List;

@Data
public class FinancialCalculationInputDTO {
    private Integer companyId;
    private double initialInvestment;
    private double gainFromInvestment;
    private double discountRate;
    private List<Double> cashFlows;
    private List<Double> opex;
    private double capex;
    private double uptime;
    private double downtime;
}