package com.api.whitable.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;

@Data
@Table(name = "reviews")
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Связь с пользователем
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Связь с рестораном
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private Integer rating;
    private String content;
    private LocalDate createdAt;
}

