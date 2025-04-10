package com.api.whitable.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Long id; // Для удаления
    private Long userId;
    private Long restaurantId;
    private String content;
    private Integer rating;
}
