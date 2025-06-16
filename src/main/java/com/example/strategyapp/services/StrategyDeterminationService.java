package com.example.strategyapp.services;

import com.example.strategyapp.DTO.ITStrategyResultDTO;
import com.example.strategyapp.entity.Company;
import com.example.strategyapp.entity.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StrategyDeterminationService {

    private final CompanyRepository companyRepository;
    private final AIService aiService; // Убедитесь, что AIService находится в том же пакете или корректно импортирован

    public String getAIOutputForStrategy(Integer companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Извлечение финансовых данных (поля должны быть заполнены в сущности Company)
        double initialInvestment = company.getInitialInvestment() != null ? company.getInitialInvestment() : 0;
        double gainFromInvestment = company.getGainFromInvestment() != null ? company.getGainFromInvestment() : 0;
        double discountRate = company.getDiscountRate() != null ? company.getDiscountRate() : 0.1;
        List<Double> cashFlows = company.getCashFlows();
        List<Double> opex = company.getOpex();
        double capex = company.getCapex() != null ? company.getCapex() : initialInvestment;
        double uptime = company.getUptime() != null ? company.getUptime() : 720;
        double downtime = company.getDowntime() != null ? company.getDowntime() : 0;

        // Выполнение расчетов
        double computedROI = initialInvestment != 0 ? ((gainFromInvestment - initialInvestment) / initialInvestment * 100.0) : 0;
        double computedNPV = -initialInvestment;
        if (cashFlows != null) {
            for (int t = 0; t < cashFlows.size(); t++) {
                computedNPV += cashFlows.get(t) / Math.pow(1 + discountRate, t + 1);
            }
        }
        double sumDiscounted = 0;
        if (cashFlows != null) {
            for (int t = 0; t < cashFlows.size(); t++) {
                sumDiscounted += cashFlows.get(t) / Math.pow(1 + discountRate, t + 1);
            }
        }
        double computedPI = initialInvestment != 0 ? sumDiscounted / initialInvestment : 0;
        double computedIRR = (cashFlows != null && initialInvestment != 0) ? calculateIRR(cashFlows, initialInvestment, discountRate) : 0;
        double computedTCO = capex;
        if (opex != null) {
            for (Double expense : opex) {
                computedTCO += expense;
            }
        }
        double computedSLA = uptime != 0 ? ((uptime - downtime) / uptime * 100.0) : 0;
        double computedKPI = computedROI;

        int employees = company.getNumberOfEmployees() != null ? company.getNumberOfEmployees() : 0;
        double normalizedIRR = computedIRR / 15.0;
        double npvBonus = computedNPV > 0 ? 1.0 : 0.0;
        double financialScore = (computedPI + normalizedIRR + npvBonus) / 3.0;

        double sizeScore;
        if (employees >= 1 && employees <= 15) {
            sizeScore = 0.8;
        } else if (employees >= 16 && employees <= 100) {
            sizeScore = 1.0;
        } else if (employees >= 101 && employees <= 250) {
            sizeScore = 1.2;
        } else if (employees >= 251) {
            sizeScore = 1.5;
        } else {
            sizeScore = 1.0;
        }

        double overallScore = (financialScore + sizeScore) / 2.0;
        boolean isLongTerm = overallScore >= 1.3;
        String strategyType = isLongTerm ? "Долгосрочная IT-стратегия" : "Краткосрочная IT-стратегия";
        String recommendedMethodology = pickSingleMethodology(isLongTerm, computedPI, computedNPV, computedIRR, computedROI, computedTCO, computedSLA, computedKPI);

        // Формирование prompt для AI
        StringBuilder prompt = new StringBuilder();
        prompt.append("Даны следующие финансовые показатели для компании:\n")
                .append("PI: ").append(String.format("%.2f", computedPI)).append("\n")
                .append("NPV: ").append(String.format("%.2f", computedNPV)).append("\n")
                .append("IRR: ").append(String.format("%.2f", computedIRR)).append("%\n")
                .append("ROI: ").append(String.format("%.2f", computedROI)).append("%\n")
                .append("TCO: ").append(String.format("%.2f", computedTCO)).append("\n")
                .append("SLA: ").append(String.format("%.2f", computedSLA)).append("%\n")
                .append("KPI: ").append(String.format("%.2f", computedKPI)).append("\n")
                .append("Общий балл готовности: ").append(String.format("%.2f", overallScore)).append("\n")
                .append("Стратегия: ").append(strategyType).append("\n")
                .append("Рекомендуемая методология: ").append(recommendedMethodology).append("\n")
                .append("Объясни, почему для данной компании рекомендуется именно эта IT-стратегия и методология.");

        // Получение AI-вывода
        String aiResponse = aiService.generateResponse(prompt.toString());
        return aiResponse;
    }

    // Метод calculateIRR с защитой от деления на ноль
    private double calculateIRR(List<Double> cashFlows, double initialInvestment, double initialGuess) {
        double irr = initialGuess;
        double npv = calculateNPV(cashFlows, irr, initialInvestment);
        int iterations = 1000;
        while (Math.abs(npv) > 0.01 && iterations > 0) {
            double dNPV = (calculateNPV(cashFlows, irr + 0.0001, initialInvestment) - npv) / 0.0001;
            if (Math.abs(dNPV) < 1e-10) {
                break;
            }
            irr = irr - npv / dNPV;
            npv = calculateNPV(cashFlows, irr, initialInvestment);
            iterations--;
        }
        return irr * 100;
    }

    private double calculateNPV(List<Double> cashFlows, double rate, double initialInvestment) {
        double npv = -initialInvestment;
        for (int t = 0; t < cashFlows.size(); t++) {
            npv += cashFlows.get(t) / Math.pow(1 + rate, t + 1);
        }
        return npv;
    }

    private String pickSingleMethodology(boolean isLongTerm,
                                         double pi, double npv, double irr,
                                         double roi, double tco, double sla, double kpi) {
        if (!isLongTerm) {
            if (irr > 20) {
                return "SWOT-анализ";
            } else if (npv < 0) {
                return "PESTLE";
            } else {
                return "GAP-анализ";
            }
        } else {
            if (tco > 500_000) {
                return "TOGAF";
            } else if (sla < 99) {
                return "ITIL";
            } else if (roi > 15) {
                return "COBIT";
            } else {
                return "Balanced Scorecard";
            }
        }
    }
}
