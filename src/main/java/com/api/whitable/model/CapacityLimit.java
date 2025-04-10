package com.api.whitable.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "capacity_limits")
public class CapacityLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    private Integer maxGuestsPerHour;
    private String timeSlot; // например, "12:00-14:00"
}
