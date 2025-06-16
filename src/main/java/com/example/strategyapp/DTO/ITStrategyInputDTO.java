package com.example.strategyapp.DTO;

import lombok.Data;

@Data
public class ITStrategyInputDTO {
    private Integer companyId;
    private double pi;
    private double npv;
    private double irr;
    private double roi;
    private double tco;
    private double sla;
    private double kpi;
}