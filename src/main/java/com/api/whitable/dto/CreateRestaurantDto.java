package com.api.whitable.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CreateRestaurantDto {
    private String name;
    private String description;
    private Map<String, Map<String, String>> openingHours;
    private String address;
    private String phoneNumber;
    private Integer averageCheck;
    private String urlToRestaurant;
    private List<String> facilities; // Удобства
    private String cuisineType;
    private Integer capacityForHour; // Кол-во клиентов, которых ресторан может за час обслужить
}
