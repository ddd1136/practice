package com.example.authclothingstore.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@JsonPropertyOrder({"orderId", "userId", "products"})
public class Strategy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("orderId")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("userId")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "orders_product",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )

    @JsonProperty(value = "products")
    private List<Company> companyList = new ArrayList<>();
    
}
