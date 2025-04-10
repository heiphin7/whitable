package com.api.whitable.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "amenities")
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;
}
