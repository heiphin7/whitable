package com.api.whitable.model;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "cuisine_types")
@Entity
@Data
public class CuisineType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String name;
}
