package com.example.strategyapp.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Data
@AllArgsConstructor
public class CompanyDTO {
    private String name;
    private String description;
    private Integer price;
    private Integer quantity;
    private Integer numberOfEmployees;
    private Double initialInvestment;
    private Double gainFromInvestment;
    private Double discountRate;
    private List<Double> cashFlows;
    private List<Double> opex;
    private Double capex;
    private Double uptime;
    private Double downtime;
}