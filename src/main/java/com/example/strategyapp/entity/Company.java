package com.example.strategyapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;
    @Getter
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "quantity")
    private Integer quantity;

    // Поле для количества сотрудников
    @Column(name = "number_of_employees")
    private Integer numberOfEmployees;

    // Финансовые исходные данные
    @Column(name = "initial_investment")
    private Double initialInvestment;

    @Column(name = "gain_from_investment")
    private Double gainFromInvestment;

    @Column(name = "discount_rate")
    private Double discountRate;

    @ElementCollection
    @CollectionTable(name = "company_cash_flows", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "cash_flow")
    private List<Double> cashFlows;

    @ElementCollection
    @CollectionTable(name = "company_opex", joinColumns = @JoinColumn(name = "company_id"))
    @Column(name = "opex")
    private List<Double> opex;

    @Column(name = "capex")
    private Double capex;

    @Column(name = "uptime")
    private Double uptime; // например, часы в месяц

    @Column(name = "downtime")
    private Double downtime; // часы простоя
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
