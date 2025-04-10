package com.api.whitable.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;

    @ElementCollection
    @CollectionTable(name = "restaurant_opening_hours", joinColumns = @JoinColumn(name = "restaurant_id"))
    @MapKeyColumn(name = "day")   // Колонка для хранения ключа (день)
    @Column(name = "hours")       // Колонка для хранения значения (часы работы)
    private Map<String, String> openingHours;

    private String address;
    private String phoneNumber;
    private Double rating = 0.0;
    private Integer ratingCount;
    private Integer averageCheck;
    private String cuisineType;

    // Связь с удобствами
    @ManyToMany
    @JoinTable(
            name = "restaurant_amenities",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private List<Amenity> amenities;
    private String photoUrl;
    private String urlToRestaurants;
    private Integer capacityForHour;
}

