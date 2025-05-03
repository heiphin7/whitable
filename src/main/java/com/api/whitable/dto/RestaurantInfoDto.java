package com.api.whitable.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantInfoDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phoneNumber;
    private Double rating;
    private Integer reviewCount;
    private String cuisineType;
    private String url;
}
