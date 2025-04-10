package com.api.whitable.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RestaurantDto {
    private Long id;
    private String name;
    private String image;
    private String description;
    private Double rating;
    private String price;
    private String cuisine;
    private String location;
    private List<String> features;
}
