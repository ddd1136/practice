package com.example.authclothingstore.DTO;


import lombok.Data;

@Data
public class ProductDTO {
    private String name;
    private String description;
    private Integer price;
    private Integer quantity;
}