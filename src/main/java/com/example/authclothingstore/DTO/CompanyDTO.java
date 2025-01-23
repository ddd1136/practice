package com.example.authclothingstore.DTO;


import lombok.Data;

@Data
public class CompanyDTO {
    private String name;
    private String description;
    private Integer price;
    private Integer quantity;
}