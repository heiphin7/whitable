package com.api.whitable.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private Long id; // Для удаления
    private String title;
    private String content;
    private Integer rating;
}
